package algorithms.clustering;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javolution.util.FastSet;
import topology.AbstractSimpleEdge;
import topology.GraphInterface;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;

import common.Heap;
import common.Pair;

/**
 * Caution: Inconsistent state: clusters, border information is maintained only in m_clusterToVertices and m_clusterToBorders at this stage.
 * The appropriate information is set in the vertices only after clustering is complete. Be careful not to use v.isBorder() or v.getClusters()
 * internally in this class.
 * @author root
 *
 * @param <VertexType>
 */
public class BudgetedGreedyClustering<VertexType extends Number,VertexInfoStructure> extends EdgeCutClustering<VertexType,VertexInfoStructure> {

	protected int desiredEdges;
	protected int minEdges;
	protected int estimate;
	Map<Pair<Integer, Integer>, Move> pairToMove;

	protected Map<Integer, Set<VertexType>> bestClusterToVertices = new HashMap<Integer, Set<VertexType>>();
	protected Map<Integer, Set<VertexType>> bestClusterToBorders = new HashMap<Integer, Set<VertexType>>();

	public BudgetedGreedyClustering(GraphInterface<VertexType,VertexInfoStructure> g, int desired, int lowerbound) {
		super(g);
		desiredEdges = desired;
		minEdges = lowerbound;
		estimate = g.getNumberOfEdges();
		pairToMove = new HashMap<Pair<Integer,Integer>, Move>();
	}

	protected void doClustering() {
		int trial = 0;
		int bestEstimate = Integer.MAX_VALUE;
		
		//Initially every vertex is assigned to its own cluster
		for(VertexType v : m_graph.getVertices()){
			applyLabel(v, v.intValue());
		}
		computeBorderVertices();

		//Compute scores of merging every possible pair
		//Store scores in a priority queue
		Heap<Move> pQ = new Heap<Move>(m_graph.getNumberOfEdges());

		for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e :m_graph.getEdges()) {
			int c0 = e.getV0().intValue(); //Initially every vertex is its own cluster
			int c1 = e.getV1().intValue();
			if(c0 != c1) { //To avoid self loops from being considered
				Set<VertexType> noB = new FastSet<VertexType>();
				double s = computeScore2(c0, c1, noB);
				Move m = new Move(c0, c1, s, noB);
				pQ.insert(m.score, m);
				pairToMove.put(new Pair<Integer, Integer>(c0, c1), m);
			}
		}		

		do {
			//Dequeue best merge
			Move bestMove = pQ.deleteMin();
			//Remove moves related to best moves
			for(int cn : neighboursOfCluster(bestMove.c0)) {
				Pair<Integer, Integer> p1 = new Pair<Integer, Integer>(bestMove.c0, cn);
				Pair<Integer, Integer> p2 = new Pair<Integer, Integer>(cn, bestMove.c0);
				if (pairToMove.containsKey(p1)) {
					pQ.remove(pairToMove.get(p1));
					pairToMove.remove(p1);
				}
				if (pairToMove.containsKey(p2)) {
					pQ.remove(pairToMove.get(p2));
					pairToMove.remove(p2);
				}
			}
			for(int cn : neighboursOfCluster(bestMove.c1)) {
				Pair<Integer, Integer> p1 = new Pair<Integer, Integer>(bestMove.c1, cn);
				Pair<Integer, Integer> p2 = new Pair<Integer, Integer>(cn, bestMove.c1);
				if (pairToMove.containsKey(p1)) {
					pQ.remove(pairToMove.get(p1));
					pairToMove.remove(p1);
				}
				if (pairToMove.containsKey(p2)) {
					Move m1 = pairToMove.get(p2);
					pQ.remove(m1);
					pairToMove.remove(p2);
				}
			}

			merge(bestMove);

			//Recalculate changed scores
			for(int cn : neighboursOfCluster(bestMove.c0)) {
				Set<VertexType> noB = new FastSet<VertexType>();
				double s = computeScore2(bestMove.c0, cn, noB);
				Move m = new Move(bestMove.c0, cn, s, noB);
				pQ.insert(s,m);
				pairToMove.put(new Pair<Integer, Integer>(bestMove.c0, cn), m);
			}	

			estimate = estimate + (int)bestMove.score;
			System.out.println(trial+" : "+estimate);
			trial++;

			if(estimate < bestEstimate && estimate > minEdges) {
				bestEstimate = estimate;
				updateSnapshots();
			}
						
		} while(estimate > desiredEdges && !pQ.isEmpty());//Check number of edges, If < desiredEdges then stop

		//finally just set all borders to false so that they can be reset correctly by computeBorderVertices()
		for(VertexType v : m_graph.getVertices()) {
			if (VertexFactory.isVertexInfo(m_graph.getVertex(v))){
				((VertexInfo)m_graph.getVertex(v)).setBorder(false);
				((VertexInfo)m_graph.getVertex(v)).getClusters().clear();
			}
			else 
				throw new IllegalArgumentException("The Vertex must be VertexInfo");
		}

		//Now the best clustering is stored in bestClusterToVertices and bestClusterToBorders. Apply these
		m_clusterToVertices.clear();
		m_clusterToBorders.clear();
		for(int c : bestClusterToVertices.keySet()) {
			for(VertexType v : bestClusterToVertices.get(c)) {
				applyLabel(v, c);
			}
		}
		m_clusterToBorders = bestClusterToBorders;


		//finally unmerge all the clusters which are not good for sato construction
		Set<Integer> clusters = new HashSet<Integer>();
		clusters.addAll(getClusterIds());
		for(int c : clusters) {
			int b = getBorderVertices(c).size();
			Set<VertexType> vertices = getVertices(c);
			int a = vertices.size() - b;
			int sato = b*a + (b*(b-1))/2;
			int m = getNumberOfEdges(vertices);

			if(m < sato) {
				m_clusterToVertices.remove(c);
				m_clusterToBorders.remove(c);
				for(VertexType v : vertices) {
					//remove the label c
					if (VertexFactory.isVertexInfo(m_graph.getVertex(v))){
						((VertexInfo)getGraph().getVertex(v)).removeFromCluster(c);
						applyLabel(v, v.intValue());
					}
					else 
						throw new IllegalArgumentException("The Vertex must be VertexInfo");
				}
			}
		}		
	}	

	protected void updateSnapshots() {
		bestClusterToVertices = new HashMap<Integer, Set<VertexType>>();
		for(int c : m_clusterToVertices.keySet()) {
			Set<VertexType> vertices = new HashSet<VertexType>();
			for(VertexType v : m_clusterToVertices.get(c)) {
				vertices.add(v);
			}
			bestClusterToVertices.put(c, vertices);
		}
		bestClusterToBorders = new HashMap<Integer, Set<VertexType>>();
		for(int c : m_clusterToBorders.keySet()) {
			Set<VertexType> vertices = new HashSet<VertexType>();
			for(VertexType v : m_clusterToBorders.get(c)) {
				vertices.add(v);
			}
			bestClusterToBorders.put(c, vertices);
		}		
	}

	protected void merge(Move m) {
		for(VertexType v: this.getVertices(m.c1)){
			applyLabel(v, m.c0);
			BasicVertexInfo vi = m_graph.getVertex(v);
			if (VertexFactory.isVertexInfo(vi))
				((VertexInfo)vi).getClusters().remove((Integer)m.c1);
			else 
				throw new IllegalArgumentException("The Vertex must be VertexInfo");
		}

		m_clusterToVertices.remove(m.c1);
		m_clusterToBorders.get(m.c0).addAll((m_clusterToBorders.get(m.c1)));
		m_clusterToBorders.remove(m.c1);

		Set<VertexType> b1 = m_clusterToBorders.get(m.c0);
		b1.removeAll(m.getNoB());		
	}

	@Deprecated
	protected int computeScore(int c1, int c2, Set<VertexType> noLongerBorder){
		int b1 = getBorderVertices(c1).size();
		int b2 = getBorderVertices(c2).size();
		int a1 = getVertices(c1).size() - b1;
		int a2 = getVertices(c2).size() - b2;
		int delta = getCutSize(c1, c2);
		noLongerBorder.addAll(getExclusiveVertices(c1, c2));
		int nE = noLongerBorder.size();
		return score(b1+b2-nE,a1+a2+nE) - (score(b1,a1) + score(b2,a2) + delta);			
	}
	@Deprecated
	private int score(int b, int a) {
		return b*a + (b*(b-1))/2;
	}

	protected int computeScore2(int c1, int c2, Set<VertexType> noLongerBorder) {
		noLongerBorder.addAll(getExclusiveVertices(c1, c2));
		int nE = noLongerBorder.size();
		int delta = getCutSize(c1, c2);
		return -nE;
		//return score(c1,c2,nE) - (score(c1) + score(c2) + delta);			
	}

	private int score(int c) {
		int b = getBorderVertices(c).size();
		Set<VertexType> vertices = getVertices(c);
		int a = vertices.size() - b;
		//int sato = b*a + (b*(b-1))/2;
		int sato = (b*(b-1))/2;
		return sato;		
	}	

	private int score(int c1, int c2, int nE) {
		int b1 = getBorderVertices(c1).size();
		int b2 = getBorderVertices(c2).size();
		Set<VertexType> vertices1 = getVertices(c1);
		Set<VertexType> vertices2 = getVertices(c2);
		int a1 = vertices1.size() - b1;
		int a2 = vertices2.size() - b2;
		//int sato = (b1+b2-nE)*(a1+a2+nE) + ((b1+b2-nE)*((b1+b2-nE)-1))/2;
		int sato = ((b1+b2-nE)*((b1+b2-nE)-1))/2;
		return sato;		
	}

	/**
	 * Returns the number of edges in the original graph as induced by the given vertices
	 * @param vertices
	 * @return
	 */
	private int getNumberOfEdges(Set<VertexType> vertices) {
		int m = 0;
		GraphInterface<VertexType,VertexInfoStructure> g = getGraph();
		for(VertexType v : vertices) {
			for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e : g.getOutgoingEdges(v)) {
				if(vertices.contains(e.getNeighbor(v))) {
					m++;
				}
			}
		}
		m = m/2;
		return m;
	}
	protected int getCutSize(int c1, int c2){
		int n = 0;
		for(VertexType v: getBorderVertices(c1)){
			for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e: m_graph.getOutgoingEdges(v)){
				int c = getClusters(e.getNeighbor(v)).get(0);
				if(c==c2) n++;						
			}	
		}
		return n;
	}

	protected Set<VertexType> getExclusiveVertices(int c1, int c2){
		Set<VertexType> ex = new FastSet<VertexType>();
		for(VertexType b1: getBorderVertices(c1)){
			if(isExclusive(b1, c1, c2))
				ex.add(b1);
		}
		for(VertexType b2: getBorderVertices(c2)){
			if(isExclusive(b2, c1, c2))
				ex.add(b2);
		}
		return ex;
	}

	protected boolean isExclusive(VertexType v, int c1, int c2){
		boolean ex = true;
		for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e: m_graph.getOutgoingEdges(v)){
			int c = getClusters(e.getNeighbor(v)).get(0);
			if( c!= c2 && c!=c1)
				return false;
		}
		return ex;
	}

	public class Move {
		int c0, c1;
		double score;
		Set<VertexType> NoB;

		protected Move(int c0, int c1) {
			super();
			this.c0 = c0;
			this.c1 = c1;
		}		
		protected Move(int c0, int c1, double score, Set<VertexType> noB) {
			super();
			this.c0 = c0;
			this.c1 = c1;
			this.score = score;
			NoB = noB;			
		}

		protected double getScore() {
			return score;
		}
		protected void setScore(double score) {
			this.score = score;
		}
		protected Set<VertexType> getNoB() {
			return NoB;
		}
		protected void setNoB(Set<VertexType> noB) {
			NoB = noB;
		}		

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(c0).append(",").append(c1).append(" : ").append(score);
			return sb.toString();
		}
	}
}
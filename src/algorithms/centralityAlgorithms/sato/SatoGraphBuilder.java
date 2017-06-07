package algorithms.centralityAlgorithms.sato;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.Index;
import topology.AbstractHyperEdge;
import topology.BasicVertexInfo;
import topology.DiGraphAsHashMap;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.TrastTrafficMatrix;
import algorithms.clustering.Clustering;
import algorithms.shortestPath.GraphTraversalListener;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;
import algorithms.shortestPath.ShortestPathFactory;


public class SatoGraphBuilder implements GraphTraversalListener{
	
	protected GraphInterface<Index,BasicVertexInfo> m_graph;
	protected GraphInterface<Index,BasicVertexInfo> m_satoGraph;
	protected Clustering<Index,BasicVertexInfo> m_clustering;
	public CurrentContext m_context;
	protected FastSet<AbstractSimpleEdge<Integer,BasicVertexInfo>> m_borderToBorderLinks = new FastSet<AbstractSimpleEdge<Integer,BasicVertexInfo>>();
	protected ShortestPathAlgorithmInterface m_traversal;
	protected TrastTrafficMatrix tm;

	
	public SatoGraphBuilder (Clustering<Index,BasicVertexInfo> clustering, ShortestPathAlg traversalAlgorithm){
		m_clustering = clustering;
		m_graph = m_clustering.getGraph();
		m_traversal = ShortestPathFactory.getShortestPathAlgorithm(traversalAlgorithm, m_graph, false);
		m_traversal.addListener(this);
		m_context = new CurrentContext();
	}

	public GraphInterface<Index,BasicVertexInfo> buildSATOGraph()  throws Exception{
		m_clustering.generateClusters();
		m_satoGraph = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(Index v: m_graph.getVertices()){
			m_satoGraph.addVertex(v, m_graph.getVertex(v));
		}
		for(Index b: m_clustering.getBorderVertices()){
			m_context.updateContext(b);
			//System.out.println("Handling border vertex: "+b.intValue());
			handleBorderVertex(b);
		}
		//All appropriate edges have been added. Now add weights to border vertices
		tm = new TrastTrafficMatrix(m_clustering.getBorderVertices().size());
		for(int c : m_clustering.getClusterIds()) {
			Set<Index> borders = m_clustering.getBorderVertices(c);
			int b = borders.size();
			int a = m_clustering.getVertices(c).size() - b;
			for(Index v : borders) {
				tm.addWeight(v, (1.0*a)/b);
			}
		}
		
		//return new OptimizedGraphAsArray<Index>(m_satoGraph);
		return m_satoGraph;
		//return directed(m_satoGraph);
	}
	
	/**
	 * Precondition: buildSatoGraph() is already executed
	 * @return
	 */
	public TrastTrafficMatrix getTrafficMatrix() {
		return tm;
	}
	
	protected DiGraphAsHashMap<Index,BasicVertexInfo> directed(GraphInterface<Index,BasicVertexInfo> sato) {
		DiGraphAsHashMap<Index,BasicVertexInfo> directedSato = new DiGraphAsHashMap<Index,BasicVertexInfo>();
		for(Index v: sato.getVertices()) {
			directedSato.addVertex(v, sato.getVertex(v));
		}
		for(Index v : sato.getVertices()) {
			if(m_clustering.isBorder(v)) {
				for(AbstractSimpleEdge<Index,BasicVertexInfo> e : sato.getOutgoingEdges(v)) {
					directedSato.addEdge(v, e.getNeighbor(v), sato.getEdgeWeight(e));
				}
			}
		}
		return directedSato;
	}
	protected void handleBorderVertex(Index border){
		m_context.updateContext(border);
		m_traversal.run(border.intValue());
		for(Index i: m_context.sigma.keySet()){
			if(m_clustering.isBorder(i)) {
				if(i.intValue() <= border.intValue()) 
					continue;
				EdgeInfo<Index,BasicVertexInfo> e = new EdgeInfo<Index,BasicVertexInfo>(m_traversal.getDistance(i.intValue()), 1.0);
				e.setMultiplicity(m_context.sigma.get(i));
				m_satoGraph.addEdge(border, i, e);
			}
		}
	}
	
	@Override
	public void beforeExpand(Index v) {
		//Nothing to do
		
	}
	
	@Override
	public boolean isExpandable(Index v) {
		if(m_clustering.isBorder(v) && v.intValue() != m_context.border.intValue())
			return false;
		else 
			return true;
	}

	@Override
	public void vertexDiscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge) {
		if(m_context.sigma.containsKey(w))
			m_context.sigma.put(w, m_context.sigma.get(w) + m_context.sigma.get(v));
		else
			m_context.sigma.put(w, m_context.sigma.get(v));
	}

	@Override
	public void vertexRediscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge, double dist) {
		if(m_context.sigma.containsKey(w) && dist == m_traversal.getDistance(w.intValue()))
			m_context.sigma.put(w, m_context.sigma.get(w) + m_context.sigma.get(v));
	}

	@Override
	public void afterExpand(Index v) {
		// Nothing to do
	}
	
	class CurrentContext{
		protected FastMap<Index, Integer> sigma; //This is the buffer of integers that stores current values
		//of sigmas for current border vertex
		protected Index border;
		
		void updateContext(Index border){
			this.border = border;
			if(sigma!=null)
				sigma.clear();
			else
				sigma = new FastMap<Index, Integer>();
			sigma.put(border, 1);
		}
	}
	
	public String toString(){
		List<AbstractSimpleEdge<Index,BasicVertexInfo>> edges = new ArrayList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
		edges.addAll(m_satoGraph.getEdges());
		Collections.sort(edges, new EdgeComparator());
		String res = "";
		for(AbstractSimpleEdge<Index,BasicVertexInfo> e : edges){
			EdgeInfo<Index,BasicVertexInfo> eInfo = m_satoGraph.getEdgeWeight(e);
			res += "( "+ e.getV0() +", "+ e.getV1() + ": " + eInfo.getLatency() + ", " + eInfo.getMultiplicity()  + " ); ";
		}
		return res;
	}
	
	class EdgeComparator implements Comparator<AbstractSimpleEdge<Index,BasicVertexInfo>>{

		@Override
		public int compare(AbstractSimpleEdge<Index,BasicVertexInfo> o1, AbstractSimpleEdge<Index,BasicVertexInfo> o2) {
			if(o1.getV0().intValue() < o2.getV0().intValue())
				return -1;
			else if(o1.getV0().intValue() > o2.getV0().intValue())
				return 1;
			if(o1.getV1().intValue() < o2.getV1().intValue())
				return -1;
			else if(o1.getV1().intValue() > o2.getV1().intValue())
				return 1;
			else
				return 0;
		}
		
	}
}
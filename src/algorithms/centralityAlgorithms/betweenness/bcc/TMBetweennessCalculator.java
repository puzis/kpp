package algorithms.centralityAlgorithms.betweenness.bcc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;
import server.common.DummyProgress;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.GraphUtils;
import topology.TopologyConstants;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.bcc.BCCAlgorithm;
import algorithms.bcc.BCCAlgorithmListener;
import algorithms.bcc.BiConnectedComponent;
import algorithms.centralityAlgorithms.betweenness.brandes.TrafficMatrixBC;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

import common.Pair;


/** The BetweennessCalculator listens to the given BCCAlgorithm and whenever a component 
 *  or a cutoff vertex is closed, the BetweennessCalculator updates the component tree
 *  and the communication array. 
 */ 
public class TMBetweennessCalculator implements BCCAlgorithmListener, BCCalculatorInterface{

	protected BCCAlgorithm m_bccAlg;
	protected GraphInterface<Object,Object> m_componentsTree;
	protected ShortestPathAlg m_spAlg;
	protected int m_numberOfVertices;

	// Sum of multiplicities of the vertices in the original graph (the input graph to the BCCAlgorithm)
	protected int m_size; 
	protected FastList<Index[]> m_components;//@Rami
	
	private double[] m_communication;
	
	private AbsTrafficMatrix m_gtm;
	private int[] discoveryOrder;
//	private FastMap<Index, LinkedList<EdgeInterface<Index>>> m_creatingEdges;
	private Map<GraphInterface<Index,BasicVertexInfo>, Pair<Index, Index>> m_componentRanges;
	
	
	public TMBetweennessCalculator(BCCAlgorithm bccAlg, AbsTrafficMatrix tm){
		this(bccAlg, ShortestPathAlgorithmInterface.DEFAULT, tm);
	}
	
	/**
	 * @param bccAlg - the bi-connected-components (BCC) extractor algorithm that the BetweennessCalculator listens to.  
	 * @param spAlg - shortest path algorithm, this parameter is required for the computation of intra-communication.
	 * @param tm - the traffic matrix of the original graph (over which the BCC algorithm is applied).
	 */
	public TMBetweennessCalculator(BCCAlgorithm bccAlg, ShortestPathAlg spAlg, AbsTrafficMatrix tm){
		m_bccAlg = bccAlg;
		m_componentsTree = new GraphAsHashMap<Object,Object>();
		m_bccAlg.addListener(this);
		m_spAlg = spAlg;
		m_numberOfVertices = m_bccAlg.getGraph().getNumberOfVertices();
		m_size = m_bccAlg.getGraph().getSize();
		m_components = new FastList<Index[]>();
		m_gtm = tm;
		m_communication = new double[m_numberOfVertices];
		Arrays.fill(m_communication, 0);
		discoveryOrder = m_bccAlg.getDiscoverOrder();
//		m_creatingEdges = new FastMap<Index, LinkedList<EdgeInterface<Index>>>();
		m_componentRanges = new HashMap<GraphInterface<Index,BasicVertexInfo>, Pair<Index,Index>>();
	}
	
	/** The BetweennessCalculator listens to the given BCCAlgorithm and whenever a component 
	 *  or a cutoff vertex is closed, the BetweennessCalculator updates the component tree
	 *  and the communication array. 
	 */ 
	public void runBCCAlgorithm(){
		m_bccAlg.run();
	}
	
	//======================= Functions called by the BCC algorithm =================================
	@Override
	public void addComponent(GraphInterface<Index,BasicVertexInfo> component) {
		if (!m_componentsTree.isVertex(component))
			m_componentsTree.addVertex(component, new BiConnectedVertexInfo());
	}

	/** Each time a bcc is generated by a cutoff v:
	 *  Let C be the relevant connected component (which includes the bcc after removal of v)
	 *  Let w be the vertex with minimal discover order in C (v is connected to w via discovery edge).
	 *  C = {z : discoverOrder[z]>=discoverOrder[w]}
	 *  discoverOrder[z] <= maximal discover order in the current connected component.
	 *  Note that at this point there are no vertices z such that
	 *  discoverOrder[z] > maximal discover order in the current connected component.
	 *  because maximal discover order in the current (just generated) connected component = maximal discover order so far.
	 *  
	 *  interBC[v]+= \sum_{x in C\cup{v}, y in V\C} GTM[x,y]
	 *  this includes communications of v to and from C. */
	@Override
	public void addCutoffVertex(Index cutoffVertex, AbstractSimpleEdge<Index,BasicVertexInfo> creatingEdge) {
		if (!m_componentsTree.isVertex(cutoffVertex)){
			BasicVertexInfo vInfo = new BiConnectedVertexInfo();
			if (VertexFactory.isVertexInfo(vInfo))
				((VertexInfo)vInfo).setLabel(TopologyConstants.VERTEX_MULTIPLICITY, Integer.toString(
						((VertexInfo)m_bccAlg.getGraph().getVertex(cutoffVertex)).getMultiplicity()));
			else 
				throw new IllegalArgumentException("The Vertex must be VertexInfo");
			m_componentsTree.addVertex(cutoffVertex, vInfo);
		}
//		if (!m_creatingEdges.containsKey(cutoffVertex)){
//			m_creatingEdges.put(cutoffVertex, new LinkedList<EdgeInterface<Index>>());
//		}
//		m_creatingEdges.get(cutoffVertex).add(creatingEdge);
		int interBC = 0;
		int v = cutoffVertex.intValue();
		int w = (creatingEdge.getV0().intValue() == v ? creatingEdge.getV1().intValue():
			                                            creatingEdge.getV0().intValue());
		for (int x=0; x<discoveryOrder.length; x++){
			for (int y=0; y<discoveryOrder.length; y++){
				if ((discoveryOrder[x] >= discoveryOrder[w] && discoveryOrder[x]!=Integer.MAX_VALUE)&&
				    (y!=v && (discoveryOrder[y] < discoveryOrder[w] || discoveryOrder[y]==Integer.MAX_VALUE))){
					interBC += m_gtm.getWeight(x, y); //[x][y];
				}
			}
		}
		m_communication[cutoffVertex.intValue()]-=interBC;
	}
	
	@Override
	public void addEdge(Index v0, GraphInterface<Index,BasicVertexInfo> v1){
		if (!m_componentsTree.isEdge(v0, v1)){
			m_componentsTree.addEdge(v0, v1, new BCCTreeEdgeInfo(-1));
			((BiConnectedVertexInfo)m_componentsTree.getVertex(v0)).increaseUnmarkedNeighborsCounter();
			((BiConnectedVertexInfo)m_componentsTree.getVertex(v1)).increaseUnmarkedNeighborsCounter();
		}
	}

	public void closeComponent(GraphInterface<Index,BasicVertexInfo> component, int minDiscoveryOrder, int maxDiscoveryOrder){
		m_componentRanges.put(component, new Pair<Index, Index>(Index.valueOf(minDiscoveryOrder), 
                Index.valueOf(maxDiscoveryOrder)));
		
		computeIntraCommunication(component);		
		
		//@Rami
//		Index[] componentType = new Index[0];
		LinkedList<Index> comp = new LinkedList<Index>(); 
		for (Index v : component.getVertices())
    		comp.add(v);        	
    	m_components.add(comp.toArray(new Index[comp.size()]));    	
    	//TODO: extract method, pull up method: BCCalculator.computeIntraCommunication
	}

	/** when v is done (dfs back off):
	 *  C = {z : discoverOrder[z]<discoverOrder[v] or discoverOrder[z]=NA}
	 *  interBC[v]+= \sum_{x in C\cup{v}, y in V\C} GTM[x,y]
	 *  this includes communications of v to and from C.
	 *  @param cutoffVertex */
	@Override
	public void closeCutoffVertex(Index cutoffVertex) {
		int interBC = 0;
		int v = cutoffVertex.intValue();
		
		ArrayList<Index> ys = getVertexesInRange(cutoffVertex);
		
		for (int x=0; x<discoveryOrder.length; x++){
			for (int y=0; y<discoveryOrder.length; y++){
				
				boolean xCondition = x!=v && !ys.contains(Index.valueOf(x));  
				
				boolean yCondition = y!=v && ys.contains(Index.valueOf(y)); 
				
				if (xCondition && yCondition){
					interBC += m_gtm.getWeight(x, y); //[x][y];
				}
			}
		}
		m_communication[cutoffVertex.intValue()]-=interBC;
	}

	//===============================================================================================

	private void computeIntraCommunication(GraphInterface<Index,BasicVertexInfo> component){
		AbsTrafficMatrix lcw = null;
    	try{
    		lcw = createTrafficMatrix(component);
    	}
    	catch(Exception ex){
    		
    	}
        GraphInterface<Index,BasicVertexInfo> mappedGraph = GraphUtils.mapGraph(component, m_numberOfVertices - 1);
        TrafficMatrixBC tmBC = new TrafficMatrixBC(m_spAlg, mappedGraph, lcw, new DummyProgress(), 1);
        tmBC.run();
        int index = 0;
		for (Index vIdx : component.getVertices()){
        	int v = vIdx.intValue();
        	m_communication[v] += tmBC.getCentrality(index);
        	index++;
        }
	}
	
	private ArrayList<Pair<Index, Index>> getRanges(Index v, GraphInterface<Index,BasicVertexInfo> component){
		ArrayList<Pair<Index, Index>> ranges = new ArrayList<Pair<Index,Index>>(m_componentRanges.size());
		ranges.ensureCapacity(m_componentRanges.size());
		
		for(AbstractSimpleEdge<Object,Object> e: m_componentsTree.getOutgoingEdges(v)){
			GraphInterface<Index,BasicVertexInfo> c = (GraphInterface<Index,BasicVertexInfo>) e.getNeighbor(v);
			Pair<Index, Index> range = m_componentRanges.get(c);
			if (range != null && (component==null || !c.equals(component)) && !ranges.contains(range))
				ranges.add(range);
		}
		return ranges;
	}
	
	private ArrayList<Index> getVertexesInRange(Index v){
		ArrayList<Pair<Index, Index>> yRanges = getRanges(v, null);
		ArrayList<Index> ys = new ArrayList<Index>(discoveryOrder.length);
		ys.ensureCapacity(discoveryOrder.length);
		
		for (int y=0; y<discoveryOrder.length; y++){
			Iterator<Pair<Index, Index>> ranges = yRanges.iterator();
			while (ranges.hasNext()){
				Pair<Index, Index> range = ranges.next();
				int r = range.getValue1().intValue();
				int l = range.getValue2().intValue();
				if (r<=discoveryOrder[y] && discoveryOrder[y]<=l){
					ys.add(Index.valueOf(y));
					break;
				}
			}
		}
		return ys;
	}
	
	private ArrayList<Index> getVertexesInRange(Index v, GraphInterface<Index,BasicVertexInfo> component){
		ArrayList<Index> ys = new ArrayList<Index>(discoveryOrder.length);
		ys.ensureCapacity(discoveryOrder.length);
		
		boolean vCutoff = m_componentsTree.isVertex(v);

		if(vCutoff){
			boolean creating = isCreatingVertex(component, v);
			if(creating){				
				Pair<Index, Index> range = m_componentRanges.get(component);
				for (int y=0; y<discoveryOrder.length; y++){
					int yOrder = discoveryOrder[y];
					if(yOrder < range.getValue1().intValue() || yOrder > range.getValue2().intValue() || yOrder == Integer.MAX_VALUE){
						ys.add(Index.valueOf(y));						
					}
				}
				
			}else{//not creating
				ArrayList<Pair<Index, Index>> yRanges = getRanges(v, component);
				for (int y=0; y<discoveryOrder.length; y++){
					Iterator<Pair<Index, Index>> ranges = yRanges.iterator();
					while (ranges.hasNext()){
						Pair<Index, Index> range = ranges.next();
						int r = range.getValue1().intValue();
						int l = range.getValue2().intValue();
						if (r<=discoveryOrder[y] && discoveryOrder[y]<=l){
							ys.add(Index.valueOf(y));
							break;
						}
					}
				}
			}
		}
		return ys;			
	}
	
	private boolean isCreatingVertex(GraphInterface<Index,BasicVertexInfo> subGraph, Index v){
//		Set<EdgeInterface<Index>> creatingEdges = m_bccTree.m_bccAlg.getCreatingCutoffVertices();
//		Iterator<EdgeInterface<Index>> itr = creatingEdges.iterator();
//		while(itr.hasNext()){
//			EdgeInterface<Index> e = itr.next(); 
		
		FastMap<AbstractSimpleEdge<Index,BasicVertexInfo>, BiConnectedComponent> compsPerCreatingEdges = m_bccAlg.getComponentsPerCreatingEdges();
		for(FastMap.Entry<AbstractSimpleEdge<Index,BasicVertexInfo>, BiConnectedComponent> entry = compsPerCreatingEdges.head(), end = compsPerCreatingEdges.tail();
			(entry = entry.getNext()) != end;){
		
			AbstractSimpleEdge<Index,BasicVertexInfo> e = entry.getKey();
		
			Index v0 = e.getV0();	Index v1 = e.getV1();
			if (subGraph.isEdge(e)&&(v0.equals(v)||v1.equals(v))){
				if (discoveryOrder[v.intValue()]<discoveryOrder[v0.intValue()]||
					discoveryOrder[v.intValue()]<discoveryOrder[v1.intValue()])
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Assume a component C and a global traffic matrix (GTM) we need to create a local traffic matrix (LTM). 
	 * Let v be the creating cutoff, u,w be known cutoffs, x,y be non-cutoffs. 
	 * Let S_v, S_u, and S_w be sets of vertexes separated by v,u, or w from the component C (inclusive).
	 * LTM[v,v]=GTM[v,v] ?
	 * LTM[u,u]=GTM[u,u] ?
	 * LTM[x,x]=GTM[x,x] .
	 * LTM[x,y]=GTM[x,y] .
	 * LTM[v,x]=sum_{z in S_v}GTM[z,x] .
	 * similarly for LTM[x,v], LTM[u,x]...
	 * LTM[v,u]=sum_{z in S_v, a in S_u} GTM[z,a]
	 * similarly for LTM[u,v], LTM[u,w]...
	 * 
	 * how to determine S_v and S_u?
	 * S_v = {w : discoverOrder[w]<=discoverOrder[v] OR discoverOrder[w]=NA}
	 * S_u = {w : discoverOrder[w]>=discoverOrder[u] AND w not in C}
	 * NOTE: S_u only valid just before C is closed! 
	 * at this point vertexes separated by u are already discovered 
	 * and all vertexes discovered after that belong to C (we did not leave C yet).
	 * 
	 * @param subGraph
	 * @return
	 * @throws Exception
	 */
	public AbsTrafficMatrix createTrafficMatrix(GraphInterface<Index,BasicVertexInfo> subGraph) throws Exception{
		if (m_componentsTree.getNumberOfVertices() == 0)
			throw new Exception("First call for runDFS for constructing the bi-connected components tree.");

		//double[][] cw = new double[subGraph.getNumberOfVertices()][subGraph.getNumberOfVertices()];
		AbsTrafficMatrix cw = new DenseTrafficMatrix(subGraph.getNumberOfVertices());
		int[] verticesMap = new int[m_numberOfVertices];
		for (int k = 0; k < subGraph.getNumberOfVertices(); k++)
			for (int k_ = 0; k_ < subGraph.getNumberOfVertices(); k_++)
				cw.setWeight(k, k_, 0.0);// Arrays.fill(cw[k], 0.0);
		
		
		/** Computes D values for the sub graph cutoff vertices. */
		int index = 0;
		for (Index v : subGraph.getVertices()){
			/** Mapping vertices for use in the cw matrix. */
			verticesMap[v.intValue()] = index++;
		}

		for (Index iIdx : subGraph.getVertices()){
			int i = iIdx.intValue();
			boolean iCutoff = m_componentsTree.isVertex(iIdx);
			
			for (Index jIdx : subGraph.getVertices()){
				int j = jIdx.intValue();
				boolean jCutoff = m_componentsTree.isVertex(jIdx);
				
				/** LTM[v,v]=GTM[v,v] ?
				 *  LTM[u,u]=GTM[u,u] ?
				 *  LTM[x,x]=GTM[x,x] .
				 *  LTM[x,y]=GTM[x,y] . */
				if (i==j || (!iCutoff && !jCutoff))
					//cw[verticesMap[i]][verticesMap[j]] += m_gcw[i][j];
					cw.setWeight(verticesMap[i], verticesMap[j], cw.getWeight(verticesMap[i], verticesMap[j]) + m_gtm.getWeight(i, j));
				else{
					
					ArrayList<Index> ys = getVertexesInRange(iIdx, subGraph);
					ArrayList<Index> xs = getVertexesInRange(jIdx, subGraph);
					if (!ys.contains(iIdx))
						ys.add(iIdx);
					if (!xs.contains(jIdx))
						xs.add(jIdx);
					
					Iterator<Index> xsItr = xs.iterator();
					while(xsItr.hasNext()){
						Index x=xsItr.next();
						
						Iterator<Index> ysItr = ys.iterator();
						while(ysItr.hasNext()){ 
							Index y=ysItr.next();
//							cw[verticesMap[i]][verticesMap[j]] += m_gcw[x.intValue()][y.intValue()];
							cw.setWeight(verticesMap[i], verticesMap[j], cw.getWeight(verticesMap[i], verticesMap[j]) + m_gtm.getWeight(x.intValue(), y.intValue())); //[x.intValue()][y.intValue()]);
						}
					}
				}
			}
		}
		return cw;
	}
	// ======================= Getters ======================================================================
	public Set<Index> getCutoffVertices(){
		return m_bccAlg.getCutoffVertices();
	}
	
	public GraphInterface<Object,Object> getBiConnectedComponentsTree(){
		return m_componentsTree;
	}
	
	public Iterator<BiConnectedComponent> getSubGraphs(){
		return m_bccAlg.getSubGraphs();
	}

	/**
	 * @precondition computeIntraCommunication
	 */
	@Override
	public Index[][] getComponents() {
		Index[][] components = m_components.toArray(new Index[m_components.size()][]);
		return components;
	}
	
	@Override
	public double[] getCommunications(){
		return m_communication;
	}
	// ======================================================================================================
	
	
	public class BCCTreeEdgeInfo extends EdgeInfo<Object,Object> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		int m_weight;
		
		public BCCTreeEdgeInfo(int weight){
			m_weight = weight;
		}
		
	}


	@Override
	public GraphInterface<Index, ? extends BasicVertexInfo> getGraph() {
		return m_bccAlg.getGraph();
	}
	
}
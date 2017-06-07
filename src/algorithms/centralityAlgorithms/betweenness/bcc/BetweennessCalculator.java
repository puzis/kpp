package algorithms.centralityAlgorithms.betweenness.bcc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javolution.util.FastList;
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
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

/** The BetweennessCalculator listens to the given BCCAlgorithm and whenever a component 
 *  or a cutoff vertex is closed, the BetweennessCalculator updates the component tree
 *  and the communication array. 
 */ 
public class BetweennessCalculator implements BCCAlgorithmListener, BCCalculatorInterface{

	protected BCCAlgorithm m_bccAlg;
	protected GraphInterface<Object,Object> m_componentsTree;
	protected ShortestPathAlg m_spAlg;
	protected int m_numberOfVertices;

	// Sum of multiplicities of the vertices in the original graph (the input graph to the BCCAlgorithm)
	protected int m_size; 
	protected FastList<Index[]> m_components;//@Rami
	private FastList<Object> _Q = null;
	
	private double[] m_communication;
	private AbsTrafficMatrix m_originalTM;
	
	public BetweennessCalculator(BCCAlgorithm bccAlg){
		this(bccAlg, ShortestPathAlgorithmInterface.DEFAULT, new DefaultTrafficMatrix(bccAlg.getGraph().getNumberOfVertices()));
	}
	
	public BetweennessCalculator(BCCAlgorithm bccAlg, AbsTrafficMatrix originalTM){
		this(bccAlg, ShortestPathAlgorithmInterface.DEFAULT, originalTM);
	}
	
	public BetweennessCalculator(BCCAlgorithm bccAlg, ShortestPathAlg spAlg){
		this(bccAlg, spAlg, new DefaultTrafficMatrix(bccAlg.getGraph().getNumberOfVertices()));
	}

	/**
	 * @param bccAlg - the bi-connected-components (BCC) extractor algorithm that the BetweennessCalculator listens to.  
	 * @param spAlg - shortest path algorithm, this parameter is required for the computation of intra-communication.
	 * @param originalTM - the traffic matrix of the original graph (over which the BCC algorithm is applied).
	 */
	public BetweennessCalculator(BCCAlgorithm bccAlg, ShortestPathAlg spAlg, AbsTrafficMatrix originalTM){
		m_bccAlg = bccAlg;
		m_componentsTree = new GraphAsHashMap<Object,Object>();
		m_bccAlg.addListener(this);
		m_spAlg = spAlg;
		m_numberOfVertices = m_bccAlg.getGraph().getNumberOfVertices();
		m_size = m_bccAlg.getGraph().getSize();
		m_components = new FastList<Index[]>();
		_Q = new FastList<Object>();
		m_originalTM = originalTM;
		m_communication = new double[m_numberOfVertices];
		Arrays.fill(m_communication, 0);
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

	@Override
	public void addCutoffVertex(Index cutoffVertex, AbstractSimpleEdge<Index,BasicVertexInfo> creatingEdge) {
		BasicVertexInfo vInfo;
		if (!m_componentsTree.isVertex(cutoffVertex)){
			vInfo = new BiConnectedVertexInfo();
			if (VertexFactory.isVertexInfo(vInfo)){
				((VertexInfo)vInfo).setLabel(TopologyConstants.VERTEX_MULTIPLICITY, Integer.toString(
						((VertexInfo)m_bccAlg.getGraph().getVertex(cutoffVertex)).getMultiplicity()));
			}
			else 
				throw new IllegalArgumentException("this method must use VertexInfo type");
			m_componentsTree.addVertex(cutoffVertex, vInfo);
		}
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
		
		storeComponent(component);
		
		Index unmarkedCutoffVertex = null;
			
		/* Calculate sizes of the two parts. */
		/* number of vertices disconnected by the unmarked cutoff from the component*/
		int partSize = component.getSize();
		
		for(AbstractSimpleEdge<Object,Object> e: m_componentsTree.getOutgoingEdges(component)){
			Index cutoffVertex = (Index)e.getNeighbor(component);
			int weight = ((BCCTreeEdgeInfo)m_componentsTree.getEdgeWeight(e)).getWeight();
			if (weight != -1){
				if (VertexFactory.isVertexInfo( m_componentsTree.getVertex(cutoffVertex)))
					partSize += m_size - weight - ((VertexInfo)m_componentsTree.getVertex(cutoffVertex)).getMultiplicity();
				else
					partSize += m_size - weight - 1;
			}
			else{
				//invariant: there is at most one unmarked edge for every component in the Q.
				unmarkedCutoffVertex = cutoffVertex;
				if (VertexFactory.isVertexInfo(m_componentsTree.getVertex(cutoffVertex)))
					partSize -= ((VertexInfo)m_componentsTree.getVertex(cutoffVertex)).getMultiplicity();
				else
					partSize -= 1; 

			}
		}
		if (unmarkedCutoffVertex != null){
			markNeighbor(_Q, component, unmarkedCutoffVertex, partSize);
		}
		computeIntraCommunication(component);
	}

	@Override
	public void closeCutoffVertex(Index cutoffVertex){
		BiConnectedVertexInfo cutoffVertexInfo = (BiConnectedVertexInfo) m_componentsTree.getVertex(cutoffVertex);
		if (cutoffVertexInfo.getUnmarkedNeighborsCounter()==1){
			GraphInterface<Index,BasicVertexInfo> unmarkedNeighbor = null;
			
			/** Calculate sizes of the two parts. The cutoff vertex is not counted. */
			/** number of vertices disconnected from the unmarked component by the cutoff*/
			int partSize = 0;
			
			for(AbstractSimpleEdge<Object,Object> e: m_componentsTree.getOutgoingEdges(cutoffVertex)){
				GraphInterface<Index,BasicVertexInfo> neighbor = (GraphInterface<Index,BasicVertexInfo>)e.getNeighbor(cutoffVertex);
				int weight = ((BCCTreeEdgeInfo) m_componentsTree.getEdgeWeight(e)).getWeight();
				if (weight != -1)
					partSize += weight;
				else
					//invariant: there is at most one unmarked edge for every cutoff vertex in the Q.
					unmarkedNeighbor = neighbor;
			}
				
			if (unmarkedNeighbor != null){
				int weight; 
				if (VertexFactory.isVertexInfo(m_componentsTree.getVertex(cutoffVertex))){
					weight = m_size - partSize - ((VertexInfo)m_componentsTree.getVertex(cutoffVertex)).getMultiplicity();
				}
				else{
					weight = m_size - partSize - 1;
				}
				markNeighbor(_Q, cutoffVertex, unmarkedNeighbor, weight);
			}
		}
		computeInterCommunication(cutoffVertex);
	}

	//===============================================================================================

	private void computeIntraCommunication(GraphInterface<Index,BasicVertexInfo> component){
    	/* if the component is a clique that also does not include vertices with multiplicity<>1 then */
    	if (GraphUtils.isClique(component)){
            for(Index vIdx : component.getVertices()){
        		int v = vIdx.intValue();
        		if (m_componentsTree.isVertex(vIdx)){ /* if v is cutoff */
        			/*  2*w(v,C)*(|V|-w(v,C)-1) communication through v,
        			 * + 2(|V|-1)                communications of v. */
        			int wvC = ((BCCTreeEdgeInfo)m_componentsTree.getEdgeWeight(m_componentsTree.getEdge(vIdx, component))).getWeight();
        			
        			for(AbstractSimpleEdge<Object,Object> e: m_componentsTree.getOutgoingEdges(component)){
        				Index uIdx = (Index)e.getNeighbor(component);
        				if (!uIdx.equals(vIdx)){

        					int wuC = ((BCCTreeEdgeInfo)m_componentsTree.getEdgeWeight(m_componentsTree.getEdge(uIdx, component))).getWeight();
        					m_communication[v]+=2*(m_size-wvC)*(m_size-wuC);
        				}
        			}
        			int nonCutoffs = component.getSize() - m_componentsTree.getDegree(component);
        			m_communication[v] += 2*(m_size-wvC)*nonCutoffs;
         		}
        		else{
        			m_communication[v] += 2*(m_size - 1);
        		}
        		m_communication[v] += m_originalTM.getWeight(v, v);
        	}
    	}
    	else
    	{
    		AbsTrafficMatrix cw = null;
    		try{
    			// Compute component's traffic matrix.
    			cw = createTrafficMatrix(component, m_originalTM);
    		}
    		catch(Exception ex){
    			ex.printStackTrace();
    			System.out.println(ex);
    		}
        	GraphInterface<Index,BasicVertexInfo> mappedGraph = GraphUtils.mapGraph(component, m_numberOfVertices - 1);
    		// Compute the Betweenness centrality of the vertices in the component.
        	TrafficMatrixBC tmBC = new TrafficMatrixBC(m_spAlg, mappedGraph, cw, new DummyProgress(), 1);
        	tmBC.run();
        	int index = 0;
            for(Index vIdx : component.getVertices()){
        		int v = vIdx.intValue();
        		m_communication[v] += tmBC.getCentrality(index);
        		index++;
        	}
    	}
	}
	
	/**
	 * Let C be bi-connected component.
	 * A cutoff vertex is a vertex that belongs to at least two connected components. 
	 * Another definition is a vertex which is associated with an edge in a components tree.
	 * 
	 * @2 intra-component communications
	 * 
	 * If C is a degenerated bcc then its contribution to @2 is 2*w(C,v).
	 * For example, if all bccs of v are degenerated @2=\sum_{C|v\in C} 2*w(C,v)
	 * 
	 * If C is a complex bcc then @2 should be computed by calculating BC inside the component where traffic matrix values are as following (v!=u are cutoffs, x!=y are not):
	 * T[x,x]=0
     * T[x,y]=1
	 * T[v,x]= |V| - w(C,v) 
	 * T[v,v]=0
	 * T[v,u]=(|V| - w(C,v))(|V| - w(C,u))
	 */
	public AbsTrafficMatrix createTrafficMatrix(GraphInterface<Index,BasicVertexInfo> subGraph) throws Exception{
		return createTrafficMatrix(subGraph, new DefaultTrafficMatrix(subGraph.getNumberOfVertices()));
	}
	
	public AbsTrafficMatrix createTrafficMatrix(GraphInterface<Index,BasicVertexInfo> subGraph, AbsTrafficMatrix originalTM) throws Exception{
		GraphInterface<Object,Object> componentsTree = m_componentsTree;
		// Graph size is the sum of multiplicities of all vertices in the graph.
		int graphSize = m_size; 
		
		if (componentsTree.getNumberOfVertices() == 0)
			throw new Exception("First call for runDFS for constructing the bi-connected components tree.");

		AbsTrafficMatrix cw = new DenseTrafficMatrix(subGraph.getNumberOfVertices());
		cw.setAllWeights(0.0);
		
		/** Computes D values for the sub graph cutoff vertices. */
		int[] verticesMap = new int[m_numberOfVertices];
		int index = 0;
        for(Index v : subGraph.getVertices()){
			/** Mapping vertices for use in the cw matrix. */
			verticesMap[v.intValue()] = index++;
		}
		
		// TODO: n is the size of the connected component that the subgraph is a part of.
		// in order to compute the cut with and cut without weights correctly in disconnected
        // networks n must be changed from the total multiplicity of all vertices in the graph
        // to total multiplicity of all vertices in the connected component that includes 
        // subGraph
        
		int n = graphSize;
		
        for(Index iIdx : subGraph.getVertices()){
			int i = iIdx.intValue();
			boolean iCutoff = componentsTree.isVertex(iIdx);
			
	        for(Index jIdx : subGraph.getVertices()){
				int j = jIdx.intValue();
				boolean jCutoff = componentsTree.isVertex(jIdx);
				
				/** i and j are non-cutoff */
				if (!iCutoff && !jCutoff){
					cw.setWeight(verticesMap[i], verticesMap[j], originalTM.getWeight(i, j));
				}
				/** i and j are cutoff */
				else if(iCutoff && jCutoff){
					if (i == j){
						cw.setWeight(verticesMap[i], verticesMap[i], originalTM.getWeight(i, j));
					}
					else{
						/** (|V| - w(C,v))(|V| - w(C,u)) */
						int wCi = ((BCCTreeEdgeInfo)componentsTree.getEdgeWeight(componentsTree.getEdge(iIdx, subGraph))).getWeight();
						int wCj = ((BCCTreeEdgeInfo)componentsTree.getEdgeWeight(componentsTree.getEdge(jIdx, subGraph))).getWeight();
						cw.setWeight(verticesMap[i], verticesMap[j], (n-wCi)*(n-wCj)); //[verticesMap[i]][verticesMap[j]] = (n-wCi)*(n-wCj);
					}
				}
				/** i is cutoff and j is non-cutoff */
				else if (iCutoff && !jCutoff){
					int wCi = ((BCCTreeEdgeInfo)componentsTree.getEdgeWeight(componentsTree.getEdge(iIdx, subGraph))).getWeight();
					if (VertexFactory.isVertexInfo(subGraph.getVertex(jIdx)))
						cw.setWeight(verticesMap[i], verticesMap[j], (n - wCi)*((VertexInfo)subGraph.getVertex(jIdx)).getMultiplicity()); //[verticesMap[i]][verticesMap[j]] = (n - wCi);
					else 
						cw.setWeight(verticesMap[i], verticesMap[j], (n - wCi)); //[verticesMap[i]][verticesMap[j]] = (n - wCi);

				}
				else if (!iCutoff && jCutoff){
					int wCj = ((BCCTreeEdgeInfo)componentsTree.getEdgeWeight(componentsTree.getEdge(jIdx, subGraph))).getWeight();
					if (VertexFactory.isVertexInfo(subGraph.getVertex(iIdx)))
						cw.setWeight(verticesMap[i], verticesMap[j], (n - wCj)*((VertexInfo)subGraph.getVertex(iIdx)).getMultiplicity()); //[verticesMap[i]][verticesMap[j]] = (n - wCj);
				}
			}
		}
		return cw;
	}
	
	/** Unmarked neighbor/edge is the last neighboring edge, with no weight, of a component or a cutoff. 
	 *  Marking a neighbor/edge means putting a weight on the edge and reducing the number of unmarked neighbors in the BiPartiteVertexInfo. 
	 * 
	 * @param _Q
	 * @param v
	 * @param unmarkedNeighbor
	 * @param weight
	 */
	protected void markNeighbor(FastList<Object> _Q, Index v, GraphInterface<Index,BasicVertexInfo> unmarkedNeighbor,
			int weight){
		m_componentsTree.setEdgeWeight(v, unmarkedNeighbor, new BCCTreeEdgeInfo(weight));
		markNeighbor_cont(_Q,v,unmarkedNeighbor);
	}
	protected void markNeighbor(FastList<Object> _Q, GraphInterface<Index,BasicVertexInfo> v, Index  unmarkedNeighbor,
			int weight){
		m_componentsTree.setEdgeWeight(unmarkedNeighbor, v, new BCCTreeEdgeInfo(weight));
		markNeighbor_cont(_Q,v,unmarkedNeighbor);		
	}		
	protected void markNeighbor_cont(FastList<Object> _Q, Object v, Object unmarkedNeighbor){
		BiConnectedVertexInfo vInfo = (BiConnectedVertexInfo) m_componentsTree.getVertex(v);
		vInfo.decreaseUnmarkedNeighborsCounter();
		BiConnectedVertexInfo neighborInfo = (BiConnectedVertexInfo) m_componentsTree.getVertex(unmarkedNeighbor);
		neighborInfo.decreaseUnmarkedNeighborsCounter();
		
		if (neighborInfo.getUnmarkedNeighborsCounter() == 1)
			_Q.addLast(unmarkedNeighbor);
	}
	
	/** @Rami store component **/
	protected void storeComponent(GraphInterface<Index,BasicVertexInfo> subGraph)
	{
    	FastList<Index> component = new FastList<Index>();
        for(Index w : subGraph.getVertices())
    		component.add(w);
    	Index[] compArr = component.toArray(new Index[component.size()]); 
    	m_components.add(compArr);    	
	}
	
	private void computeInterCommunication(Index cutoffVertex){
		int interBC = 0;
		/** If v is a cutoff vertex. */
		for(AbstractSimpleEdge<Object,Object> e: m_componentsTree.getOutgoingEdges(cutoffVertex)){
			int weight_e = ((BCCTreeEdgeInfo)m_componentsTree.getEdgeWeight(e)).getWeight(); 
			if(VertexFactory.isVertexInfo(m_componentsTree.getVertex(cutoffVertex)))	
				interBC += weight_e * (m_size - weight_e -((VertexInfo)m_componentsTree.getVertex(cutoffVertex)).getMultiplicity());
			else 
				interBC += weight_e * (m_size - weight_e -1);

		}
		// Increase interBC by original_cw[i,i]*(num_of_neighbors-1) ==> 
		// the communication of the cutoff with itself which should be counted only once.
		interBC += m_originalTM.getWeight(cutoffVertex.intValue(), cutoffVertex.intValue()) * 
														(m_componentsTree.getDegree(cutoffVertex)-1);
		m_communication[cutoffVertex.intValue()]-=interBC;
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
		
		private int getWeight(){
			return m_weight;
		}
	}


	@Override
	public GraphInterface<Index, ? extends BasicVertexInfo> getGraph() {
		return m_bccAlg.getGraph();
	}
}
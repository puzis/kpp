package algorithms.bcc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;

/** Extracts bi-connected components during a DFS run 
 * and adds them to the growing bi-connected components' tree.
 * Thus, the tree is also constructed eagerly (and not after the DFS has finished). */
public class BCCAlgorithm{

	protected GraphInterface<Index,BasicVertexInfo> m_graph;
	protected Set<Index> m_cutoffVertices;
	protected FastMap<AbstractSimpleEdge<Index,BasicVertexInfo>, BiConnectedComponent> m_components;
	protected FastList<BCCAlgorithmListener> m_listeners;
	protected FastMap<Index, FastList<BiConnectedComponent>> m_componentsPerVertices = null;
	
	protected enum Label {TREE, FORWARD, BACK, CROSS, ERROR};
	protected FastMap<AbstractSimpleEdge<Index,BasicVertexInfo>, Index> markedEdges = new FastMap<AbstractSimpleEdge<Index,BasicVertexInfo>, Index>();
	
	/** Cell index is the vertex, and cell contents is the discovery order of the vertex. */
	protected int[] discoverOrder = null;
	protected FastList<Index> _S = new FastList<Index>();
	protected FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> _O = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
	protected FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> _C = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
	
	/** Used for identification of a cutoff vertex. 
	 *  Indicates how many vertices have been added to S and removed from S. */
	private int m_removedFromS;
	
	/** All the cutoff in the current closed component,
	 *  that are not equal to the cutoff that the component is associated to. */
	private Set<Index> m_currentCutoffs;
	private int m_maxDiscoveryOrder;
	private int m_minDiscoveryOrder;
	
	
	/**
	 * NOTE: was not tested with directed graphs !
	 * @param graph - 
	 */
	public BCCAlgorithm(GraphInterface<Index,BasicVertexInfo> graph) {
		m_graph = graph;
		m_cutoffVertices = new HashSet<Index>();
		m_components = new FastMap<AbstractSimpleEdge<Index,BasicVertexInfo>, BiConnectedComponent>();
		m_componentsPerVertices = new FastMap<Index, FastList<BiConnectedComponent>>();
		m_listeners = new FastList<BCCAlgorithmListener>();
		
		discoverOrder = new int[graph.getNumberOfVertices()];
		Arrays.fill(discoverOrder, Integer.MAX_VALUE);
		
		m_removedFromS = 0;
		m_currentCutoffs = new HashSet<Index>();
		m_maxDiscoveryOrder = 0;
		m_minDiscoveryOrder = Integer.MAX_VALUE;
	}
	
	public void run() {
		AbstractSimpleEdge<Index,BasicVertexInfo>[] incoming = new AbstractSimpleEdge[m_graph.getNumberOfVertices()]; 
		int discoverIndex = 1;
		
        for(Index s : m_graph.getVertices()){
			if (discoverOrder[s.intValue()]==Integer.MAX_VALUE){
				discoverOrder[s.intValue()] = discoverIndex++;
				_S.addFirst(s);
				incoming[s.intValue()] = null;
				root(s);
				
				while (_S.size()>0){
					Index v = _S.getFirst();
					AbstractSimpleEdge<Index,BasicVertexInfo> unmarkedEdge = getUnmarkedEdge(v);
					if (unmarkedEdge != null){
						Index w = unmarkedEdge.getNeighbor(v);
						traverse(v, unmarkedEdge, w);
						markedEdges.put(unmarkedEdge, Index.valueOf(discoverOrder[v.intValue()]));
						if (discoverOrder[w.intValue()]==Integer.MAX_VALUE){
							discoverOrder[w.intValue()] = discoverIndex++;
							_S.addFirst(w);
							incoming[w.intValue()] = unmarkedEdge; 
						}
					}
					else{
						Index w = _S.removeFirst();
						Index u = null;
						if (_S.size() != 0)	
							u = _S.getFirst();
						backtrack(w, incoming[w.intValue()], u);
					}
				}
			}
		}
	}
	
	private AbstractSimpleEdge<Index,BasicVertexInfo> getUnmarkedEdge(Index v){
		AbstractSimpleEdge<Index,BasicVertexInfo> unmarkedEdge = null;
		for (AbstractSimpleEdge<Index,BasicVertexInfo> e: m_graph.getOutgoingEdges(v))
			if(unmarkedEdge == null){
			if (!markedEdges.containsKey(e))
				unmarkedEdge = e;
		}
		return unmarkedEdge;
	}
	
	protected void root(Index s){}
	
	private void traverse(Index v, AbstractSimpleEdge<Index,BasicVertexInfo> e, Index w){
		if (v.equals(w)){
			m_components.put(e, new BiConnectedComponent(m_graph));
			m_components.get(e).addEdge(e);
			
			ascribeVertexToComponent(e, m_components.get(e));
			addComponentToTree(e, m_components.get(e));
		}
		else{
			_O.addFirst(e);
			if (getEdgeClassification(w, e, v).equals(Label.TREE))	
				_C.addFirst(e);
			
			if (getEdgeClassification(w, e, v).equals(Label.BACK)){
				while (discoverOrder[w.intValue()]< markedEdges.get(_C.getFirst()).intValue()){
					_C.removeFirst();
				}
			}
		}
	}
	
	/**
	 * tree edge - w not marked
	 * forward edge - w marked, v < w
     * back edge - w marked, w <= v, w in S
	 * cross edge - w marked, w < v, w not in S
	 * @param w
	 * @param e
	 * @param v
	 * @param marked
	 * @return
	 */
	private Label getEdgeClassification(Index w, AbstractSimpleEdge<Index,BasicVertexInfo> e, Index v){
		if (discoverOrder[w.intValue()]==Integer.MAX_VALUE)	return Label.TREE;
		if (discoverOrder[v.intValue()]<discoverOrder[w.intValue()])	return Label.FORWARD;
		if (discoverOrder[v.intValue()]>=discoverOrder[w.intValue()] && _S.contains(w))	return Label.BACK;
		if (discoverOrder[v.intValue()]>discoverOrder[w.intValue()] && !_S.contains(w))	return Label.CROSS;
		return Label.ERROR;
	}
	
	protected void ascribeVertexToComponent(AbstractSimpleEdge<Index,BasicVertexInfo> e, BiConnectedComponent component){
		Index v = e.getV0();
		Index w = e.getV1();
		
		if (m_componentsPerVertices.get(v)==null)
			m_componentsPerVertices.put(v, new FastList<BiConnectedComponent>());
		
		if (!m_componentsPerVertices.get(v).contains(component))
			m_componentsPerVertices.get(v).add(component);
		
		if (m_componentsPerVertices.get(w)==null)
			m_componentsPerVertices.put(w, new FastList<BiConnectedComponent>());
		
		if (!m_componentsPerVertices.get(w).contains(component))
			m_componentsPerVertices.get(w).add(component);
		
		Index v0 = e.getV0();
		Index v1 = e.getV1();
		
		if (m_cutoffVertices.contains(v0))
			m_currentCutoffs.add(v0);
		if (m_cutoffVertices.contains(v1))
			m_currentCutoffs.add(v1);
		
		int v0DiscoveryOrder = discoverOrder[v0.intValue()];
		int v1DiscoveryOrder = discoverOrder[v1.intValue()];
		if (v0DiscoveryOrder < v1DiscoveryOrder){
			if (v1DiscoveryOrder > m_maxDiscoveryOrder)
				m_maxDiscoveryOrder = v1DiscoveryOrder;
		}
		else{
			if (v0DiscoveryOrder > m_maxDiscoveryOrder)
				m_maxDiscoveryOrder = v0DiscoveryOrder;
		}
	}
	
	protected void addComponentToTree(AbstractSimpleEdge<Index,BasicVertexInfo> e, BiConnectedComponent component){
		GraphInterface<Index,BasicVertexInfo> subgraph = component.getComponent(); 
		addComponent(subgraph);
		closeComponent(subgraph, e);
	}
	
	protected void backtrack(Index w, AbstractSimpleEdge<Index,BasicVertexInfo> e, Index v){
		m_removedFromS++;
		m_currentCutoffs = new HashSet<Index>();		
		
		if (e!=null && e.equals(_C.getFirst())){
			_C.removeFirst();
			m_components.put(e, new BiConnectedComponent(m_graph));
			AbstractSimpleEdge<Index,BasicVertexInfo> ePrime = null;
			do{
				ePrime = _O.removeFirst();
				m_components.get(e).addEdge(ePrime);
				ascribeVertexToComponent(ePrime, m_components.get(e));
			}while(!ePrime.equals(e));
			addComponentToTree(e, m_components.get(e));
		}
	}
	
	private void addCutoffVertex(Index v, AbstractSimpleEdge<Index,BasicVertexInfo> generatingEdge){
		m_cutoffVertices.add(v);
		for (BCCAlgorithmListener listener: m_listeners)
			listener.addCutoffVertex(v, generatingEdge);
	}
	
	private void addComponent(GraphInterface<Index,BasicVertexInfo> component){
		for (BCCAlgorithmListener listener: m_listeners)
			listener.addComponent(component);
	}
	
	private void closeComponent(GraphInterface<Index,BasicVertexInfo> component, AbstractSimpleEdge<Index,BasicVertexInfo> e){
		/** Take care of generating cutoff */
		Index v = e.getV0();
		Index w = e.getV1();
		Index cutoff = ((discoverOrder[v.intValue()]<discoverOrder[w.intValue()])? v : w);
		int cutoffNeighbor = ((discoverOrder[v.intValue()]<discoverOrder[w.intValue()])? w.intValue() : v.intValue());
		m_minDiscoveryOrder = discoverOrder[cutoffNeighbor];

		if (m_removedFromS < m_graph.getNumberOfVertices()-1){

			m_currentCutoffs.remove(cutoff);
			
			/** This cutoff is the generator of the current component. */
			addCutoffVertex(cutoff, e);
			addEdge(cutoff, component);
		}

		/** Take care of non-generating cutoffs */
		Iterator<Index> cutoffItr = m_currentCutoffs.iterator();
		while(cutoffItr.hasNext()){
			Index cutoffVertex = cutoffItr.next(); 
			/** This cutoff is now being closed, and it is not the generator of the current component.
			 *  There is at most one component which a cutoff DOES NOT generate. */
			addEdge(cutoffVertex, component);
			
			for (BCCAlgorithmListener listener: m_listeners)
				listener.closeCutoffVertex(cutoffVertex);
		}
		
		for (BCCAlgorithmListener listener: m_listeners)
			listener.closeComponent(component, m_minDiscoveryOrder, m_maxDiscoveryOrder);
		
		m_minDiscoveryOrder = Integer.MAX_VALUE;
	}
	
	public int[] getDiscoverOrder(){
		return discoverOrder;
	}
	
	private void addEdge(Index v0, GraphInterface<Index,BasicVertexInfo> v1){
		for (BCCAlgorithmListener listener: m_listeners)
			listener.addEdge(v0, v1);
	}
	
	// Listeners (tree builders) management 
	public void addListener(BCCAlgorithmListener listener){
		m_listeners.add(listener);
	}
	public void removeListener(BCCAlgorithmListener listener){
		m_listeners.remove(listener);
	}
	
	// Getters
	public GraphInterface<Index,BasicVertexInfo> getGraph(){
		return m_graph;
	}
	
	public Set<Index> getCutoffVertices(){
		return m_cutoffVertices;
	}
	
	public FastMap<Index, FastList<BiConnectedComponent>> getComponentsPerVertices() {
		return m_componentsPerVertices;
	}

	public FastMap<AbstractSimpleEdge<Index,BasicVertexInfo>, BiConnectedComponent> getComponentsPerCreatingEdges(){
		return m_components;
	}

	public Iterator<BiConnectedComponent> getSubGraphs(){
		return m_components.values().iterator();
	}
}
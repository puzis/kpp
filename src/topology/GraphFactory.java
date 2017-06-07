package topology;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Random;

import javolution.util.Index;


public class GraphFactory<VertexType,VertexInfoStructure> implements Serializable{
	
	public static enum GraphDataStructure{UNSPECIFIED, DI_GRAPH_AS_HASH_MAP,GRAPH_AS_HASH_MAP,/*OPTIMIZED_GRAPH_AS_ARRAY,*/ HYPER_GRAPH_AS_HASH_MAP, WEIGHTED_HYPER_GRAPH}
	public static enum EdgeDataStructure{UNSPECIFIED, HYPER_EDGE, DI_EDGE, DI_HASH_EDGE,UNDI_EDGE, UNDI_HASH_EDGE, WEIGHTED_HYPER_EDGE}
	public static enum VertexInfoType {VERTEX_INFO,VERTEX}

	
	
	public final static GraphDataStructure DEFAULT_GRAPH_TYPE = GraphDataStructure.GRAPH_AS_HASH_MAP;
	public final static EdgeDataStructure DEFAULT_EDGE_TYPE = EdgeDataStructure.UNDI_HASH_EDGE;
	public final static VertexInfoType DEFAULT_VERTEX_INFO_TYPE = GraphFactory.VertexInfoType.VERTEX_INFO;

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1650720709017763174L;

//	public static <GraphType extends HyperGraphInterface<Index>> GraphType createGraphDataStructure(Class<?> graphTypeClass){
//		try {
//			Field graphTypeField = graphTypeClass.getField("GRAPH_TYPE");
//			GraphDataStructure gds = (GraphDataStructure) graphTypeField.get(null); 
//			return (GraphType)createGraph(gds); 
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchFieldException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public static GraphInterface<Index,BasicVertexInfo> createGraphSimple(GraphFactory.GraphDataStructure gds){
		return new GraphFactory<Index,BasicVertexInfo>(gds).createGraphSimple();		
	}
	
	public static boolean isSimple(GraphFactory.GraphDataStructure gds){
		return new GraphFactory<Index,BasicVertexInfo>(gds).isSimple();
	}
	
	public static boolean isHyper(GraphFactory.GraphDataStructure gds){
		return new GraphFactory<Index,BasicVertexInfo>(gds).isHyper();
	}
	public static boolean isDirected(GraphFactory.GraphDataStructure gds){
		return new GraphFactory<Index,BasicVertexInfo>(gds).isDirected();
	}
	public static boolean isUndirected(GraphFactory.GraphDataStructure gds){
		return new GraphFactory<Index,BasicVertexInfo>(gds).isUndirected();
	}
	
	
	public static Class<?> getGraphType(GraphFactory.GraphDataStructure graphDataStructure)
	{	
		if (graphDataStructure==null)
			throw new IllegalArgumentException("graph data structure can't be null");
		
		switch (graphDataStructure)
		{
		case DI_GRAPH_AS_HASH_MAP:
			return DiGraphAsHashMap.class;
		case GRAPH_AS_HASH_MAP:
			return GraphAsHashMap.class;
		case HYPER_GRAPH_AS_HASH_MAP:
			return UndirectedHyperGraphAsHashMap.class;
		default:
			throw new IllegalArgumentException("graph data structure is not recognized");
		}
	}
	
	
	public static HyperGraphInterface<Index,BasicVertexInfo> createGraph(GraphFactory.GraphDataStructure gds)
	{	
		return new GraphFactory<Index,BasicVertexInfo>(gds).createGraph(); 
	}

	
	public static boolean isSimple(EdgeDataStructure eds){
		return  
				eds == EdgeDataStructure.DI_EDGE ||
				eds == EdgeDataStructure.DI_HASH_EDGE || 
				eds == EdgeDataStructure.UNDI_EDGE ||
				eds == EdgeDataStructure.UNDI_HASH_EDGE; 
		
	}
	
	
	
	/**
	 * This method will attempt copying the given graph data into 
	 * a data structure specified by the gds parameter.
	 * 
	 * converting undirected graph into directed graph will need special treatment: 
	 * 		for each undirected edge at the oldGraph (from U to v),
	 * 		will be added second edge at the opposite direction (from V to U)  
	 * 		for example, the undirected edge:
	 * 		[u,v]  -> will becomes into two edges [u,v][v,u]
	 * 
	 * conversion from simple (directed / undirected) graphs to Hyper Graphs is
	 * done by treating every edge as a set of size 2. Note that duplicate edge 
	 * may exist if a Directed Graph is converted to HYPER_GRAPH_AS_HASH_MAP.
	 * 
	 * during conversion from hyper graph to simple graph every hyper edge
	 * is replaced by a clique. 
	 * @param from TODO
	 * @param gds specifies the target graph data structure. 
	 * 
	 * @return a graph implementation specified by gds.
	 */
	public static HyperGraphInterface<Index,BasicVertexInfo> copyAs(GraphDataInterface<Index, BasicVertexInfo> from, GraphFactory.GraphDataStructure gds){
		HyperGraphInterface<Index,BasicVertexInfo> graph = new GraphFactory<Index,BasicVertexInfo>(gds).createGraph();
	    GraphFactory.fillInGraphData(from, graph);
	    return graph;    	
	}	


	/**
	 * Same as copyAs(graph, gds) but the target graph data structure is automatically chosen as 
	 * the simple graph data structure closest to the given graph.
	 * E.g. HYPER_GRAPH_AS_HASH_MAP will be converted to GRAPH_AS_HASH_MAP
	 * If given graph is simple graph the returned object will be of the same type.
	 *  
	 * @param from
	 * @return
	 */
	public static GraphInterface<Index,BasicVertexInfo> copyAsSimple(GraphDataInterface<Index, BasicVertexInfo> from)
	{
		GraphInterface<Index,BasicVertexInfo> graph = GraphFactory.createGraphSimple(from.getType());
	    GraphFactory.fillInGraphData(from, graph);
	    return graph;
	}

	static void fillInGraphData(GraphDataInterface<Index,BasicVertexInfo> from, GraphDataInterface<Index,BasicVertexInfo> graph) {
		
		
		GraphFactory<Index,BasicVertexInfo> t_gfac = new GraphFactory<Index,BasicVertexInfo>(graph.getType());
		
	    for (Index v : from.getVertices())
	    {
	        graph.addVertex(v, from.getVertex(v));
	    }
	
	    EdgeInfo<Index,BasicVertexInfo> weight = null;
	    int i =0;
	    int n = from.getNumberOfEdges();
	    for (AbstractHyperEdge<Index,BasicVertexInfo> edge : from.getEdges())
	    {
	    	i++;
	        
	        weight = from.getEdgeWeight(edge);
	        
	        if (t_gfac.isUndirected() & t_gfac.isSimple())
	        {
	        	for (Index u: edge.getVertices()){
	        		double mem = (Runtime.getRuntime().totalMemory() -Runtime.getRuntime().freeMemory())/new Double(Math.pow(2, 20)).longValue();
	        		
	        		
	        		//System.out.println("fillInGraphData: edge#"+i+" out of "+n+": : cardinality " + edge.getNumberOfVertices() + ": :simple edges so far "+graph.getNumberOfEdges()+": : memory "+mem);
	        		
	        		
	        		
	        		for (Index v: edge.getVertices())
	        			//no self loops in current implementation
	        			if (u.intValue() < v.intValue()) {
	        				AbstractSimpleEdge<Index,BasicVertexInfo> new_edge = t_gfac.createSimpleEdge(u, v);
	        				if(((GraphInterface<Index,BasicVertexInfo>)graph).isEdge(new_edge)) {
	        					double m = ((GraphInterface<Index,BasicVertexInfo>)graph).getEdgeWeight(new_edge).getMultiplicity();
	        					((GraphInterface<Index,BasicVertexInfo>)graph).getEdgeWeight(new_edge).setMultiplicity(m+weight.getMultiplicity());
	        				}
	        				/**
	        				 * TODO: Optional properties are ignored and must be corrected soon
	        				 */
	        				else {
	        					((GraphInterface<Index,BasicVertexInfo>)graph).addEdge(new_edge,new EdgeInfo<Index,BasicVertexInfo>(weight.getLatency(), weight.getMultiplicity()));
	        				}
	        			}            	
	        	}
	        }
	        else if (t_gfac.isDirected() & t_gfac.isSimple())
	        {
	        	for (Index u: edge.getSources())
	        		for (Index v: edge.getTargets())
	        			//no self loops in current implementation
	        			if (u.intValue() != v.intValue()) {
	        				AbstractSimpleEdge<Index,BasicVertexInfo> new_edge = t_gfac.createSimpleEdge(u, v);
	        				if(((GraphInterface<Index,BasicVertexInfo>)graph).isEdge(new_edge)) {
	        					double m = ((GraphInterface<Index,BasicVertexInfo>)graph).getEdgeWeight(new_edge).getMultiplicity();
	        					((GraphInterface<Index,BasicVertexInfo>)graph).getEdgeWeight(new_edge).setMultiplicity(m+weight.getMultiplicity());
	        				}
	        				/**
	        				 * TODO: Optional properties are ignored and must be corrected soon
	        				 */
	        				else {
	        					((GraphInterface<Index,BasicVertexInfo>)graph).addEdge(new_edge,new EdgeInfo<Index,BasicVertexInfo>(weight.getLatency(), weight.getMultiplicity()));
	        				}
	        			}            	
	        }
	        else if (t_gfac.isUndirected() & t_gfac.isHyper())
	        {            	
	        	graph.addEdge(edge.getVertices(), weight);
	        }
	        else 
	        {
	            //TODO: add conversion directed hyper graph   
	        	throw new IllegalArgumentException("Conversion to directed hypergraphs is not supported.");
	        }
	    }        
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	// Factory instance 
	
	
	GraphDataStructure m_gds;
	EdgeDataStructure m_eds;

	
	public GraphFactory(GraphDataStructure gds, EdgeDataStructure eds){
		if (gds==null | eds==null)
			throw new IllegalArgumentException("graph or edge data structure specifiers can't be null");
		m_gds = gds;
		m_eds = eds;
	}
		
	public GraphFactory(GraphDataStructure gds){
		if (gds==null)
			throw new IllegalArgumentException("graph data structure specifier can't be null");
		
		m_gds=gds;
		
		switch (m_gds)
		{
		case DI_GRAPH_AS_HASH_MAP:
			m_eds = EdgeDataStructure.DI_HASH_EDGE;
			break;
		case GRAPH_AS_HASH_MAP:
			m_eds = EdgeDataStructure.UNDI_HASH_EDGE;
			break;
		case HYPER_GRAPH_AS_HASH_MAP:
			m_eds = EdgeDataStructure.HYPER_EDGE;
			break;
		case WEIGHTED_HYPER_GRAPH:
			m_eds = EdgeDataStructure.WEIGHTED_HYPER_EDGE;
			break;
		default:
			throw new IllegalArgumentException("Could not initialize GraphFactory for the given GDS: " + gds.toString());
		}
		
	}

	public GraphDataStructure getType(){
		return m_gds;
	}
	
	public HyperGraphInterface<Index,BasicVertexInfo> createGraph()
	{	
		switch (m_gds)
		{
		case DI_GRAPH_AS_HASH_MAP:
			return  new DiGraphAsHashMap<Index,BasicVertexInfo>();
		case GRAPH_AS_HASH_MAP:
			return  new GraphAsHashMap<Index,BasicVertexInfo>();
		case HYPER_GRAPH_AS_HASH_MAP:
			return  new UndirectedHyperGraphAsHashMap<Index,BasicVertexInfo>();
		default:
			throw new IllegalStateException("graph data structure is not recognized");
		}
	}
	
	public GraphInterface<VertexType,VertexInfoStructure> createGraphSimple(){ 		
		if(isSimple(m_gds))
			return (GraphInterface<VertexType,VertexInfoStructure>)createGraph(m_gds);
		else if (isHyper() & isUndirected()){
			return (GraphInterface<VertexType,VertexInfoStructure>)createGraph(GraphDataStructure.GRAPH_AS_HASH_MAP);
		} else
			throw new IllegalArgumentException("Simple-graph data structure specfier expected, got: "+ m_gds.toString());
	}
	
	public AbstractSimpleEdge<Index,BasicVertexInfo> createSimpleEdge(Index v0, Index v1){
		switch (m_eds) {
		case DI_EDGE:
			return new DirectedSimpleEdge<Index,BasicVertexInfo>(v0, v1);
		case DI_HASH_EDGE:
			return new HashableDirectedEdge<Index,BasicVertexInfo>(v0, v1);
		case UNDI_EDGE:
			return new UndirectedSimpleEdge<Index,BasicVertexInfo>(v0, v1);
		case UNDI_HASH_EDGE:
			return new HashableUndirectedEdge<Index,BasicVertexInfo>(v0, v1);
		default:
			throw new IllegalStateException("edge data structure is not compatible with simple edges");
		}
	}

	
	public AbstractHyperEdge<VertexType,VertexInfoStructure> createEdge(Iterable<VertexType> vertices, EdgeInfo<VertexType,VertexInfoStructure> info){
		Iterator<VertexType> itr = vertices.iterator();
		VertexType v0 = itr.next();
		VertexType v1 = itr.next();
		
		switch (m_eds) {
		case DI_EDGE:
			return new DirectedSimpleEdge<VertexType,VertexInfoStructure>(v0,v1,info);
		case DI_HASH_EDGE:
			return new HashableDirectedEdge<VertexType,VertexInfoStructure>(v0, v1,info);
		case UNDI_EDGE:
			return new UndirectedSimpleEdge<VertexType,VertexInfoStructure>(v0, v1,info);
		case UNDI_HASH_EDGE:
			return new HashableUndirectedEdge<VertexType,VertexInfoStructure>(v0, v1,info);
		case HYPER_EDGE:
			return new HyperEdgeAsSet<VertexType,VertexInfoStructure>(vertices,info);
		case WEIGHTED_HYPER_EDGE:
			return new MultiWeightedHyperEdge<VertexType,VertexInfoStructure>(vertices);
		default:
			throw new IllegalStateException("edge data structure is not recognized.");
		}		
	}
	public AbstractHyperEdge<VertexType,VertexInfoStructure> createEdge(Iterable<VertexType> vertices){
		
		Iterator<VertexType> itr = vertices.iterator();
		VertexType v0 = itr.next();
		VertexType v1 = itr.next();
		
		switch (m_eds) {
		case DI_EDGE:
			return new DirectedSimpleEdge<VertexType,VertexInfoStructure>(v0,v1);
		case DI_HASH_EDGE:
			return new HashableDirectedEdge<VertexType,VertexInfoStructure>(v0, v1);
		case UNDI_EDGE:
			return new UndirectedSimpleEdge<VertexType,VertexInfoStructure>(v0, v1);
		case UNDI_HASH_EDGE:
			return new HashableUndirectedEdge<VertexType,VertexInfoStructure>(v0, v1);
		case HYPER_EDGE:
			return new HyperEdgeAsSet<VertexType,VertexInfoStructure>(vertices);
		default:
			throw new IllegalStateException("edge data structure is not compatible with simple edges");
		}
	}

	public boolean isHyper(){
		return m_gds==GraphFactory.GraphDataStructure.HYPER_GRAPH_AS_HASH_MAP;
	}
	
	public boolean isSimple() {
		return 	m_gds==GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP ||
				m_gds==GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP ;
	}

	public boolean isUndirected() {
		return 	m_gds==GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP ||
				m_gds==GraphFactory.GraphDataStructure.HYPER_GRAPH_AS_HASH_MAP;	
	}
	
	public boolean isDirected() {
		return 	m_gds==GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP;
	}

	public static HyperGraphInterface<Index,BasicVertexInfo> copy(GraphDataInterface<Index, BasicVertexInfo> from)
	{
		HyperGraphInterface<Index,BasicVertexInfo> graph = GraphFactory.createGraph(from.getType());
		GraphFactory.fillInGraphData(from, graph);
	    return graph;
	}

}

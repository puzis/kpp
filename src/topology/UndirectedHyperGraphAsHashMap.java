package topology;


import javolution.util.FastMap;
import javolution.util.FastSet;
import topology.GraphFactory.EdgeDataStructure;
import topology.GraphFactory.GraphDataStructure;

public class UndirectedHyperGraphAsHashMap<VertexType,VertexInfoStructure> 
	extends SerializableGraphRepresentation<VertexType,VertexInfoStructure>
	implements HyperGraphInterface<VertexType,VertexInfoStructure> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final GraphDataStructure GRAPH_TYPE = GraphDataStructure.HYPER_GRAPH_AS_HASH_MAP;
	public static final EdgeDataStructure  EDGE_TYPE =  EdgeDataStructure.HYPER_EDGE;


	/** The size of a graph is the sum of multiplicities of its vertices. */
	protected int m_size = 0;

	/** Key - Index representing a vertex, Value - set of HyperEdge*/
	protected FastMap<VertexType, FastSet<AbstractHyperEdge<VertexType,VertexInfoStructure>>> m_adjacency;

	public UndirectedHyperGraphAsHashMap(){
		super(GraphFactory.GraphDataStructure.HYPER_GRAPH_AS_HASH_MAP);
		
		m_adjacency  = new FastMap<VertexType, FastSet<AbstractHyperEdge<VertexType,VertexInfoStructure>>>();
		m_gfac  = new GraphFactory<VertexType,VertexInfoStructure>(GRAPH_TYPE,EDGE_TYPE);		
	}
	
	
	@Override
	public UndirectedHyperGraphAsHashMap<VertexType,VertexInfoStructure> clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}
	
	
	
	@Override
	public int getSize() {
		return m_size;
	}

	@Override
	public void addVertex(VertexType v, BasicVertexInfo vInfo) {
		if(!isVertex(v)){
			super.addVertex(v,vInfo);
			// TODO: If modifying VInfo after adding the vertex, then m_size is not updated.
			if (VertexFactory.isVertexInfo(vInfo))
				m_size += ((VertexInfo)vInfo).getMultiplicity();
			else 
				m_size++;
			m_adjacency.put(v,  new FastSet<AbstractHyperEdge<VertexType,VertexInfoStructure>>());
		}
	}


	@Override
	public boolean removeVertex(VertexType v) {
		if(isVertex(v)){
			//remove vertex info 
			m_size -= getVertex(v).getMultiplicity(); //Check for correctness
			getVertexInfoMap().remove(v);
			
			//remove vertex from hyper edges
			FastSet<AbstractHyperEdge<VertexType,VertexInfoStructure>> edges = m_adjacency.remove(v);
			for (AbstractHyperEdge<VertexType,VertexInfoStructure> e: edges){
				if(e instanceof HyperEdgeAsSet)
					((HyperEdgeAsSet<VertexType,VertexInfoStructure>)e).remove(v);	
				if (edges.isEmpty())
					removeEdge(e);
			}
			return true;
		}
		return false;
	}
	
	
	/**
	 * adds a hyper edge to the graph connecting all v vertices in the given collection 
	 * 
	 * NOTE: current implementation always creates a new edge even if a hyper edge with 
	 * the same collection of vertices exists.
	 * 
	 * When HyperEdge will implement hash and equals methods that evaluate the collection
	 * this method will test existence of a similar hyper edge before creating a new one.  
	 */
	@Override
	public void addEdge(Iterable<VertexType> vertices, EdgeInfo<VertexType,VertexInfoStructure> eInfo) {
		//AbstractHyperEdge<VertexType,VertexInfoStructure> e = super.addEdge(vertices, eInfo);
				
		AbstractHyperEdge<VertexType,VertexInfoStructure> e = (AbstractHyperEdge<VertexType,VertexInfoStructure>)m_gfac.createEdge(vertices,eInfo);  
		
		//if (!isEdge(e)) redundant inspection until implementation of a valid hash function and equals for HyperEdge 
		{ 
			getEdgeInfoMap().put(e, e);
			for(VertexType v : e.getVertices()){
				m_adjacency.get(v).add(e);
			}
		}
	}


	@Override
	public boolean removeEdge(AbstractHyperEdge<VertexType,VertexInfoStructure> e) {
		if (isEdge(e)){
			for(VertexType v : e.getVertices()){
				if(isVertex(v)){
					m_adjacency.get(v).remove(e);
				}
			}
			getEdgeInfoMap().remove(e);
			return true;
		}
		return false;
	}

	@Override
	public Iterable<? extends AbstractHyperEdge<VertexType,VertexInfoStructure>> getIncomingEdges(
			VertexType v) {
		return m_adjacency.get(v);
	}

	@Override
	public Iterable<? extends AbstractHyperEdge<VertexType,VertexInfoStructure>> getOutgoingEdges(
			VertexType v) {
		return m_adjacency.get(v);
	}

	public int getDegree(VertexType v) {
		if(m_adjacency.get(v)==null) 
			return 0;
		return m_adjacency.get(v).size();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(AbstractHyperEdge<VertexType, VertexInfoStructure> edge : getEdges()) {
			sb.append('{');
			for(VertexType v : edge.getVertices()) {
				sb.append(v);
				sb.append(' ');
			}
			sb.append('}');
		}
		return sb.toString();
	}
}

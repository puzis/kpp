package topology;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import topology.GraphFactory.GraphDataStructure;

import javolution.util.FastMap;

/**
 * Implementation of UNDIRECTED graph.
 * 
 * @author Polina Zilberman
 *
 */
public class GraphAsHashMap<VertexType,VertexInfoStructure> extends AbstractUndirectedGraph<VertexType,VertexInfoStructure>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final GraphFactory.GraphDataStructure GRAPH_TYPE = GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP;
	
	
	/** Key - VertexInfo representing a vertex, Value - List of neighbors. */
	protected FastMap<VertexType, List<AbstractSimpleEdge<VertexType,VertexInfoStructure>>> m_adjacencies = new FastMap<VertexType, List<AbstractSimpleEdge<VertexType,VertexInfoStructure>>>();

	
	public GraphAsHashMap(){
		super(GraphDataStructure.GRAPH_AS_HASH_MAP);
	}
	
	public void addEdge(VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> eInfo){	
		addEdge(new HashableUndirectedEdge<VertexType,VertexInfoStructure>(v0, v1), eInfo);	
	}
	
	public void addEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e, EdgeInfo<VertexType,VertexInfoStructure> eInfo)
	{
		VertexType v0 = e.getV0();
		VertexType v1 = e.getV1();
		
		if (!isEdge(e)){
			super.addEdge(e, eInfo);
		
			if (!m_adjacencies.get(v0).contains(e)) m_adjacencies.get(v0).add(e);
			if (!m_adjacencies.get(v1).contains(e)) m_adjacencies.get(v1).add(e);
		}
	}
	
	//public void addVertex(VertexType v, VertexInfo vInfo)
	public void addVertex(VertexType v, BasicVertexInfo vInfo)
	{
		if (!isVertex(v)){
			super.addVertex(v, vInfo);
			m_adjacencies.put(v, new LinkedList<AbstractSimpleEdge<VertexType,VertexInfoStructure>>());
		}
	}
	
	@Override
	public boolean removeVertex(VertexType v) {
		if (super.removeVertex(v)){			
			Iterator<AbstractSimpleEdge<VertexType,VertexInfoStructure>> edges = m_adjacencies.get(v).iterator();
			while(edges.hasNext()){
				AbstractSimpleEdge<VertexType,VertexInfoStructure> e = edges.next();
				this.removeEdge(e);				
			}
			m_adjacencies.remove(v);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e){
		//remove edges from the both directions if any 
		if(super.removeEdge(e)){			
			VertexType u = e.getV0();
			VertexType v = e.getV1();
			//TODO:optimize
			if (m_adjacencies.get(u).contains(e)) 
				m_adjacencies.get(u).remove(e);
//			if (m_adjacencies.get(u).contains(e.flip())) 
//				m_adjacencies.get(u).remove(e.flip());
			if (m_adjacencies.get(v).contains(e)) 
				m_adjacencies.get(v).remove(e);
//			if (m_adjacencies.get(v).contains(e.flip())) 
//				m_adjacencies.get(v).remove(e.flip());
			return true;
		}
		return false;
	}
	


	@Override
	public AbstractSimpleEdge<VertexType,VertexInfoStructure> getEdge(VertexType v0, VertexType v1) {
		HashableUndirectedEdge<VertexType,VertexInfoStructure> e = new HashableUndirectedEdge<VertexType,VertexInfoStructure>(v0,v1);
		FastMap.Entry<AbstractSimpleEdge<VertexType,VertexInfoStructure>, EdgeInfo<VertexType,VertexInfoStructure>> entry = super.getEdgeEntry(e);		
		if (entry ==null){
			entry = super.getEdgeEntry(e.flip());		
		}
		if (entry == null) return null;
		else return entry.getKey();
	}

	public int getDegree(VertexType v) {	
		if (m_adjacencies.get(v) != null){
			int d = m_adjacencies.get(v).size(); 
			return d;
		}
		return 0;	
	}

	public int getInDegree(VertexType v) {
		return getDegree(v);
	}

	public int getOutDegree(VertexType v) {
		return getDegree(v);
	}

	public Iterable<AbstractSimpleEdge<VertexType,VertexInfoStructure>> getOutgoingEdges(VertexType v) {	
		return m_adjacencies.get(v);
	}

	public Iterable<AbstractSimpleEdge<VertexType,VertexInfoStructure>> getIncomingEdges(VertexType v) {	
		return getOutgoingEdges(v);	
	}

	public void addEdge(VertexType v0, VertexType v1) {	
		addEdge(v0, v1, new EdgeInfo<VertexType,VertexInfoStructure>());	
	}
	public GraphFactory.GraphDataStructure getType (){
		return GRAPH_TYPE;
	}

	public boolean isDirected(){
		return false;
	}
}
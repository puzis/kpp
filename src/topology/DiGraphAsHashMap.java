package topology;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import topology.GraphFactory.GraphDataStructure;

import javolution.util.FastMap;


/**
 * Implementation of DIRECTED graph.
 * 
 * @author Polina Zilberman
 *
 */
public class DiGraphAsHashMap<VertexType,VertexInfoStructure> extends AbstractDirectedGraph<VertexType,VertexInfoStructure> implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final GraphFactory.GraphDataStructure GRAPH_TYPE = GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP;

	
	/** Key - VertexInfo representing a vertex, Value - List of neighbors. */
	protected Map<VertexType, List<AbstractSimpleEdge<VertexType,VertexInfoStructure>>> m_incomingEdges = new FastMap<VertexType, List<AbstractSimpleEdge<VertexType,VertexInfoStructure>>>();
	/** Key - VertexInfo representing a vertex, Value - List of neighbors. */
	protected Map<VertexType, List<AbstractSimpleEdge<VertexType,VertexInfoStructure>>> m_outgoingEdges = new FastMap<VertexType, List<AbstractSimpleEdge<VertexType,VertexInfoStructure>>>();

	
	public DiGraphAsHashMap(){
		super(GraphDataStructure.DI_GRAPH_AS_HASH_MAP);
		m_incomingEdges = new FastMap<VertexType, List<AbstractSimpleEdge<VertexType,VertexInfoStructure>>>();
		m_outgoingEdges = new FastMap<VertexType, List<AbstractSimpleEdge<VertexType,VertexInfoStructure>>>();
	}
	
	
	private DiGraphAsHashMap(DiGraphAsHashMap<VertexType,VertexInfoStructure> other){
		this();
		
		for (VertexType v : other.getVertices()){			
			this.addVertex(v, other.getVertex(v).clone());
		}
		
		for (AbstractSimpleEdge<VertexType,VertexInfoStructure> e: other.getEdges()){
			EdgeInfo<VertexType,VertexInfoStructure> eInfo = other.getEdgeWeight(e);
			eInfo = (EdgeInfo<VertexType,VertexInfoStructure>)eInfo.clone();
			this.addEdge(e.getV0(),e.getV1(), eInfo);
		}
	}
	
	public void addEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e, EdgeInfo<VertexType,VertexInfoStructure> eInfo)
	{
		VertexType v0 = e.getV0();
		VertexType v1 = e.getV1();
		
		if (!this.isEdge(e)){
			super.addEdge(e, eInfo);
			
			if (!m_outgoingEdges.get(v0).contains(e))	m_outgoingEdges.get(v0).add(e);
			if (!m_incomingEdges.get(v1).contains(e))	m_incomingEdges.get(v1).add(e);
		}
	}
	
	public void addVertex(VertexType v, BasicVertexInfo vInfo)
	{
		if (!isVertex(v)){
			super.addVertex(v, vInfo);
			m_outgoingEdges.put(v, new LinkedList<AbstractSimpleEdge<VertexType,VertexInfoStructure>>());
			m_incomingEdges.put(v, new LinkedList<AbstractSimpleEdge<VertexType,VertexInfoStructure>>());
		}
	}
	
	public void addEdge(VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> eInfo){	
		addEdge(new HashableDirectedEdge<VertexType,VertexInfoStructure>(v0, v1, eInfo), eInfo);	
	}
	
	public boolean removeEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e) {
		VertexType v0 = e.getV0();
		VertexType v1 = e.getV1();
		super.removeEdge(e);
			
		m_outgoingEdges.get(v0).remove(e);
		m_incomingEdges.get(v1).remove(e);
		
		return false;
	}
	
	@Override
	public boolean removeVertex(VertexType v) {
		if (super.removeVertex(v)){
			List<AbstractSimpleEdge<VertexType,VertexInfoStructure>> edges;
			
			edges = m_outgoingEdges.get(v);
			for (int i=edges.size()-1;i>=0;i--)
			{
				AbstractSimpleEdge<VertexType,VertexInfoStructure> e = edges.get(i);
				this.removeEdge(e);				
				
				VertexType u = e.getNeighbor(v);
				List<AbstractSimpleEdge<VertexType,VertexInfoStructure>> incoming_edges = m_incomingEdges.get(u);
				for (int j=incoming_edges.size()-1;j>=0;j--)
				{
					AbstractSimpleEdge<VertexType,VertexInfoStructure> incoming_e = incoming_edges.get(j);
				    if (incoming_e.getNeighbor(u).equals(v)){
				    	m_incomingEdges.get(u).remove(incoming_e);
				        break;
				    }
				}
			}
			m_outgoingEdges.remove(v);
			
			edges = m_incomingEdges.get(v);
			for (int i=edges.size()-1;i>=0;i--)
			{
				AbstractSimpleEdge<VertexType,VertexInfoStructure> e = edges.get(i);
				this.removeEdge(e);	
				
				VertexType u = e.getNeighbor(v);
				List<AbstractSimpleEdge<VertexType,VertexInfoStructure>> outgoing_edges = m_outgoingEdges.get(u);
				for (int j=outgoing_edges.size()-1;j>=0;j--)
				{
					AbstractSimpleEdge<VertexType,VertexInfoStructure> outgoing_e = outgoing_edges.get(j);
				    if (outgoing_e.getNeighbor(u).equals(v)){
				    	m_outgoingEdges.get(u).remove(outgoing_e);
				        break;
				    }
				}
			}
			m_incomingEdges.remove(v);
			return true;
		}		
		return false;
	}
	
	@Override
	public AbstractSimpleEdge<VertexType,VertexInfoStructure> getEdge(VertexType v0, VertexType v1) {
		HashableDirectedEdge<VertexType,VertexInfoStructure> e = new HashableDirectedEdge<VertexType,VertexInfoStructure>(v0,v1);
		FastMap.Entry<AbstractSimpleEdge<VertexType,VertexInfoStructure>, EdgeInfo<VertexType,VertexInfoStructure>> entry = super.getEdgeEntry(e);		
		if (entry == null) return null;
		else return entry.getKey();
	}

	@Override
	public Iterable<AbstractSimpleEdge<VertexType,VertexInfoStructure>> getIncomingEdges(VertexType v) {
		return m_incomingEdges.get(v);
	}


	@Override
	public Iterable<AbstractSimpleEdge<VertexType,VertexInfoStructure>> getOutgoingEdges(VertexType v) {
		return m_outgoingEdges.get(v);
	}


	@Override
	public int getDegree(VertexType v) {
		java.util.HashSet<VertexType> set = new HashSet<VertexType>();
		for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e : this.getIncomingEdges(v)){
			set.add(e.getNeighbor(v));
		}
		for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e : this.getOutgoingEdges(v)){
			set.add(e.getNeighbor(v));
		}
		return set.size();
	}


	@Override
	public int getOutDegree(VertexType v) {	
		if (m_outgoingEdges.get(v) != null)
			return m_outgoingEdges.get(v).size();
		return 0;	
	}


	@Override
	public int getInDegree(VertexType v) {	
		if (m_incomingEdges.get(v) != null)
			return m_incomingEdges.get(v).size();
		return 0;	
	}
	
	public GraphFactory.GraphDataStructure getType (){
		return GRAPH_TYPE;
	}


	public void addEdge(VertexType v0, VertexType v1) {	
		addEdge(v0, v1, new EdgeInfo<VertexType,VertexInfoStructure>());	
	}
	
	
	@Override
	public DiGraphAsHashMap<VertexType,VertexInfoStructure> clone() throws CloneNotSupportedException{
		return new DiGraphAsHashMap<VertexType,VertexInfoStructure>(this);		
	}

	public boolean isDirected(){
		return true;
	}
}
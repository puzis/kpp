package topology;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import topology.GraphFactory.GraphDataStructure;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.Index;

import common.Pair;

/**
 * Created by IntelliJ IDEA.
 * User: puzis
 * Date: Sep 6, 2007
 * Time: 10:50:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class SerializableGraphRepresentation<VertexType,VertexInfoStructure> implements GraphDataInterface<VertexType, VertexInfoStructure>
{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -8396973509418250171L;
	
	
	/** Key - Index representing a vertex, Value - VertexInfo */
	protected FastMap<VertexType, BasicVertexInfo> m_vertices;
	/** Key - HyperEdge representing a set of vertices, Value - EdgeInfo */
	protected FastMap<AbstractHyperEdge<VertexType,VertexInfoStructure>, EdgeInfo<VertexType,VertexInfoStructure>> m_edgeInfos;
	protected GraphFactory<VertexType,VertexInfoStructure> m_gfac;


    public SerializableGraphRepresentation(HyperGraphInterface<VertexType,VertexInfoStructure> graph) 
    {
    	this(graph.getType());

        BasicVertexInfo info = null;
		for (VertexType vertex : graph.getVertices()){
            info = graph.getVertex(vertex);
            this.addVertex(vertex, info.clone());
        }

		for(AbstractHyperEdge<VertexType,VertexInfoStructure> e : graph.getEdges()) {
            this.addEdge(e.getVertices(), graph.getEdgeWeight(e).clone());
        }
    }

    
    public SerializableGraphRepresentation(GraphFactory.GraphDataStructure gds) {
		m_vertices = new FastMap<VertexType, BasicVertexInfo>();
		m_edgeInfos = new FastMap<AbstractHyperEdge<VertexType,VertexInfoStructure>, EdgeInfo<VertexType,VertexInfoStructure>>();
		m_gfac = new GraphFactory<VertexType,VertexInfoStructure>(gds);
	}

    
	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#clone()
	 */
	@Override
	public SerializableGraphRepresentation<VertexType,VertexInfoStructure> clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}


	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#getNumberOfVertices()
	 */
	@Override
	public int getNumberOfVertices() {
		return getVertexInfoMap().size();	
	}


	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#getNumberOfEdges()
	 */
	@Override
	public int getNumberOfEdges() {
		return getEdgeInfoMap().size();
	}


	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#getType()
	 */
	@Override
	public GraphFactory.GraphDataStructure getType() {
		return m_gfac.getType();
	}


	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#addVertex(VertexType)
	 */
	@Override
	public void addVertex(VertexType v, BasicVertexInfo info) {
		getVertexInfoMap().put(v,info);
	}

	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#addVertex(VertexType)
	 */
	@Override
	public void addVertex(VertexType v) {
		addVertex(v, new VertexInfo());
	}



	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#isVertex(VertexType)
	 */
	@Override
	public boolean isVertex(VertexType v) {
		return getVertexInfoMap().containsKey(v);	
	}


	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#getVertex(VertexType)
	 */
	@Override
	public BasicVertexInfo getVertex(VertexType v) {
		return getVertexInfoMap().get(v);
	}


	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#setEdgeWeight(topology.AbstractHyperEdge, topology.EdgeInfo)
	 */
	@Override
	public void setEdgeWeight(AbstractHyperEdge<VertexType,VertexInfoStructure> e, EdgeInfo<VertexType,VertexInfoStructure> weight) {
		if(isEdge(e))
			getEdgeInfoMap().put(e, weight);
		else
			throw new IllegalArgumentException("Edge does not exist.");
	}


	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#getEdgeWeight(topology.AbstractHyperEdge)
	 */
	@Override
	public EdgeInfo<VertexType,VertexInfoStructure> getEdgeWeight(AbstractHyperEdge<VertexType,VertexInfoStructure> e) {	
		return getEdgeInfoMap().get(e);
	}


	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#getVertices()
	 */
	@Override
	public Iterable<VertexType> getVertices() {
		return getVertexInfoMap().keySet();
	}


	/* (non-Javadoc)
	 * @see topology.GraphDataInterface#getEdges()
	 */
	@Override
	public Collection<? extends AbstractHyperEdge<VertexType,VertexInfoStructure>> getEdges() {
		return getEdgeInfoMap().keySet();
	}


	@Override
	public boolean isEdge(AbstractHyperEdge<VertexType,VertexInfoStructure> e) {
		return getEdgeInfoMap().containsKey(e);
	}


	@Override
	public void addEdge(Iterable<VertexType> vertices,
			EdgeInfo<VertexType, VertexInfoStructure> eInfo) {
		
		AbstractHyperEdge<VertexType,VertexInfoStructure> e = (AbstractHyperEdge<VertexType,VertexInfoStructure>)m_gfac.createEdge(vertices,eInfo);  
		
		//if (!isEdge(e)) redundant inspection until implementation of a valid hash function and equals for HyperEdge 
		{ 
			getEdgeInfoMap().put(e, e);
		}
	}


	@Override
	public void addEdge(Iterable<VertexType> e) {
		addEdge(e, new EdgeInfo<VertexType,VertexInfoStructure>());		
	}


	protected Map<VertexType, BasicVertexInfo> getVertexInfoMap() {
		return m_vertices;
	}


	protected Map<AbstractHyperEdge<VertexType,VertexInfoStructure>, EdgeInfo<VertexType,VertexInfoStructure>> getEdgeInfoMap() {
		return m_edgeInfos;
	}


	/*
	protected void setEdgeInfoMap(FastMap<AbstractHyperEdge<VertexType,VertexInfoStructure>, EdgeInfo<VertexType,VertexInfoStructure>> m_edgeInfos) {
		this.m_edgeInfos = m_edgeInfos;
	}
	*/
	
	/*
	protected void setVertexInfoMap(FastMap<VertexType, BasicVertexInfo> m_vertices) {
		this.m_vertices = m_vertices;
	}
	*/
    
}

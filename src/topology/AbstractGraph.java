package topology;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import topology.GraphFactory.GraphDataStructure;

import javolution.util.FastMap;

public abstract class AbstractGraph<VertexType,VertexInfoStructure>
	extends SerializableGraphRepresentation<VertexType, VertexInfoStructure>
	implements GraphInterface<VertexType,VertexInfoStructure> {

	public AbstractGraph(GraphDataStructure gds) {
		super(gds);
	}

	protected static final long serialVersionUID = 1L;

	
	private int m_hashCode = 0;
	/** The size of a graph is the sum of multiplicities of its vertices. */
	private int m_size = 0;
	
	/*
	 * TODO: vertex and edge maps are inherited by all specific graph data structures 
	 * though some of them do not use maps. Push down fields to the relevant subclass 
	 * hierarchies. 
	 */
	
	/** Key - Index representing a vertex, Value - VertexInfo */
	//private FastMap<VertexType, BasicVertexInfo> m_vertices = new FastMap<VertexType, BasicVertexInfo>();	
	
	/** Key - Edge representing a pair of vertices, Value - EdgeInfo */
	protected FastMap<AbstractSimpleEdge<VertexType,VertexInfoStructure>, EdgeInfo<VertexType,VertexInfoStructure>> m_edgeInfos = new FastMap<AbstractSimpleEdge<VertexType,VertexInfoStructure>, EdgeInfo<VertexType,VertexInfoStructure>>();

	

	protected Map<VertexType, BasicVertexInfo> getVertexInfoMap() {
		return m_vertices;
	}


	protected Map<AbstractHyperEdge<VertexType,VertexInfoStructure>,EdgeInfo<VertexType,VertexInfoStructure>> getEdgeInfoMap() {
		Map<AbstractHyperEdge<VertexType,VertexInfoStructure>,EdgeInfo<VertexType,VertexInfoStructure>> rslt;
		rslt = new FastMap<AbstractHyperEdge<VertexType,VertexInfoStructure>,EdgeInfo<VertexType,VertexInfoStructure>>();
		rslt.putAll(m_edgeInfos);
		return rslt;
	}
	
	
	/** The size of a graph is the sum of multiplicities of its vertices. */
	public int getSize(){
		return m_size;
	}
	
	public int getNumberOfVertices(){	
		return getVertexInfoMap().size();	
	}
	
	public int getNumberOfEdges(){	
		return m_edgeInfos.size();	
	}
	
	public Iterable<VertexType> getVertices(){	
		return getVertexInfoMap().keySet();	
	}
	
	public Collection<? extends AbstractSimpleEdge<VertexType,VertexInfoStructure>> getEdges(){	
		return m_edgeInfos.keySet();	
	}
	
	
	public void addEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e){	
		addEdge(e, new EdgeInfo<VertexType,VertexInfoStructure>());	
	}
	
	
	public void addVertex(VertexType v){	
		addVertex(v, new VertexInfo());
	}
	
	
	public boolean isEdge(VertexType v0, VertexType v1){	
		return isEdge(getEdge(v0, v1));	
	}
	
	public boolean isEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e){	
		if (e==null)
			return false;
		return m_edgeInfos.containsKey(e);	
	}	
	
	public boolean isVertex(VertexType v){	
		return getVertexInfoMap().containsKey(v);	
	}
	
	public BasicVertexInfo getVertex(VertexType v){	
		return getVertexInfoMap().get(v);	
	}
	
	public EdgeInfo<VertexType,VertexInfoStructure> getEdgeWeight(VertexType v0, VertexType v1){	
		AbstractSimpleEdge<VertexType,VertexInfoStructure> e = getEdge(v0, v1);
		return getEdgeWeight(e);	
	}
	
	public EdgeInfo<VertexType,VertexInfoStructure> getEdgeWeight(AbstractSimpleEdge<VertexType,VertexInfoStructure> e){
		if (e==null)
			return null;
		return m_edgeInfos.get(e);	
	}
	
	public void setEdgeWeight(AbstractSimpleEdge<VertexType,VertexInfoStructure> e, EdgeInfo<VertexType,VertexInfoStructure> weight){	
		m_edgeInfos.put(e, weight);	
	}
	
	public void setEdgeWeight(VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> weight){	
		setEdgeWeight(getEdge(v0, v1), weight);	
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Links:").append(m_edgeInfos).append("\tVertices:").append(getVertexInfoMap());//.append("\n");
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return m_hashCode;
		
		
//		return m_id.hashCode();

//		return this.toString().hashCode();
		
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((m_edges == null) ? 0 : m_edges.hashCode());
//		result = prime * result
//				+ ((m_vertices == null) ? 0 : m_vertices.hashCode());
//		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final AbstractGraph<VertexType,VertexInfoStructure> other = (AbstractGraph<VertexType,VertexInfoStructure>) obj;
		if (!m_edgeInfos.equals(other.m_edgeInfos))
			return false;
		if (!getVertexInfoMap().equals(other.getVertexInfoMap()))
			return false;
		return true;
	}

	
	@Override
	public void addVertex(VertexType v, BasicVertexInfo vInfo){
		if(!isVertex(v)){
			m_hashCode += 31^v.hashCode();
			// TODO: If modifying VInfo after adding the vertex, then m_size is not updated.
			if (VertexFactory.isVertexInfo(vInfo))
				m_size += ((VertexInfo)vInfo).getMultiplicity();
			else 
				m_size++; 
		}
		getVertexInfoMap().put(v, vInfo);
	}

	@Override
	public boolean removeVertex(VertexType v){
		if(isVertex(v)){
			m_hashCode -= 31^v.hashCode();
			getVertexInfoMap().remove(v);
			return true;
			//TODO:WHAT ABOUT THE EDGES OF VERTEX v???
		}
		return false;
	}

	@Override
	public void addEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e, EdgeInfo<VertexType,VertexInfoStructure> w){
		
		if(!isEdge(e)){
			m_hashCode += 31^e.getV0().hashCode(); 		
			m_hashCode += 31^e.getV1().hashCode();
		}
		m_edgeInfos.put(e,w);
	}
	
	@Override
	public boolean removeEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e){
		if(isEdge(e)){
			m_hashCode -= 31^e.getV0().hashCode(); 		
			m_hashCode -= 31^e.getV1().hashCode();
			m_edgeInfos.remove(e);
			return true;
		}
		return false;
	}
	
	protected FastMap.Entry<AbstractSimpleEdge<VertexType,VertexInfoStructure>, EdgeInfo<VertexType,VertexInfoStructure>> getEdgeEntry(Object e){
		return m_edgeInfos.getEntry(e);
	}
	
	@Override
	public AbstractGraph<VertexType,VertexInfoStructure> clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();		
	}

	@Override
	public void addEdge(Iterable<VertexType> e, EdgeInfo<VertexType,VertexInfoStructure> eInfo) {
		Iterator<VertexType> itr = e.iterator();
		VertexType v1 = itr.next();
		VertexType v2 = itr.next();
		if (itr.hasNext())
			throw new IllegalArgumentException("AddEdge should receive exactly two vertices. More were found.");
		this.addEdge(v1,v2,eInfo);
	}

	@Override
	public void addEdge(Iterable<VertexType> e) {
		Iterator<VertexType> itr = e.iterator();
		VertexType v1 = itr.next();
		VertexType v2 = itr.next();
		if (itr.hasNext())
			throw new IllegalArgumentException("AddEdge should receive exactly two vertices. More were found.");
		this.addEdge(v1,v2);
	}

	@Override
	public boolean removeEdge(AbstractHyperEdge<VertexType,VertexInfoStructure> e) {
		if (e instanceof AbstractSimpleEdge<?,?>)
			return this.removeEdge((AbstractSimpleEdge<VertexType,VertexInfoStructure>)e);
		else
			throw new IllegalArgumentException("Edge argument type not supported.");
	}

	@Override
	public boolean isEdge(AbstractHyperEdge<VertexType,VertexInfoStructure> e) {
		if (e instanceof AbstractSimpleEdge<?,?>)
			return this.isEdge((AbstractSimpleEdge<VertexType,VertexInfoStructure>)e);
		else
			throw new IllegalArgumentException("Edge argument type not supported.");
	}

	@Override
	public void setEdgeWeight(AbstractHyperEdge<VertexType,VertexInfoStructure> e, EdgeInfo<VertexType,VertexInfoStructure> weight) {
		if (e instanceof AbstractSimpleEdge<?,?>)
			this.setEdgeWeight((AbstractSimpleEdge<VertexType,VertexInfoStructure>)e, weight);
		else
			throw new IllegalArgumentException("Edge argument type not supported.");
	}

	@Override
	public EdgeInfo<VertexType,VertexInfoStructure> getEdgeWeight(AbstractHyperEdge<VertexType,VertexInfoStructure> e) {
		if (e instanceof AbstractSimpleEdge<?,?>)
			return this.getEdgeWeight((AbstractSimpleEdge<VertexType,VertexInfoStructure>)e);
		else
			throw new IllegalArgumentException("Edge argument type not supported.");
	}

}

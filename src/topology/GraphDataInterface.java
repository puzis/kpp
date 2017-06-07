package topology;

import java.io.Serializable;
import java.util.Collection;

import topology.GraphFactory.GraphDataStructure;

public interface GraphDataInterface<VertexType, VertexInfoStructure> extends Serializable, Cloneable
{

	public int getNumberOfVertices();
	public int getNumberOfEdges();

	public GraphDataStructure getType();

	public void addVertex(VertexType v);
	public void addVertex(VertexType v, BasicVertexInfo vInfo);
	public boolean isVertex(VertexType v);
	/** If v is not a vertex, returns null. */
	public BasicVertexInfo getVertex(VertexType v);
	public Iterable<VertexType> getVertices();


	//TODO: change return type of addEdge to AbstractHyperEdge<VertexType,VertexInfoStructure> 
	public void addEdge(Iterable<VertexType> e, EdgeInfo<VertexType,VertexInfoStructure> eInfo);
	public void addEdge(Iterable<VertexType> e);
	public boolean isEdge(AbstractHyperEdge<VertexType,VertexInfoStructure> e); 	
	@Deprecated
	public void setEdgeWeight(AbstractHyperEdge<VertexType,VertexInfoStructure> e, EdgeInfo<VertexType,VertexInfoStructure> weight); 
	/** If e is not an edge, returns null. 
	 **/
	@Deprecated
	public EdgeInfo<VertexType,VertexInfoStructure> getEdgeWeight(AbstractHyperEdge<VertexType,VertexInfoStructure> e);
	public Collection<? extends AbstractHyperEdge<VertexType,VertexInfoStructure>> getEdges();	
	
	
	
	public GraphDataInterface<VertexType,VertexInfoStructure> clone() throws CloneNotSupportedException;

	
}
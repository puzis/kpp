package topology;

import java.util.Collection;

public interface GraphInterface<VertexType,VertexInfoStructure> extends HyperGraphInterface<VertexType,VertexInfoStructure > 
{
	public void addEdge(VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> eInfo);
	public void addEdge(VertexType v0, VertexType v1);
	public void addEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e, EdgeInfo<VertexType,VertexInfoStructure> eInfo);
	public void addEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e);
	
	
	public boolean removeEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e);
	public Collection<? extends AbstractSimpleEdge<VertexType,VertexInfoStructure>> getEdges();
	
	/** If there are no incoming edges, returns null. */
	public Iterable<? extends AbstractSimpleEdge<VertexType,VertexInfoStructure>> getIncomingEdges(VertexType v);
	/** If there are no outgoing edges, returns null. */
	public Iterable<? extends AbstractSimpleEdge<VertexType,VertexInfoStructure>> getOutgoingEdges(VertexType v);
	
	public boolean isEdge(VertexType v0, VertexType v1);
	public boolean isEdge(AbstractSimpleEdge<VertexType,VertexInfoStructure> e);	
	
	
	/** If e is not an edge, returns null. */
	public EdgeInfo<VertexType,VertexInfoStructure> getEdgeWeight(AbstractSimpleEdge<VertexType,VertexInfoStructure> e);
	/** If e is not an edge, returns null. */
	public EdgeInfo<VertexType,VertexInfoStructure> getEdgeWeight(VertexType v0, VertexType v1);
	/** If there is no such edge, returns null. */
	public AbstractSimpleEdge<VertexType,VertexInfoStructure> getEdge(VertexType v0, VertexType v1);
	
	public void setEdgeWeight(AbstractSimpleEdge<VertexType,VertexInfoStructure> e, EdgeInfo<VertexType,VertexInfoStructure> weight);
	public void setEdgeWeight(VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> weight);
	
	public int getOutDegree (VertexType v);
	public int getInDegree (VertexType v);
		
	public GraphInterface<VertexType,VertexInfoStructure> clone() throws CloneNotSupportedException;
	
	public boolean isDirected();
}

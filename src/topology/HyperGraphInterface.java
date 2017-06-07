package topology;

import topology.GraphFactory.GraphDataStructure;


public interface HyperGraphInterface<VertexType,VertexInfoStructure> extends GraphDataInterface<VertexType, VertexInfoStructure> {
	
	public static final GraphFactory.GraphDataStructure GRAPH_TYPE = GraphDataStructure.UNSPECIFIED;

	public GraphFactory.GraphDataStructure getType();

	
	/** The size of a graph is the sum of multiplicities of its vertices. */
	public int getSize();

	
	public boolean removeVertex(VertexType v);
	/** For a hypergraph, returns the number of hyperedges a vertex participates in*/
	public int getDegree(VertexType v);
	
	
	public boolean removeEdge(AbstractHyperEdge<VertexType,VertexInfoStructure> e); 
	
	/** If there are no incoming edges, returns null. 
	 * */
	public Iterable<? extends AbstractHyperEdge<VertexType,VertexInfoStructure>> getIncomingEdges(VertexType v) ;
	/** If there are no outgoing edges, returns null. 
	 * */
	public Iterable<? extends AbstractHyperEdge<VertexType,VertexInfoStructure>> getOutgoingEdges(VertexType v) ;

	public HyperGraphInterface<VertexType,VertexInfoStructure> clone() throws CloneNotSupportedException;

}

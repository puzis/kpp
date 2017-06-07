package topology;

import topology.GraphFactory.EdgeDataStructure;
import topology.GraphFactory.GraphDataStructure;

public class MultiWeightedHyperGraph<VertexType extends Comparable<VertexType>,VertexInfoStructure> extends UndirectedHyperGraphAsHashMap<VertexType,VertexInfoStructure> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final GraphDataStructure GRAPH_TYPE = GraphDataStructure.WEIGHTED_HYPER_GRAPH;
	public static final EdgeDataStructure  EDGE_TYPE =  EdgeDataStructure.WEIGHTED_HYPER_EDGE;

	
	
	@Override
	public Iterable<? extends MultiWeightedHyperEdge<VertexType,VertexInfoStructure>> getOutgoingEdges(
			VertexType v) {		
		return (Iterable<MultiWeightedHyperEdge<VertexType,VertexInfoStructure>>)
				super.getOutgoingEdges(v);
	}

}

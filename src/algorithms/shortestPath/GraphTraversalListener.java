package algorithms.shortestPath;

import topology.AbstractHyperEdge;
import topology.BasicVertexInfo;
import javolution.util.Index;

public interface GraphTraversalListener {

	public void beforeExpand(Index v);
	
	public void vertexDiscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge);
	
	public void vertexRediscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge, double dist);
	
	public void afterExpand(Index v);
	
	public boolean isExpandable(Index v);

}

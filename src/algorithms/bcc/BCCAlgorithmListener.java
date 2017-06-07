package algorithms.bcc;

import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;

public interface BCCAlgorithmListener {
	
	public void addComponent(GraphInterface<Index,BasicVertexInfo> component);
	public void closeComponent(GraphInterface<Index,BasicVertexInfo> component, int minDiscoveryOrder, int maxDiscoveryOrder);
	public void closeCutoffVertex(Index cutoffVertex);
	public void addCutoffVertex(Index cutoffVertex, AbstractSimpleEdge<Index,BasicVertexInfo> creatingEdge);
	public void addEdge(Index v0, GraphInterface<Index,BasicVertexInfo> v1);
}

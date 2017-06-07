package algorithms.centralityAlgorithms.betweenness.brandes;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;

public interface BrandesBCAlgListener {
	
	public void algorithmInitialization(GraphInterface<Index,BasicVertexInfo>  graph);
	public void beforeSingleSourceShortestPaths(int s);	
	public void singleSourceInitialization(int s);
	public void afterSingleSourceShortestPaths(int s);
	
	public void beforeAccumulation(int s);
	public void beforeSourceDependencyUpdate(int s, int w);	
	public void sourceDependencyUpdate(int s, int v, int w);
	public void afterSourceDependencyUpdate(int s, int w);
	public void afterAccumulation(int s);
	

}

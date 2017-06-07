package algorithms.centralityAlgorithms.betweenness.brandes;

import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public interface AlgorithmInterface
{
	public void addMember(int v);
	public void addMember(AbstractSimpleEdge<Index,BasicVertexInfo> e);
	
	public double getGroupBetweenness();
	public double getNormalizedGroupBetweenness();
}

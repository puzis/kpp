package algorithms.centralityAlgorithms.betweenness.brandes.utils;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public interface BasicSetInterface
{
	public int size();
	public boolean isMember(int v);
	public boolean isMember(AbstractSimpleEdge<Index,BasicVertexInfo> e);

	public double getNormalizedGroupCentrality();
	public void add(int v);
	public void add(int[] group);
	public void add(AbstractSimpleEdge<Index,BasicVertexInfo> e);

	public void remove(int v) throws Exception;
	public void remove(FastList<Index> group) throws Exception;
	public double getContribution(int v);
	public double getContribution(Object[] group) throws Exception;
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e);
}

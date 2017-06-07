package algorithms.centralityAlgorithms;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public interface BasicSetInterface extends Cloneable
{
	public int size();
	public boolean isMember(Index v);
	public boolean isMember(AbstractSimpleEdge<Index,BasicVertexInfo> e);
	public FastList<Index> getVertexMembers();		
	public FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> getLinkMembers();

	public double getGroupCentrality();
	public void add(Index v);
	public void add(FastList<Index> group);
	public void add(AbstractSimpleEdge<Index,BasicVertexInfo> e);

	public void remove(Index v); 
	public void remove(FastList<Index> group); 
	public void remove(AbstractSimpleEdge<Index,BasicVertexInfo> e);
	public double getContribution(Index v);
	public double getContribution(Object[] group);
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e);
	
	public BasicSetInterface clone(); 
}

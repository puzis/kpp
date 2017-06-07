package algorithms.centralityAlgorithms.betweenness.brandes.utils;

import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public abstract class AbstractBasicSet implements BasicSetInterface
{
	public abstract FastSet<Index> getMembers();		
	protected abstract FastSet<AbstractSimpleEdge<Index,BasicVertexInfo>> getLinkMembers();

	public int size(){	return getMembers().size();	}
	
	public boolean isMember(int v){	return (getMembers().contains(v));	}
	public boolean isMember(AbstractSimpleEdge<Index,BasicVertexInfo> e){	return (getLinkMembers().contains(e));	}

	public abstract double getNormalizedGroupCentrality();
	public abstract void add(int v);
	public abstract void add(int[] group);
	public abstract void add(AbstractSimpleEdge<Index,BasicVertexInfo> e);

	public abstract void remove(int v) throws Exception;
	public abstract void remove(FastList<Index> group) throws Exception;
	public abstract double getContribution(int v); 
	public abstract double getContribution(Object[] group) throws Exception;
	public abstract double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e);

}

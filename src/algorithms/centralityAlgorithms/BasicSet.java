package algorithms.centralityAlgorithms;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public class BasicSet implements BasicSetInterface{
	
	protected FastList<Index> m_vertices = new FastList<Index>();
	protected FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> m_edges = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();

	
	public BasicSet(){	}
	protected BasicSet(BasicSet other){		
		m_vertices = new FastList<Index>();
		m_vertices.addAll(other.m_vertices);
		m_edges = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
		m_edges.addAll(other.m_edges);
	}
	
	public BasicSet clone(){
		return new BasicSet(this);
	}
	public int size(){	
		return m_vertices.size()+m_edges.size();	
	}
	
	@Override
	public void add(Index v) {
		if (!isMember(v))
			m_vertices.add(v);	
	}
	
	@Override
	public void add(FastList<Index> group) {
		for (Index v : group) add(v);
	}
	
	@Override
	public void add(AbstractSimpleEdge<Index,BasicVertexInfo> e) {
		if (!isMember(e))
	        m_edges.add(e);
	}
	
	@Override
	public void remove(AbstractSimpleEdge<Index,BasicVertexInfo> e) {
		if (!isMember(e))
	        m_edges.add(e);
	}

	@Override
	public FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> getLinkMembers(){	
		return m_edges;	
	}

	@Override
	public FastList<Index> getVertexMembers(){	
		return m_vertices;	
	}
	
	@Override
	public boolean isMember(Index v){	
		return (getVertexMembers().contains(v));	
	}

	@Override
	public boolean isMember(AbstractSimpleEdge<Index,BasicVertexInfo> e){	
		return (getLinkMembers().contains(e));	
	}

	@Override
	public void remove(Index v) {
		m_vertices.remove(v);	
	}

	@Override
	public void remove(FastList<Index> group) {	
		m_vertices.removeAll(group);	
	}
	@Override
	public double getContribution(Index v) {
		return 1;
	}
	@Override
	public double getContribution(Object[] group) {
		//TODO: correct to return size of the difference (this - group)
		return group.length;
	}
	@Override
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e) {
		return 1;
	}
	@Override
	public double getGroupCentrality() {
		return size();
	}	
}

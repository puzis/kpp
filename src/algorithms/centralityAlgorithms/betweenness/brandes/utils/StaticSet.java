package algorithms.centralityAlgorithms.betweenness.brandes.utils;


import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.Index;
import server.common.DummyProgress;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;


public class StaticSet extends AbstractBasicSet
{
	private DataWorkshop m_dw;
      	private FastSet<Index> m_members = new FastSet<Index>();		
        private FastSet<AbstractSimpleEdge<Index,BasicVertexInfo>> m_edges = new FastSet<AbstractSimpleEdge<Index,BasicVertexInfo>>();

     /**
	 * OptimizedDynamicSet constructor
	 * @param dw - DataWorkshop
	 * @param candidates - Candidates for highest contribution.
	 * @throws Exception - Exception is thrown if there are candidates of invalid type.
	 */
	public StaticSet(DataWorkshop dw) throws Exception	{	m_dw = dw;	}
	
	@Override
	public void add(int v)
	{
		if (!isMember(v))
			m_members.add(Index.valueOf(v));	
	}

	@Override
	public void add(int[] group)
	{
		for (int v : group) add(v);
	}

	@Override
	public void add(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
		if (!isMember(e))
            m_edges.add(e);

	}

	@Override
	public double getContribution(int v){	return m_dw.getBetweenness(v);	}

	@Override
	public double getContribution(Object[] group) throws Exception
	{
		return CandidatesBasedAlgorithm.calculateGB(m_dw, group, new DummyProgress(), 1);
	}

	@Override
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
		return m_dw.getPairBetweenness(e.getV0().intValue(), e.getV1().intValue());
	}

	@Override
	protected FastSet<AbstractSimpleEdge<Index,BasicVertexInfo>> getLinkMembers(){	return m_edges;	}

	@Override
	public FastSet<Index> getMembers(){	return m_members;	}

	@Override
	public double getNormalizedGroupCentrality()
	{            
        Object[] group = new Object[m_members.size() + m_edges.size()];
        System.arraycopy(m_members.toArray(), 0, group, 0, m_members.size());
        System.arraycopy(m_edges, 0, group, m_members.size(), m_edges.size());
		return CandidatesBasedAlgorithm.calculateGB(m_dw,group, new DummyProgress(), 1);            		
	}

	@Override
	public void remove(int v) throws Exception{	m_members.remove(v);	}

	@Override
	public void remove(FastList<Index> group) throws Exception	{	m_members.removeAll(group);	}
}
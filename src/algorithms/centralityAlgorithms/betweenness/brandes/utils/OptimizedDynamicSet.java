package algorithms.centralityAlgorithms.betweenness.brandes.utils;

import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.Index;
import server.common.DummyProgress;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;

public class OptimizedDynamicSet extends AbstractBasicSet
{
	private FastList<Index> m_candidates;
	private DataWorkshop m_dw;
	private CandidatesBasedAlgorithm m_algortihm;

	/**
	 * OptimizedDynamicSet constructor
	 * @param dw - DataWorkshop
	 * @param candidates - Candidates for highest contribution.
	 * @throws Exception - Exception is thrown if there are candidates of invalid type.
	 */
	public OptimizedDynamicSet(DataWorkshop dw, FastList<Index> candidates) throws Exception
	{
		m_dw = dw;
		m_candidates = candidates;
		m_algortihm = new CandidatesBasedAlgorithm(m_dw, m_candidates.toArray());
	}
	
	@Override
	public void add(int v)
	{
		if (isMember(v))
			return;
		m_algortihm.addMember(v);	
	}

	@Override
	public void add(int[] group)
	{
		for (int v : group ) {	add(v);	}
	}

	@Override
	public void add(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
		if (isMember(e))
			return;
		m_algortihm.addMember(e);
	}

	@Override
	public double getContribution(int v)
	{
		return m_algortihm.getBetweenness(v);
	}

	@Override
	public double getContribution(Object[] group) throws Exception
	{
		return CandidatesBasedAlgorithm.calculateGB(m_algortihm.getDataWorkshop(), group, new DummyProgress(), 1);
	}

	@Override
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
		return m_algortihm.getPairBetweenness(e.getV0().intValue(), e.getV1().intValue());
	}

	@Override
	protected FastSet<AbstractSimpleEdge<Index,BasicVertexInfo>> getLinkMembers(){	return m_algortihm.getEdgeMembers();	}

	@Override
	public FastSet<Index> getMembers(){	return m_algortihm.getMembers();	}

	@Override
	public double getNormalizedGroupCentrality(){	return m_algortihm.getNormalizedGroupBetweenness();	}

	@Override
	public void remove(int v) throws Exception
	{
		FastList<Index> newGroup = new FastList<Index>(getMembers());
		newGroup.remove(v);
		updateAlgorithm(newGroup);
	}

	@Override
	public void remove(FastList<Index> group) throws Exception
	{
		FastList<Index> newGroup = new FastList<Index>(getMembers());
        newGroup.removeAll(group);
		updateAlgorithm(newGroup);
	}

	private void updateAlgorithm(FastList<Index> newGroup) throws Exception
	{
		m_algortihm = new CandidatesBasedAlgorithm(m_dw, m_candidates.toArray());
		
		for (FastList.Node<Index> v = newGroup.head(), end = newGroup.tail(); (v = v.getNext()) != end;)
		{
			int vValue = v.getValue().intValue();
			add(vValue);
		}
	}
}
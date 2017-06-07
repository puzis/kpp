package algorithms.centralityAlgorithms.betweenness.brandes.sets;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DummyProgress;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;

public class OptimizedDynamicBetweennessSet extends BasicSet
{
	private FastList<Index> m_candidates;
	private transient DataWorkshop m_dw;
	private CandidatesBasedAlgorithm m_algortihm;

	/**
	 * OptimizedDynamicSet constructor
	 * @param dw - DataWorkshop
	 * @param candidates - Candidates for highest contribution.
	 * @throws Exception - Exception is thrown if there are candidates of invalid type.
	 */
	public OptimizedDynamicBetweennessSet(DataWorkshop dw, FastList<Index> candidates)
	{
		m_dw = dw;
		m_candidates = candidates;
		m_algortihm = new CandidatesBasedAlgorithm(m_dw, m_candidates.toArray());
	}
	
	public OptimizedDynamicBetweennessSet(OptimizedDynamicBetweennessSet other) {
		super(other);
		m_dw = other.m_dw;
		m_candidates = new FastList<Index>();
		m_candidates.addAll(other.m_candidates);
		m_algortihm = other.m_algortihm.clone();
	}

	@Override
	public double getContribution(Index v)
	{
		return m_algortihm.getBetweenness(v.intValue());
	}

	@Override
	public double getContribution(Object[] group) 
	{
		return CandidatesBasedAlgorithm.calculateGB(m_algortihm.getDataWorkshop(), group, new DummyProgress(), 1);
	}

	@Override
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
		return m_algortihm.getPairBetweenness(e.getV0().intValue(), e.getV1().intValue());
	}


	@Override
	public double getGroupCentrality(){	
		return m_algortihm.getNormalizedGroupBetweenness();	
	}

	@Override
	public void remove(Index v)
	{
		super.remove(v);
		FastList<Index> newGroup = new FastList<Index>(getVertexMembers());
		newGroup.remove(v);
		updateAlgorithm(newGroup);
	}

	@Override
	public void remove(FastList<Index> group) 
	{
		super.remove(group);
		FastList<Index> newGroup = new FastList<Index>(getVertexMembers());
        newGroup.removeAll(group);
		updateAlgorithm(newGroup);
	}

	private void updateAlgorithm(FastList<Index> newGroup) 
	{
		m_algortihm = new CandidatesBasedAlgorithm(m_dw, m_candidates.toArray());
		
		for (FastList.Node<Index> v = newGroup.head(), end = newGroup.tail(); (v = v.getNext()) != end;)
		{
			add(v.getValue());
		}
	}
	
	@Override
	public void add(Index v) {
		super.add(v);
		m_algortihm.addMember(v);
	}
	
	@Override
	public void add(FastList<Index> group) {
		for (Index v : group) add(v);
	}
	
	@Override
	public void add(AbstractSimpleEdge<Index,BasicVertexInfo> e) {
		super.add(e);
		m_algortihm.addMember(e);
	}
	
	public FastList<Index> getCandidates(){
		return m_candidates;	
	}

	@Override
	public OptimizedDynamicBetweennessSet clone() {
		return new OptimizedDynamicBetweennessSet(this);
	}
}
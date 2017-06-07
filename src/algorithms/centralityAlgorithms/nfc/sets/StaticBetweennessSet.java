package algorithms.centralityAlgorithms.nfc.sets;


import javolution.util.Index;
import server.common.DummyProgress;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.nfc.CandidatesBasedNFC;
import algorithms.centralityAlgorithms.nfc.preprocessing.DataWorkshopNFC;


public class StaticBetweennessSet extends BasicSet
{
	protected transient DataWorkshopNFC m_dw;
	
	/**
	 * StaticSet constructor
	 * @param dw - DataWorkshop
	 */
	public StaticBetweennessSet(DataWorkshopNFC dw)
	{	
		m_dw = dw;	
	}
	protected StaticBetweennessSet(StaticBetweennessSet other) {
		super(other);
		m_dw=other.m_dw;		
	}
	
	@Override
	public double getContribution(Index v){	
		return m_dw.getBetweenness(v.intValue());	
	}

	@Override
	public double getContribution(Object[] group) 
	{
		return CandidatesBasedNFC.calculateGB(m_dw, group, new DummyProgress(), 1);
	}

	@Override
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
		return m_dw.getPairBetweenness(e.getV0().intValue(), e.getV1().intValue());
	}


	@Override
	public double getGroupCentrality()
	{            
        Object[] group = new Object[m_vertices.size() + m_edges.size()];
        System.arraycopy(m_vertices.toArray(), 0, group, 0, m_vertices.size());
        System.arraycopy(m_edges.toArray(), 0, group, m_vertices.size(), m_edges.size());
		return CandidatesBasedNFC.calculateGB(m_dw,group, new DummyProgress(), 1);            		
	}

	@Override
	public StaticBetweennessSet clone() {
		return new StaticBetweennessSet(this);
	}
}
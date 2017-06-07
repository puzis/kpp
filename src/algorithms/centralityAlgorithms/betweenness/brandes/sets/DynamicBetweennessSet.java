package algorithms.centralityAlgorithms.betweenness.brandes.sets;

import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;

public class DynamicBetweennessSet extends StaticBetweennessSet {

	public DynamicBetweennessSet(DataWorkshop dw) {
		super(dw);
	}
	
	protected DynamicBetweennessSet(DynamicBetweennessSet other){
		super(other);
	}
	
	public DynamicBetweennessSet clone(){
		return new DynamicBetweennessSet(this);
	}
	
	
	
	@Override
	public double getContribution(Index v){
        Object[] group = new Object[m_vertices.size() + m_edges.size()+1];
        System.arraycopy(m_vertices.toArray(), 0, group, 0, m_vertices.size());
        System.arraycopy(m_edges.toArray(), 0, group, m_vertices.size(), m_edges.size());
        group[group.length-1]=v;
    	CandidatesBasedAlgorithm algorithm = new CandidatesBasedAlgorithm(m_dw, group);
    	for (int i=0;i<group.length-1;i++)
    		algorithm.addMember(group[i]);
    	return algorithm.getBetweenness(v.intValue());
	}

	/**
	 * not tested.
	 */
	@Override
	public double getContribution(Object[] newgroup) 
	{
        Object[] group = new Object[m_vertices.size() + m_edges.size()+newgroup.length];
        System.arraycopy(m_vertices.toArray(), 0, group, 0, m_vertices.size());
        System.arraycopy(m_edges.toArray(), 0, group, m_vertices.size(), m_edges.size());
        System.arraycopy(newgroup, 0, group, m_vertices.size()+m_edges.size(), newgroup.length);
    	CandidatesBasedAlgorithm algorithm = new CandidatesBasedAlgorithm(m_dw, group);
    	for (int i=0;i<group.length-newgroup.length;i++)
    		algorithm.addMember(group[i]);
    	double gbc1 = algorithm.getGroupBetweenness();
    	for (int i=group.length-newgroup.length;i<group.length;i++)
    		algorithm.addMember(group[i]);
    	double gbc2 = algorithm.getGroupBetweenness();
    	return gbc2-gbc1;    	
	}

	@Override
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
        Object[] group = new Object[m_vertices.size() + m_edges.size()+1];
        System.arraycopy(m_vertices.toArray(), 0, group, 0, m_vertices.size());
        System.arraycopy(m_edges.toArray(), 0, group, m_vertices.size(), m_edges.size());
        group[group.length-1]=e;
    	CandidatesBasedAlgorithm algorithm = new CandidatesBasedAlgorithm(m_dw, group);
    	for (int i=0;i<group.length-1;i++)
    		algorithm.addMember(group[i]);
    	return algorithm.getPairBetweenness(e.getV0().intValue(),e.getV1().intValue());
	}


}

package algorithms.centralityAlgorithms.nfc.sets;

import algorithms.centralityAlgorithms.nfc.CandidatesBasedNFC;
import algorithms.centralityAlgorithms.nfc.preprocessing.DataWorkshopNFC;
import javolution.util.Index;
import server.common.DummyProgress;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public class DynamicBetweennessSet extends StaticBetweennessSet {

	public DynamicBetweennessSet(DataWorkshopNFC dw) {
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
        
		Object[] group = new Object[m_vertices.size()];
        System.arraycopy(m_vertices.toArray(), 0, group, 0, m_vertices.size());
        
		double oldGNFC = CandidatesBasedNFC.calculateGB(m_dw, group, new DummyProgress(), 1);
		
		group = new Object[m_vertices.size() + 1];
        System.arraycopy(m_vertices.toArray(), 0, group, 0, m_vertices.size());
        group[group.length-1]=v;
    	
        double newGNFC = CandidatesBasedNFC.calculateGB(m_dw, group, new DummyProgress(), 1);
        
    	return newGNFC - oldGNFC;
	}

	/**
	 * not tested.
	 */
	@Override
	public double getContribution(Object[] newgroup) 
	{
		Object[] group = new Object[m_vertices.size()];
        System.arraycopy(m_vertices.toArray(), 0, group, 0, m_vertices.size());
        
        double oldGNFC = CandidatesBasedNFC.calculateGB(m_dw, group, new DummyProgress(), 1);
        
        group = new Object[m_vertices.size() + newgroup.length];
        System.arraycopy(m_vertices.toArray(), 0, group, 0, m_vertices.size());
        System.arraycopy(newgroup, 0, group, m_vertices.size(), newgroup.length);
        
        double newGNFC = CandidatesBasedNFC.calculateGB(m_dw, group, new DummyProgress(), 1);
        
    	return newGNFC - oldGNFC;    	
	}

	@Override
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
    	return 0;
	}


}

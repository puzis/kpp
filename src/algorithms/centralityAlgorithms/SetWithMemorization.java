package algorithms.centralityAlgorithms;

import java.util.Arrays;

import javolution.util.FastList;
import javolution.util.Index;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public class SetWithMemorization implements BasicSetInterface {

    protected int m_groupVersion;
    protected int m_centralityVersion;
    protected double m_centralityValue;
    protected double[] m_contributionVersions;
    protected double[] m_contributionValues;

    protected BasicSetInterface m_theset;
    
    public SetWithMemorization(BasicSetInterface theset,int numberOfVertices){
    	m_theset=theset;
    	m_groupVersion=0;
        m_centralityVersion=-1;
    	m_contributionValues = new double[numberOfVertices];
    	m_contributionVersions = new double[numberOfVertices];
    	Arrays.fill(m_contributionVersions, -1);
    }
    
	@Override
	public SetWithMemorization clone() {
		return new SetWithMemorization(m_theset.clone(),m_contributionValues.length);		
	}

	@Override
	public void add(Index v) {
		m_theset.add(v);
		m_groupVersion+=1;		
	}

	@Override
	public void add(FastList<Index> group) {
		m_theset.add(group);
		m_groupVersion+=1;				
	}

	@Override
	public void add(AbstractSimpleEdge<Index,BasicVertexInfo> e) {
		m_theset.add(e);
		m_groupVersion+=1;				
	}

	@Override
	public double getContribution(Index v) {
		int i = v.intValue();
		if (m_groupVersion > m_contributionVersions[i]){
			m_contributionValues[i]=m_theset.getContribution(v);
			m_contributionVersions[i]=m_groupVersion;
		}
		return m_contributionValues[i];
	}

	@Override
	public double getContribution(Object[] group) {
		throw new NotImplementedException();
	}

	@Override
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e) {
		throw new NotImplementedException();
	}

	@Override
	public double getGroupCentrality() {
		if (m_groupVersion > m_centralityVersion){
			m_centralityValue=m_theset.getGroupCentrality();
			m_centralityVersion=m_groupVersion;
		}
		return m_centralityValue;
	}

	@Override
	public FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> getLinkMembers() {
		return m_theset.getLinkMembers();
	}

	@Override
	public FastList<Index> getVertexMembers() {
		return m_theset.getVertexMembers();
	}

	@Override
	public boolean isMember(Index v) {
		return m_theset.isMember(v);
	}

	@Override
	public boolean isMember(AbstractSimpleEdge<Index,BasicVertexInfo> e) {
		return m_theset.isMember(e);
	}

	@Override
	public void remove(Index v) {
		m_theset.remove(v);
		m_groupVersion+=1;				
	}

	@Override
	public void remove(FastList<Index> group) {
		m_theset.remove(group);
		m_groupVersion+=1;						
	}

	@Override
	public void remove(AbstractSimpleEdge<Index,BasicVertexInfo> e) {
		m_theset.remove(e);
		m_groupVersion+=1;				
	}

	@Override
	public int size() {
		return m_theset.size();
	}

}

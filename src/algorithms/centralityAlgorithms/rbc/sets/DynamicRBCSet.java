package algorithms.centralityAlgorithms.rbc.sets;

import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.rbc.FasterGRBC;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

import javolution.util.FastList;
import javolution.util.Index;


public class DynamicRBCSet extends BasicSet
{
	protected transient FasterGRBC m_grbc;
	
	/**
	 * StaticSet constructor
	 * @param dw - DataWorkshop
	 */
	public DynamicRBCSet(FasterGRBC grbc)
	{	
		m_grbc = grbc;	
	}
	
	protected DynamicRBCSet(DynamicRBCSet other) {
		super(other);
		m_grbc=other.m_grbc;		
	}
	
	@Override
	public double getContribution(Index v){	
		
		double current = this.getGroupCentrality();
		this.add(v);
		double updated = this.getGroupCentrality();
		this.remove(v);
		return updated - current;
	}

	@Override
	public double getContribution(Object[] newgroup) 
	{
		FastList<Index> newgrouplist = new FastList<Index>(newgroup.length);
		for (Object v: newgroup){
			newgrouplist.add((Index)v);
		}

		return this.getContribution(newgrouplist);
	}

	public double getContribution(FastList<Index> newgroup) 
	{
		double current = this.getGroupCentrality();
		for (Index i: newgroup)
			this.add(i);
		double updated = this.getGroupCentrality();
		for (Index i: newgroup)
			this.remove(i);
		return updated - current;
	}
	
	@Override
	public double getContribution(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
		throw new NotImplementedException();
	}


	@Override
	public double getGroupCentrality()
	{            
		return this.m_grbc.getBetweeness(this.m_vertices);
	}

	@Override
	public DynamicRBCSet clone() {
		return new DynamicRBCSet(this);
	}
}
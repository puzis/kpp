package algorithms.dfbnb.samples;

import java.util.Vector;

import javolution.util.Index;
import algorithms.centralityAlgorithms.BasicSetInterface;
import algorithms.dfbnb.InfGroup;



public class BiModalGroup extends algorithms.dfbnb.AbsGroup<Index> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1306890654675994051L;
	
	
	protected BasicSetInterface utilitySet;
	protected BasicSetInterface costSet;
	
	public BiModalGroup(BasicSetInterface utilitySet, BasicSetInterface costSet){
		this.utilitySet=utilitySet;
		this.costSet=costSet;
	}
	
	protected BiModalGroup(BiModalGroup other) {
		this.costSet = other.costSet.clone();
		this.utilitySet = other.utilitySet.clone();
		this.m_groupMembers = new Vector<Index>();
		this.m_groupMembers.addAll(other.m_groupMembers);
	}

	@Override
	public InfGroup<Index> clone() {
		return new BiModalGroup(this);
	}

	@Override
	public Double getCost() {
		return costSet.getGroupCentrality();
	}

	@Override
	public Double getCostOf(Index member) {
		return costSet.getContribution(member);
	}

	@Override
	public Double getUtility() {
		return utilitySet.getGroupCentrality();
	}

	@Override
	public Double getUtilityOf(Index member) {
		return utilitySet.getContribution(member);
	}
	
	@Override
	public void add(Index v){
		super.add(v);
		utilitySet.add(v);
		costSet.add(v);
	}

}

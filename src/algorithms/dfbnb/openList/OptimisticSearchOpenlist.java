package algorithms.dfbnb.openList;

import java.util.Iterator;

import algorithms.dfbnb.BudgetedUtilitySearch_ol;
import algorithms.dfbnb.InfNode;

public class OptimisticSearchOpenlist<E> implements InfOpenList<InfNode<E>>{
	private static final long serialVersionUID = OptimisticSearchOpenlist.class.hashCode();
	private InfOpenList<InfNode<E>> admissibleOpenlist;
	private InfOpenList<InfNode<E>> inadmissibleOpenlist;
	private AbstractValueComparator<E> admissibleComparator;
	private AbstractValueComparator<E> inadmissibleComparator;
	private BudgetedUtilitySearch_ol<E> implementation;
	
	public OptimisticSearchOpenlist(final BudgetedUtilitySearch_ol<E> implementation,final double weight)
	{		
		this.implementation = implementation;
		this.admissibleComparator = new AdmissibleComparator<E>();
		this.inadmissibleComparator = new WeightedComparator<E>(weight);
		this.admissibleOpenlist = new AbstractInfOpenList<E>(implementation,admissibleComparator);
		this.inadmissibleOpenlist = new AbstractInfOpenList<E>(implementation,inadmissibleComparator); 
	}
	
	/**
	 * Returns the next node to expand, and remove it from openlist.
	 */
	@Override
	public InfNode<E> getNext() {
		double bestUtility = this.implementation.getBestGroupUtility();
		InfNode<E> bestNode = this.inadmissibleOpenlist.peek();
		double bestInadmissibleValue = this.inadmissibleComparator.getNodeValue(bestNode);
		if(bestInadmissibleValue>bestUtility){
			this.inadmissibleOpenlist.getNext();
			this.admissibleOpenlist.removeItem(bestNode);
		}
		else{
			bestNode = this.admissibleOpenlist.getNext();
			this.inadmissibleOpenlist.removeItem(bestNode);
		}
		return bestNode;
	}
	
	@Override
	public boolean insert(InfNode<E> newNode, boolean accept) {
		this.inadmissibleOpenlist.insert(newNode, accept);
		return this.admissibleOpenlist.insert(newNode, accept);
	}
	
	@Override
	public boolean isEmpty() {
		return this.admissibleOpenlist.isEmpty();
	}
	@Override
	public InfNode<E> peek() {
		double bestUtility = this.implementation.getBestGroupUtility();
		InfNode<E> bestNode = this.inadmissibleOpenlist.peek();
		double bestInadmissibleValue = this.inadmissibleComparator.getNodeValue(bestNode);
		if(bestInadmissibleValue>bestUtility){
			return bestNode;
		}
		else{
			return this.admissibleOpenlist.peek();
		}
	}
	@Override
	public boolean removeItem(InfNode<E> toRemove) {
		this.inadmissibleOpenlist.removeItem(toRemove);
		return this.admissibleOpenlist.removeItem(toRemove);
	}
	@Override
	public int size() {
		return this.admissibleOpenlist.size();
	}
	
	@Override
	public void updateBestUtility(double groupUtility) {
	}
	
	@Override
	public Iterator<InfNode<E>> iterator() {
		return this.admissibleOpenlist.iterator();
	}
	
	/**
	 * Clears all items from the openlist
	 */
	@Override
	public void clear(){
		this.admissibleOpenlist.clear();
		this.inadmissibleOpenlist.clear();
	}
	
}
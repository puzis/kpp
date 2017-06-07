package algorithms.dfbnb.openList;

import algorithms.dfbnb.BudgetedUtilitySearch_ol;
import algorithms.dfbnb.InfNode;

/**
 * Weighted A* open list.
 * @author Roni
 */
public class WeightedOpenlist<E> extends AbstractInfOpenList<E>{
	
	private static final long serialVersionUID = WeightedOpenlist.class.hashCode();
	private double weight;
	public WeightedOpenlist(final BudgetedUtilitySearch_ol<E> implementation, double weight)
	{
		super(implementation,new WeightedComparator<E>(weight));
		this.weight = weight;
	}
	
	public double getHeuristic(InfNode<E> pNode){
		return pNode.getG()+pNode.getH()*this.weight;
	}
}

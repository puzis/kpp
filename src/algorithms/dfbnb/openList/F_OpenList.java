package algorithms.dfbnb.openList;

import algorithms.dfbnb.BudgetedUtilitySearch_ol;
import algorithms.dfbnb.InfNode;

public class F_OpenList<E> extends AbstractInfOpenList<E> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public F_OpenList(final BudgetedUtilitySearch_ol<E> implementation)
	{
		super(implementation,new AdmissibleComparator<E>());
	}
	
	public double getHeuristic(InfNode<E> pNode){
		double f = pNode.getF();
		if (pNode.getG()+pNode.getH()!=f)
			System.out.println("PROBLEM!");
		return pNode.getF();
	}	
}

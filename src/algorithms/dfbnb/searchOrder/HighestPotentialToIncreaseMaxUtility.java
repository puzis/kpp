package algorithms.dfbnb.searchOrder;

import java.util.Comparator;

import algorithms.dfbnb.BudgetedUtilitySearch;
import algorithms.dfbnb.InfNode;

public class HighestPotentialToIncreaseMaxUtility<E> implements Comparator<InfNode<E>> {
	BudgetedUtilitySearch<E> m_search;
	public HighestPotentialToIncreaseMaxUtility(BudgetedUtilitySearch<E> search){
		m_search = search;
	}	
	@Override
	public int compare(InfNode<E> arg0, InfNode<E> arg1) {
		double p0 = m_search.getUtilityH(arg0)/(m_search.getBestGroupUtility() - m_search.getUtilityG(arg1));
		double p1 = m_search.getUtilityH(arg1)/(m_search.getBestGroupUtility() - m_search.getUtilityG(arg1));		
		return (int)Math.signum(p1 - p0);
	}
}

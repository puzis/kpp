package algorithms.dfbnb.searchOrder;

import java.util.Comparator;

import algorithms.dfbnb.BudgetedUtilitySearch;
import algorithms.dfbnb.InfNode;

public class HighestFFirst<E> implements Comparator<InfNode<E>> {
	BudgetedUtilitySearch<E> m_search;
	public HighestFFirst(BudgetedUtilitySearch<E> search){
		m_search = search;
	}	
	@Override
	public int compare(InfNode<E> arg0, InfNode<E> arg1) {
		return (int)Math.signum(m_search.getUtilityF(arg1)- m_search.getUtilityF(arg0));
	}
}

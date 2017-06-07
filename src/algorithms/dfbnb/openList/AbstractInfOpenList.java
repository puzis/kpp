package algorithms.dfbnb.openList;

import java.util.Comparator;
import java.util.PriorityQueue;

import algorithms.dfbnb.BudgetedUtilitySearch_ol;
import algorithms.dfbnb.InfNode;

/**
 * Abstract openlist class, allowing subclasses and usages by setting desired comparators.
 * @author sternron
 *
 * @param <E>
 */
public class AbstractInfOpenList<E> extends PriorityQueue<InfNode<E>> implements InfOpenList<InfNode<E>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_INITIAL_CAPACITY = 11;
	BudgetedUtilitySearch_ol<E> m_implementation;
		
	public AbstractInfOpenList(final BudgetedUtilitySearch_ol<E> pImplementation, Comparator<InfNode<E>> pComparator)
	{
		super(DEFAULT_INITIAL_CAPACITY,pComparator);
		m_implementation = pImplementation;
	}

	@Override
	public InfNode<E> getNext() {
		return poll();
	}

	@Override
	public boolean insert(InfNode<E> newNode, boolean accept) {
		if (accept)
			newNode.setG(newNode.getGroup().getUtility());
		newNode.setH(m_implementation.getUtilityH(newNode));
		return add(newNode);
	}

	@Override
	public boolean removeItem(InfNode<E> toRemove) {
		return remove(toRemove);
	}

	@Override
	public void updateBestUtility(double groupUtility) {
		
	}
}
package algorithms.dfbnb.elementOrder;

import algorithms.dfbnb.InfNode;

public class NoOrder<E> implements ElementOrderingStrategyInterface<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public E findBestCandidate(InfNode<E> node) {
		return node.getCandidates().firstElement();
	}

}

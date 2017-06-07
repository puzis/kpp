package algorithms.dfbnb.heuristics;

import algorithms.dfbnb.InfNode;

public class TrivialUtilityUpperBound<E> implements UtilityHeuristicFunction<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public double h(double budget, InfNode<E> node) {
		return Double.POSITIVE_INFINITY;
	}
}

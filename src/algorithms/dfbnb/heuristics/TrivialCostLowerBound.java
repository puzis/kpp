package algorithms.dfbnb.heuristics;

import algorithms.dfbnb.InfNode;

public class TrivialCostLowerBound<E> implements CostHeuristicFunction<E>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public double h(double targetUtility, InfNode<E> node) {
		return 0;
	}

}

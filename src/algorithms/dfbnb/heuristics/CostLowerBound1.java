package algorithms.dfbnb.heuristics;

import java.util.Collection;

import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;

public class CostLowerBound1<E> implements CostHeuristicFunction<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public double h(double targetUtility, InfNode<E> node) {
		double h = 0;
		if (node.getCandidates().size() > 0) {
			double bestRatio = 0.0;
			Collection<E> candidates = node.getCandidates();
			InfGroup<E> group = node.getGroup();
			for (E element: candidates){
				double currentUtility = group.getUtilityOf(element);
				double currentCost = group.getCostOf(element);
				if (currentCost == 0)
					return Double.POSITIVE_INFINITY;
				double currentRatio = currentCost / currentUtility;
				if (currentRatio < bestRatio)
					bestRatio = currentRatio;
			}
			h = bestRatio * (targetUtility - group.getUtility());
		}
		return h;
	}

}

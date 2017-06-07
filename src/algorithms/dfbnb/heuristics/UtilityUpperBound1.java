package algorithms.dfbnb.heuristics;

import java.util.Collection;

import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;

public class UtilityUpperBound1<E> implements UtilityHeuristicFunction<E> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public double h(double budget, InfNode<E> node) {		
		double h = 0;
		if (node.getCandidates().size() > 0) {
			Double bestRatio = 0.0;
			Collection<E> candidates = node.getCandidates();
			InfGroup<E> group = node.getGroup();
			for (E element: candidates){
				Double currentUtility = group.getUtilityOf(element);
				Double currentCost = group.getCostOf(element);
				if (currentCost == 0)
					return Double.POSITIVE_INFINITY;
				Double currentRatio = currentUtility / currentCost;
				if (currentRatio > bestRatio)
					bestRatio = currentRatio;
			}
			h = bestRatio * (budget - group.getCost());
		}
		return h;
	}
}

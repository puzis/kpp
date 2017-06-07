package algorithms.dfbnb.heuristics;

import java.util.Collection;
import java.util.PriorityQueue;

import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;

import common.ComparablePair;

public class UtilityUpperBound2<E> implements UtilityHeuristicFunction<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public double h(double budget, InfNode<E> node) {		
		double currentUtility = 0;
		double currentCost = 0;
		if (node.getCandidates().size() > 0) {
			Collection<E> candidates = node.getCandidates();
			InfGroup<E> group = node.getGroup();
			PriorityQueue<ComparablePair<Double,Double>> queue = new PriorityQueue<ComparablePair<Double,Double>>();		
			for (E element: candidates){
				currentUtility = group.getUtilityOf(element);
				currentCost = group.getCostOf(element);
				//order the elements' cost-profit by maximal utility per cost
				//in case of tie breaking let the higher cost win to speedup the next loop 
				queue.add(new ComparablePair<Double, Double>(- currentUtility / currentCost ,-currentCost)); 
			}
			
			currentUtility = 0;
			double remainingBudget = (budget - node.getGroup().getCost());
			double upc = 0;
			while ((remainingBudget>0) & (queue.size()>0)){				
				//buy the next most profitable element
				ComparablePair<Double,Double> upc_c = queue.poll();
				upc = -upc_c.getValue1();
				double c = Math.min(remainingBudget,-upc_c.getValue2()); 				
				remainingBudget-=c;				
				currentUtility+=c*upc;				
			}
			assert(remainingBudget==0);
		}
		return currentUtility;
	}

}

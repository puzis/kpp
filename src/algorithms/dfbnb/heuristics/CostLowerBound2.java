package algorithms.dfbnb.heuristics;

import java.util.Collection;
import java.util.PriorityQueue;

import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;

import common.ComparablePair;

public class CostLowerBound2<E> implements CostHeuristicFunction<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public double h(double targetUtility, InfNode<E> node) {		
		double currentUtility = 0;
		double currentCost = 0;
		if (node.getCandidates().size() > 0) {
			Collection<E> candidates = node.getCandidates();
			InfGroup<E> group = node.getGroup();
			PriorityQueue<ComparablePair<Double,Double>> queue = new PriorityQueue<ComparablePair<Double,Double>>();		
			for (E element: candidates){
				currentUtility = group.getUtilityOf(element);
				currentCost = group.getCostOf(element);
				//order the elements' cost-profit by minimal cost per utility 
				//in case of tie breaking let the higher utility win to speedup the next loop 
				queue.add(new ComparablePair<Double, Double>(currentCost /currentUtility ,-currentUtility)); 
			}
			
			currentUtility = 0;
			currentCost = 0;
			double remainingUtility = (targetUtility - node.getGroup().getUtility());
			double cpu = 0;
			while ((remainingUtility>0) & (queue.size()>0)){				
				//buy the next most profitable element
				ComparablePair<Double,Double> cpu_u = queue.poll();
				cpu = cpu_u.getValue1();
				double u = Math.min(remainingUtility,-cpu_u.getValue2()); 				
				remainingUtility-=u;				
				currentCost+=cpu*u;				
			}
			assert(remainingUtility==0);
		}
		return currentCost;
	}

}

package algorithms.dfbnb;

import java.io.Serializable;

import algorithms.dfbnb.elementOrder.ElementOrderingStrategyInterface;
import algorithms.dfbnb.heuristics.CostHeuristicFunction;
import algorithms.dfbnb.heuristics.UtilityHeuristicFunction;

public interface InfSearch<E> extends Serializable {

	public abstract void setElementOrderingStrategy(
			ElementOrderingStrategyInterface<E> nodeOrdering);

	public abstract void setUtilityHeuristic(UtilityHeuristicFunction<E> h);

	public abstract void setCostHeuristic(CostHeuristicFunction<E> h);

	public abstract void offerGroup(InfGroup<E> group);

	public abstract InfGroup<E> getBestGroup();

	public abstract int getNodeCheckCounter();

	public abstract InfGroup<E> execute();

	public abstract InfGroup<E> execute(int maxNumOfNodes);

	public abstract void expandNextNode();

	public abstract boolean isSearchDone();

	public abstract InfGroup<E> getCurrentGroup();

	public abstract boolean isFeasible(InfGroup<E> group);

	public abstract boolean isBest(InfGroup<E> group);

	public abstract void clear(); // Clear used memory
}
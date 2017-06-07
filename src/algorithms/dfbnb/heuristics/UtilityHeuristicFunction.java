package algorithms.dfbnb.heuristics;

import java.io.Serializable;

import algorithms.dfbnb.InfNode;

public interface UtilityHeuristicFunction<E> extends Serializable {
	double h(double budget, InfNode<E> node);
}

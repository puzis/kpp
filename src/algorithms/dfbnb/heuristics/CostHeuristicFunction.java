package algorithms.dfbnb.heuristics;

import java.io.Serializable;

import algorithms.dfbnb.InfNode;

public interface CostHeuristicFunction<E> extends Serializable {
	double h(double targetUtility, InfNode<E> node);
}

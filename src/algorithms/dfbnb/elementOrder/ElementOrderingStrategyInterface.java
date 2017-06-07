package algorithms.dfbnb.elementOrder;

import java.io.Serializable;

import algorithms.dfbnb.InfNode;

public interface ElementOrderingStrategyInterface<E> extends Serializable {
	E findBestCandidate(InfNode<E> node);
}

package algorithms.dfbnb.searchOrder;

import java.util.Comparator;

import algorithms.dfbnb.InfNode;

public class FirstCreatedFirstExpanded<E> implements Comparator<InfNode<E>>{
	@Override
	public int compare(InfNode<E> o1, InfNode<E> o2) {
		return o1.getID() - o2.getID();
	}
}

package algorithms.dfbnb.openList;

import java.util.Stack;

import algorithms.dfbnb.InfNode;

public class LIFO_OpenList<E> extends Stack<InfNode<E>> implements InfOpenList<InfNode<E>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public InfNode<E> getNext() {
		return pop();
	}

	@Override
	public boolean insert(InfNode<E> newNode, boolean accept) {
		return (push(newNode)!=null);
	}

	@Override
	public boolean removeItem(InfNode<E> toRemove) {
		return remove(toRemove);
	}

	@Override
	public void updateBestUtility(double groupUtility) {
		
	}

}

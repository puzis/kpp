package algorithms.dfbnb.openList;

import java.util.Date;
import java.util.Random;
import java.util.Vector;

import algorithms.dfbnb.InfNode;

public class Random_OpenList<E> extends Vector<InfNode<E>> implements InfOpenList<InfNode<E>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public InfNode<E> getNext() {
		int index=(new Random((new Date()).getTime())).nextInt(size());
		return remove(index);
	}

	@Override
	public boolean insert(InfNode<E> newNode, boolean accept) {
		return add(newNode);
	}

	@Override
	public boolean removeItem(InfNode<E> toRemove) {
		return remove(toRemove);
	}

	@Override
	public InfNode<E> peek() {
		//TODO:BUG! peek and pull (getNext) should return the same item!!!  
		return get(0);
	}
	
	@Override
	public void updateBestUtility(double groupUtility) {		
	}
}

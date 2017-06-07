package algorithms.dfbnb.openList;

import java.io.Serializable;

public interface InfOpenList<E> extends Iterable<E>, Serializable{
	
	public boolean insert(E newNode, boolean accept);
	
	public E getNext();
	
	public E peek();
	
	public int size();
	
	public boolean isEmpty();
	
	public boolean removeItem(E toRemove);

	public void updateBestUtility(double groupUtility);
	//TODO: ? add updateBestCost method 

	public void clear(); // Clears all items from the openlist
}

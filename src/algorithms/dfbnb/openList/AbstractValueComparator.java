package algorithms.dfbnb.openList;

import java.io.Serializable;
import java.util.Comparator;

import algorithms.dfbnb.InfNode;

/**
 * Abstract comparator based on node value function.
 * @author Roni
 *
 * @param <E>
 */
public abstract class AbstractValueComparator<E> implements Comparator<InfNode<E>>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int compare(InfNode<E> node1, InfNode<E> node2)
	{
		double fNode1=this.getNodeValue(node1);
		double fNode2=this.getNodeValue(node2);
		return (int)Math.signum(fNode2-fNode1);
	}
	
	abstract public double getNodeValue(InfNode<E> pNode);
}

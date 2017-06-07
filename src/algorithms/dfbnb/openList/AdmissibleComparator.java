package algorithms.dfbnb.openList;


import algorithms.dfbnb.InfNode;

/**
 * An admissible comparator, using A* cost function of f=g+h.
 * @author sternron
 *
 * @param <E>
 */
public class AdmissibleComparator<E> extends AbstractValueComparator<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AdmissibleComparator(){}
	
	@Override
	public double getNodeValue(InfNode<E> pNode){
		return pNode.getF();
	}
}

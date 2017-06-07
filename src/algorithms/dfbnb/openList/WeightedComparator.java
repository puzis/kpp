package algorithms.dfbnb.openList;

import algorithms.dfbnb.InfNode;

/**
 * Comparing objects according to a weighted version of A* cost function: f=g+w*h
 * using standard A* notation of g and h functions. 
 * 
 * @author Roni
 *
 * @param <E>
 */
public class WeightedComparator<E> extends AbstractValueComparator<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double weight;
	
	public WeightedComparator(final double weight){
		this.weight = weight;
	}
	
	@Override
	public double getNodeValue(InfNode<E> pNode){
		return pNode.getG()+pNode.getH()*this.weight;
	}
}

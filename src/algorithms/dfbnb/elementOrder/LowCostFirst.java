package algorithms.dfbnb.elementOrder;

import java.util.Vector;

import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;

public class LowCostFirst<E> implements ElementOrderingStrategyInterface<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public E findBestCandidate(InfNode<E> node) {
		Vector<E> candidates = node.getCandidates();
		InfGroup<E> group = node.getGroup();
        E bestE=candidates.firstElement();
        double minC=group.getCostOf(bestE);
        for (E e:candidates){
        	double c = group.getCostOf(e);
            if ((c < minC) || ((c==minC) && (e.hashCode()<bestE.hashCode()))){
                bestE=e;
                minC=c;
            }   
        }
        return bestE;
	}
}

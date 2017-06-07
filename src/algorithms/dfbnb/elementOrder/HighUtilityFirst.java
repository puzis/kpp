package algorithms.dfbnb.elementOrder;

import java.util.Vector;

import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;

public class HighUtilityFirst<E> implements ElementOrderingStrategyInterface<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public E findBestCandidate(InfNode<E> node) {
		Vector<E> candidates = node.getCandidates();
		InfGroup<E> group = node.getGroup();
        E bestE=candidates.firstElement();
        double maxU=group.getUtilityOf(bestE);
        for (E e:candidates){
        	double u = group.getUtilityOf(e);
            if ((u > maxU) || ((u==maxU) && (e.hashCode()>bestE.hashCode()))){
                bestE=e;
                maxU=u;
            }   
        }
        return bestE;
	}

}

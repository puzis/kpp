package algorithms.dfbnb;

import java.util.Vector;

public class LDSNode<E> extends Node<E> implements Comparable<LDSNode<E>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int errors;
	protected double tieBreaker;

	public LDSNode(Vector<E> candidates, InfGroup<E> groupMember, double g, double h) {
		super(candidates, groupMember, g, h);
		errors=0;
	}
	
	public LDSNode(Vector<E> candidates, InfGroup<E> groupMember, int errors, double g, double h) {
		super(candidates, groupMember, g, h);
		this.errors=errors;
	}

	@Override
	public void accept(E candidate) {
		super.accept(candidate);
	}
	
	@Override
	public void reject(E candidate) {
		super.reject(candidate);
		errors++;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LDSNode<E> clone() {
		Vector<E> candidates = (Vector<E>)m_candidates.clone();
    	InfGroup<E> group = m_group.clone();
    	return new LDSNode<E>(candidates,group, errors++, m_g, m_h);		
	}

	public int getErrors() {
		return errors;
	}

	public double getTieBreaker() {
		return tieBreaker;
	}

	public void setTieBreaker(double tieBreaker) {
		this.tieBreaker = tieBreaker;
	}

	@Override
	public int compareTo(LDSNode<E> o) {
		if (getErrors()==o.getErrors())
		{
			double tNode1=getTieBreaker();
			double tNode2=o.getTieBreaker();
			return (int)Math.signum(tNode2-tNode1);
		}
		else
			return getErrors()-o.getErrors();
	}
	
	
}

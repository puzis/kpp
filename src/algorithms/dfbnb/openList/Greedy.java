package algorithms.dfbnb.openList;

import java.util.Iterator;

import algorithms.dfbnb.BudgetedUtilitySearch_ol;
import algorithms.dfbnb.InfNode;

import common.markerInterfaces.SerializableIterator;

public class Greedy<E> implements InfOpenList<InfNode<E>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	InfNode<E> m_next;
	
	public Greedy(final BudgetedUtilitySearch_ol<E> implementation)
	{
		
	}
	
	@Override
	public InfNode<E> getNext() {
		InfNode<E> result = m_next;
		m_next = null;
		return result;
	}

	@Override
	public boolean insert(InfNode<E> newNode, boolean accept) {
		m_next = newNode;
		return true;
	}

	@Override
	public boolean isEmpty() {		
		return m_next==null;
	}

	@Override
	public InfNode<E> peek() {		
		return m_next;
	}

	@Override
	public boolean removeItem(InfNode<E> toRemove) {
		if (m_next==null)
			return false;
		else if (m_next.equals(toRemove)){
			m_next=null;
			return true;
		}
		else 
			return false;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public void updateBestUtility(double groupUtility) {}

	@Override
	public Iterator<InfNode<E>> iterator() {
		Iterator<InfNode<E>> itr = new SerializableIterator<InfNode<E>>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			boolean m_eof=false;
			InfNode<E> m_nextItem=m_next;
			@Override
			public boolean hasNext() {
				return !m_eof && m_nextItem!=null;
			}

			@Override
			public InfNode<E> next() {
				m_eof=true;
				return m_nextItem;
			}

			@Override
			public void remove() {
				m_eof=true;
			}			
		};
		return itr;
	}

	/**
	 *  Clears all items from the openlist
	 */
	public void clear(){
		this.m_next=null;
	}
	
}

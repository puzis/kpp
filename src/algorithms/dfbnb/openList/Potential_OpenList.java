package algorithms.dfbnb.openList;

import algorithms.dfbnb.BudgetedUtilitySearch_ol;
import algorithms.dfbnb.InfNode;

import common.markerInterfaces.SerializableComparator;

public class Potential_OpenList<E> extends AbstractInfOpenList<E>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_INITIAL_CAPACITY = 11;
	private BudgetedUtilitySearch_ol<E> m_implementation;
	
	public Potential_OpenList(final BudgetedUtilitySearch_ol<E> implementation)
	{		
		super(implementation, new SerializableComparator<InfNode<E>>(){
					/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

					public int compare(InfNode<E> arg0, InfNode<E> arg1)
					{
						double LB = implementation.getBestGroupUtility();
						double h0 = arg1.getH();
						double g0 = arg1.getG();
						double p0 = h0 / (LB - g0);
				
						double h1 = arg1.getH();
						double g1 = arg1.getG();	
						double p1 = h1 / (LB - g1);
						
						double sign = Math.signum(p1 - p0);
						int result = (int)sign;
						return result;
					}
				});
		m_implementation = implementation;
	}

	@Override
	public void updateBestUtility(double groupUtility) {		
		Object[] tmp = this.toArray(); 
		this.clear();
		for (int i=0;i<tmp.length;i++)
			this.add((InfNode<E>)(tmp[i]));		
	}	
}

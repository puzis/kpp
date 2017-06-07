/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.dfbnb;


/**
 * Assumption: cost and utility are in range: [0,Inf) 
 */
public class CertifiedUtilitySearch<E> extends  BudgetedUtilitySearch<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Double m_certificate;


	public CertifiedUtilitySearch(InfNode<E> root, double budget) {
		super(root, budget);
		m_certificate = Double.NEGATIVE_INFINITY;
	}
	

	@Override
	public void expandNextNode() {
		super.expandNextNode();
		updateCertificate();
	}


	public Double getCertificate() {
		return m_certificate;
	}

	public void updateCertificate() {
		double m_GlobalUpperBound = m_bestGroupUtility;
		for (InfNode<E> node: m_openList){
			double fValue = getUtilityF(node);
			if (m_GlobalUpperBound < fValue)
				m_GlobalUpperBound = fValue;
		}
		if (m_GlobalUpperBound != 0)
			m_certificate = m_bestGroupUtility / m_GlobalUpperBound;
	}
	
}

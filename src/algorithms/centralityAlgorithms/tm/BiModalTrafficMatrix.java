package algorithms.centralityAlgorithms.tm;

public class BiModalTrafficMatrix extends AbsTrafficMatrix {

	private static final long serialVersionUID = 1L;

	protected int m_primaryModeTMSize = 0;
	protected AbsTrafficMatrix m_primaryModeTM = null;
	protected AbsTrafficMatrix m_secondaryModeTM = null;
	
	public BiModalTrafficMatrix(int size, int primaryModeTMSize, AbsTrafficMatrix primaryModeTM, AbsTrafficMatrix secondaryModeTM){
		m_matrixDimensions = size;
		m_primaryModeTMSize = primaryModeTMSize;
		m_primaryModeTM = primaryModeTM;
		m_secondaryModeTM = secondaryModeTM;
	}
	
	@Override
	public double getWeight(int i, int j) {
		if ((i<m_primaryModeTMSize) && (j<m_primaryModeTMSize)) 
			return m_primaryModeTM.getWeight(i, j);
        else 
        	return m_secondaryModeTM.getWeight(i,j);
	}

	@Override
	public void setWeight(int i, int j, double w) {
		throw new UnsupportedOperationException("Method setWeight is not supported in BiModalTrafficMatrix.");
	}

	@Override
	public void setAllWeights(double w) {
		throw new UnsupportedOperationException("Method setAllWeights is not supported in BiModalTrafficMatrix.");
	}

	@Override
	public void mul(double a) {
		m_primaryModeTM.mul(a);
		m_secondaryModeTM.mul(a);		
	}
}
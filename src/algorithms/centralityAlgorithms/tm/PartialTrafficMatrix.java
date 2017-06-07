package algorithms.centralityAlgorithms.tm;

public class PartialTrafficMatrix extends AbsTrafficMatrix{

	private static final long serialVersionUID = 1L;

	protected int m_commsize = 0;
	protected AbsTrafficMatrix m_baseTM = null;
	
	public PartialTrafficMatrix(int size, int commSize){
		m_matrixDimensions = size;
		m_commsize = commSize;
		m_baseTM = new DefaultTrafficMatrix(m_commsize);
	}

	public PartialTrafficMatrix(int size, int commSize, AbsTrafficMatrix baseTM){
		m_matrixDimensions = size;
		m_commsize = commSize;
		m_baseTM = baseTM;
	}
	
	public double getWeight(int i, int j) {
		if ((i<m_commsize) && (j<m_commsize)) 
			return m_baseTM.getWeight(i, j);
        else 
        	return 0;
	}

	@Override
	public void setWeight(int i, int j, double w) {
		throw new UnsupportedOperationException("Method setWeight is not supported in PartialTrafficMatrix.");
	}

	@Override
	public void setAllWeights(double w) {
		throw new UnsupportedOperationException("Method setAllWeights is not supported in PartialTrafficMatrix.");
	}

	@Override
	public void mul(double a) {
		m_baseTM.mul(a);
	}
}

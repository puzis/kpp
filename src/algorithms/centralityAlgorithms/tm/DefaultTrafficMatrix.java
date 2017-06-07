package algorithms.centralityAlgorithms.tm;

public class DefaultTrafficMatrix extends AbsTrafficMatrix{

	private static final long serialVersionUID = 1L;
	
	private double m_value = 1;

	public DefaultTrafficMatrix(int size){
		m_matrixDimensions = size;
	}
	public DefaultTrafficMatrix(int size, double value){
		m_matrixDimensions = size;
		m_value = value;
	}
	
	public double getWeight(int i, int j) {
		if (i == j) return 0;
        else return m_value;
	}

	@Override
	public void setWeight(int i, int j, double w) {
		throw new UnsupportedOperationException("Method setWeight is not supported in DefaultTrafficMatrix.");		
	}

	@Override
	public void setAllWeights(double w) {
		throw new UnsupportedOperationException("Method setAllWeights is not supported in DefaultTrafficMatrix.");
	}

	@Override
	public void mul(double a) {
		m_value*=a;				
	}
}

package algorithms.centralityAlgorithms.tm;

import java.io.Serializable;

public abstract class AbsTrafficMatrix implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected int m_matrixDimensions = 0;
	
        /**
         * @return the number of dimensions for this matrix, the size of the matrix is dimensions^2
         */
	public int getDimensions(){	
		return m_matrixDimensions;	
	}
	
	public abstract double getWeight(int i, int j);
	public abstract void setWeight(int i, int j, double w);
	public abstract void setAllWeights(double w);

	public abstract void mul(double a);
	
	
}

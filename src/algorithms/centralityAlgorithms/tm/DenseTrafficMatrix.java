package algorithms.centralityAlgorithms.tm;

import java.util.Arrays;

import topology.WeightsLoader;

import common.MatricesUtils;

public class DenseTrafficMatrix extends AbsTrafficMatrix{

	private static final long serialVersionUID = 1L;
	
	private double[][] m_trafficMatrix;
	
	public DenseTrafficMatrix(int dimensionSize){
		m_trafficMatrix = new double[dimensionSize][dimensionSize];
		m_matrixDimensions = dimensionSize;
	}
	
	public DenseTrafficMatrix(double[][] trafficMatrix){
		m_trafficMatrix = trafficMatrix;
		m_matrixDimensions = trafficMatrix.length;
	}
	
	public DenseTrafficMatrix(String trafficMatrix, int dimensionSize){
		if (trafficMatrix != null && !trafficMatrix.isEmpty())
			m_trafficMatrix = WeightsLoader.loadWeightsFromString(trafficMatrix, dimensionSize);
		else
			m_trafficMatrix = MatricesUtils.getDefaultWeights(dimensionSize);
		m_matrixDimensions = m_trafficMatrix.length;
	}
	
	public double getWeight(int i, int j) {
		return m_trafficMatrix[i][j];
	}

	public void setWeight(int i, int j, double w) {
		m_trafficMatrix[i][j] = w;
	}
	
	/**
	 * Set all weights in the matrix to the given value.
	 * @param w - weight to set.
	 */
	public void setAllWeights(double w){
		for (int row = 0; row < m_trafficMatrix.length; row++){
			Arrays.fill(m_trafficMatrix[row], w);
		}
	}

	@Override
	public void mul(double a) {
		for (int i=0;i<m_trafficMatrix.length;i++)
			for (int j=0;j<m_trafficMatrix[i].length;j++)
				m_trafficMatrix[i][j]*=a;
	}
	
	
	public double[][] getAllWeights(){
		return m_trafficMatrix;
	}
}
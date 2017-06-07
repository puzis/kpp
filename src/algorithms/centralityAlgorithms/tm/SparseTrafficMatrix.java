package algorithms.centralityAlgorithms.tm;

import java.util.HashMap;
import java.util.Map;

import topology.WeightsLoader;

public class SparseTrafficMatrix extends AbsTrafficMatrix {

	private static final long serialVersionUID = 1L;

	private Map<Integer, Map<Integer, Double>> m_trafficMatrix = new HashMap<Integer, Map<Integer, Double>>();
	
	public SparseTrafficMatrix(int dimensionSize){
		m_matrixDimensions = dimensionSize;
	}
	
	public SparseTrafficMatrix(String trafficMatrix, int dimensionSize){
		if (trafficMatrix != null && !trafficMatrix.isEmpty())
			m_trafficMatrix = WeightsLoader.loadWeightsFromString(dimensionSize, trafficMatrix);
		m_matrixDimensions = dimensionSize;
	}
	
	@Override
	public double getWeight(int i, int j) {
		if (m_trafficMatrix.get(i)==null || m_trafficMatrix.get(i).get(j)==null)
			return 0.0;
		return m_trafficMatrix.get(i).get(j).doubleValue();
	}

	@Override
	public void setWeight(int i, int j, double w) {
		Map<Integer, Double> iMap = m_trafficMatrix.get(i);
		if (iMap == null){
			iMap = new HashMap<Integer, Double>();
			m_trafficMatrix.put(i, iMap);
		}
		iMap.put(j, w);
	}

	@Override
	public void setAllWeights(double w) {
		for (int i = 0; i < m_matrixDimensions; i++){
			for (int j = 0; j < m_matrixDimensions; j++){
				setWeight(i, j, w);
			}
		}
	}

	@Override
	public void mul(double a) {
		for (Map<Integer, Double> iMap : m_trafficMatrix.values())
			for (Integer j : iMap.keySet())
				iMap.put(j, iMap.get(j)*a);
	}
	
	public Map<Integer, Map<Integer, Double>> getAllWeights(){
		return m_trafficMatrix;
	}
}
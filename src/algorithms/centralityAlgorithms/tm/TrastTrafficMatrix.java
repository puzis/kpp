package algorithms.centralityAlgorithms.tm;

import java.util.HashMap;

import javolution.util.Index;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TrastTrafficMatrix extends AbsTrafficMatrix {
	public HashMap<Index,Double> vertexWeight;
	public TrastTrafficMatrix(int size) {
		vertexWeight = new HashMap<Index, Double>(size);
	}
	
	public void addWeight(Index v, double weight) {
		double old = 1.0;
		if(vertexWeight.containsKey(v)) {
			old = vertexWeight.get(v);
		}
		vertexWeight.put(v, old + weight);
	}
	@Override
	public double getWeight(int i, int j) {
		if(!vertexWeight.containsKey(Index.valueOf(i)))
				return 1;
		if(!vertexWeight.containsKey(Index.valueOf(j)))
			return 1;
		return vertexWeight.get(Index.valueOf(i))*vertexWeight.get(Index.valueOf(j));
	}

	@Override
	public void setWeight(int i, int j, double w) {
		throw new NotImplementedException();
	}

	@Override
	public void setAllWeights(double w) {
		throw new NotImplementedException();
	}

	@Override
	public void mul(double a) {
		throw new NotImplementedException();
	}

}

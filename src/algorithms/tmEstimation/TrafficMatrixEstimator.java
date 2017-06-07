package algorithms.tmEstimation;

import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import javolution.util.FastList;
import javolution.util.Index;

public class TrafficMatrixEstimator {

	private double[][] _tmEstimated = null;
	
	public TrafficMatrixEstimator(NetFlow netflow, Index[] inspectionPoints, int numberOfVertices, DataWorkshop dw){
		run(netflow, inspectionPoints, numberOfVertices, dw);
	}
	
	private void run(NetFlow netflow, Index[] inspectionPoints, int numberOfVertices, DataWorkshop dw){
		double[][] confidence = new double[numberOfVertices][numberOfVertices];//MatricesUtils.getDefaultWeights(numberOfVertices);
		_tmEstimated = new double[numberOfVertices][numberOfVertices];
		
		for (Index v : inspectionPoints){
			
			// Go over all (s,t) pairs in NetFlow(v)
			FastList<Flow> flows = netflow.getFlows(v);
			for (Flow flow:flows){
				Index s = flow.getS();
				Index t = flow.getT();
				double delta = dw.getDelta(s.intValue(), v.intValue(), t.intValue()); 
				if (delta > confidence[s.intValue()][t.intValue()]){
					_tmEstimated[s.intValue()][t.intValue()] = ((double)flow.getWeight())/delta;
					confidence[s.intValue()][t.intValue()] = delta;
				}
			}
		}
	}
	
	public double[][] getEstimatedTM(){
		return _tmEstimated;
	}
}

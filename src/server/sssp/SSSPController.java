package server.sssp;

import javolution.util.Index;
import server.common.DataBase;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphFactory.GraphDataStructure;
import topology.GraphInterface;
import topology.HyperGraphInterface;
import topology.MultiWeightedHyperGraph;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

public class SSSPController {

	public static final String ALIAS = "SSSP";
	

	public int create(int netID, String spAlgName){
		ShortestPathAlg spAlg = ShortestPathAlg.valueOf(spAlgName);
		
		HyperGraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraph();
		GraphFactory.GraphDataStructure gds = graph.getType();
		if (!ShortestPathFactory.isCompatible(spAlg, gds))
			throw new IllegalArgumentException("Graph and algorithm types are not compatible:"+spAlg.toString() + " and " + gds.toString());

		ShortestPathAlgorithmInterface spAlgImpl;
		if (graph instanceof GraphInterface<?,?>)
			spAlgImpl = ShortestPathFactory.getShortestPathAlgorithm(spAlg, (GraphInterface<Index,BasicVertexInfo>)graph);
		else if (graph instanceof MultiWeightedHyperGraph)
			spAlgImpl = ShortestPathFactory.getShortestPathAlgorithm(spAlg, (MultiWeightedHyperGraph<Index,BasicVertexInfo>)graph);
		else
			spAlgImpl = ShortestPathFactory.getShortestPathAlgorithm(spAlg, graph);
			
		int algID = DataBase.putAlgorithm(spAlgImpl, netID);
		return algID;
	}

	/** Removes the given closeness algorithm from the Database maps.
	 * @param algorithm index
	 * @return 0 */
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}
	
	public boolean runAll(int algID, int size){
		for (int i=0;i<size;i++){
			long t = System.currentTimeMillis();
			((ShortestPathAlgorithmInterface)DataBase.getAlgorithm(algID)).run(i);
			if (i%1==0){
				t = System.currentTimeMillis() - t;
				System.out.print(i);
				System.out.print(":");
				System.out.println(t);
			}
		}
		return true;
	}	
	public boolean run(int algID, int source){
		((ShortestPathAlgorithmInterface)DataBase.getAlgorithm(algID)).run(source);
		return true;
	}	
	
	public double getDistance(int algID, int v){
		return ((ShortestPathAlgorithmInterface)DataBase.getAlgorithm(algID)).getDistance(v);
	}
	
	public Object[] getDistanceArray(int algID){
		ShortestPathAlgorithmInterface spAlg = (ShortestPathAlgorithmInterface)DataBase.getAlgorithm(algID);
		double[] d = spAlg.getDistanceArray();
		Object[] dVals = new Object[d.length];
		for (int i = 0; i < dVals.length; i++)
			dVals[i] = new Double(d[i]);
		return dVals;
	}
}

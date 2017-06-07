package server.clustering;

import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.Index;
import server.common.DataBase;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.clustering.LabelPropagation;

public class LabelPropagationController {

	public static final String ALIAS = "LabelPropagation";
	
	/** 
	 * Creates LabelPropagation clustering algorithm for finding community 
	 * structure of a network.
	 *  @param network index
	 *  @return Index of the algorithm in the Database */
	public int create(int netID)
	{
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		LabelPropagation lpalg = null;
		
		if (graph != null){
			lpalg = new LabelPropagation(graph);
			lpalg.run();
		}
		
		int algID = DataBase.putAlgorithm(lpalg, netID);
		return algID; 
	}
	
	
	public Object[] getClusters(int algID){
		LabelPropagation lpalg = (LabelPropagation)DataBase.getAlgorithm(algID);
		FastMap<Integer, FastSet<Index>> clusters = lpalg.getClusters();
		
		Object[] clustersOut = new Object[clusters.size()];
		int i=0;
		for(Integer clusterID: clusters.keySet()){
			FastSet<Index> cluster = clusters.get(clusterID);
			Object [] clusterOut = new Object[cluster.size()];
			int j=0;
			for(Index v: cluster){
				clusterOut[j++] = new Integer(v.intValue());
			}
			clustersOut[i] = clusterOut;
		}
		return clustersOut;
	}
	
	
	/** Removes the given algorithm from the Database maps.
	 * @param algorithm index
	 * @return 0 */
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}
}
package server.clustering;

import java.util.List;
import java.util.Set;

import javolution.util.Index;
import server.common.DataBase;
import topology.AbstractUndirectedGraph;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.clustering.LabelPropagationClustering;

//TODO This code is not tested
public class LabelPropagationClusteringController {
	public static final String ALIAS = "LabelPropagationClustering";
	
	/** 
	 * Creates BorderBasedClustering clustering algorithm for finding community 
	 * structure of a network.
	 *  @param network index
	 *  @return Index of the algorithm in the Database */
	public int create(int netID) throws Exception
	{
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		LabelPropagationClustering bbcalg = null;
		if (graph != null){
			bbcalg = new LabelPropagationClustering((AbstractUndirectedGraph<Index,BasicVertexInfo>)graph);
			bbcalg.generateClusters();
		}
		
		int algID = DataBase.putAlgorithm(bbcalg, netID);
		return algID; 
	}
	public Object[] getClusters(int algID, int v){
		LabelPropagationClustering bbcalg = (LabelPropagationClustering)DataBase.getAlgorithm(algID);
		List<Integer> c = bbcalg.getClusters(Index.valueOf(v));
		Integer[] arr = new Integer[c.size()];
		int j=0;
		for(int i: c){
			arr[j++] = i;
		}
		return arr;
	}
	
	public int getNoOfClusters(int algID){
		LabelPropagationClustering bbcalg = (LabelPropagationClustering)DataBase.getAlgorithm(algID);
		return bbcalg.getClusterIds().size();
	}
	
	public Object[] getVertices(int algID, int c){
		LabelPropagationClustering bbcalg = (LabelPropagationClustering)DataBase.getAlgorithm(algID);
		Set<Index> v = bbcalg.getVertices(c);
		Integer[] arr = new Integer[v.size()];
		int j=0;
		for(Index i: v){
			arr[j++] = i.intValue();
		}
		return arr;
	}
	
	/** Removes the given algorithm from the Database maps.
	 * @param algorithm index
	 * @return 0 */
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}


}

package server.clustering;

import java.util.List;
import java.util.Set;

import javolution.util.Index;
import server.common.DataBase;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.clustering.BudgetedGreedyClustering;

public class BudgetedGreedyClusteringController {
public static final String ALIAS = "BudgetedGreedyClustering";
	
	/** 
	 * Creates BudgetedGreedyClustering clustering algorithm for finding community 
	 * structure of a network.
	 * TODO The parsed graph should itself be of type OptimizedGraphAsArray instead of copy constructing it here
	 *  @param network index
	 *  @return Index of the algorithm in the Database */
	public int create(int netID, int budget, int minEdges) throws Exception
	{
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		//graph = new OptimizedGraphAsArray<Index>(graph);
		BudgetedGreedyClustering<Index,BasicVertexInfo> clustering = null;
		if (graph != null){
			clustering = new BudgetedGreedyClustering<Index,BasicVertexInfo>(graph,budget,minEdges);
			clustering.generateClusters();
		}
		
		int algID = DataBase.putAlgorithm(clustering, netID);
		return algID; 
	}
	
	/**
	 * Returns clusters to which vertex v belongs
	 * TODO Remove dependence on OptimizedGraphAsArray
	 * @param algID
	 * @param v
	 * @return
	 */
	public Object[] getClusters(int algID, int v){
		BudgetedGreedyClustering<Index,BasicVertexInfo> clustering = (BudgetedGreedyClustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = clustering.getGraph();
		//OptimizedGraphAsArray<Index> graph = (OptimizedGraphAsArray<Index>)clustering.getGraph();
		List<Integer> c = clustering.getClusters(Index.valueOf(v));
		Integer[] arr = new Integer[c.size()];
		int j=0;
		for(int i: c){
			arr[j++] = i;
		}
		return arr;
	}
	
	/**
	 * Returns total number of clusters
	 * @param algID
	 * @return
	 */
	public int getNoOfClusters(int algID){
		BudgetedGreedyClustering<Index,BasicVertexInfo> clustering = (BudgetedGreedyClustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(algID);
		return clustering.getNoOfClusters();
	}
	
	/**
	 * Returns the number of border vertices grouped by cluster
	 * @param algID
	 * @return
	 */
	public Object[] getNoOfBordersByCluster(int algID) {
		BudgetedGreedyClustering<Index,BasicVertexInfo> clustering = (BudgetedGreedyClustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(algID);
		Integer[] borders  = new Integer[clustering.getNoOfClusters()];
		int i = 0;
		for(int c : clustering.getClusterIds()) {
			borders[i++] = clustering.getBorderVertices(c).size();
		}
		return borders;
	}
	
	/**
	 * Returns the total number of created borders
	 * @param algID
	 * @return
	 */
	public int getNoOfBorders(int algID) {
		BudgetedGreedyClustering<Index,BasicVertexInfo> clustering = (BudgetedGreedyClustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(algID);
		return clustering.getBorderVertices().size();
	}
	
	/**
	 * Returns the border vertices
	 * @param algID
	 * @return
	 */
	public Object[] getBorderVertices(int algID) {
		BudgetedGreedyClustering<Index,BasicVertexInfo> clustering = (BudgetedGreedyClustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(algID);
		Set<Index> v = clustering.getBorderVertices();
		Integer[] arr = new Integer[v.size()];
		int j=0;
		for(Index i: v){
			arr[j++] = i.intValue();
		}
		return arr;
	}
	
	/**
	 * Returns the number of inter cluster transit edges
	 * @param algID
	 * @return
	 */
	public int getNoOfInterClusterEdges(int algID) {
		BudgetedGreedyClustering<Index,BasicVertexInfo> clustering = (BudgetedGreedyClustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> g = clustering.getGraph();
		int sz = 0;
		for(AbstractSimpleEdge<Index,BasicVertexInfo> e: g.getEdges()) {
			Index v0 = e.getV0();
			Index v1 = e.getV1();
			if (VertexFactory.isVertexInfo(g.getVertex(v0))&& VertexFactory.isVertexInfo(g.getVertex(v1))){
				assert(((VertexInfo)g.getVertex(v0)).getClusters().size() == 1 && ((VertexInfo)g.getVertex(v1)).getClusters().size() == 1);
				if(clustering.getClusters(v0).get(0).intValue() != clustering.getClusters(v1).get(0).intValue()){
					sz++;
				}
			}
		}
		return sz;
	}
	
	/**
	 * Returns the number of vertices grouped by cluster
	 * @param algID
	 * @return
	 */
	public Object[] getClusterSizes(int algID) {
		BudgetedGreedyClustering<Index,BasicVertexInfo> clustering = (BudgetedGreedyClustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(algID);
		Integer[] sizes  = new Integer[clustering.getNoOfClusters()];
		int i = 0;
		for(int c : clustering.getClusterIds()) {
			sizes[i++] = clustering.getVertices(c).size();
		}
		return sizes;
	}
	
	/**
	 * Returns the vertices present in cluster c
	 * @param algID
	 * @param c
	 * @return
	 */
	public Object[] getVertices(int algID, int c){
		BudgetedGreedyClustering<Index,BasicVertexInfo> clustering = (BudgetedGreedyClustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(algID);
		Set<Index> v = clustering.getVertices(c);
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

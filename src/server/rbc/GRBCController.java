package server.rbc;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DataBase;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.rbc.GRBCAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;

public class GRBCController {
	public static final String ALIAS = "GRBC";
	
	/**
	 * creates a fastergrbc algorithm object in the server
	 * @param netID 
	 * @param communicationWeightsStr 
	 * @param cands - candidates array. 
	 * @return algID if succeeded in creating the algorithm in server
	 */
	public int create(int netID,String communicationWeightsStr){
		/*try{
			AbsRoutingFunction routingFunction = AbsRoutingFunction.class.newInstance();
		}catch (Exception e) {}*/
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		AbsTrafficMatrix communicationWeights = null;
		GRBCAlgorithm grbc = null;
		if (graph != null)
		{
			if (communicationWeightsStr != null && !communicationWeightsStr.isEmpty())
				communicationWeights = new DenseTrafficMatrix(communicationWeightsStr, graph.getNumberOfVertices()); // WeightsLoader.loadWeightsFromString(communicationWeightsStr, graph.getNumberOfVertices());
			else
				communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
		    grbc =new GRBCAlgorithm(graph,communicationWeights);
		}
		int algID = DataBase.putAlgorithm(grbc, netID);
		return algID; 
	}
	
	/**
	 * 
	 * @param algID
	 * @param vertices
	 * @return
	 */
	public  double getBetweenness(int algID, Object[] vertices) {
		GRBCAlgorithm grbc = (GRBCAlgorithm)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		double res=grbc.getBetweeness(group);
		return res;
	}
	
	
	/**
	 * 
	 * @param algID
	 * @param vertices
	 * @param t
	 * @return
	 */
	public  double getTargetDependency(int algID, Object[] vertices, int t) {
		GRBCAlgorithm grbc = (GRBCAlgorithm)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		return grbc.getTargetDependency(group, Index.valueOf(t));
	}
	
	
	/**
	 * 
	 * @param algID
	 * @param s
	 * @param vertices
	 * @return
	 */
	public  double getSourceDependency(int algID, int s,Object[] vertices) {
		GRBCAlgorithm grbc = (GRBCAlgorithm)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		return grbc.getSourceDependency(Index.valueOf(s),group);
	}
	
	/**
	 * 
	 * @param algID
	 * @param s
	 * @param vertices
	 * @param t
	 * @return
	 */
	public  double getDelta(int algID,int s, Object[] vertices, int t) {
		GRBCAlgorithm grbc = (GRBCAlgorithm)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		return grbc.getDelta(Index.valueOf(s),group,Index.valueOf(t));
	}
	
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}	
}
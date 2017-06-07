package server.rbc;

import javolution.util.Index;
import server.common.DataBase;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.rbc.StatefullVRBCAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;

public class StatefullVRBCController {
	public static final String ALIAS = "StatefullVRBC";
	
	/**
	 * creates a fastergrbc algorithm object in the server
	 * @param netID 
	 * @param communicationWeightsStr 
	 * @param cands - candidates array. 
	 * @return algID if succeeded in creating the algorithm in server
	 */
	public int create(int netID,String communicationWeightsStr,int cachetype){
		/*try{
			AbsRoutingFunction routingFunction = AbsRoutingFunction.class.newInstance();
		}catch (Exception e) {}*/
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		AbsTrafficMatrix communicationWeights = null;
		StatefullVRBCAlgorithm statevrbc = null;
		if (graph != null)
		{
			if (communicationWeightsStr != null && !communicationWeightsStr.isEmpty())
				communicationWeights = new DenseTrafficMatrix(communicationWeightsStr, graph.getNumberOfVertices()); // WeightsLoader.loadWeightsFromString(communicationWeightsStr, graph.getNumberOfVertices());
			else
				communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
		    statevrbc = new StatefullVRBCAlgorithm(graph,communicationWeights,cachetype);
		}
		int algID = DataBase.putAlgorithm(statevrbc, netID);
		return algID; 
	}
	
	/**
	 * 
	 * @param algID
	 * @param vertices
	 * @return
	 */
	public  double getBetweenness(int algID, int v) {
		StatefullVRBCAlgorithm stvrbc = (StatefullVRBCAlgorithm)DataBase.getAlgorithm(algID);
		/*FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}*/
		double res=stvrbc.getBetweeness(Index.valueOf(v));
		return res;
	}
	
	
	/**
	 * 
	 * @param algID
	 * @param vertices
	 * @param t
	 * @return
	 */
	public  double getTargetDependency(int algID, int v, int t) {
		StatefullVRBCAlgorithm stvrbc = (StatefullVRBCAlgorithm)DataBase.getAlgorithm(algID);
		/*FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}*/
		return stvrbc.getTargetDependency(Index.valueOf(v), Index.valueOf(t));
	}
	
	
	/**
	 * 
	 * @param algID
	 * @param s
	 * @param vertices
	 * @return
	 */
	public  double getSourceDependency(int algID, int s,int v) {
		StatefullVRBCAlgorithm stvrbc = (StatefullVRBCAlgorithm)DataBase.getAlgorithm(algID);
		/*FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}*/
		return stvrbc.getSourceDependency(Index.valueOf(s),Index.valueOf(v));
	}
	
	/**
	 * 
	 * @param algID
	 * @param s
	 * @param vertices
	 * @param t
	 * @return
	 */
	public  double getDelta(int algID,int s, int v, int t) {
		StatefullVRBCAlgorithm stvrbc = (StatefullVRBCAlgorithm)DataBase.getAlgorithm(algID);
		/*FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}*/
		return stvrbc.getDelta(Index.valueOf(s),Index.valueOf(v),Index.valueOf(t));
	}
	
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}	
}
package server.rbc;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DataBase;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.rbc.SRBCAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;

public class SRBCController {
	public static final String ALIAS = "SRBC";
	
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
		SRBCAlgorithm srbcalg = null;
		if (graph != null)
		{
			if (communicationWeightsStr != null && !communicationWeightsStr.isEmpty())
				communicationWeights = new DenseTrafficMatrix(communicationWeightsStr, graph.getNumberOfVertices()); // WeightsLoader.loadWeightsFromString(communicationWeightsStr, graph.getNumberOfVertices());
			else
				communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
		    srbcalg =new SRBCAlgorithm(graph,communicationWeights);
		}
		int algID = DataBase.putAlgorithm(srbcalg, netID);
		return algID; 
	}
	
	/**
	 * 
	 * @param algID
	 * @param vertices
	 * @return
	 */
	public  double getBetweenness(int algID, Object[] vertices) {
		SRBCAlgorithm srbcalg = (SRBCAlgorithm)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		double res=srbcalg.getBetweeness(group);
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
		SRBCAlgorithm srbcalg = (SRBCAlgorithm)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		return srbcalg.getTargetDependency(group, Index.valueOf(t));
	}
	
	
	/**
	 * 
	 * @param algID
	 * @param s
	 * @param vertices
	 * @return
	 */
	public  double getSourceDependency(int algID, int s,Object[] vertices) {
		SRBCAlgorithm srbcalg = (SRBCAlgorithm)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		return srbcalg.getSourceDependency(Index.valueOf(s),group);
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
		SRBCAlgorithm srbcalg = (SRBCAlgorithm)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		return srbcalg.getDelta(Index.valueOf(s),group,Index.valueOf(t));
	}
	
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}	
}
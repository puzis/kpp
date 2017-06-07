package server.rbc;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DataBase;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.rbc.ContributionVRBC;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;


public class ContributionVRBCController {
	public static final String ALIAS = "ContributionVRBC";
	
	/**
	 * creates a ContributionVRBC algorithm object in the server
	 * @param netID 
	 * @param communicationWeightsStr 
	 * @param cands - candidates array. 
	 * @return algID if succeeded in creating the algorithm in server
	 */
	public int create(int netID,String communicationWeightsStr, Object []  cands,int cachetype){
		/*try{
			AbsRoutingFunction routingFunction = AbsRoutingFunction.class.newInstance();
		}catch (Exception e) {}*/
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		FastList<Index> candidates =null;
		if (null!=cands && cands.length>0){// empty set means all group
			candidates =new FastList<Index> (cands.length);
			for (Object o : cands)
				candidates.add(Index.valueOf(((Integer)o).intValue()));
		}
		AbsTrafficMatrix communicationWeights = null;
		ContributionVRBC contvrbc = null;
		if (graph != null)
		{
			if (communicationWeightsStr != null && !communicationWeightsStr.isEmpty())
				communicationWeights = new DenseTrafficMatrix(communicationWeightsStr, graph.getNumberOfVertices()); // WeightsLoader.loadWeightsFromString(communicationWeightsStr, graph.getNumberOfVertices());
			else
				communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());

		    contvrbc =new ContributionVRBC(graph,communicationWeights,candidates,cachetype);
		}
		int algID = DataBase.putAlgorithm(contvrbc, netID);
		return algID; 
	}
	
	/**
	 * 
	 * @param algID
	 * @param _vertices
	 * @return
	 */
	public  double getBetweenness(int algID, int v) {
		ContributionVRBC contvrbc = (ContributionVRBC)DataBase.getAlgorithm(algID);
		/*FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}*/
		double res=contvrbc.getBetweeness(Index.valueOf(v));
		return res;
	}
	
	
	/**
	 * 
	 * @param algID
	 * @param _vertices
	 * @param t
	 * @return
	 */
	public  double getTargetDependency(int algID, int v, int t) {
		ContributionVRBC contvrbc = (ContributionVRBC)DataBase.getAlgorithm(algID);
		/*FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}*/
		return contvrbc.getTargetDependency(Index.valueOf(v), Index.valueOf(t));
	}
	
	
	/**
	 * 
	 * @param algID
	 * @param s
	 * @param _vertices
	 * @return
	 */
	public  double getSourceDependency(int algID, int s,int v) {
		ContributionVRBC contvrbc = (ContributionVRBC)DataBase.getAlgorithm(algID);
		/*FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}*/
		return contvrbc.getSourceDependency(Index.valueOf(s),Index.valueOf(v));
	}
	
	/**
	 * 
	 * @param algID
	 * @param s
	 * @param _vertices
	 * @param t
	 * @return
	 */
	public  double getDelta(int algID,int s, int v, int t) {
		ContributionVRBC contvrbc = (ContributionVRBC)DataBase.getAlgorithm(algID);
		/*FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}*/
		return contvrbc.getDelta(Index.valueOf(s),Index.valueOf(v),Index.valueOf(t));
	}
	
	
	public boolean addVertex (int algID, int v,Object [] vertices){
		ContributionVRBC contvrbc = (ContributionVRBC)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		contvrbc.add(Index.valueOf(v),group);
		return true;
	}
	
	public boolean addVertex (int algID, int v){
		ContributionVRBC contvrbc = (ContributionVRBC)DataBase.getAlgorithm(algID);
		contvrbc.add(Index.valueOf(v));
		return true;
	}
	
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}	
}
package server.rbc;

import common.ShadowedHistoriedCacheArr;

import javolution.util.FastList;

import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import server.rbc.executions.FasterGRBCCreateExecution;
import server.rbc.executions.FasterGRBCExecution;
import server.rbc.executions.FasterGRBCFindVerticesExecution;
import server.rbc.executions.IExecutable;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.rbc.FasterGRBC;
import algorithms.centralityAlgorithms.rbc.GreedyContributionRBC;
import algorithms.centralityAlgorithms.rbc.GreedyTopKRBC;
import algorithms.centralityAlgorithms.rbc.sets.DynamicRBCSet;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;

public class FasterGRBCController {
	public static final String ALIAS = "FasterGRBC";
	
	/**
	 * creates a fastergrbc algorithm object in the server
	 * @param netID 
	 * @param communicationWeightsStr 
	 * @param cands - candidates array. 
	 * @return algID if succeeded in creating the algorithm in server
	 */
	public int create(int netID,String communicationWeightsStr, Object []  cands,int cachetype){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		FastList<Index> candidates =null;
		if (null!=cands && cands.length>0){// empty set means all group
			candidates =new FastList<Index> (cands.length);
			for (Object o : cands)
				candidates.add(Index.valueOf(((Integer)o).intValue()));
		}
		AbsTrafficMatrix communicationWeights = null;
		FasterGRBC fastgrbc = null;
		if (graph != null)
		{
			if (communicationWeightsStr != null && !communicationWeightsStr.isEmpty())
				communicationWeights = new DenseTrafficMatrix(communicationWeightsStr, graph.getNumberOfVertices()); // WeightsLoader.loadWeightsFromString(communicationWeightsStr, graph.getNumberOfVertices());
			else
				communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
		    fastgrbc =new FasterGRBC(graph,communicationWeights,candidates,cachetype);
		}
		int algID = DataBase.putAlgorithm(fastgrbc, netID);
		return algID; 
	}
	
	public int createAsynch(int netID, int tmID){
		LoggingManager.getInstance().writeTrace(
				"Starting creating Faster Group RBC Algorithm.",
				FasterGRBCController.ALIAS, "createAsynch", null);

		AbsTrafficMatrix tm = DataBase.getTrafficMatrix(tmID);
		FasterGRBCCreateExecution exe = new FasterGRBCCreateExecution(netID, tm);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();
		
		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT,
				FasterGRBCController.ALIAS, "createAsynch", null);
		
		return exeID;
	}
	
	public int createAsynch(int netID, String communicationWeightsStr){
		return createAsynch(netID, communicationWeightsStr, new Object[0], ShadowedHistoriedCacheArr.CACHEID);
	}
	
	public int createAsynch(int netID,String communicationWeightsStr, Object []  cands,int cachetype){
		LoggingManager.getInstance().writeTrace(
				"Starting creating Faster Group RBC Algorithm.",
				FasterGRBCController.ALIAS, "createAsynch", null);

		FasterGRBCCreateExecution exe = new FasterGRBCCreateExecution(netID,communicationWeightsStr,cands,cachetype);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();
		
		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT,
				FasterGRBCController.ALIAS, "createAsynch", null);
		
		return exeID;
	}
	
	/**
	 * 
	 * @param algID
	 * @param vertices
	 * @return
	 */
	public  double getBetweenness(int algID, Object[] vertices) {
		FasterGRBC fgrbc = (FasterGRBC)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		double res=fgrbc.getBetweeness(group);
		return res;
	}
	
	
	public  double getBetweennessAsynch(int algID, Object[] vertices) {
		final int _algID = algID;
		final Object[] _vertices = vertices;
		FasterGRBCExecution exe = new FasterGRBCExecution(new IExecutable() {
			@Override
			public Object execute() {
				return getBetweenness(_algID, _vertices);
			}
		}, "getBetweenness");
		int exeID = DataBase.putExecution(exe);
		Thread t = new Thread(exe);
		t.start();
		return exeID;
	}
	
	
	/**
	 * 
	 * @param algID
	 * @param vertices
	 * @param t
	 * @return
	 */
	public  double getTargetDependency(int algID, Object[] vertices, int t) {
		FasterGRBC fgrbc = (FasterGRBC)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		return fgrbc.getTargetDependency(group, Index.valueOf(t));
	}
	
	
	/**
	 * 
	 * @param algID
	 * @param s
	 * @param vertices
	 * @return
	 */
	public  double getSourceDependency(int algID, int s,Object[] vertices) {
		FasterGRBC fgrbc = (FasterGRBC)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		return fgrbc.getSourceDependency(Index.valueOf(s),group);
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
		FasterGRBC fgrbc = (FasterGRBC)DataBase.getAlgorithm(algID);
		FastList<Index> group = new FastList<Index> (vertices.length);
		for (int i=0; i<vertices.length; i++){
			group.add(Index.valueOf(Index.valueOf((Integer)vertices[i]).intValue()));
		}
		return fgrbc.getDelta(Index.valueOf(s),group,Index.valueOf(t));
	}
	
	public Object[] getCentralVertices(int algID, int groupSize, Object[] givenVertices, Object[] candidates)
	{
		FasterGRBC alg = (FasterGRBC)DataBase.getAlgorithm(algID);
		DynamicRBCSet set = new DynamicRBCSet(alg);
		int[] gv = new int[givenVertices.length];
		for (int i = 0; i < gv.length; i++) {
			gv[i] = (Integer)givenVertices[i];
		}
		
		int[] c = new int[candidates.length];
		for (int i = 0; i < candidates.length; i++) {
			c[i] = (Integer)candidates[i];
		}
		Index[] tmp = GreedyContributionRBC.findVertices(set, groupSize, gv, c, new DummyProgress(), 1.0);
		Object[] res = new Object[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			res[i] = Integer.valueOf(tmp[i].intValue());
		}
		return res;
	}
	
	public Object[] getCentralVerticesTopK(int algID, int groupSize, Object[] givenVertices, Object[] candidates)
	{
		FasterGRBC alg = (FasterGRBC)DataBase.getAlgorithm(algID);
		int[] gv = new int[givenVertices.length];
		for (int i = 0; i < gv.length; i++) {
			gv[i] = (Integer)givenVertices[i];
		}
		
		int[] c = new int[candidates.length];
		for (int i = 0; i < candidates.length; i++) {
			c[i] = (Integer)candidates[i];
		}
		Index[] tmp = GreedyTopKRBC.findVertices(alg, groupSize, gv, c, new DummyProgress(), 1.0);
		Object[] res = new Object[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			res[i] = Integer.valueOf(tmp[i].intValue());
		}
		return res;
	}
	
	public int getCentralVerticesAsynch(int algID, int groupSize, Object[] givenVertices, Object[] candidates)
	{
		int[] given = new int[givenVertices.length];
		for (int i = 0; i < given.length; i++) {
			given[i] = (Integer)givenVertices[i];
		}
		int[] cands = null;
		if (candidates != null && candidates.length > 0)
		{
			cands = new int[candidates.length];
			for (int i = 0; i < cands.length; i++) {
				cands[i] = (Integer)candidates[i];
			}
		}
		FasterGRBCFindVerticesExecution exe = new FasterGRBCFindVerticesExecution(algID, groupSize, given, cands);
		int exeID = DataBase.putExecution(exe);
		Thread t = new Thread(exe);
		t.start();
		return exeID;
	}
	
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}	
}
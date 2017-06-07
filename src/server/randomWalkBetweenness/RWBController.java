package server.randomWalkBetweenness;

import javolution.util.FastList;

import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import server.randomWalkBetweenness.executions.FindCentralVerticesExecution;
import server.randomWalkBetweenness.executions.RWBExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.randomWalkBetweeness.AbsGreedyRWBetweenness;
import algorithms.centralityAlgorithms.randomWalkBetweeness.GroupRandomWalkBetweeness;


public class RWBController 
{
	public static final String ALIAS = "RWB";
	
	/** Creates RWB algorithm with the given network.
	 * @param netID index
	 * @return Index of the algorithm in the Database */
	public int create(int netID)
	{
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		GroupRandomWalkBetweeness grwb = null;
		if (graph != null)
		{
			try{
				grwb = new GroupRandomWalkBetweeness(graph);
			}
			catch(Exception ex)
			{
				LoggingManager.getInstance().writeSystem("An exception has occurred while creating RWB:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), RWBController.ALIAS, "create", ex);
			}
		}
		int algID = DataBase.putAlgorithm(grwb, netID);
		return algID; 
	}
	
	/** Starts an execution that creates RWB algorithm with the given network.
	 * @param netID index
	 * @return Execution index in the Database */
	public int createAsynch(int netID)
	{
		LoggingManager.getInstance().writeTrace("Starting creating RWB.", RWBController.ALIAS, "createAsynch", null);
    	
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
		AbstractExecution exe = new RWBExecution(netID);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, RWBController.ALIAS, "createAsynch", null);
    	return exeID;
	}
	
	/** Removes the given RWB algorithm from the Database maps.
	 * @param algID index
	 * @return 0 */
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}	
	
	/** Returns the betweenness value of the given vertex in the given RWB algorithm instance.
	 * @param algID index
	 * @param vertex a vertex
	 * @return betweenness value */
	public double getBetweenness(int algID, int vertex){
		return ((GroupRandomWalkBetweeness)DataBase.getAlgorithm(algID)).getVertexBetweeness(vertex);
	}
	
	/** Returns an array of betweenness values of the given vertices in the given RWB algorithm instance.
	 * The order of the betweenness values in the array corresponds to the order of the given vertices.
	 * @param algID index
	 * @param vertices array of vertices
	 * @return array of betweenness values */
	public Object[] getBetweenness(int algID, int[] vertices){
		Object [] betweennessValues = new Object [vertices.length];
		GroupRandomWalkBetweeness grwb = (GroupRandomWalkBetweeness)DataBase.getAlgorithm(algID);
		for (int i = 0; i < vertices.length; i++)
			betweennessValues[i] = grwb.getVertexBetweeness(vertices[i]);
		return betweennessValues;
	}
	
	/** Returns an array of betweenness values of all vertices in the given RWB algorithm instance.
	 * The order of the betweenness values in the array corresponds to the order of the vertices in the graph.
	 * @param algID index
	 * @return array of betweenness values */
	public Object[] getBetweenness(int algID){
		GroupRandomWalkBetweeness grwb = (GroupRandomWalkBetweeness)DataBase.getAlgorithm(algID);
		Object[] bVals = new Object[DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple().getNumberOfVertices()];
		for (int i = 0; i < bVals.length; i++)
			bVals[i] = grwb.getVertexBetweeness(i);
		return bVals;
	}
	
	/** Returns the sum of betweenness values of the given vertices in the given RWB instance.
	 * @param algID index
	 * @param vertices array of vertices
	 * @return betweenness value */
	public double getSumGroup(int algID, Object[] vertices){
		GroupRandomWalkBetweeness grwb = (GroupRandomWalkBetweeness)DataBase.getAlgorithm(algID);
		double grwbc = 0;
		try{
      for (Object vertex : vertices) {
        grwbc += grwb.getVertexBetweeness((Integer) vertex);
      }
		}
		catch(RuntimeException ex){
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured during getSumGroup.", RWBController.ALIAS, "getSumGroup", ex);
		}
		LoggingManager.getInstance().writeTrace("Finishing getSumGroup.", RWBController.ALIAS, "getSumGroup", null);
		return grwbc;
	}
	
	/** Searches for deployment of vertices (using TopK algorithm) according to given parameters.
     * @param algID algorithm index
     * @param k is the size of the desired deployment
     * @param candidatesObj candidates for the deployment (can be an empty list)
     * @param givenVerticesObj are the already deployed vertices (can be an empty list)
     * @param givenEdgesObj are the already deployed links (can be an empty list)
     * @return array of vertices */
	public Object[] getCentralVertices(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		GroupRandomWalkBetweeness grwb = (GroupRandomWalkBetweeness)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<Index> candidates = new FastList<Index>();
		
		if (candidatesObj != null){
      for (Object candidate : candidatesObj)
        candidates.add(Index.valueOf((Integer) candidate));
		}
		int [] givenVertices = new int[givenVerticesObj.length];
    	for (int i = 0; i < givenVertices.length; i++)
    		givenVertices[i] = (Integer) givenVerticesObj[i];
    	
    	AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length/2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++){
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf((Integer) givenEdgesObj[v]), Index.valueOf((Integer) givenEdgesObj[v + 1]));
			givenEdges[i] = e;
		}
		Object [] centralVertices = null;
		try{
			Index [] cv = AbsGreedyRWBetweenness.findVertices(grwb, candidates, givenVertices, givenEdges, Algorithm.TopK, Bound.GroupSize, k, new DummyProgress(), 1); 
			centralVertices = new Object [cv.length];
			
			int i = 0;
			for (Index v : cv){
				centralVertices[i++] = v.intValue();
			}

		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage(), RWBController.ALIAS, "getCentralVertices", ex);
		}
		return centralVertices;
	}
	
	/** Starts an execution which searches for deployment of vertices (using TopK algorithm) according to given parameters.
     * @param algID algorithm index
     * @param k is the size of the desired deployment
     * @param candidatesObj for the deployment (can be an empty list)
     * @param givenVerticesObj are the already deployed vertices (can be an empty list)
     * @param givenEdgesObj are the already deployed links (can be an empty list)
     * @return execution index */
	public int getCentralVerticesAsynch(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		LoggingManager.getInstance().writeTrace("Starting searching central vertices.", RWBController.ALIAS, "getCentralVerticesAsynch", null);
    	
		GroupRandomWalkBetweeness grwb = (GroupRandomWalkBetweeness)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<Index> candidates = new FastList<Index>();
		
		if (candidatesObj != null){
      for (Object candidate : candidatesObj)
        candidates.add(Index.valueOf((Integer) candidate));
		}
		int [] givenVertices = new int[givenVerticesObj.length];
    	for (int i = 0; i < givenVertices.length; i++)
    		givenVertices[i] = (Integer) givenVerticesObj[i];
    	
    	AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length/2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++){
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf((Integer) givenEdgesObj[v]), Index.valueOf((Integer) givenEdgesObj[v + 1]));
			givenEdges[i] = e;
		}
		
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
    	AbstractExecution exe = new FindCentralVerticesExecution(grwb, Algorithm.TopK, candidates, givenVertices, givenEdges, Bound.GroupSize, k);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, RWBController.ALIAS, "getCentralVerticesAsynch", null);
    	return exeID;
	}
}
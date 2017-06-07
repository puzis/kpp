package server.degree;

import javolution.util.FastList;

import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.degree.executions.DegreeAlgorithmExecution;
import server.degree.executions.EvaluateExecution;
import server.degree.executions.FindCentralEdgesExecution;
import server.degree.executions.FindCentralVerticesExecution;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.degree.AbsGreedyDegree;
import algorithms.centralityAlgorithms.degree.DegreeAlgorithm;
import algorithms.centralityAlgorithms.degree.GroupDegreeAlgorithm;


public class GroupDegreeController 
{
	public static final String ALIAS = "GDegree";
	
	/** Creates Group Degree algorithm with the given network.
	 * @param network index
	 * @return Index of the algorithm in the Database */
	public int create(int netID){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		DegreeAlgorithm degree = null;
		if (graph != null){
			int[] vertices = new int[graph.getNumberOfVertices()];	int i = 0;
			for (Index v : graph.getVertices())
				vertices[i++] = v.intValue();
			try{
				degree = new DegreeAlgorithm(vertices, graph, new DummyProgress(), 1);
			}
			catch(Exception ex){
				LoggingManager.getInstance().writeSystem("An exception has occured while creating DegreeAlgorithm:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), GroupDegreeController.ALIAS, "create", ex);
			}
		}
		return DataBase.putAlgorithm(degree, netID);
	}
	
	/** Starts an execution that creates Group Degree algorithm with the given network.
	 * @param network index
	 * @return Execution index in the Database */
	public int createAsynch(int netID){
		LoggingManager.getInstance().writeTrace("Starting creating DegreeAlgorithm.", GroupDegreeController.ALIAS, "createAsynch", null);
    	
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
		AbstractExecution exe = new DegreeAlgorithmExecution(netID);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, GroupDegreeController.ALIAS, "createAsynch", null);
    	return exeID;
	}
	
	/** Removes the given Group Degree algorithm from the Database maps.
	 * @param algorithm index
	 * @return 0 */
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}
	
	/** Returns group degree value of the given vertices and given edges in the given group degree algorithm instance.
	 * @param algorithm index
	 * @param array of vertices
	 * @param array of edges, where the edges are represented by edges[0]-edges[1], edges[2]-edges[3], edges[4]-edges[5] etc. 
	 * @return group degree value */
	public double getGroupDegree(int algID, Object[] vertices, Object[] edges){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		double gDegree = -1;
		boolean success = true;
		try{
			Object[] group = new Object[vertices.length + edges.length/2];
	    	for (int i = 0; i < vertices.length; i++){
	    		group[i] = Index.valueOf(((Integer)vertices[i]).intValue());
	    	}
	    	
	    	for (int v = 0, i = vertices.length; v < edges.length; v = v + 2, i++){
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf(((Integer)edges[v]).intValue()), Index.valueOf(((Integer)edges[v + 1]).intValue()));
				group[i] = e;
			}
			try{
				gDegree = GroupDegreeAlgorithm.calculateMixedGroupDegree(group, graph, new DummyProgress(), 1);
			}
			catch(Exception ex){
				LoggingManager.getInstance().writeSystem(ex.getMessage(), GroupDegreeController.ALIAS, "getGroupDegree", ex);
			}
			finally{	
				success = (gDegree != -1);
				if (!success){
					LoggingManager.getInstance().writeSystem("getGroupDegree has NOT completed successfully.", GroupDegreeController.ALIAS, "getGroupDegree", null);
					success = false;
				}
			}
		}
		catch(RuntimeException ex){
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured during getGroupDegree.", GroupDegreeController.ALIAS, "getGroupDegree", ex);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing getGroupDegree.", GroupDegreeController.ALIAS, "getGroupDegree", null);
		return gDegree;
	}
	
	/** Starts an execution which calculates group degree value of the given vertices and given edges in the given group degree algorithm instance.
	 * @param algorithm index
	 * @param array of vertices
	 * @param array of edges, where the edges are represented by edges[0]-edges[1], edges[2]-edges[3], edges[4]-edges[5] etc. 
	 * @return execution index in the database */
	public int getGroupDegreeAsynch(int algID, Object[] vertices, Object[] edges){
		LoggingManager.getInstance().writeTrace("Starting group evaluation.", GroupDegreeController.ALIAS, "getGroupDegreeAsynch", null);
    	
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		int exeID = -1;
		try{
			Object[] group = new Object[vertices.length + edges.length/2];
	    	for (int i = 0; i < vertices.length; i++){
	    		group[i] = Index.valueOf(((Integer)vertices[i]).intValue());
	    	}
	    	
	    	for (int v = 0, i = vertices.length; v < edges.length; v = v + 2, i++){
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf(((Integer)edges[v]).intValue()), Index.valueOf(((Integer)edges[v + 1]).intValue()));
				group[i] = e;
			}
		
	    	/** Create new execution (Runnable).  
	    	 *  Store execution into database. 
	    	 *  Create new thread, give it the execution and start it.
	    	 *  Return exeID. 
	    	 *  (At the end of run() the execution parameters of progress and success are updated.)
	    	 */
			AbstractExecution exe = new EvaluateExecution(graph, group);
	    	exeID = DataBase.putExecution(exe);
	    	exe.setID(exeID);
	    	Thread t = new Thread(exe);
	    	t.start();
	    }
		catch(RuntimeException ex){
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured during getGroupDegreeAsynch.", GroupDegreeController.ALIAS, "getGroupDegreeAsynch", ex);
		}
		LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, GroupDegreeController.ALIAS, "getGroupDegreeAsynch", null);
    	return exeID;
	}
	
	/** Searches for deployment of vertices (using Contribution algorithm) according to given parameters.
     * @param Group degree algorithm index
     * @param k is the size of the desired deployment
     * @param vertex candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return array of vertices */
	public Object[] getCentralVertices(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		DegreeAlgorithm degreeAlgorithm = (DegreeAlgorithm)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<Index> candidates = new FastList<Index>();
		
		if (candidatesObj != null){
			for (int v = 0; v < candidatesObj.length; v++)
				candidates.add(Index.valueOf(((Integer)candidatesObj[v]).intValue()));
		}
		int [] givenVertices = new int[givenVerticesObj.length];
    	for (int i = 0; i < givenVertices.length; i++)
    		givenVertices[i] = ((Integer) givenVerticesObj[i]).intValue();
    	
    	AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length/2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++){
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf(((Integer)givenEdgesObj[v]).intValue()), Index.valueOf(((Integer)givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}
		Object [] centralVertices = null;
		try{
			Index [] cv = AbsGreedyDegree.findVertices(degreeAlgorithm, Algorithm.Contribution, candidates, givenVertices, givenEdges, Bound.GroupSize, k, new DummyProgress(), 1);
			centralVertices = new Object [cv.length];
			
			int i = 0;
			for (Index v : cv){
				centralVertices[i++] = new Integer(((Index)v).intValue());
			}
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage(), GroupDegreeController.ALIAS, "getCentralVertices", ex);
		}
		return centralVertices;
	}
	
	/** Searches for deployment of edges (using Contribution algorithm) according to given parameters.
     * @param Group degree algorithm index
     * @param k is the size of the desired deployment
     * @param edge candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return array of edges
     * In the candidates array and in the result array every two successive elements represent an edge.
     * Namely, A[0]-A[1], A[2]-A[3], A[4]-A[5] represent 3 edges. */
	public Object[] getCentralEdges(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		DegreeAlgorithm degreeAlgorithm = (DegreeAlgorithm)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
		
		if (candidatesObj != null){
			for (int v = 0; v < candidatesObj.length; v = v + 2){
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf(((Integer)candidatesObj[v]).intValue()), Index.valueOf(((Integer)candidatesObj[v + 1]).intValue()));
				candidates.add(e);
			}
		}
		int [] givenVertices = new int[givenVerticesObj.length];
    	for (int i = 0; i < givenVertices.length; i++)
    		givenVertices[i] = ((Integer) givenVerticesObj[i]).intValue();
    	
    	AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length/2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++){
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf(((Integer)givenEdgesObj[v]).intValue()), Index.valueOf(((Integer)givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}
		
		Object [] centralEdges = null;
		try{
			AbstractSimpleEdge<Index,BasicVertexInfo>[] ce = AbsGreedyDegree.findEdges(degreeAlgorithm, Algorithm.Contribution, candidates, givenVertices, givenEdges, Bound.GroupSize, k, new DummyProgress(), 1); 
			centralEdges = new Object [ce.length];
			
			int i = 0;
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : ce){
				centralEdges[i++] = new Integer(((Index)e.getV0()).intValue());
				centralEdges[i++] = new Integer(((Index)e.getV1()).intValue());
			}
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage(), GroupDegreeController.ALIAS, "getCentralEdges", ex);
		}
		return centralEdges;
	}
	
	/** Starts an execution which searches for deployment of vertices (using Contribution algorithm) according to given parameters.
     * @param Group degree index
     * @param k is the size of the desired deployment
     * @param candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return execution index */
	public int getCentralVerticesAsynch(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		LoggingManager.getInstance().writeTrace("Starting searching central vertices.", GroupDegreeController.ALIAS, "getCentralVerticesAsynch", null);
    	
		DegreeAlgorithm degreeAlgorithm = (DegreeAlgorithm)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<Index> candidates = new FastList<Index>();
		
		if (candidatesObj != null){
			for (int v = 0; v < candidatesObj.length; v++)
				candidates.add(Index.valueOf(((Integer)candidatesObj[v]).intValue()));
		}
		int [] givenVertices = new int[givenVerticesObj.length];
    	for (int i = 0; i < givenVertices.length; i++)
    		givenVertices[i] = ((Integer) givenVerticesObj[i]).intValue();
    	
    	AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length/2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++){
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf(((Integer)givenEdgesObj[v]).intValue()), Index.valueOf(((Integer)givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}
		
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
    	AbstractExecution exe = new FindCentralVerticesExecution(degreeAlgorithm, Algorithm.Contribution, candidates, givenVertices, givenEdges, Bound.GroupSize, k);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, GroupDegreeController.ALIAS, "getCentralVerticesAsynch", null);
    	return exeID;
	}
	
	/** Starts an execution which searches for deployment of edges (using Contribution algorithm) according to given parameters.
     * @param Group degree index
     * @param k is the size of the desired deployment
     * @param edge candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return execution index
     * In the candidates array and in the result array every two successive elements represent an edge.
     * Namely, A[0]-A[1], A[2]-A[3], A[4]-A[5] represent 3 edges. */
	public int getCentralEdgesAsynch(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		LoggingManager.getInstance().writeTrace("Starting searching central edges.", GroupDegreeController.ALIAS, "getCentralEdgesAsynch", null);
    	
		DegreeAlgorithm degreeAlgorithm = (DegreeAlgorithm)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
		
		if (candidatesObj != null){
			for (int v = 0; v < candidatesObj.length; v = v + 2){
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf(((Integer)candidatesObj[v]).intValue()), Index.valueOf(((Integer)candidatesObj[v + 1]).intValue()));
				candidates.add(e);
			}
		}
		int [] givenVertices = new int[givenVerticesObj.length];
    	for (int i = 0; i < givenVertices.length; i++)
    		givenVertices[i] = ((Integer) givenVerticesObj[i]).intValue();
    	
    	AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length/2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++){
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf(((Integer)givenEdgesObj[v]).intValue()), Index.valueOf(((Integer)givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}
		
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
    	AbstractExecution exe = new FindCentralEdgesExecution(degreeAlgorithm, Algorithm.Contribution, candidates, givenVertices, givenEdges, Bound.GroupSize, k);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, GroupDegreeController.ALIAS, "getCentralEdgesAsynch", null);
    	return exeID;
	}
}
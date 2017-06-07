package server.closeness;

import javolution.util.FastList;


import javolution.util.Index;
import server.closeness.executions.ClosenessAlgorithmExecution;
import server.closeness.executions.FindCentralEdgesExecution;
import server.closeness.executions.FindCentralVerticesExecution;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.closeness.searchAlgorithms.AbsGreedyClosenessNG;
import algorithms.centralityAlgorithms.closeness.searchAlgorithms.AbsGreedyEdgeClosenessNG;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

public class ClosenessController 
{
	public static final String ALIAS = "Closeness";
	

	public int create(int netID, int formulaID){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		IClosenessFormula formula = DataBase.getFormula(formulaID);
		
		return create(netID, graph, formula);
	}

	private int create(int netID, GraphInterface<Index, BasicVertexInfo> graph, IClosenessFormula formula) {
		IClosenessAlgorithm closeness = null;
		if (graph != null){
			try{
				closeness = new ClosenessAlgorithm(graph, formula, new DummyProgress(), 1);
			}
			catch(Exception ex){
				LoggingManager.getInstance().writeSystem("An exception has occured while creating ClosenesAlgorithm:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), ClosenessController.ALIAS, "create", ex);
			}
		}
		int algID = DataBase.putAlgorithm(closeness, netID);
		return algID;
	}
	
	public int create(int netID){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
				ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
				new DummyProgress(), 1.0);

		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_STANDARD, dists, null);
		
		return create(netID, graph, formula);
	}
	
	public int createAsynch(int netID){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
				ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
				new DummyProgress(), 1.0);
		
		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_STANDARD, dists, null);
		return createAsynch(netID, formula);
	}
	
	/** Starts an execution that creates closeness algorithm with the given network.
	 * @param network index
	 * @return Execution index in the Database */
	public int createAsynch(int netID, int formulaID){
		LoggingManager.getInstance().writeTrace("Starting creating ClosenessAlgorithm.", ClosenessController.ALIAS, "createAsynch", null);
    	
		IClosenessFormula formula = DataBase.getFormula(formulaID);
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
		return createAsynch(netID, formula);
	}

	private int createAsynch(int netID, IClosenessFormula formula) {
		AbstractExecution exe = new ClosenessAlgorithmExecution(netID, formula);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, ClosenessController.ALIAS, "createAsynch", null);
    	return exeID;
	}
	
	/** Removes the given closeness algorithm from the Database maps.
	 * @param algorithm index
	 * @return 0 */
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}

	/** Returns the closeness value of the given vertex in the given closeness algorithm instance.
	 * @param algorithm index
	 * @param vertex
	 * @return closeness value */
	public double getCloseness(int algID, int v){
		return ((IClosenessAlgorithm)DataBase.getAlgorithm(algID)).getCloseness(v);
	}
	
	/** Returns an array of closeness of the given vertices in the given Degree algorithm instance.
	 * The order of the closeness values in the array corresponds to the order of the given vertices.
	 * @param algorithm index
	 * @param array of vertices
	 * @return array of closeness values */
	public Object[] getCloseness(int algID, int[] vertices){
		Object [] closenessValues = new Object [vertices.length];
		IClosenessAlgorithm cAlg = (IClosenessAlgorithm)DataBase.getAlgorithm(algID);
		for (int i = 0; i < vertices.length; i++)
			closenessValues[i] = cAlg.getCloseness(vertices[i]);
		return closenessValues;
	}
	
	/** Returns an array of closeness values of all vertices in the given Degree algorithm instance.
	 * The order of the closeness values in the array corresponds to the order of the vertices in the graph.
	 * @param algorithm index
	 * @return array of closeness values */
	public Object[] getCloseness(int algID){
		IClosenessAlgorithm cAlg = (IClosenessAlgorithm)DataBase.getAlgorithm(algID);
		double[] c = cAlg.getCloseness();
		Object[] cVals = new Object[c.length];
		for (int i = 0; i < cVals.length; i++)
			cVals[i] = new Double(c[i]);
		return cVals;
	}
	
	/** Returns the sum of closeness values of the given vertices in the given Degree instance.
	 * @param algorithm index
	 * @param array of vertices
	 * @return closeness value */
	public double getSumGroup(int algID, Object[] vertices, Object[] edges){
		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		double gCloseness = 0;
		try{
			for (int i = 0; i < vertices.length; i++){
	    		gCloseness += closenessAlgorithm.getCloseness(((Integer)vertices[i]).intValue());
	    	}
	    	for (int v = 0; v < edges.length; v = v + 2){
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index.valueOf(((Integer)edges[v]).intValue()), Index.valueOf(((Integer)edges[v + 1]).intValue()));
				gCloseness += closenessAlgorithm.getCloseness(e);
			}
		}
		catch(RuntimeException ex){
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured during getSumGroup.", ClosenessController.ALIAS, "getSumGroup", ex);
		}
		LoggingManager.getInstance().writeTrace("Finishing getSumGroup.", ClosenessController.ALIAS, "getSumGroup", null);
		return gCloseness;
	}
	
	/** Searches for deployment of vertices (using TopK algorithm) according to given parameters.
     * @param Closeness algorithm index
     * @param k is the size of the desired deployment
     * @param vertex candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return array of vertices */
	public Object[] getCentralVertices(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm)DataBase.getAlgorithm(algID);
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
			Index [] cv = AbsGreedyClosenessNG.findVertices(Algorithm.TopK, candidates, givenVertices, givenEdges, closenessAlgorithm, Bound.GroupSize, k, new DummyProgress(), 1);
			centralVertices = new Object [cv.length];
			
			int i = 0;
			for (Index v : cv){
				centralVertices[i++] = new Integer(((Index)v).intValue());
			}
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage(), ClosenessController.ALIAS, "getCentralVertices", ex);
		}
		return centralVertices;
	}
	
	/** Searches for deployment of edges (using TopK algorithm) according to given parameters.
     * @param Closeness algorithm index
     * @param k is the size of the desired deployment
     * @param edge candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return array of edges
     * In the candidates array and in the result array every two successive elements represent an edge.
     * Namely, A[0]-A[1], A[2]-A[3], A[4]-A[5] represent 3 edges. */
	public Object[] getCentralEdges(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm)DataBase.getAlgorithm(algID);
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
			AbstractSimpleEdge<Index,BasicVertexInfo>[] ce = AbsGreedyEdgeClosenessNG.findEdges(Algorithm.TopK, candidates, givenVertices, givenEdges, closenessAlgorithm, Bound.GroupSize, k, new DummyProgress(), 1);
			centralEdges = new Object [ce.length*2];
			
			int i = 0;
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : ce){
				centralEdges[i++] = new Integer(((Index)e.getV0()).intValue());
				centralEdges[i++] = new Integer(((Index)e.getV1()).intValue());
			}
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage(), ClosenessController.ALIAS, "getCentralEdges", ex);
		}
		return centralEdges;
	}
	
	/** Starts an execution which searches for deployment of vertices (using TopK algorithm) according to given parameters.
     * @param Closeness algorithm index
     * @param k is the size of the desired deployment
     * @param candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return execution index */
	public int getCentralVerticesAsynch(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		LoggingManager.getInstance().writeTrace("Starting searching central vertices.", ClosenessController.ALIAS, "getCentralVerticesAsynch", null);
    	
		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm)DataBase.getAlgorithm(algID);
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
    	AbstractExecution exe = new FindCentralVerticesExecution(candidates, givenVertices, givenEdges, closenessAlgorithm, Algorithm.TopK, Bound.GroupSize, k);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, ClosenessController.ALIAS, "getCentralVerticesAsynch", null);
    	return exeID;
	}
	
	/** Starts an execution which searches for deployment of edges (using TopK algorithm) according to given parameters.
     * @param Closeness algorithm index
     * @param k is the size of the desired deployment
     * @param edge candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return execution index
     * In the candidates array and in the result array every two successive elements represent an edge.
     * Namely, A[0]-A[1], A[2]-A[3], A[4]-A[5] represent 3 edges. */
	public int getCentralEdgesAsynch(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		LoggingManager.getInstance().writeTrace("Starting searching central edges.", ClosenessController.ALIAS, "getCentralEdgesAsynch", null);
    	
		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm)DataBase.getAlgorithm(algID);
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
    	AbstractExecution exe = new FindCentralEdgesExecution(candidates, givenVertices, givenEdges, closenessAlgorithm, Algorithm.TopK, Bound.GroupSize, k);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, ClosenessController.ALIAS, "getCentralEdgesAsynch", null);
    	return exeID;
	}
}
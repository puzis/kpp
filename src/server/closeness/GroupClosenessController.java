package server.closeness;


import javolution.util.FastList;

import javolution.util.Index;
import server.closeness.executions.EvaluateExecution;
import server.closeness.executions.FindCentralEdgesExecution;
import server.closeness.executions.FindCentralVerticesExecution;
import server.closeness.executions.GClosenessAlgorithmExecution;
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
import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithmEnum;
import algorithms.centralityAlgorithms.closeness.GroupClosenessAlgorithmMSBFS;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.OptimizedGreedyGroupCloseness;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.closeness.searchAlgorithms.AbsGreedyClosenessNG;
import algorithms.centralityAlgorithms.closeness.searchAlgorithms.AbsGreedyEdgeClosenessNG;

public class GroupClosenessController {
	
	public static final String ALIAS = "GCloseness";
	
	
	public int create(int netID, int formulaID, int algType){
		
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		IClosenessFormula formula = DataBase.getFormula(formulaID);
		IClosenessAlgorithm closeness = null;
		switch (ClosenessAlgorithmEnum.values()[algType])
		{
		case STANDARD:
		{
			closeness = new ClosenessAlgorithm(graph, formula, new DummyProgress(), 1);
			break;
		}
		case OPTIMIZED:
		{
			closeness = new OptimizedGreedyGroupCloseness(graph, formula, new DummyProgress(), 1);
			break;
		}
		case MSBFS:
		{
			closeness = new GroupClosenessAlgorithmMSBFS(graph, formula);
		}
		}
		int algID = DataBase.putAlgorithm(closeness, netID);
		return algID;
	}
	
	public int createAsynch(int netID, int formulaID, int algType){
		LoggingManager.getInstance().writeTrace(
				"Starting creating Group Closeness Algorithm.",
				GroupClosenessController.ALIAS, "createAsynch", null);

		IClosenessFormula formula = DataBase.getFormula(formulaID);
		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new GClosenessAlgorithmExecution(netID, formula, algType);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();
	
		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT,
				GroupClosenessController.ALIAS, "createAsynch", null);
		return exeID;
	}
	
	/**
	 * Removes the given group closeness algorithm from the Database maps.
	 * 
	 * @param algorithm
	 *            index
	 * @return 0
	 */
	public int destroy(int algID) {
		DataBase.releaseAlgorithm(algID);
		return 0;
	}

	/**
	 * Returns group closeness value of the given vertices and given edges in
	 * the given group closeness algorithm instance.
	 * 
	 * @param algorithm
	 *            index
	 * @param array
	 *            of vertices
	 * @param array
	 *            of edges, where the edges are represented by
	 *            edges[0]-edges[1], edges[2]-edges[3], edges[4]-edges[5] etc.
	 * @return group closeness value
	 */
	public double getGroupCloseness(int algID, Object[] vertices, Object[] edges) {
		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm) DataBase
				.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(
				DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		double gCloseness = -1;
		boolean success = true;
		try {
			Object[] group = new Object[vertices.length + edges.length / 2];
			for (int i = 0; i < vertices.length; i++) {
				group[i] = Index.valueOf(((Integer) vertices[i]).intValue());
			}

			for (int v = 0, i = vertices.length; v < edges.length; v = v + 2, i++) {
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(
						Index.valueOf(((Integer) edges[v]).intValue()),
						Index.valueOf(((Integer) edges[v + 1]).intValue()));
				group[i] = e;
			}
			try {
				gCloseness = closenessAlgorithm.getGroupCloseness(group);
			} catch (Exception ex) {
				LoggingManager.getInstance()
						.writeSystem(ex.getMessage(),
								GroupClosenessController.ALIAS,
								"getGroupCloseness", ex);
			} finally {
				success = (gCloseness != -1);
				if (!success) {
					LoggingManager
							.getInstance()
							.writeSystem(
									"getGroupCloseness has NOT completed successfully.",
									GroupClosenessController.ALIAS,
									"getGroupCloseness", null);
					success = false;
				}
			}
		} catch (RuntimeException ex) {
			LoggingManager.getInstance().writeSystem(
					"A RuntimeException has occured during getGroupCloseness.",
					GroupClosenessController.ALIAS, "getGroupCloseness", ex);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing getGroupCloseness.",
				GroupClosenessController.ALIAS, "getGroupCloseness", null);
		return gCloseness;
	}

	/**
	 * Starts an execution which calculates group closeness value of the given
	 * vertices and given edges in the given group closeness algorithm instance.
	 * 
	 * @param algorithm
	 *            index
	 * @param array
	 *            of vertices
	 * @param array
	 *            of edges, where the edges are represented by
	 *            edges[0]-edges[1], edges[2]-edges[3], edges[4]-edges[5] etc.
	 * @return execution index in the database
	 */
	public int getGroupClosenessAsynch(int algID, Object[] vertices,
			Object[] edges) {
		LoggingManager.getInstance()
				.writeTrace("Starting group evaluation.",
						GroupClosenessController.ALIAS,
						"getGroupClosenessAsynch", null);

		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm) DataBase
				.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(
				DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		int exeID = -1;
		try {
			Object[] group = new Object[vertices.length + edges.length / 2];
			for (int i = 0; i < vertices.length; i++) {
				group[i] = Index.valueOf(((Integer) vertices[i]).intValue());
			}

			for (int v = 0, i = vertices.length; v < edges.length; v = v + 2, i++) {
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(
						Index.valueOf(((Integer) edges[v]).intValue()),
						Index.valueOf(((Integer) edges[v + 1]).intValue()));
				group[i] = e;
			}

			/**
			 * Create new execution (Runnable). Store execution into database.
			 * Create new thread, give it the execution and start it. Return
			 * exeID. (At the end of run() the execution parameters of progress
			 * and success are updated.)
			 */
			AbstractExecution exe = new EvaluateExecution(closenessAlgorithm, group);
			exeID = DataBase.putExecution(exe);
			exe.setID(exeID);
			Thread t = new Thread(exe);
			t.start();
		} catch (RuntimeException ex) {
			LoggingManager
					.getInstance()
					.writeSystem(
							"A RuntimeException has occured during getGroupClosenessAsynch.",
							GroupClosenessController.ALIAS,
							"getGroupClosenessAsynch", ex);
		}
		LoggingManager.getInstance()
				.writeTrace(ServerConstants.RETURNING_TO_CLIENT,
						GroupClosenessController.ALIAS,
						"getGroupClosenessAsynch", null);
		return exeID;
	}

	/**
	 * Searches for deployment of vertices (using Contribution algorithm)
	 * according to given parameters.
	 * 
	 * @param Group
	 *            closeness algorithm index
	 * @param k
	 *            is the size of the desired deployment
	 * @param vertex
	 *            candidates for the deployment (can be an empty list)
	 * @param givenVertices
	 *            are the already deployed vertices (can be an empty list)
	 * @param givenLinks
	 *            are the already deployed links (can be an empty list)
	 * @return array of vertices
	 */
	public Object[] getCentralVertices(int algID, int k,
			Object[] candidatesObj, Object[] givenVerticesObj,
			Object[] givenEdgesObj) {
		return 	getCentralVerticesByMethod(algID,k,
				candidatesObj, givenVerticesObj,
				givenEdgesObj, Algorithm.Contribution);
	}
	
	
	public Object[] getCentralVerticesTopK(int algID, int k,
			Object[] candidatesObj, Object[] givenVerticesObj,
			Object[] givenEdgesObj) {
			return 	getCentralVerticesByMethod(algID,k,
					candidatesObj, givenVerticesObj,
					givenEdgesObj, Algorithm.TopK);
	}
	
	
	private Object[] getCentralVerticesByMethod(int algID, int k,
			Object[] candidatesObj, Object[] givenVerticesObj,
			Object[] givenEdgesObj, Algorithm algMethod) {
		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm) DataBase
				.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(
				DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<Index> candidates = new FastList<Index>();

		if (candidatesObj != null) {
			for (int v = 0; v < candidatesObj.length; v++)
				candidates.add(Index.valueOf(((Integer) candidatesObj[v])
						.intValue()));
		}
		int[] givenVertices = new int[givenVerticesObj.length];
		for (int i = 0; i < givenVertices.length; i++)
			givenVertices[i] = ((Integer) givenVerticesObj[i]).intValue();

		AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length / 2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++) {
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(
					Index.valueOf(((Integer) givenEdgesObj[v]).intValue()),
					Index.valueOf(((Integer) givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}
		Object[] centralVertices = null;
		try {
			Index[] cv = AbsGreedyClosenessNG.findVertices(
					algMethod, candidates, givenVertices,
					givenEdges, closenessAlgorithm, Bound.GroupSize, k,
					new DummyProgress(), 1);
			centralVertices = new Object[cv.length];

			int i = 0;
			for (Index v : cv) {
				centralVertices[i++] = Integer.valueOf(((Index) v).intValue());
			}
		} catch (Exception ex) {
			LoggingManager.getInstance().writeSystem(ex.getMessage(),
					GroupClosenessController.ALIAS, "getCentralVertices", ex);
		}
		return centralVertices;
	}
	

	/**
	 * Searches for deployment of edges (using Contribution algorithm) according
	 * to given parameters.
	 * 
	 * @param Group
	 *            closeness algorithm index
	 * @param k
	 *            is the size of the desired deployment
	 * @param edge
	 *            candidates for the deployment (can be an empty list)
	 * @param givenVertices
	 *            are the already deployed vertices (can be an empty list)
	 * @param givenLinks
	 *            are the already deployed links (can be an empty list)
	 * @return array of edges In the candidates array and in the result array
	 *         every two successive elements represent an edge. Namely,
	 *         A[0]-A[1], A[2]-A[3], A[4]-A[5] represent 3 edges.
	 */
	public Object[] getCentralEdges(int algID, int k, Object[] candidatesObj,
			Object[] givenVerticesObj, Object[] givenEdgesObj) {
		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm) DataBase
				.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(
				DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();

		if (candidatesObj != null) {
			for (int v = 0; v < candidatesObj.length; v = v + 2) {
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index
						.valueOf(((Integer) candidatesObj[v]).intValue()),
						Index.valueOf(((Integer) candidatesObj[v + 1])
								.intValue()));
				candidates.add(e);
			}
		}
		int[] givenVertices = new int[givenVerticesObj.length];
		for (int i = 0; i < givenVertices.length; i++)
			givenVertices[i] = ((Integer) givenVerticesObj[i]).intValue();

		AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length / 2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++) {
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(
					Index.valueOf(((Integer) givenEdgesObj[v]).intValue()),
					Index.valueOf(((Integer) givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}

		Object[] centralEdges = null;
		try {
			AbstractSimpleEdge<Index,BasicVertexInfo>[] ce = AbsGreedyEdgeClosenessNG.findEdges(
					Algorithm.Contribution, candidates, givenVertices,
					givenEdges, closenessAlgorithm, Bound.GroupSize, k,
					new DummyProgress(), 1);
			centralEdges = new Object[ce.length*2];

			int i = 0;
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : ce) {
				centralEdges[i++] = Integer.valueOf(((Index) e.getV0()).intValue());
				centralEdges[i++] = Integer.valueOf(((Index) e.getV1()).intValue());
			}
		} catch (Exception ex) {
			LoggingManager.getInstance().writeSystem(ex.getMessage(),
					GroupClosenessController.ALIAS, "getCentralEdges", ex);
		}
		return centralEdges;
	}

	/**
	 * Starts an execution which searches for deployment of vertices (using
	 * Contribution algorithm) according to given parameters.
	 * 
	 * @param Group
	 *            closeness index
	 * @param k
	 *            is the size of the desired deployment
	 * @param candidates
	 *            for the deployment (can be an empty list)
	 * @param givenVertices
	 *            are the already deployed vertices (can be an empty list)
	 * @param givenLinks
	 *            are the already deployed links (can be an empty list)
	 * @return execution index
	 */
	public int getCentralVerticesAsynch(int algID, int k,
			Object[] candidatesObj, Object[] givenVerticesObj,
			Object[] givenEdgesObj) {
		LoggingManager.getInstance().writeTrace(
				"Starting searching central vertices.",
				GroupClosenessController.ALIAS, "getCentralVerticesAsynch",
				null);

		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm) DataBase
				.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(
				DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<Index> candidates = new FastList<Index>();

		if (candidatesObj != null) {
			for (int v = 0; v < candidatesObj.length; v++)
				candidates.add(Index.valueOf(((Integer) candidatesObj[v])
						.intValue()));
		}
		int[] givenVertices = new int[givenVerticesObj.length];
		for (int i = 0; i < givenVertices.length; i++)
			givenVertices[i] = ((Integer) givenVerticesObj[i]).intValue();

		AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length / 2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++) {
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(
					Index.valueOf(((Integer) givenEdgesObj[v]).intValue()),
					Index.valueOf(((Integer) givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new FindCentralVerticesExecution(candidates,
				givenVertices, givenEdges, closenessAlgorithm,
				Algorithm.Contribution, Bound.GroupSize, k);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT,
				GroupClosenessController.ALIAS, "getCentralVerticesAsynch",
				null);
		return exeID;
	}

	/**
	 * Starts an execution which searches for deployment of edges (using
	 * Contribution algorithm) according to given parameters.
	 * 
	 * @param Group
	 *            closeness index
	 * @param k
	 *            is the size of the desired deployment
	 * @param edge
	 *            candidates for the deployment (can be an empty list)
	 * @param givenVertices
	 *            are the already deployed vertices (can be an empty list)
	 * @param givenLinks
	 *            are the already deployed links (can be an empty list)
	 * @return execution index In the candidates array and in the result array
	 *         every two successive elements represent an edge. Namely,
	 *         A[0]-A[1], A[2]-A[3], A[4]-A[5] represent 3 edges.
	 */
	public int getCentralEdgesAsynch(int algID, int k, Object[] candidatesObj,
			Object[] givenVerticesObj, Object[] givenEdgesObj) {
		LoggingManager.getInstance().writeTrace(
				"Starting searching central edges.",
				GroupClosenessController.ALIAS, "getCentralEdgesAsynch", null);

		IClosenessAlgorithm closenessAlgorithm = (IClosenessAlgorithm) DataBase
				.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(
				DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();

		if (candidatesObj != null) {
			for (int v = 0; v < candidatesObj.length; v = v + 2) {
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index
						.valueOf(((Integer) candidatesObj[v]).intValue()),
						Index.valueOf(((Integer) candidatesObj[v + 1])
								.intValue()));
				candidates.add(e);
			}
		}
		int[] givenVertices = new int[givenVerticesObj.length];
		for (int i = 0; i < givenVertices.length; i++)
			givenVertices[i] = ((Integer) givenVerticesObj[i]).intValue();

		AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges = new AbstractSimpleEdge[givenEdgesObj.length / 2];
		for (int v = 0, i = 0; v < givenEdgesObj.length; v = v + 2, i++) {
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(
					Index.valueOf(((Integer) givenEdgesObj[v]).intValue()),
					Index.valueOf(((Integer) givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new FindCentralEdgesExecution(candidates,
				givenVertices, givenEdges, closenessAlgorithm,
				Algorithm.Contribution, Bound.GroupSize, k);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT,
				GroupClosenessController.ALIAS, "getCentralEdgesAsynch", null);
		return exeID;
	}
}
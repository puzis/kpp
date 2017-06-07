package server.shortestPathBetweenness;

import java.io.File;


import javolution.util.FastList;
import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.Network;
import server.common.ServerConstants;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import server.shortestPathBetweenness.executions.AnalysisExecution;
import server.shortestPathBetweenness.executions.EvaluateExecution;
import server.shortestPathBetweenness.executions.FindCentralEdgesExecution;
import server.shortestPathBetweenness.executions.FindCentralVerticesExecution;
import server.shortestPathBetweenness.executions.StoreAnalysisExecution;
import server.shortestPathBetweenness.executions.WriteDeploymentExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.GraphPrinter;
import algorithms.centralityAlgorithms.betweenness.brandes.AbsGreedyBetweeness;
import algorithms.centralityAlgorithms.betweenness.brandes.AbsGreedyEdgeBetweeness;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.GreedyBetweenessContribution;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

public class GBCController {
	public static final String ALIAS = "GBC";

	/**
	 * Creates, if needed, an instance of a dataworkshop corresponding to the
	 * given network. Checks whether *.dw file already exists with the given
	 * network's name exists. If the *.dw file exists, it is loaded and saved in
	 * the DataBase, otherwise, a new *.dw file is created. Note: only
	 * DenseTrafficMatrix is created, we might want to use SparseTrafficMatrix
	 * sometime.
	 * 
	 * @param netID
	 * @param communicationWeightsStr
	 * @param createRoutingTable
	 * @return index of the dataworkshop in the DataBase.
	 */
	public int create(int netID, String communicationWeightsStr,
			boolean createRoutingTable, boolean fullPrecomutation) {
		Network network = DataBase.getNetwork(netID);
		int algID = loadDataWorkshop(network, netID);

		/**
		 * If .dw file does not exists (algID == -1), then try to create a new
		 * Dataworkshop.
		 */
		if (algID == -1) {
			GraphInterface<Index,BasicVertexInfo> graph = network.getGraphSimple();
			DataWorkshop dw = null;
			AbsTrafficMatrix communicationWeights = null;
			if (graph != null) {
				if (communicationWeightsStr != null
						&& !communicationWeightsStr.isEmpty())
					communicationWeights = new DenseTrafficMatrix(
							communicationWeightsStr, graph
									.getNumberOfVertices()); // WeightsLoader.loadWeightsFromString(communicationWeightsStr,
																// graph.getNumberOfVertices());
				else
					communicationWeights = new DefaultTrafficMatrix(graph
							.getNumberOfVertices()); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());

				try {
					dw = new DataWorkshop(
							ShortestPathAlgorithmInterface.DEFAULT, graph,
							communicationWeights, createRoutingTable,
							new DummyProgress(), 1);
					algID = DataBase.putAlgorithm(dw, netID);
				} catch (Exception ex) {
					LoggingManager.getInstance().writeSystem(
							"An exception has occured while creating dataWorkshop:\n"
									+ ex.getMessage() + "\n"
									+ ex.getStackTrace(), GBCController.ALIAS,
							"create", ex);
				}
			}
		}
		return algID;
	}

	public int create(int netID, int tmID, boolean createRoutingTable, boolean fullPrecomutation) {
		return create(netID, tmID, "BFS", createRoutingTable, fullPrecomutation);
	}
	public int create(int netID, int tmID, String spAlg, boolean createRoutingTable, boolean fullPrecomutation) {
		Network network = DataBase.getNetwork(netID);
		/** First check whether the .dw file already exists. */
		int algID = loadDataWorkshop(network, netID);

		/**
		 * If .dw file does not exists (algID == -1), then try to create a new
		 * Dataworkshop.
		 */
		if (algID == -1) {
			GraphInterface<Index,BasicVertexInfo> graph = network.getGraphSimple();
			DataWorkshop dw = null;
			AbsTrafficMatrix communicationWeights;
			if (tmID != -1)
				communicationWeights = DataBase.getTrafficMatrix(tmID);
			else
				communicationWeights = new DefaultTrafficMatrix(graph
						.getNumberOfVertices());
			if (graph != null) {
				try {
					dw = new DataWorkshop(
							ShortestPathAlgorithmInterface.ShortestPathAlg.valueOf(spAlg), graph,
							communicationWeights, createRoutingTable,
							new DummyProgress(), 1);
					algID = DataBase.putAlgorithm(dw, netID);
				} catch (Exception ex) {
					LoggingManager.getInstance().writeSystem(
							"An exception has occured while creating dataWorkshop:\n"
									+ ex.getMessage() + "\n"
									+ ex.getStackTrace(), GBCController.ALIAS,
							"create", ex);
				}
			}
		}
		return algID;
	}

	private int loadDataWorkshop(Network network, int netID) {
		int algID = -1;
		try {
			File dwFile = getDataWorkshopFile(network);
			if (dwFile.exists()) {
				DataWorkshop dataWorkshop = new DataWorkshop();
				try {
					dataWorkshop.loadFromDisk(dwFile, new DummyProgress(), 1);
					algID = DataBase.putAlgorithm(dataWorkshop, netID);
				} catch (Exception ex) {
					LoggingManager.getInstance().writeSystem(
							"Couldn't load " + network.getName() + ".dw.",
							GBCController.ALIAS, "create", ex);
				}
			}
		} catch (RuntimeException ex) {
			LoggingManager.getInstance().writeSystem(
					"The file " + ServerConstants.DATA_DIR + network.getName()
							+ ".dw doesn't exist.", GBCController.ALIAS,
					"create", null);
		}
		return algID;
	}
	
	private File getDataWorkshopFile(Network network){
		return new File(ServerConstants.DATA_DIR + network.getName()
				+ ".dw");
	}

	/**
	 * Checks if the network has a matching .dw file
	 * @param network the network handle
	 * @return 0 if has a matching analysis file, -1 if not.
	 */
	public int dataWorkshopExists(int netID){
		
		Network network = DataBase.getNetwork(netID);
		if (getDataWorkshopFile(network).exists())
			return 0;
		return -1;		
	}
	
	/**
	 * Deletes the data workshop file for the network from the disk.
	 * @param network the network handle
	 * @return 0 if operation was successful, -1 if not.
	 */
	public int deleteAnalysis(int netID){
		Network network = DataBase.getNetwork(netID);
		File dwFile = getDataWorkshopFile(network);
		if (!dwFile.exists())
			return 0;
		try{
			dwFile.delete();
			return 0;
		}
		catch (SecurityException e){
			return -1;
		}
	}

	/**
	 * Creates, if needed, an instance of a dataworkshop corresponding to the
	 * given network. Checks whether *.dw file already exists with the given
	 * network's name exists. If the *.dw file exists, it is loaded and saved in
	 * the DataBase, otherwise, a new *.dw file is created. Note: only
	 * DenseTrafficMatrix is created, we might want to use SparseTrafficMatrix
	 * sometime.
	 * 
	 * @param netID
	 * @param communicationWeightsStr
	 * @param createRoutingTable
	 * @return index of the analysis execution.
	 */
	public int createAsynch(int netID, String communicationWeightsStr,
			boolean createRoutingTable) {
		LoggingManager.getInstance().writeTrace(
				"Starting network analysis (creating dataworkshop).",
				GBCController.ALIAS, "createAsynch", null);

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new AnalysisExecution(netID,
				communicationWeightsStr, createRoutingTable);
		return createGBCAsynch(exe);
	}

	public int createAsynch(int netID, int tmID, boolean createRoutingTable) {
		LoggingManager.getInstance().writeTrace(
				"Starting network analysis (creating dataworkshop).",
				GBCController.ALIAS, "createAsynch", null);

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new AnalysisExecution(netID, tmID,
				createRoutingTable);
		return createGBCAsynch(exe);
	}

	public int createAsynch(int netID, boolean createRoutingTable) {
		return createAsynch(netID, -1, createRoutingTable);
	}

	private int createGBCAsynch(AbstractExecution exe) {
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, GBCController.ALIAS,
				"createAsynch", null);
		return exeID;
	}

	/**
	 * Saves the given dataworkshop to a file with the name of the given
	 * network.
	 * 
	 * @param algID
	 *            - Index of the dataworkshop in the DataBase.
	 * @param netID
	 *            - Index of the network in the DataBase.
	 * @return true.
	 */
	public boolean storeAnalysis(int algID, int netID) {
		LoggingManager.getInstance().writeTrace("Storing network analysis.",
				GBCController.ALIAS, ServerConstants.STORE_ANALYSIS, null);

		DataWorkshop dw = (DataWorkshop) DataBase.getAlgorithm(algID);
		String filename = ServerConstants.DATA_DIR
				+ DataBase.getNetwork(netID).getName() + ".dw";
		try {
			dw.saveToDisk(filename, new DummyProgress(), 1);
		} catch (Exception ex) {
			LoggingManager.getInstance().writeSystem(
					"An exception has occured while storing dataWorkshop:\n"
							+ ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex),
					GBCController.ALIAS, ServerConstants.STORE_ANALYSIS, ex);
		}
		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, GBCController.ALIAS,
				ServerConstants.STORE_ANALYSIS, null);
		return true;
	}

	/**
	 * Saves the given dataworkshop to a file with the name of the given
	 * network.
	 * 
	 * @param algID
	 *            - Index of the dataworkshop in the DataBase.
	 * @param netID
	 *            - Index of the network in the DataBase.
	 * @return index of the store execution.
	 */
	public int storeAnalysisAsynch(int algID, int netID) {
		LoggingManager.getInstance().writeTrace("Storing network analysis.",
				GBCController.ALIAS, ServerConstants.STORE_ANALYSIS, null);

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new StoreAnalysisExecution(algID, netID);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, GBCController.ALIAS,
				ServerConstants.STORE_ANALYSIS, null);
		return exeID;
	}

	/**
	 * Returns as file contents the analyzed graph and the computed deployment.
	 * 
	 * @param dataworkshop
	 *            index
	 * @param network
	 *            index
	 * @return String of file contents
	 */
	public String writeDeployment(int algID, int netID) {
		String result = null;
		try {
			GreedyBetweenessContribution.writeDeployment(DataBase.getNetwork(
					netID).getGraphSimple(), (DataWorkshop) DataBase
					.getAlgorithm(algID), new DummyProgress(), 1);
			GraphPrinter gPrinter = new GraphPrinter(DataBase.getNetwork(netID)
					.getGraphSimple());
			result = gPrinter.getAnalyzedFile((DataWorkshop) DataBase
					.getAlgorithm(algID));
		} catch (Exception ex) {
			LoggingManager.getInstance().writeSystem(
					"An exception has occured while writting deployment of dataWorkshop:\n"
							+ ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex),
					GBCController.ALIAS, "writeDeployment", ex);
		}
		return result;
	}

	/**
	 * Starts an execution which creates the String of file contents the
	 * analyzed graph and the computed deployment.
	 * 
	 * @param dataworkshop
	 *            index
	 * @param network
	 *            index
	 * @return execution index in the Database
	 */
	public int writeDeploymentAsynch(int algID, int netID) {
		LoggingManager.getInstance().writeTrace(
				"Starting writting deployment.", GBCController.ALIAS,
				"writeDeploymentAsynch", null);

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new WriteDeploymentExecution(algID, DataBase
				.getNetwork(netID));
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, GBCController.ALIAS,
				"writeDeploymentAsynch", null);
		return exeID;
	}

	/**
	 * Searches for deployment of vertices (using Contribution algorithm)
	 * according to given parameters.
	 * 
	 * @param Dataworkshop
	 *            index
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
	public Object[] getEdgeBetweenness(int algID){
 		DataWorkshop dw = (DataWorkshop) DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(
				DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		
		Object[] result = new Double[graph.getNumberOfEdges()*3];
		int i=0;
		for(AbstractSimpleEdge<Index,BasicVertexInfo> e : graph.getEdges()) {
			int v0 = e.getV0().intValue();
			result[i+0] = new Double(v0);
			int v1 = e.getV1().intValue();
			result[i+1] = new Double(v1);
			result[i+2] = new Double(dw.getPairBetweenness(v0,v1));
			i+=3;
		}
		return result;
	}	
		
	/**
	 * Returns the sum of betweenness values of the given vertices in the given
	 * Dataworkshop instance.
	 * 
	 * @param algorithm
	 *            index
	 * @param array
	 *            of vertices
	 * @return betweenness value
	 */
	public double getSumGroup(int algID, Object[] vertices, Object[] edges) {
		DataWorkshop dw = (DataWorkshop) DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(
				DataBase.getNetworkOfAlgorithm(algID)).getGraphSimple();
		double gBetweenness = -1;
		try {
			Object[] group = new Object[vertices.length + edges.length / 2];
			for (int i = 0; i < vertices.length; i++) {
				group[i] = Index.valueOf(((Integer) vertices[i]).intValue());
			}

			for (int v = 0, i = vertices.length; v < edges.length; v = v + 2, i++) {
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index
						.valueOf(((Integer) edges[v]).intValue()), Index
						.valueOf(((Integer) edges[v + 1]).intValue()));
				group[i] = e;
			}
			try {
				gBetweenness = CandidatesBasedAlgorithm.calculateSumGroup(dw,
						group, new DummyProgress(), 1);
			} catch (Exception ex) {
				LoggingManager.getInstance().writeSystem(ex.getMessage(),
						GBCController.ALIAS, "getSumGroup", ex);
			}
		} catch (RuntimeException ex) {
			LoggingManager.getInstance().writeSystem(
					"A RuntimeException has occured during getSumGroup.",
					GBCController.ALIAS, "getSumGroup", ex);
		}
		LoggingManager.getInstance().writeTrace("Finishing getSumGroup.",
				GBCController.ALIAS, "getSumGroup", null);
		return gBetweenness;
	}

	/**
	 * Returns group betweenness value of the given vertices and given edges in
	 * the given Dataworkshop instance.
	 * 
	 * @param algorithm
	 *            index
	 * @param array
	 *            of vertices
	 * @param array
	 *            of edges, where the edges are represented by
	 *            edges[0]-edges[1], edges[2]-edges[3], edges[4]-edges[5] etc.
	 * @return group betweenness value
	 */
	public double getGBC(int algID, Object[] vertices, Object[] edges) {
		DataWorkshop dw = (DataWorkshop) DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
		double gbc = -1;
		boolean success = true;
		try {
			Object[] group = new Object[vertices.length + edges.length / 2];
			for (int i = 0; i < vertices.length; i++) {
				group[i] = Index.valueOf(((Integer) vertices[i]).intValue());
			}

			for (int v = 0, i = vertices.length; v < edges.length; v = v + 2, i++) {
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index
						.valueOf(((Integer) edges[v]).intValue()), Index
						.valueOf(((Integer) edges[v + 1]).intValue()));
				group[i] = e;
			}
			try {
				gbc = CandidatesBasedAlgorithm.calculateGB(dw, group,
						new DummyProgress(), 1);
			} catch (Exception ex) {
				LoggingManager.getInstance().writeSystem(ex.getMessage(),
						GBCController.ALIAS, "getGBC", ex);
			} finally {
				success = (gbc != -1);
				if (!success) {
					LoggingManager.getInstance().writeSystem(
							"getGBC has NOT completed successfully.",
							GBCController.ALIAS, "getGBC", null);
					success = false;
				}
			}
		} catch (RuntimeException ex) {
			LoggingManager.getInstance().writeSystem(
					"A RuntimeException has occured during getGBC.",
					GBCController.ALIAS, "getGBC", ex);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing getGBC.",
				GBCController.ALIAS, "getGBC", null);
		return gbc;
	}

	/**
	 * Starts an execution which calculates group betweenness value of the given
	 * vertices and given edges in the given Dataworkshop instance.
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
	public int getGBCAsynch(int algID, Object[] vertices, Object[] edges) {
		LoggingManager.getInstance().writeTrace("Starting group evaluation.",
				GBCController.ALIAS, "getGBCAsynch", null);

		DataWorkshop dw = (DataWorkshop) DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
		int exeID = -1;
		try {
			Object[] group = new Object[vertices.length + edges.length / 2];
			for (int i = 0; i < vertices.length; i++) {
				group[i] = Index.valueOf(((Integer) vertices[i]).intValue());
			}

			for (int v = 0, i = vertices.length; v < edges.length; v = v + 2, i++) {
				AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index
						.valueOf(((Integer) edges[v]).intValue()), Index
						.valueOf(((Integer) edges[v + 1]).intValue()));
				group[i] = e;
			}

			/**
			 * Create new execution (Runnable). Store execution into database.
			 * Create new thread, give it the execution and start it. Return
			 * exeID. (At the end of run() the execution parameters of progress
			 * and success are updated.)
			 */
			AbstractExecution exe = new EvaluateExecution(dw, group);
			exeID = DataBase.putExecution(exe);
			exe.setID(exeID);
			Thread t = new Thread(exe);
			t.start();
		} catch (RuntimeException ex) {
			LoggingManager.getInstance().writeSystem(
					"A RuntimeException has occured during getGBCAsynch.",
					GBCController.ALIAS, "getGBCAsynch", ex);
		}
		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, GBCController.ALIAS,
				"getGBCAsynch", null);
		return exeID;
	}

	/**
	 * Searches for deployment of vertices (using Contribution algorithm)
	 * according to given parameters.
	 * 
	 * @param Dataworkshop
	 *            index
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
		DataWorkshop dw = (DataWorkshop) DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
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
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index
					.valueOf(((Integer) givenEdgesObj[v]).intValue()), Index
					.valueOf(((Integer) givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}
		Object[] centralVertices = null;
		try {
			Index[] cv = AbsGreedyBetweeness.findVertices(candidates,
					givenVertices, givenEdges, dw, Algorithm.Contribution,
					Bound.GroupSize, k, new DummyProgress(), 1);
			centralVertices = new Object[cv.length];

			int i = 0;
			for (Index v : cv) {
				centralVertices[i++] = new Integer(((Index) v).intValue());
			}
		} catch (Exception ex) {
			LoggingManager.getInstance().writeSystem(ex.getMessage(),
					GBCController.ALIAS, "getCentralVertices", ex);
		}
		return centralVertices;
	}

	/**
	 * Searches for deployment of edges (using Contribution algorithm) according
	 * to given parameters.
	 * 
	 * @param Dataworkshop
	 *            index
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
		DataWorkshop dw = (DataWorkshop) DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
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
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index
					.valueOf(((Integer) givenEdgesObj[v]).intValue()), Index
					.valueOf(((Integer) givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}

		Object[] centralEdges = null;
		try {
			AbstractSimpleEdge<Index,BasicVertexInfo>[] ce = AbsGreedyEdgeBetweeness.findEdges(
					candidates, givenVertices, givenEdges, dw,
					Algorithm.Contribution, Bound.GroupSize, k,
					new DummyProgress(), 1);
			centralEdges = new Object[ce.length*2];

			int i = 0;
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : ce) {
				centralEdges[i++] = new Integer(((Index) e.getV0()).intValue());
				centralEdges[i++] = new Integer(((Index) e.getV1()).intValue());
			}
		} catch (Exception ex) {
			LoggingManager.getInstance().writeSystem(ex.getMessage(),
					GBCController.ALIAS, "getCentralEdges", ex);
		}
		return centralEdges;
	}

	/**
	 * Starts an execution which searches for deployment of vertices (using
	 * Contribution algorithm) according to given parameters.
	 * 
	 * @param Dataworkshop
	 *            index
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
				"Starting searching central vertices.", GBCController.ALIAS,
				"getCentralVerticesAsynch", null);

		DataWorkshop dw = (DataWorkshop) DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
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
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index
					.valueOf(((Integer) givenEdgesObj[v]).intValue()), Index
					.valueOf(((Integer) givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new FindCentralVerticesExecution(candidates,
				givenVertices, givenEdges, dw, Algorithm.Contribution,
				Bound.GroupSize, k);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, GBCController.ALIAS,
				"getCentralVerticesAsynch", null);
		return exeID;
	}

	/**
	 * Starts an execution which searches for deployment of edges (using
	 * Contribution algorithm) according to given parameters.
	 * 
	 * @param Dataworkshop
	 *            index
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
				"Starting searching central vertices.", GBCController.ALIAS,
				"getCentralEdgesAsynch", null);

		DataWorkshop dw = (DataWorkshop) DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
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
			AbstractSimpleEdge<Index,BasicVertexInfo> e = graph.getEdge(Index
					.valueOf(((Integer) givenEdgesObj[v]).intValue()), Index
					.valueOf(((Integer) givenEdgesObj[v + 1]).intValue()));
			givenEdges[i] = e;
		}

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new FindCentralEdgesExecution(candidates,
				givenVertices, givenEdges, dw, Algorithm.Contribution,
				Bound.GroupSize, k);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, GBCController.ALIAS,
				"getCentralEdgesAsynch", null);
		return exeID;
	}
	
	/** Searches for deployment of vertices (using TopK algorithm) according to given parameters.
     * @param GBC algorithm index
     * @param k is the size of the desired deployment
     * @param vertex candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return array of vertices */
	public Object[] getTopkCentralVertices(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		DataWorkshop dw = (DataWorkshop)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
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
			Index [] cv = AbsGreedyBetweeness.findVertices(candidates, givenVertices, givenEdges, dw, Algorithm.TopK, Bound.GroupSize, k, new DummyProgress(), 1);
			centralVertices = new Object [cv.length];
			
			int i = 0;
			for (Index v : cv){
				centralVertices[i++] = new Integer(((Index)v).intValue());
			}
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage(), BrandesController.ALIAS, "getCentralVertices", ex);
		}
		return centralVertices;
	}
	
	/** Searches for deployment of edges (using TopK algorithm) according to given parameters.
     * @param GBC algorithm index
     * @param k is the size of the desired deployment
     * @param edge candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return array of edges
     * In the candidates array and in the result array every two successive elements represent an edge.
     * Namely, A[0]-A[1], A[2]-A[3], A[4]-A[5] represent 3 edges. */
	public Object[] getTopkCentralEdges(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		DataWorkshop dw = (DataWorkshop)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
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
			AbstractSimpleEdge<Index,BasicVertexInfo>[] ce = AbsGreedyEdgeBetweeness.findEdges(candidates, givenVertices, givenEdges, dw, Algorithm.TopK, Bound.GroupSize, k, new DummyProgress(), 1);
			centralEdges = new Object [ce.length*2];
			
			int i = 0;
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : ce){
				centralEdges[i++] = new Integer(((Index)e.getV0()).intValue());
				centralEdges[i++] = new Integer(((Index)e.getV1()).intValue());
			}
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage(), BrandesController.ALIAS, "getCentralEdges", ex);
		}
		return centralEdges;
	}
	
	/** Starts an execution which searches for deployment of vertices (using TopK algorithm) according to given parameters.
     * @param GBC algorithm index
     * @param k is the size of the desired deployment
     * @param candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return execution index */
	public int getTopkCentralVerticesAsynch(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj){
		LoggingManager.getInstance().writeTrace("Starting searching central vertices.", BrandesController.ALIAS, "getCentralVerticesAsynch", null);
    	
		DataWorkshop dw = (DataWorkshop)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
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
    	AbstractExecution exe = new FindCentralVerticesExecution(candidates, givenVertices, givenEdges, dw, Algorithm.TopK, Bound.GroupSize, k);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, BrandesController.ALIAS, "getCentralVerticesAsynch", null);
    	return exeID;
	}

	/** Starts an execution which searches for deployment of edges (using TopK algorithm) according to given parameters.
     * @param GBC algorithm index
     * @param k is the size of the desired deployment
     * @param edge candidates for the deployment (can be an empty list)
     * @param givenVertices are the already deployed vertices (can be an empty list)
     * @param givenLinks are the already deployed links (can be an empty list)
     * @return execution index
     * In the candidates array and in the result array every two successive elements represent an edge.
     * Namely, A[0]-A[1], A[2]-A[3], A[4]-A[5] represent 3 edges. */
	public int getTopkCentralEdgesAsynch(int algID, int k, Object[] candidatesObj, Object[] givenVerticesObj, Object[] givenEdgesObj)
	{
		LoggingManager.getInstance().writeTrace("Starting searching central vertices.", BrandesController.ALIAS, "getCentralEdgesAsynch", null);
    	
		DataWorkshop dw = (DataWorkshop)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = dw.getGraph();
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
    	AbstractExecution exe = new FindCentralEdgesExecution(candidates, givenVertices, givenEdges, dw, Algorithm.TopK, Bound.GroupSize, k);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, BrandesController.ALIAS, "getCentralEdgesAsynch", null);
    	return exeID;
	}
	
	
	/**
	 * Compute and return the characteristic path length of the network.
	 * Characteristic path length is the average distance between all pairs of vertices.
	 * 
	 * @param netID
	 *            - The index of the network in the DataBase.
	 * @return characteristic path length.
	 */
	public double getCharactersicticPathLength(int algID) {
		LoggingManager.getInstance().writeTrace("Computing characteristic path length.", GBCController.ALIAS, "getCharactersicticPathLength", null);
    	DataWorkshop dw = (DataWorkshop)DataBase.getAlgorithm(algID);
		return dw.computeCharacteristicPathLength();
	}


	/**
	 * Releases the pointer to the dataworkshop in the DataBase maps.
	 * 
	 * @param algID
	 *            - The index of the dataworkshop in the DataBase.
	 * @return 0.
	 */
	public int destroy(int algID) {
		DataBase.releaseAlgorithm(algID);
		return 0;
	}
}
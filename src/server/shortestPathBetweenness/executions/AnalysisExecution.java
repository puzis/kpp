package server.shortestPathBetweenness.executions;

import java.io.File;

import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

public class AnalysisExecution extends AbstractExecution {
	private AbsTrafficMatrix m_communicationWeights = null;
	private boolean m_createRoutingTable = true;
	private int m_dwID = -1;
	private int m_netID = -1;

	/** CONSTANTS */
	private static final String NEW_LINE = "\n";

	public AnalysisExecution(int netID, String input, boolean createRoutingTable) {
		this.m_netID = netID;
		this.m_network = DataBase.getNetwork(netID);

		if (input != null && !input.isEmpty())
			m_communicationWeights = new DenseTrafficMatrix(input, m_network
					.getGraphSimple().getNumberOfVertices()); // WeightsLoader.loadWeightsFromString(m_communicationWeights,
														// graph.getNumberOfVertices());
		else
			m_communicationWeights = new DefaultTrafficMatrix(m_network
					.getGraphSimple().getNumberOfVertices()); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());

		this.m_createRoutingTable = createRoutingTable;
	}

	public AnalysisExecution(int netID, int tmID, boolean createRoutingTable) {
		this.m_netID = netID;
		this.m_network = DataBase.getNetwork(netID);
		if (tmID != -1)
			m_communicationWeights = DataBase.getTrafficMatrix(tmID);
		else
			m_communicationWeights = new DefaultTrafficMatrix(m_network
					.getGraphSimple().getNumberOfVertices());
		this.m_createRoutingTable = createRoutingTable;
	}

	public void run() {
		boolean success = true;
		reportSuccess(AbstractExecution.PHASE_FAILURE,
				AbstractExecution.PHASE_NONCOMPLETE);
		try {
			double totalPercentage = 1;
			/** First check whether the .dw file already exists. */
			try {
				File dwFile = new File(ServerConstants.DATA_DIR
						+ m_network.getName() + ".dw");
				if (dwFile.exists()) {
					DataWorkshop dataWorkshop = new DataWorkshop();
					try {
						dataWorkshop.loadFromDisk(dwFile, new DummyProgress(),
								1);
						m_dwID = DataBase.putAlgorithm(dataWorkshop, m_netID);
					} catch (Exception ex) {
						LoggingManager.getInstance()
								.writeSystem(
										"Couldn't load " + m_network.getName()
												+ ".dw.", "AnalysisExecution",
										"run", ex);
					}
				}
			} catch (RuntimeException ex) {
				LoggingManager.getInstance().writeSystem(
						"The file " + ServerConstants.DATA_DIR
								+ m_network.getName() + ".dw doesn't exist.",
						"AnalysisExecution", "run", null);
			}

			/**
			 * If .dw file does not exists (m_dwID == -1), then try to create a
			 * new Dataworkshop.
			 */
			if (m_dwID == -1) {
				GraphInterface<Index,BasicVertexInfo> graph = m_network.getGraphSimple();
				DataWorkshop dw = null;

				if (graph != null) {
					double progress = getProgress();
					progress += 0.05 * totalPercentage;
					setProgress(progress);

					try {
						dw = new DataWorkshop(
								ShortestPathAlgorithmInterface.DEFAULT, graph,
								m_communicationWeights, m_createRoutingTable,
								this, 0.3 * 0.85 * totalPercentage);
						m_dwID = DataBase.putAlgorithm(dw, m_netID);

						progress += 0.2 * totalPercentage;
						reportSuccess(AbstractExecution.PHASE_SUCCESS,
								AbstractExecution.PHASE_COMPLETE);
						success = true;
					} catch (Exception ex) {
						LoggingManager.getInstance().writeSystem(
								"An exception has occured while creating dataWorkshop:\n"
										+ ex.getMessage() + NEW_LINE
										+ ex.getStackTrace(),
								"AnalysisExecution", "run", ex);
						success = false;
					}
				} else {
					LoggingManager
							.getInstance()
							.writeSystem(
									"The graph has not been loaded properly. Check logs please.",
									"AnalysisExecution", "run", null);
					success = false;
				}
			}
		} catch (RuntimeException ex) {
			LoggingManager.getInstance().writeSystem(
					"A RuntimeException has occured during analysis.",
					"AnalysisExecution", "run", ex);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing network analysis.",
				"AnalysisExecution", "run", null);

		if (!success) {
			LoggingManager.getInstance().writeSystem(
					m_methodName
							+ " hasn't completed successfully, check logs.",
					"AnalysisExecution", "run", null);
			reportSuccess(AbstractExecution.PHASE_FAILURE,
					AbstractExecution.PHASE_COMPLETE);
		} else {
			reportSuccess(AbstractExecution.PHASE_SUCCESS,
					AbstractExecution.PHASE_COMPLETE);
		}
	}

	public Object getResult() {
		return m_dwID;
	}

	public synchronized void cancel() {
		m_done = true;
		String dwFileName = m_network.getName() + ".dw";
		File dwFile = new File(ServerConstants.DATA_DIR + dwFileName);
		if (dwFile.exists())
			dwFile.delete();
		reportSuccess(AbstractExecution.PHASE_FAILURE,
				AbstractExecution.PHASE_COMPLETE);
	}
}
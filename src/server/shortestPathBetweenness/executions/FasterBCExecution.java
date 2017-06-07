package server.shortestPathBetweenness.executions;

import javolution.util.Index;
import server.common.DataBase;
import server.common.LoggingManager;
import server.common.ServerConstants.BCCalculatorAlgorithm;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.bcc.BCCAlgorithm;
import algorithms.centralityAlgorithms.betweenness.bcc.BCCalculatorInterface;
import algorithms.centralityAlgorithms.betweenness.bcc.BetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.bcc.EvenFasterBetweenness;
import algorithms.centralityAlgorithms.betweenness.bcc.TMBetweennessCalculator;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

public class FasterBCExecution extends AbstractExecution
{
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	private int m_netID;
	private BCCalculatorAlgorithm m_bcCalculatorType;
	private AbsTrafficMatrix m_communicationWeights;
	
	private int m_fasterBCID = -1;
	/** CONSTANTS */
	private static final String NEW_LINE = "\n";

	public FasterBCExecution(int netID){
		m_graph = DataBase.getNetwork(netID).getGraphSimple();
		m_netID = netID;
		m_bcCalculatorType = BCCalculatorAlgorithm.DEFAULT;
		m_communicationWeights = new DefaultTrafficMatrix(m_graph.getNumberOfVertices()); // MatricesUtils.getDefaultWeights(m_graph.getNumberOfVertices());
	}

	public FasterBCExecution(int netID, int tmID){
		m_graph = DataBase.getNetwork(netID).getGraphSimple();
		m_netID = netID;
		m_bcCalculatorType = BCCalculatorAlgorithm.TRAFIC_MATRIX_BC;
		m_communicationWeights = DataBase.getTrafficMatrix(tmID);
	}
	
	public FasterBCExecution(int netID, int tmID, BCCalculatorAlgorithm bcCalculatorType){
		m_graph = DataBase.getNetwork(netID).getGraphSimple();
		m_netID = netID;
		m_bcCalculatorType = bcCalculatorType;
		m_communicationWeights = DataBase.getTrafficMatrix(tmID);
	}
	
	
	@Override
	public void run() 
	{
		boolean success = true;
		reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		try{
			if (m_graph != null)
			{
				try{
					BCCAlgorithm bccAlg = new BCCAlgorithm(m_graph);
					BCCalculatorInterface ebcc = null;
					
					switch(m_bcCalculatorType){
						case DEFAULT:
							ebcc = new BetweennessCalculator(bccAlg);
							break;
						case TRAFIC_MATRIX_BC:
							ebcc = new TMBetweennessCalculator(bccAlg, m_communicationWeights);
							break;	
					}
			    	EvenFasterBetweenness eagerbc = new EvenFasterBetweenness(m_graph, ebcc);	
			    	eagerbc.run();
			    	
					m_fasterBCID = DataBase.putAlgorithm(eagerbc, m_netID);
					success = true;
				}
				catch(Exception ex)
				{
					LoggingManager.getInstance().writeSystem("An exception has occured while creating FasterBCAlgorithm:\n" + ex.getMessage() + NEW_LINE + ex.getStackTrace(), "FasterBCExecution", "run", ex);
					success = false;
				}
			}
			else
			{
				LoggingManager.getInstance().writeSystem("The graph has not been loaded properly. Check logs please.", "FasterBCExecution", "run", null);
				success = false;
			}
		}
		catch(RuntimeException ex)
		{
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured while creating FasterBCAlgorithm.", "FasterBCExecution", "run", ex);
			success = false;
		}
		
		LoggingManager.getInstance().writeTrace("Finishing creating FasterBCAlgorithm.", "FasterBCExecution", "run", null);
		reportSuccess((success ? AbstractExecution.PHASE_SUCCESS : AbstractExecution.PHASE_FAILURE), AbstractExecution.PHASE_COMPLETE);		
	}
	
	@Override
	public Object getResult() {
		return m_fasterBCID;
	}
}
package server.closeness.executions;

import javolution.util.Index;

import server.common.DataBase;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithmEnum;
import algorithms.centralityAlgorithms.closeness.GroupClosenessAlgorithmMSBFS;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.OptimizedGreedyGroupCloseness;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;

public class GClosenessAlgorithmExecution  extends AbstractExecution
{
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	private int m_netID = -1;
	private IClosenessFormula m_formula = null;
	private int m_algType = -1;
	private int m_closenessAlgorithmID = -1;
	
	/* CONSTANTS */
	private static final String NEW_LINE = "\n";
	
	public GClosenessAlgorithmExecution(int netID, IClosenessFormula formula, int algType)
	{
		m_graph = DataBase.getNetwork(netID).getGraphSimple();
		m_netID = netID;
		m_formula = formula;
		m_algType = algType;
	}

	@Override
	public void run() {
		boolean success = true;
		reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		
		try{
			if (m_graph != null)
			{
				try{
					IClosenessAlgorithm closeness = null;
					switch 	(ClosenessAlgorithmEnum.values()[m_algType]){
					
					case STANDARD:{
						closeness = new ClosenessAlgorithm(m_graph, m_formula, this, 1);
						break;
					}
					case OPTIMIZED: {
						closeness = new OptimizedGreedyGroupCloseness(m_graph, m_formula , this, 1);
						break;
					}
					case MSBFS: {
						closeness = new GroupClosenessAlgorithmMSBFS(m_graph, m_formula);// this, 1);
					}
					
					}
					
					m_closenessAlgorithmID = DataBase.putAlgorithm(closeness, m_netID);
					success = true;
				}
				catch(Exception ex){
					
					LoggingManager.getInstance().writeSystem("An exception has occured while creating Group Closeness Algorithm:\n" 
							+ ex.getMessage() + NEW_LINE + ex.getStackTrace(), "GClosenessAlgorithmExecution", "run", ex);
					success = false;
				}
			}
			else{
				
				LoggingManager.getInstance().writeSystem("The graph has not been loaded properly. Check logs please.", 
						"GClosenessAlgorithmExecution", "run", null);
				success = false;
			}
		}
		catch(RuntimeException ex)
		{
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured while creating Group Closeness Algorithm.", 
					"GClosenessAlgorithmExecution", "run", ex);
			success = false;
		}
		
		LoggingManager.getInstance().writeTrace("Finishing creating Group Closeness Algorithm.", "GClosenessAlgorithmExecution", "run", null);
		reportSuccess((success? AbstractExecution.PHASE_SUCCESS : AbstractExecution.PHASE_FAILURE), AbstractExecution.PHASE_COMPLETE);
	}
	
	@Override
	public Object getResult() {
		return m_closenessAlgorithmID;
	}
}
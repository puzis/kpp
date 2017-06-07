package server.randomWalkBetweenness.executions;

import javolution.util.Index;
import server.common.DataBase;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.randomWalkBetweeness.GroupRandomWalkBetweeness;

public class RWBExecution  extends AbstractExecution
{
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	private int m_netID = -1;
	private int m_RWBID = -1;
	/** CONSTANTS */
	private static final String NEW_LINE = "\n";
	
	public RWBExecution(int netID)
	{
		m_netID = netID;
		m_graph = DataBase.getNetwork(netID).getGraphSimple();
	}

	@Override
	public void run() {
		
		boolean success = true;
		reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		try{
			double totalPercentage = 1;
			GroupRandomWalkBetweeness grwb = null;
			
			if (m_graph != null)
			{
				double progress = getProgress(); 
				progress += 0.05 * totalPercentage;
				
				setProgress(progress);
			
				try{
					grwb = new GroupRandomWalkBetweeness(m_graph);
				}
				catch(Exception ex)
				{
					LoggingManager.getInstance().writeSystem("An exception has occured while creating RWB:\n" + ex.getMessage() + NEW_LINE + ex.getStackTrace(), "RWBExecution", "run", ex);
					success = false;
				}
				progress += 0.2 * totalPercentage;
				
				m_RWBID = DataBase.putAlgorithm(grwb, m_netID);
				success = true;
			}
			else 
			{	
				LoggingManager.getInstance().writeSystem("The graph has not been loaded properly. Check logs please.", "RWBExecution", "run", null);
				success = false;
			}
		}
		catch(RuntimeException ex)
		{
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured during creating RWB.", "RWBExecution", "run", ex);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing creating RWB.", "RWBExecution", "run", null);
		
		if (!success)
		{
			LoggingManager.getInstance().writeSystem(m_methodName + " hasn't completed successfully, check logs.", "RWBExecution", "run", null);
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
		}
		else
		{
			reportSuccess(AbstractExecution.PHASE_SUCCESS, AbstractExecution.PHASE_COMPLETE);
		}
	}
	
	@Override
	public Object getResult() {
		return m_RWBID;
	}
}
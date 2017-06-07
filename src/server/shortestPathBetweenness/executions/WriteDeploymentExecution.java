package server.shortestPathBetweenness.executions;

import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.Network;
import server.execution.AbstractExecution;
import topology.GraphPrinter;
import algorithms.centralityAlgorithms.betweenness.brandes.GreedyBetweenessContribution;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;

public class WriteDeploymentExecution extends AbstractExecution 
{
	private int m_algID = -1;
	private String m_txtFile = null;
	
	public WriteDeploymentExecution(int algID, Network network)
	{
		this.m_network = network;
		m_algID = algID;
	}
	
	public void run()
	{
		boolean success = true;
		reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		try{
			GreedyBetweenessContribution.writeDeployment(m_network.getGraphSimple(), (DataWorkshop)DataBase.getAlgorithm(m_algID), new DummyProgress(), 1);
			GraphPrinter gPrinter = new GraphPrinter(m_network.getGraphSimple());
			m_txtFile = gPrinter.getAnalyzedFile((DataWorkshop)DataBase.getAlgorithm(m_algID));
		}
		catch(Exception ex)
		{
			LoggingManager.getInstance().writeSystem("An exception has occured while writting deployment of dataWorkshop:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), "WriteDeploymentExecution", "run", ex);
			success = false;
		}
		if (m_txtFile == null)
		{
			LoggingManager.getInstance().writeSystem(m_methodName + " hasn't completed successfully, check logs.", "WriteDeploymentExecution", "run", null);
			success = false;
		}
		else
		{
			success = true;
			
		}
		LoggingManager.getInstance().writeTrace("Finishing writting deployment.", "WriteDeploymentExecution", "run", null);
		reportSuccess((success ? AbstractExecution.PHASE_SUCCESS : AbstractExecution.PHASE_FAILURE), AbstractExecution.PHASE_COMPLETE);
		
	}
	
	public Object getResult(){	return m_txtFile;	}
}
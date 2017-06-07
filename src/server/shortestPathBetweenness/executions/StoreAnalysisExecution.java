package server.shortestPathBetweenness.executions;

import server.common.DataBase;
import server.common.LoggingManager;
import server.common.ServerConstants;
import server.execution.AbstractExecution;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;

public class StoreAnalysisExecution extends AbstractExecution
{
	private int m_algID = -1;
	private int m_netID = -1;

	public StoreAnalysisExecution(int algID, int netID)
	{
		m_algID = algID;
		m_netID = netID;
	}

	public void run()
	{
		boolean success = true;
		try
		{
			DataWorkshop dw = (DataWorkshop)DataBase.getAlgorithm(m_algID);
			String filename = ServerConstants.DATA_DIR + DataBase.getNetwork(m_netID).getName() + ".dw";
			try{
				dw.saveToDisk(filename, this, 0.8);
				success = true;
			}
			catch(Exception ex){
				LoggingManager.getInstance().writeSystem("An exception has occured while storing dataWorkshop:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), "StoreAnalysisExecution", "run", ex);
				success = false;
			}
		}
		catch (RuntimeException ex){
			LoggingManager.getInstance().writeSystem("An exception has occured while storing dataWorkshop:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), "StoreAnalysisExecution", "run", ex);
			success = false;
		}
		
		reportSuccess((success ? AbstractExecution.PHASE_SUCCESS : AbstractExecution.PHASE_FAILURE), AbstractExecution.PHASE_COMPLETE);
	}

	@Override
	public Object getResult() {
		//nothing really to return. so just say if success or not.
		return getSuccess();
	}
}
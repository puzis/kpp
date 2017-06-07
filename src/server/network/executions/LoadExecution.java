package server.network.executions;

import server.common.DataBase;
import server.common.LoggingManager;
import server.common.Network;
import server.execution.AbstractExecution;


public class LoadExecution extends AbstractExecution
{
	private int m_netID = -1;
	
	public LoadExecution(Network network){
		this.m_network = network;
	}
	
	public void run(){
		reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		boolean success = m_network.loadNetwork(this);

    	/** The newly added network is added to the networks map. */
    	m_netID = DataBase.putNetwork(m_network);
    	
    	if (!success)
    	{
			LoggingManager.getInstance().writeSystem(m_methodName + " hasn't completed successfully, check logs.", "LoadExecution", m_methodName, null);
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
		}
    	else
    	{
    		reportSuccess(AbstractExecution.PHASE_SUCCESS, AbstractExecution.PHASE_COMPLETE);    		
    	}
	}
	
	public Object getResult(){	return m_netID;	}
}

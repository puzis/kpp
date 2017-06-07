package server.shortestPathBetweenness.executions;

import server.common.LoggingManager;
import server.execution.AbstractExecution;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;

public class EvaluateExecution extends AbstractExecution{

	private double m_GBC = -1;
	private DataWorkshop m_dataWorkshop = null;
	private Object[] m_group = null;
	
	public EvaluateExecution(DataWorkshop workShop, Object[] group) 
	{
		m_dataWorkshop = workShop;
		m_group = group;
	}
	@Override
	public Object getResult() {
		return m_GBC;
	}

	@Override
	public void run() 
	{
		try{
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
			m_GBC = CandidatesBasedAlgorithm.calculateGB(m_dataWorkshop, m_group, this, 1);
			if (m_GBC == -1)
			{
				LoggingManager.getInstance().writeSystem("getGBC has NOT completed successfully.", "EvaluateExecution", "run", null);
				reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
			}
			else
			{
				reportSuccess(AbstractExecution.PHASE_SUCCESS, AbstractExecution.PHASE_COMPLETE);
			}
		}
		catch(Exception ex)
		{
			LoggingManager.getInstance().writeSystem(ex.getMessage(), "EvaluateExecution", "run", ex);
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
		}
	}
}

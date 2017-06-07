package server.closeness.executions;

import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import server.common.LoggingManager;

import server.execution.AbstractExecution;

public class EvaluateExecution extends AbstractExecution{

	private double m_groupCloseness = -1;
	private IClosenessAlgorithm m_closenessAlgorithm = null;
	private Object[] m_group = null;
	
	public EvaluateExecution(IClosenessAlgorithm closenessAlgorithm, Object[] group) 
	{
		m_closenessAlgorithm = closenessAlgorithm;
		m_group = group;
	}
	@Override
	public Object getResult() {
		return m_groupCloseness;
	}

	@Override
	public void run() 
	{
		try{
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
			m_groupCloseness = m_closenessAlgorithm.calculateMixedGroupCloseness(m_group, this, 1);
			if (m_groupCloseness == -1)
			{
				LoggingManager.getInstance().writeSystem("getGroupCloseness has NOT completed successfully.", "EvaluateExecution", "run", null);
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

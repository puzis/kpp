package server.degree.executions;

import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.degree.GroupDegreeAlgorithm;

public class EvaluateExecution extends AbstractExecution{

	private double m_groupDegree = -1;
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	private Object[] m_group = null;
	
	public EvaluateExecution(GraphInterface<Index,BasicVertexInfo> graph, Object[] group) 
	{
		m_graph = graph;
		m_group = group;
	}
	@Override
	public Object getResult() {
		return m_groupDegree;
	}

	@Override
	public void run() 
	{
		try{
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
			m_groupDegree = GroupDegreeAlgorithm.calculateMixedGroupDegree(m_group, m_graph, this, 1);
			if (m_groupDegree == -1)
			{
				LoggingManager.getInstance().writeSystem("getGroupDegree has NOT completed successfully.", "EvaluateExecution", "run", null);
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

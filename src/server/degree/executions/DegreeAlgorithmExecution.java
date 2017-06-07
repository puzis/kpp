package server.degree.executions;

import javolution.util.Index;
import server.common.DataBase;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.degree.DegreeAlgorithm;

public class DegreeAlgorithmExecution  extends AbstractExecution
{
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	private int m_netID = -1;
	private int m_degreeAlgorithmID = -1;
	/** CONSTANTS */
	private static final String NEW_LINE = "\n";
	
	public DegreeAlgorithmExecution(int netID)
	{
		m_graph = DataBase.getNetwork(netID).getGraphSimple();
		m_netID = netID;
	}

	@Override
	public void run() {
		boolean success = true;
		reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		try{
			if (m_graph != null)
			{
				try{
					int[] vertices = new int[m_graph.getNumberOfVertices()];	
					int i = 0;
					for (Index v : m_graph.getVertices()){
						vertices[i++] = v.intValue();
					}
					DegreeAlgorithm degree = new DegreeAlgorithm(vertices, m_graph, this, 1);
					m_degreeAlgorithmID = DataBase.putAlgorithm(degree, m_netID);
					success = true;
				}
				catch(Exception ex)
				{
					LoggingManager.getInstance().writeSystem("An exception has occured while creating DegreeAlgorithm:\n" + ex.getMessage() + NEW_LINE + ex.getStackTrace(), "DegreeAlgorithmExecution", "run", ex);
					success = false;
				}
			}
			else
			{
				LoggingManager.getInstance().writeSystem("The graph has not been loaded properly. Check logs please.", "DegreeAlgorithmExecution", "run", null);
				success = false;
			}
		}
		catch(RuntimeException ex)
		{
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured while creating DegreeAlgorithm.", "DegreeAlgorithmExecution", "run", ex);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing creating DegreeAlgorithm.", "DegreeAlgorithmExecution", "run", null);
		reportSuccess((success? AbstractExecution.PHASE_SUCCESS : AbstractExecution.PHASE_FAILURE), AbstractExecution.PHASE_COMPLETE);
		
	}
	
	@Override
	public Object getResult() {
		return m_degreeAlgorithmID;
	}
}
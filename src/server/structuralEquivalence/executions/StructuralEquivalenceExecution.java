package server.structuralEquivalence.executions;

import javolution.util.Index;
import server.common.DataBase;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.structuralEquivalence.StructuralEquivalenceUnifier;

public class StructuralEquivalenceExecution extends AbstractExecution
{
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	private int m_netID;
	
	private int m_seID = -1;
	/** CONSTANTS */
	private static final String NEW_LINE = "\n";
	
	public StructuralEquivalenceExecution(int netID){
		m_graph = DataBase.getNetwork(netID).getGraphSimple();
		m_netID = netID;
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
					
					StructuralEquivalenceUnifier seUnifier = null;
					if (m_graph != null){
						seUnifier = new StructuralEquivalenceUnifier(m_graph);
						seUnifier.run();
					}
			    	
					m_seID = DataBase.putAlgorithm(seUnifier, m_netID);
					success = true;
					
				}
				catch(Exception ex)
				{
					LoggingManager.getInstance().writeSystem("An exception has occured while creating StructuralEquivalenceUnifier:\n" + ex.getMessage() + NEW_LINE + ex.getStackTrace(), "StructuralEquivalenceExecution", "run", ex);
					success = false;
				}
			}
			else
			{
				LoggingManager.getInstance().writeSystem("The graph has not been loaded properly. Check logs please.", "StructuralEquivalenceExecution", "run", null);
				success = false;
			}
		}
		catch(RuntimeException ex)
		{
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured while creating StructuralEquivalenceUnifier.", "StructuralEquivalenceExecution", "run", ex);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing creating StructuralEquivalenceUnifier.", "StructuralEquivalenceExecution", "run", null);
		reportSuccess((success ? AbstractExecution.PHASE_SUCCESS : AbstractExecution.PHASE_FAILURE), AbstractExecution.PHASE_COMPLETE);
	}
	
	@Override
	public Object getResult() {
		return m_seID;
	}
}
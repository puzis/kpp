package server.network.executions;

import javolution.util.Index;


import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.Network;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.HyperGraphInterface;

public class ImportExecution extends AbstractExecution 
{
	private String m_importedNet = null;
	private String m_filename = null;
	private int m_netID = -1;
	
	public ImportExecution(Network network, String filename, String input)
	{
		this.m_network = network;
		this.m_filename = filename;
		this.m_importedNet = input;
	}
	
	public void run()
	{
		reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
//		boolean success = m_network.importNetwork(this, m_filename, m_importedNet, "net", GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP);
		
		String networkName = m_network.getName();
		Network undirected = new Network(networkName);
		boolean success = undirected.importNetwork(this, m_filename, m_importedNet, "net", GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP);
		
		if (success){
			GraphFactory.GraphDataStructure targetGDS = GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP;
			HyperGraphInterface<Index,BasicVertexInfo> newGraph = undirected.getGraphAs(targetGDS);
			
			m_network = new Network(networkName,newGraph);
//			m_network.loadNetwork(new DummyProgress());//What is it for?
		}
		/** The newly added network is added to the networks map. */
    	m_netID = DataBase.putNetwork(m_network);
    	
		if (!success)
		{
			LoggingManager.getInstance().writeSystem(m_methodName + " hasn't completed successfully, check logs.", "ImportExecution", m_methodName, null);
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
		}
		else
			reportSuccess(AbstractExecution.PHASE_SUCCESS, AbstractExecution.PHASE_COMPLETE);
	}
	
	public Object getResult(){	return m_netID;	}
}

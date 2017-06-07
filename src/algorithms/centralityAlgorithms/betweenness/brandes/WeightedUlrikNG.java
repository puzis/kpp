/**
 * 
 */
package algorithms.centralityAlgorithms.betweenness.brandes;

import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

import common.IndexFastList;

/**
 * @author user
 *
 */
public class WeightedUlrikNG extends TrafficMatrixBC {

	protected double [][] m_distance = null;
	protected double [][] m_deltaDot = null;
	protected double [][] m_sigmas = null;
	protected IndexFastList [][] m_routingTable = null;
	protected boolean m_createRoutingTable = true; 
	
	
//	/**
//	 * @param spAlg
//	 * @param graph
//	 * @param progress
//	 * @param percentage
//	 */
//	public WeightedUlrikNG(ShortestPathAlg spAlg, GraphInterface<Index> graph, AbstractExecution progress, double percentage){
//		super(spAlg, graph, progress, percentage);
//		m_createRoutingTable = true;
//	}

	/**
	 * @param spAlg
	 * @param graph
	 * @param createRoutingTable
	 * @param progress
	 * @param percentage
	 */
	public WeightedUlrikNG(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph, boolean createRoutingTable, AbstractExecution progress, double percentage) {
		super(spAlg, graph, progress, percentage);
		m_createRoutingTable = createRoutingTable;
	}
	
	/**
	 * @param spAlg
	 * @param graph
	 * @param communicationWeights
	 * @param createRoutingTable
	 * @param progress
	 * @param percentage
	 */
	public WeightedUlrikNG(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph, AbsTrafficMatrix communicationWeights, boolean createRoutingTable, AbstractExecution progress, double percentage) {
		super(spAlg, graph, communicationWeights, progress, percentage);
		m_createRoutingTable = createRoutingTable;
	}

//	/**
//	 * @param spAlg
//	 * @param graph
//	 * @param communicationWeights
//	 * @param progress
//	 * @param percentage
//	 */
//	public WeightedUlrikNG(ShortestPathAlg spAlg, GraphInterface<Index> graph, 
//			AbsTrafficMatrix communicationWeights,
//			AbstractExecution progress, double percentage)  {
//		super(spAlg, graph, communicationWeights, progress, percentage);
//		m_communicationWeights = communicationWeights;
//	}

	@Override
	public void algorithmInitialization(GraphInterface<Index,BasicVertexInfo>  graph) {
        m_numOfVertices = graph.getNumberOfVertices();
        m_distance = new double[m_numOfVertices][m_numOfVertices];
        m_deltaDot = new double[m_numOfVertices][m_numOfVertices];
        m_sigmas = new double[m_numOfVertices][m_numOfVertices];
        
        m_routingTable = new IndexFastList[m_numOfVertices][m_numOfVertices];
        if (m_createRoutingTable)
        {
        	for (int i = 0; i < m_numOfVertices; i++)
        		for (int j = 0; j < m_numOfVertices; j++)
        			m_routingTable[i][j] = new IndexFastList();
        }
	}
	
	@Override
	public void beforeSourceDependencyUpdate(int s, int w) 
	{
		/** update routing table of w to s */
	    if (m_createRoutingTable)
	    {	
	      	if (s == w)
	      		m_routingTable[w][s].add(Index.valueOf(s));
	      	else
	      		m_routingTable[w][s] = m_P.get(w);
	    }
		
		m_delta[w] += m_communicationWeights.getWeight(s, w);		
	}
	
	@Override
	public void afterAccumulation(int s) 
	{
		System.arraycopy(m_sigma, 0, m_sigmas[s], 0, m_sigma.length);
		System.arraycopy(m_d, 0, m_distance[s], 0, m_d.length);
		System.arraycopy(m_delta, 0, m_deltaDot[s], 0, m_deltaDot.length);
	}
	
	public double[][] getDeltaDot(){	return m_deltaDot;	}

	public double[][] getDistance(){	return m_distance;	}

	public double[][] getSigma(){	return m_sigmas;	}
	
	public AbsTrafficMatrix getCommunicationWeights(){	return m_communicationWeights;	}
	
	public FastList<Index>[][] getRoutingTable(){	return m_routingTable;	}
}
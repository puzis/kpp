/**
 * 
 */
package algorithms.centralityAlgorithms.betweenness.brandes;

import java.util.Set;

import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

/**
 * @author user
 *
 */
public class TrafficMatrixBC extends BrandesBC {

	protected AbsTrafficMatrix m_communicationWeights;
	
	
	/**
	 * @param spAlg
	 * @param graph
	 * @param progress
	 * @param percentage
	 */
	public TrafficMatrixBC(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph,
			AbstractExecution progress, double percentage){
		super(spAlg, graph, progress, percentage);
		m_communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); //MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
	}

	/**
	 * @param spAlg
	 * @param graph
	 * @param communicationWeights
	 * @param progress
	 * @param percentage
	 */
	public TrafficMatrixBC(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph,
			AbsTrafficMatrix communicationWeights, AbstractExecution progress,
			double percentage) {
		super(spAlg, graph, progress, percentage);
		m_communicationWeights = communicationWeights;
	}

	/**
	 * @param spAlg
	 * @param graph
	 * @param communicationWeights
	 * @param sourceVertices
	 * @param progress
	 * @param percentage
	 */
	public TrafficMatrixBC(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph,
			AbsTrafficMatrix communicationWeights, Index[] sources,
			AbstractExecution progress, double percentage) {
		super(spAlg, graph, sources, progress, percentage);
		m_communicationWeights = communicationWeights;
	}

	
	/**
	 * @param spAlg
	 * @param graph
	 * @param communicationWeights
	 * @param sourceVertices
	 * @param group
	 * @param progress
	 * @param percentage
	 */
	public TrafficMatrixBC(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph,
			AbsTrafficMatrix communicationWeights, Index[] sources, Index[] group,
			AbstractExecution progress, double percentage) {
		super(spAlg, graph, sources, progress, group, percentage);
		m_communicationWeights = communicationWeights;
	}

	
	@Override
	public void beforeSourceDependencyUpdate(int s, int w) {
        m_delta[w] += m_communicationWeights.getWeight(s, w);		
	}
	

}

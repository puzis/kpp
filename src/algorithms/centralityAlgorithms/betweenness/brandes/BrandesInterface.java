package algorithms.centralityAlgorithms.betweenness.brandes;

import javolution.util.Index;
import algorithms.centralityAlgorithms.betweenness.CentralityAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

public interface BrandesInterface extends CentralityAlgorithmInterface {
	public void setSources(Index[] sources);
	public ShortestPathAlgorithmInterface getShortestPathAlgorithm();
}

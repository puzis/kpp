package algorithms.centralityAlgorithms.betweenness;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.HyperGraphInterface;

public interface CentralityAlgorithmInterface {
	
	public void run();
	public double getCentrality(int v);
	public double[] getCentralitites();
	
	public HyperGraphInterface<Index,? extends BasicVertexInfo> getGraph();
}

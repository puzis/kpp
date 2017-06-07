package algorithms.centralityAlgorithms.betweenness.bcc;

import java.util.Iterator;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.HyperGraphInterface;
import algorithms.bcc.BiConnectedComponent;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

public interface BCCalculatorInterface {
	public void runBCCAlgorithm();
	public Index[][] getComponents();
	public Iterator<BiConnectedComponent> getSubGraphs();
	public double[] getCommunications();
	public AbsTrafficMatrix createTrafficMatrix(GraphInterface<Index,BasicVertexInfo> subGraph) throws Exception;
	public GraphInterface<Index,? extends BasicVertexInfo> getGraph();
	
	
}

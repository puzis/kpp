package tests.betweenness.trast;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.Network;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.sato.SatoGraphBuilder;
import algorithms.centralityAlgorithms.sato.TrastUpperBoundBC;
import algorithms.clustering.BudgetedGreedyClustering;
import algorithms.clustering.Clustering;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class TrastUpperBoundBCTest extends TestCase {

	GraphInterface<Index,BasicVertexInfo> graph;
	GraphInterface<Index,BasicVertexInfo> sato;
	Clustering<Index,BasicVertexInfo> clustering;
	public void setUp() {
		Network network = new Network("SatoGraphTest");
		network.importNetwork(new DummyProgress(), "greedyClustering.net", "", "net",GraphFactory.DEFAULT_GRAPH_TYPE );
		graph = network.getGraphSimple();
		clustering = new BudgetedGreedyClustering<Index,BasicVertexInfo>(graph, 10, 5);
		SatoGraphBuilder sgb = new SatoGraphBuilder(clustering, ShortestPathAlg.BFS);
		try {
			sato = sgb.buildSATOGraph();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testConstructor() {
		TrastUpperBoundBC trastUpper = new TrastUpperBoundBC(sato);
		trastUpper.run();
		for(Index v : sato.getVertices()) {
			System.out.println("BC of "+v.intValue()+" <= "+trastUpper.getCentrality(v.intValue()));
		}
	}
}

package tests.betweenness.trast;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.Network;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.sato.SatoGraphBuilder;
import algorithms.clustering.BudgetedGreedyClustering;
import algorithms.clustering.Clustering;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class SatoGraphBuilderTest extends TestCase{
	
	Clustering<Index,BasicVertexInfo> m_clustering;

	public void setUp() {
		Network network = new Network("SatoGraphTest");
		network.importNetwork(new DummyProgress(), "greedyClustering.net", "", "net",GraphFactory.DEFAULT_GRAPH_TYPE );
		GraphInterface<Index,BasicVertexInfo> graph = network.getGraphSimple();
		m_clustering = new BudgetedGreedyClustering<Index,BasicVertexInfo>(graph, 10, 5);		
	}
	
	public void testBuildSato() throws Exception{
		SatoGraphBuilder sgb = new SatoGraphBuilder(m_clustering, ShortestPathAlg.BFS);
		GraphInterface<Index,BasicVertexInfo> sato = sgb.buildSATOGraph();
		String sExp = "Links:[[1, 0]= 1.0 , [1, 2]= 1.0 , [1, 3]= 1.0 , [1, 4]= 1.0 , [1, 8]= 1.0 , [8, 1]= 1.0 , [8, 5]= 1.0 , [8, 6]= 1.0 , [8, 7]= 1.0 ]	Vertices:[0= 0 '' 0.0 0.0 0.0  border false , 1= 1 '' 0.0 0.0 0.0  border true , 2= 2 '' 0.0 0.0 0.0  border false , 3= 3 '' 0.0 0.0 0.0  border false , 4= 4 '' 0.0 0.0 0.0  border false , 5= 5 '' 0.0 0.0 0.0  border false , 6= 6 '' 0.0 0.0 0.0  border false , 7= 7 '' 0.0 0.0 0.0  border false , 8= 8 '' 0.0 0.0 0.0  border true ]";
		assertEquals(sExp, sato.toString());
	}
}

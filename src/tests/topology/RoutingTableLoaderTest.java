package tests.topology;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DummyProgress;
import server.common.ServerConstants;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.GraphPrinter;
import topology.RoutingTableLoader;
import topology.SerializableGraphRepresentation;
import topology.GraphFactory.GraphDataStructure;
import topology.graphParsers.GraphParserFactory;
import algorithms.centralityAlgorithms.betweenness.brandes.WeightedUlrikNG;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import junit.framework.TestCase;

public class RoutingTableLoaderTest extends TestCase{

	
	public void testLoader(){
		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphDataStructure.GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "defaultnet.net", "", "net", new DummyProgress(), 1, graph);
		GraphInterface<Index, BasicVertexInfo> g = GraphFactory.copyAsSimple(graph);

		WeightedUlrikNG uAlg = new WeightedUlrikNG(ShortestPathAlgorithmInterface.DEFAULT, g, true, new  DummyProgress(), 0);
        uAlg.run();
        FastList<Index>[][] expectedRoutingTable = uAlg.getRoutingTable();
        
		GraphPrinter printer = new GraphPrinter(g);
		String expectedRoutingTableStr = printer.getRoutingTableStr(expectedRoutingTable);
		
		FastList<Index>[][] actualRoutingTable = RoutingTableLoader.loadRoutingTable(expectedRoutingTableStr, g.getNumberOfVertices());
		String actualRoutingTableStr = printer.getRoutingTableStr(actualRoutingTable);
		
		assertEquals(expectedRoutingTableStr, actualRoutingTableStr);
	}
}

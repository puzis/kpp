package tests.topology;

import javolution.util.Index;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import server.common.DummyProgress;
import server.common.Network;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.GraphPrinter;
import topology.graphParsers.CaidaAsGraphParser;


public class CaidaAsGraphParserTest extends ParserTest{

    //private GraphInterface<Index,BasicVertexInfo> graphFromString;

    @Before
	public void setUp(){

        progress = 0.25;
        fileName = "forex-2010-08.txt" ;
        ext = "txt";
        content = "5 2 1\n" + "2 3 1\n" + "3 1 1\n";
        expectedVerticesFromFile = 580;
        expectedEdgesFromFile = 726;
        expectedVerticesFromString = 4;
        expectedEdgesFromString = 3;
        networkGraphParser = new CaidaAsGraphParser();

        super.setUp();
	}

    @Test
	public void testCaidaPrserViaGraphPrinter() {
		GraphPrinter printer = new GraphPrinter(graphFromString);

		Network newNet = new Network("new");
		newNet.importNetwork(new DummyProgress(), "testnet", printer.getVerticesStr() + printer.getEdgesStr(), "net0", GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP);
		GraphInterface<Index,BasicVertexInfo> newGraph =  newNet.getGraphSimple();


        Assert.assertEquals(newGraph.getNumberOfVertices(), graphFromString.getNumberOfVertices());
        Assert.assertEquals(newGraph.getNumberOfEdges(), graphFromString.getNumberOfEdges());

	}

}

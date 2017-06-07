package tests.topology;

import javolution.util.Index;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import server.common.DummyProgress;
import server.common.Network;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.HyperGraphInterface;
import topology.graphParsers.CSVEdgeListParser;

public class CSVHyperEdgesFileParserTest  extends ParserTest{
    @Before
public void setUp(){
    progress = 0.25;
    fileName = "smallnet.e.csv" ;
    ext = "e.csv";
    content = "1,6\n" + "1,2\n" + "2,3\n" + "2,4\n" + "2,5\n" + "6,7\n" + "7,8\n" + "7,9\n" + "7,10\n";
    expectedVerticesFromFile = 10;
    expectedEdgesFromFile = 9;
    expectedVerticesFromString = 10;
    expectedEdgesFromString = 9;
    networkGraphParser = new CSVEdgeListParser();

    super.setUp();
	}


    @Test
	public void testHyperGraph(){
		Network network = new Network("something");
		HyperGraphInterface<Index,BasicVertexInfo> graph = null;
		if(network.importNetwork(
				new DummyProgress(),
				"hypernet.e.csv",
				"",
				"e.csv",
				GraphFactory.GraphDataStructure.HYPER_GRAPH_AS_HASH_MAP)){
			graph = network.getGraph();
		}
		else{
			Assert.assertTrue(false);
		}

        Assert.assertEquals(6, graph.getNumberOfVertices());
        Assert.assertEquals(2, graph.getNumberOfEdges());
		
		
	}
}

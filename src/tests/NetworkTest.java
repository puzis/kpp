package tests;

import javolution.util.Index;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import server.common.DummyProgress;
import server.common.Network;
import server.execution.AbstractExecution;
import topology.*;


/**
 * Created with IntelliJ IDEA.
 * User: danahend
 * Date: 09/09/12
 * Time: 15:34
 */
public class NetworkTest {

    //CONSTANTS
    private static final int EDGES_NUMBER = 9;
    private static final int VERTICES_NUMBER = 10;
    private static final String SMALLNET_NET = "smallnet.net";
    private static final String NETWORK = "network";
    private static final String NET = "net";

    private Network network;
    private String expected;
    private String actual;

    @Before
    public void SetUp(){
        network = new Network(NETWORK);
    }

    @Test
    public void testConstructorWithName(){
        expected = NETWORK;
        actual = network.getName();
        Assert.assertEquals("network name is wrong",expected,actual);
    }

    @Test
    public void testConstructorWithNameAndGraph(){
        network = new Network(NETWORK , new UndirectedHyperGraphAsHashMap<Index, BasicVertexInfo>());
        expected = network.getName();
        actual = NETWORK ;
        Assert.assertEquals("network name is wrong",expected,actual);
        expected = network.getGraphSimple().toString();
        //content of empty graph is: Links:[]  Vertices:[]
        actual = "Links:[]\tVertices:[]";
        Assert.assertEquals("failed to create graph",expected,actual);
    }

    @Test
    public void testStoreAndLoadNetwork(){
        AbstractExecution exe = new DummyProgress();
        network.importNetwork(new DummyProgress(),SMALLNET_NET,"", NET , GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP, GraphFactory.VertexInfoType.VERTEX_INFO);
        Assert.assertTrue("can't store network",network.storeGraph());
        Assert.assertTrue("can't load network"+SMALLNET_NET , network.loadNetwork(exe));
        Assert.assertEquals("vertices number is wrong",VERTICES_NUMBER, network.getNumberOfVertices());
        Assert.assertEquals("edges number is wrong",EDGES_NUMBER , network.getNumberOfEdges());
    }

    @Test
    public void testImportNetworkWithoutExtensionAndVertexStructure(){
        Assert.assertTrue("import network failed",network.importNetwork(new DummyProgress(), SMALLNET_NET , "", GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP));
        networkCompare(network.getGraphSimple());
    }

    @Test
    public void testImportNetworkWithoutVertexStructure(){
        Assert.assertTrue("import network failed",network.importNetwork(new DummyProgress(), SMALLNET_NET ,"", NET , GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP));
        networkCompare(network.getGraphSimple());
    }

    @Test
    public void testImportNetwork(){
        Assert.assertTrue("network import failed",network.importNetwork(new DummyProgress(),SMALLNET_NET,"", NET , GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP, GraphFactory.VertexInfoType.VERTEX_INFO));
       networkCompare(network.getGraphSimple());
    }

    @Test
    public void testGetGraphSimple(){
        network.importNetwork(new DummyProgress(), SMALLNET_NET , "", NET , GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP, GraphFactory.VertexInfoType.VERTEX_INFO);
        GraphInterface<Index,BasicVertexInfo> graph =  network.getGraphSimple();
        Assert.assertEquals("edges number is wrong",EDGES_NUMBER,graph.getNumberOfEdges());
        Assert.assertEquals("vertices number is wrong",VERTICES_NUMBER,graph.getNumberOfVertices());
        networkCompare(network.getGraphSimple());
    }

    @Test
    public  void  testCreateGraph(){
        network.importNetwork(new DummyProgress(),SMALLNET_NET,"", NET , GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP, GraphFactory.VertexInfoType.VERTEX_INFO);
        HyperGraphInterface<Index,BasicVertexInfo> graph =  network.getGraphSimple();
        Assert.assertEquals("edges number is wrong",EDGES_NUMBER,graph.getNumberOfEdges());
        Assert.assertEquals("vertices number is wrong",VERTICES_NUMBER,graph.getNumberOfVertices());
        networkCompare(network.getGraphSimple());
    }

    @Test
    public void testGetGraphAs(){
        GraphFactory.GraphDataStructure gdsTarget = GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP;
        network.importNetwork(new DummyProgress(), SMALLNET_NET , "", NET, GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP, GraphFactory.VertexInfoType.VERTEX_INFO);
        HyperGraphInterface graph = network.getGraphAs(gdsTarget);
        Assert.assertEquals("graph data structure is wrong",GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP,graph.getType());
        networkCompare(network.getGraphSimple());
    }

    private void networkCompare(GraphInterface<Index,BasicVertexInfo> graphActual){
        int[] verticesFrom = {0,0,1,1,1,5,6,6,6};
        int[] verticesTo = {5,1,2,3,4,6,7,8,9};

        //compare edges
        for (int i=0; i<graphActual.getNumberOfEdges();i++){
            StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            Assert.assertTrue(("comparing failed at test"+ste[2].getMethodName()),graphActual.isEdge(Index.valueOf(verticesFrom[i]),Index.valueOf(verticesTo[i])) );
        }

        //compare vertices
        for (int i=0; i<graphActual.getNumberOfVertices();i++){
            Assert.assertTrue(graphActual.isVertex(Index.valueOf(i)));
        }
    }
}

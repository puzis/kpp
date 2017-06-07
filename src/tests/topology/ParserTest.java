package tests.topology;

import javolution.util.Index;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import server.common.DummyProgress;
import server.common.Network;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.graphParsers.NetworkGraphParser;

/**
 * Created with IntelliJ IDEA.
 * User: danahend
 * Date: 13/09/12
 * Time: 17:11
 */
public  class  ParserTest {

    protected static final String EMPTY = "";
    protected static final GraphFactory.GraphDataStructure DI_GRAPH_AS_HASH_MAP = GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP;


    protected double  progress ;
    protected String fileName;
    protected String ext;
    protected String content;
    protected int expectedVerticesFromFile ;
    protected int expectedEdgesFromFile ;
    protected int expectedVerticesFromString ;
    protected int expectedEdgesFromString ;

    protected GraphInterface<Index,BasicVertexInfo> graphFromFileWithExt;
    protected GraphInterface<Index,BasicVertexInfo> graphFromFileNoExt;
    protected GraphInterface<Index,BasicVertexInfo> graphFromString ;

    protected NetworkGraphParser networkGraphParser ;

    @Before
    public void setUp(){
        graphFromFileWithExt = createNetwork( "fromfile" , fileName , EMPTY , ext , DI_GRAPH_AS_HASH_MAP );
        graphFromFileNoExt = createNetwork( "fromfile" , fileName , EMPTY , EMPTY , DI_GRAPH_AS_HASH_MAP );
        graphFromString = createNetworkFromString( "fromfile" , fileName , content  , DI_GRAPH_AS_HASH_MAP );

    }

    protected GraphInterface<Index,BasicVertexInfo> createNetwork(String networkName, String filename_with_extension, String importedNet, String ext, GraphFactory.GraphDataStructure graphDataStructure){
        Network network= new Network(networkName);
        if (ext!=null && !ext.isEmpty())
            network.importNetwork(new DummyProgress(), filename_with_extension, importedNet, ext, graphDataStructure);
        else
            network.importNetwork(new DummyProgress(), filename_with_extension, importedNet, graphDataStructure);
        return network.getGraphSimple();
    }

    protected GraphInterface<Index,BasicVertexInfo> createNetworkFromString(String networkName, String filename_with_extension, String importedNet , GraphFactory.GraphDataStructure graphDataStructure){
        Network network= new Network(networkName);
        network.importNetwork(new DummyProgress(), filename_with_extension, importedNet, graphDataStructure);
        return network.getGraphSimple();
    }

    @Test
    public void testParsingFromFileWithExt(){
        Assert.assertEquals(expectedVerticesFromFile, graphFromFileWithExt.getNumberOfVertices());
        Assert.assertEquals(expectedEdgesFromFile, graphFromFileWithExt.getNumberOfEdges());
    }

    @Test
    public void testParsingFromFileNoExt(){
        Assert.assertEquals(expectedVerticesFromFile, graphFromFileNoExt.getNumberOfVertices());
        Assert.assertEquals(expectedEdgesFromFile, graphFromFileNoExt.getNumberOfEdges());
    }

    @Test
    public void testParsingFromString(){
        Assert.assertEquals(expectedVerticesFromString, graphFromString.getNumberOfVertices());
        Assert.assertEquals(expectedEdgesFromString, graphFromString.getNumberOfEdges());
    }

    @Test
    public void testGetextension() {
        String expected = ext;
        String actual = networkGraphParser.getextension();
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void testUpdateLoadProgress(){
        double  expected = progress;
        AbstractExecution actualProgress = new DummyProgress();
        networkGraphParser.updateLoadProgress(actualProgress,1.0);
        Assert.assertEquals(expected,actualProgress.getProgress());



    }
}

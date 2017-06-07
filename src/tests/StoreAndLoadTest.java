package tests;

import javolution.util.Index;

import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.ServerConstants;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.SerializableGraphRepresentation;
import topology.graphParsers.NetFileParser;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

/**
 * Created by IntelliJ IDEA.
 * User: puzis
 * Date: Sep 6, 2007
 * Time: 11:43:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class StoreAndLoadTest extends TestCase
{
    private DataWorkshop m_dw1 = null;
    private DataWorkshop m_dw2 = null;

    public StoreAndLoadTest(String arg0)
    {
        super(arg0);
    }

    public void setUp() throws Exception
    {
        NetFileParser graphLoader = new NetFileParser();
		SerializableGraphRepresentation snet = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
        graphLoader.analyzeFile(ServerConstants.DATA_DIR + "defaultnet.net", new DummyProgress(), 1, snet);
        GraphInterface<Index,BasicVertexInfo> graph = GraphFactory.copyAsSimple(snet);
        
        m_dw1 = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, true, new DummyProgress(), 1);
        m_dw1.computePairBetweenness();

        m_dw1.saveToDisk(ServerConstants.DATA_DIR + "defaultnet.dw", new DummyProgress(), 1);
        m_dw2 = new DataWorkshop();
        m_dw2.loadFromDisk(ServerConstants.DATA_DIR + "defaultnet.dw", new DummyProgress(), 1);
        m_dw2.computePairBetweenness();
    }

    public void testPathBetweenness()
    {
        double[][] pathBetweeness1 = m_dw1.getPathBetweeness();
        double[][] pathBetweeness2 = m_dw2.getPathBetweeness();

        assertEquals(pathBetweeness1.length, pathBetweeness2.length);
        for (int i = 0; i < pathBetweeness1.length / 2; i++)
		{
			for (int j = 0; j < pathBetweeness2.length / 2; j++)
				assertEquals(pathBetweeness1[i][j], pathBetweeness2[i][j]);
		}
        
        assertEquals(m_dw1.getGraph(), m_dw2.getGraph());
    }
}
package tests.betweenness.brandes;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.ServerConstants;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.SerializableGraphRepresentation;
import topology.VertexInfo;
import topology.graphParsers.NetFileParser;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

import common.MatricesUtils;

public class CapacityTest extends TestCase
{
	GraphInterface<Index,BasicVertexInfo> g_circle, g_line, g_clique, g_7_1, g_7_2, defaultnet;
	double [] g_line_betweenness = {12.00, 22.00, 28.00, 30.00, 28.00, 22.00, 12.00};
    double [] g_clique_betweenness = {12.00, 12.00, 12.00, 12.00, 12.00, 12.00, 12.00};
    double [] g_7_1_betweenness = {12.00, 12.00, 28.00, 30.00, 28.00, 12.00, 12.00};
    double [] g_7_2_betweenness = {17.9222, 11.4667, 11.2444, 16.501587, 10.83333, 12.031746};
    
    private int m_commonFactor = 3; 
	
	public CapacityTest(String arg0){	super(arg0);	}

    public void setUp()
    {
    	g_circle = new GraphAsHashMap<Index,BasicVertexInfo>();
    	for (int v = 0; v < 4; v++)
            g_circle.addVertex(Index.valueOf(v), new VertexInfo());

    	g_circle.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>(new Double(2*m_commonFactor)));
    	g_circle.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>(new Double(1*m_commonFactor)));
    	g_circle.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>(new Double(2*m_commonFactor)));
    	g_circle.addEdge(Index.valueOf(3), Index.valueOf(0), new EdgeInfo<Index,BasicVertexInfo>(new Double(3*m_commonFactor)));
    	
        g_clique = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_clique.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 7; v++)
            for (int u = 0; u < 7; u++)
            {   /** The different weights should not affect the betweenness of vertices in a clique. */
            	if (((v == 6 && u == 5) || (v == 1 && u == 2)) && !g_clique.isEdge(Index.valueOf(u), Index.valueOf(v)))
            		g_clique.addEdge(Index.valueOf(u), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>(new Double(5)));
            	else if (u != v)
                {
                	if (!g_clique.isEdge(Index.valueOf(u), Index.valueOf(v)))
                		g_clique.addEdge(Index.valueOf(u), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>(new Double(2)));
                }
            }
        
        g_7_2 = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 6; v++)
            g_7_2.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 5; v++)
        {
            g_7_2.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>(new Double(2*m_commonFactor)));
        }
        g_7_2.addEdge(Index.valueOf(0), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>(new Double(4*m_commonFactor))); 
        g_7_2.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>(new Double(3*m_commonFactor)));
        
        g_line = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_line.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 3; v++)
        {
            g_line.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>(new Double(2)));
        }
        /** The different weights should not affect the betweenness of vertices in a line graph. */
        g_line.addEdge(Index.valueOf(3), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>(new Double(5)));
        g_line.addEdge(Index.valueOf(4), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>(new Double(4)));
        g_line.addEdge(Index.valueOf(5), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>(new Double(6)));

        g_7_1 = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_7_1.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 1; v < 6; v++)
        {
            g_7_1.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>(new Double(2)));
        }
        /** The different weights should not affect the betweenness of vertices in this graph. 
         *  This is so, because there is no two ways to get from some s to some t. */
        g_7_1.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>(new Double(1))); 
        g_7_1.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>(new Double(5))); 
        g_7_1.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>(new Double(3)));

		SerializableGraphRepresentation snet = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
        new NetFileParser().analyzeFile(ServerConstants.DATA_DIR + "defaultnet.net", new DummyProgress(), 1, snet);
        defaultnet = GraphFactory.copyAsSimple(snet);
    }
    
    public void testDeltaUpdate() throws Exception
    {
    	testDeltaUpdate(g_circle);
    	testDeltaUpdate(g_7_2);
    	testDeltaUpdate(g_line);
    }
    
    public void testDataWorkshopBetweenness() throws Exception
    {
        assertTrue(testDWBetweennessPerGraph(g_clique, g_clique_betweenness, null));
        assertTrue(testDWBetweennessPerGraph(g_line, g_line_betweenness, null));
        assertTrue(testDWBetweennessPerGraph(g_7_1, g_7_1_betweenness, null));
        assertTrue(testDWBetweennessPerGraph(g_7_2, g_7_2_betweenness, null));
    }
    
    private boolean testDWBetweennessPerGraph(GraphInterface<Index,BasicVertexInfo> graph, double [] betweenness, double [][] commWeights) throws Exception
    {
    	if (commWeights == null)
    		commWeights = MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
        
    	NumberFormat formatter = new DecimalFormat("0.000");
    	
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, new DenseTrafficMatrix(commWeights), true, new DummyProgress(), 1);
        for (int i = 0; i < graph.getNumberOfVertices(); i++)
        {
            if (!formatter.format(dw.getBetweenness(i)).equals(formatter.format(betweenness[i])))
            	return false;
        }
        return true;
    }
    
    public void testDeltaUpdate(GraphInterface<Index,BasicVertexInfo> graph) throws Exception
    {
    	AbsTrafficMatrix	commWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); //MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
        NumberFormat formatter = new DecimalFormat("0.000");
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, commWeights, true, new DummyProgress(), 1);
        
    	System.out.print("================================================\n[");
    	for (int i = 0; i < graph.getNumberOfVertices(); i++)
        {
            System.out.print(formatter.format(dw.getBetweenness(i)));
            if (i < graph.getNumberOfVertices() - 1)
            	System.out.print(", ");
        }
    	System.out.println("]");
    }
}
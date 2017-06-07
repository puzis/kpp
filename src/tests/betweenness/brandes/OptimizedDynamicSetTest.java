package tests.betweenness.brandes;

import java.util.Random;


import javolution.util.FastList;
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
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.OptimizedDynamicBetweennessSet;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

import common.MatricesUtils;

public class OptimizedDynamicSetTest extends TestCase
{
	GraphInterface<Index,BasicVertexInfo> g_line, g_clique, g_7_1, g_7_2;
	GraphInterface<Index,BasicVertexInfo> defaultnet, joe;
	
	public OptimizedDynamicSetTest(String arg0){	super(arg0);	}
	
	public void setUp(){
        g_line = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_line.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
        {
            g_line.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }

        g_clique = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_clique.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 7; v++)
            for (int u = 0; u < 7; u++)
                if (u != v)
                {
                	if (!g_clique.isEdge(Index.valueOf(u), Index.valueOf(v)))
                		g_clique.addEdge(Index.valueOf(u), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
                }

        g_7_1 = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_7_1.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
        {
            g_7_1.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_1.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>()); g_7_1.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());

        g_7_2 = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 6; v++)
            g_7_2.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 5; v++)
        {
            g_7_2.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_2.addEdge(Index.valueOf(0), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>()); g_7_2.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        
		SerializableGraphRepresentation snet = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
        new NetFileParser().analyzeFile(ServerConstants.DATA_DIR + "defaultnet.net", new DummyProgress(), 1, snet);
        defaultnet = GraphFactory.copyAsSimple(snet);
        
		SerializableGraphRepresentation sjoe = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
        new NetFileParser().analyzeFile(ServerConstants.DATA_DIR + "joe.net", new DummyProgress(), 1, sjoe);
        joe = GraphFactory.copyAsSimple(snet);
    }
	
	public void testMatricesUpdate() throws Exception
    {
    	testMatricesUpdates(g_line);
    	testMatricesUpdates(g_clique);
    	testMatricesUpdates(g_7_1);
    	testMatricesUpdates(g_7_2);
    	testMatricesUpdates(joe);
    	testMatricesUpdates(defaultnet);
    }
    
    private void testMatricesUpdates(GraphInterface<Index,BasicVertexInfo> graph) throws Exception
    {
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, true, new DummyProgress(), 1);
    	FastList<Index> group = new FastList<Index>();
    	for (int i = 0; i < graph.getNumberOfVertices(); i++)
    		group.add(Index.valueOf(i));
    	
    	BasicSet dynamicSet = null;
    	
    	/** Add vertices in random order (by implementing random permutation). */
    	for (int i = 0; i < 10; i++){
    		dynamicSet = new OptimizedDynamicBetweennessSet(dw, group);
	    	int [] permutation = MatricesUtils.permute(graph.getNumberOfVertices());
	    	
	    	for (int v = 0; v < graph.getNumberOfVertices(); v++){
	    		dynamicSet.add(Index.valueOf(permutation[v]));
	    		for (int w = 0; w < graph.getNumberOfVertices(); w++){
	    			assertTrue(dynamicSet.getContribution(Index.valueOf(permutation[w])) >= -0.05);
	    	    }
	    	}
	    	
	    	for (int v = 0; v < graph.getNumberOfVertices(); v++){
	    		assertTrue(dynamicSet.getContribution(Index.valueOf(v)) >= -0.05);
	    	}
    	}
    	/** Add random groups of vertices (by implementing random permutation). */
    	for (int tries = 0; tries < 10; tries++)
    	{
    		int firstGroupSize = (new Random()).nextInt(graph.getNumberOfVertices());
	    	dynamicSet = new OptimizedDynamicBetweennessSet(dw, group);
	    	int [] permutation = MatricesUtils.permute(graph.getNumberOfVertices());
	    	
	    	FastList<Index> g = new FastList<Index>();
	    	for (int i = 0; i < firstGroupSize; i++) 
	    		g.add(Index.valueOf(permutation[i]));
	    	dynamicSet.add(g);
	    	
	    	g = new FastList<Index>();//new int[graph.getNumberOfVertices() - firstGroupSize];
	    	for (int i = firstGroupSize, j = 0; i < graph.getNumberOfVertices(); i++, j++) 
	    		g.add(Index.valueOf(permutation[i]));
	    	
	    	dynamicSet.add(g);
	    	
	    	for (int v = 0; v < graph.getNumberOfVertices(); v++){
	    		assertTrue(dynamicSet.getContribution(Index.valueOf(v)) >= -0.05);
	    	}
    	}
    
    	/** Create OptimizedDynamicSet based on group of candidates (and not on all of the vertices in the graph).
    	 *  Add the whole group as members of the set. */
    	for (int tries = 0; tries < 10; tries++){
	    	int [] permutation = MatricesUtils.permute(graph.getNumberOfVertices());
	    	int firstGroupSize = (new Random()).nextInt(graph.getNumberOfVertices() - 1) + 1;
	    	FastList<Index> partialGroup = new FastList<Index>();
	    	FastList<Index> pt = new FastList<Index>();
	    	for (int i = 0; i < firstGroupSize; i++){
	    		partialGroup.add(Index.valueOf(permutation[i]));
	    		pt.add(Index.valueOf(permutation[i]));
	    	}
	    	dynamicSet = new OptimizedDynamicBetweennessSet(dw, pt);
	    	
	    	dynamicSet.add(partialGroup);
	    	
	    	for (int v = 0; v < graph.getNumberOfVertices(); v++){
	    		assertTrue(dynamicSet.getContribution(Index.valueOf(permutation[v])) >= -0.05);
	    	}
    	}
    }
}
package tests.randomWalkBetweeness;

import java.util.ArrayList;


import javolution.util.FastList;
import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.randomWalkBetweeness.AbsGreedyRWBetweenness;
import algorithms.centralityAlgorithms.randomWalkBetweeness.GroupRandomWalkBetweeness;
import algorithms.centralityAlgorithms.randomWalkBetweeness.RandomWalkBetweeness;


public class SearchTest extends TestCase 
{
    private double [][] m_degrees = {{6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0}, 
			 {0, 0, 0 ,4, 0, 0, 0, 0, 0, 0, 0}, {0, 0 ,0, 0, 4, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0}, 
			 {0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0},
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4}};
	private ArrayList<FastList<Index>> m_G = new ArrayList<FastList<Index>>(11);
	private double [][] m_A = {
	{0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0}, // A
	{1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1}, // B
	{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // C
	{1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0}, {1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0}, {1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0}, {1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0},
	{0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1}, {0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1}, {0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1}, {0, 1, 0, 0, 0, 0 ,0, 1, 1, 1, 0}};
	@SuppressWarnings("unused")
	private RandomWalkBetweeness m_randomWalkAlgorithm = null;
	
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	
	public SearchTest(String string) {
		super(string);
	}

public void setUp()
{
	m_graph = new GraphAsHashMap<Index,BasicVertexInfo>();
	for (int i = 0; i < 11; i++)
	m_graph.addVertex(Index.valueOf(i), new VertexInfo());
	for (int i = 1; i < 7; i++)
	m_graph.addEdge(Index.valueOf(0), Index.valueOf(i), new EdgeInfo<Index,BasicVertexInfo>());
	m_graph.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
	for (int i = 7; i < 11; i++)
	m_graph.addEdge(Index.valueOf(1), Index.valueOf(i), new EdgeInfo<Index,BasicVertexInfo>());
	for (int i = 4; i < 7; i++)
	m_graph.addEdge(Index.valueOf(3), Index.valueOf(i), new EdgeInfo<Index,BasicVertexInfo>());
	
	// A's neighbors
	FastList<Index> neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(1)); // B
	neighbors.add(Index.valueOf(2)); // C
	
	neighbors.add(Index.valueOf(3)); // All the rest click
	neighbors.add(Index.valueOf(4));
	neighbors.add(Index.valueOf(5));
	neighbors.add(Index.valueOf(6));
	m_G.add(0, neighbors);
	
	// B's neighbors
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(0)); // A
	neighbors.add(Index.valueOf(2)); // C
	
	neighbors.add(Index.valueOf(7)); // All the rest click
	neighbors.add(Index.valueOf(8));
	neighbors.add(Index.valueOf(9));
	neighbors.add(Index.valueOf(10));
	m_G.add(1, neighbors);
	
	// C's neighbors
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(0)); // A
	neighbors.add(Index.valueOf(1)); // B
	m_G.add(2, neighbors);
	
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(0));
	neighbors.add(Index.valueOf(4));
	neighbors.add(Index.valueOf(5));
	neighbors.add(Index.valueOf(6));
	m_G.add(3, neighbors);
	
	m_graph.addEdge(Index.valueOf(4), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
	m_graph.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(0));
	neighbors.add(Index.valueOf(3));
	neighbors.add(Index.valueOf(5));
	neighbors.add(Index.valueOf(6));
	m_G.add(4, neighbors);
	
	m_graph.addEdge(Index.valueOf(5), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(0));
	neighbors.add(Index.valueOf(3));
	neighbors.add(Index.valueOf(4));
	neighbors.add(Index.valueOf(6));
	m_G.add(5, neighbors);
	
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(0));
	neighbors.add(Index.valueOf(3));
	neighbors.add(Index.valueOf(4));
	neighbors.add(Index.valueOf(5));
	m_G.add(6, neighbors);
	
	for (int i = 8; i < 11; i++)
	m_graph.addEdge(Index.valueOf(7), Index.valueOf(i), new EdgeInfo<Index,BasicVertexInfo>());
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(1));
	neighbors.add(Index.valueOf(8));
	neighbors.add(Index.valueOf(9));
	neighbors.add(Index.valueOf(10));
	m_G.add(7, neighbors);
	
	m_graph.addEdge(Index.valueOf(8), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
	m_graph.addEdge(Index.valueOf(8), Index.valueOf(10), new EdgeInfo<Index,BasicVertexInfo>());
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(1));
	neighbors.add(Index.valueOf(7));
	neighbors.add(Index.valueOf(9));
	neighbors.add(Index.valueOf(10));
	m_G.add(8, neighbors);
	
	m_graph.addEdge(Index.valueOf(9), Index.valueOf(10), new EdgeInfo<Index,BasicVertexInfo>());
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(1));
	neighbors.add(Index.valueOf(7));
	neighbors.add(Index.valueOf(8));
	neighbors.add(Index.valueOf(10));
	m_G.add(9, neighbors);
	
	neighbors = new FastList<Index>();
	neighbors.add(Index.valueOf(1));
	neighbors.add(Index.valueOf(7));
	neighbors.add(Index.valueOf(8));
	neighbors.add(Index.valueOf(9));
	m_G.add(10, neighbors);
	
	FastList<Index> vsToRemove = new FastList<Index>();
	vsToRemove.add(Index.valueOf(m_degrees.length - 1));
	//vsToRemove.add(Index.valueOf(m_degrees.length - 6));
	m_randomWalkAlgorithm = new RandomWalkBetweeness(m_degrees, m_A, m_G, vsToRemove);
	}
	public void testSearch() throws Exception
	{
		FastList<Index> candidates = new FastList<Index>();
		for (int i = 0; i < 11; i++)
			candidates.add(Index.valueOf(i));
		int[] givenV = new int[0];
		AbstractSimpleEdge<Index,BasicVertexInfo>[] givenL = new AbstractSimpleEdge[0];
		
		GroupRandomWalkBetweeness grwb = new GroupRandomWalkBetweeness(m_graph);
		Index[] result = AbsGreedyRWBetweenness.findVertices(grwb, candidates, givenV, givenL, Algorithm.TopK, Bound.GroupSize, 11, new DummyProgress(), 1);
		assertTrue(result[0].intValue() == 0 || result[0].intValue() == 1 || result[0].intValue() == 2);
		
		givenV = new int[1]; givenV[0] = 1;
		result = AbsGreedyRWBetweenness.findVertices(grwb, candidates, givenV, givenL, Algorithm.TopK, Bound.GroupSize, 11, new DummyProgress(), 1);
		assertTrue(result[0].intValue() == 0 || result[0].intValue() == 2);
		
		result = AbsGreedyRWBetweenness.findVertices(grwb, candidates, givenV, givenL, Algorithm.TopK, Bound.Centrality, 0.7, new DummyProgress(), 1);
		int cnt = 0; for (Index idx : result) if (idx != null) cnt++;
		assertEquals(1, cnt);
		assertTrue(result[0].intValue() == 0 || result[0].intValue() == 1 || result[0].intValue() == 2);
	}
}
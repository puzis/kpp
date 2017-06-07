package tests.randomWalkBetweeness;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javolution.util.FastList;
import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;
import Jama.Matrix;
import algorithms.centralityAlgorithms.randomWalkBetweeness.GroupRandomWalkBetweeness;
import algorithms.centralityAlgorithms.randomWalkBetweeness.RandomWalkBetweeness;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 23/10/2007
 * Time: 14:04:53
 * To change this template use File | Settings | File Templates.
 */
public class RandomWalkBetweenessTest extends TestCase
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
    private RandomWalkBetweeness m_randomWalkAlgorithm = null;

    private GraphInterface<Index,BasicVertexInfo> m_graph = null;
    
    public RandomWalkBetweenessTest(String string) {
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
//        vsToRemove.add(Index.valueOf(m_degrees.length - 6));
        m_randomWalkAlgorithm = new RandomWalkBetweeness(m_degrees, m_A, m_G, vsToRemove);
    }

    public void testBetweeness()
    {
        NumberFormat formatter = new DecimalFormat("0.000");

        assertEquals(formatter.format(0.670), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(0)));
        assertEquals(formatter.format(0.670), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(1)));
        assertEquals(formatter.format(0.333), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(2)));

        assertEquals(formatter.format(0.269), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(3)));
        assertEquals(formatter.format(0.269), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(4)));
        assertEquals(formatter.format(0.269), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(5)));
        assertEquals(formatter.format(0.269), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(6)));
        assertEquals(formatter.format(0.269), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(7)));
        assertEquals(formatter.format(0.269), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(8)));
        assertEquals(formatter.format(0.269), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(9)));
        assertEquals(formatter.format(0.269), formatter.format(m_randomWalkAlgorithm.getVertexBetweeness(10)));
    }
    
    public void testSumOfBetweenness() throws Exception
    {
    	NumberFormat formatter = new DecimalFormat("0.00");
    	
    	Index[] group = new Index[3];
    	for (int i = 0; i < 3; i++) 
    		group[i] = Index.valueOf(i);
    	
    	assertEquals(formatter.format(0.670 * 2 + 0.333), formatter.format(GroupRandomWalkBetweeness.calculateSumGroup(m_graph, group, new DummyProgress(), 1)));
    	group = new Index[11-3];
    	for (int i = 3; i < 11; i++) 
    		group[i - 3] = Index.valueOf(i);
    	assertEquals(formatter.format(0.269 * 8), formatter.format(GroupRandomWalkBetweeness.calculateSumGroup(m_graph, group, new DummyProgress(), 1)));
    }
    
    public void testRemovingRowAndColumn()
    {
    	double [][] integers = {{1, 1, 1, 1}, {2, 2, 2, 2}, {3, 3, 3, 3}, {4, 4, 4, 4}};
    	Matrix original = new Matrix(integers);
    	FastList<Index> vs = new FastList<Index>();
    	vs.add(Index.valueOf(2));
    	Matrix removed = RandomWalkBetweeness.removeGivenRowAndColumnForTest(original, vs);
    	double [][] expected = {{1, 1, 1}, {2, 2, 2}, {4, 4, 4}};
    	for (int i = 0; i < removed.getRowDimension(); i++)
    	{
    		for (int j = 0; j < removed.getColumnDimension(); j++)
    		{
    			assertEquals(expected[i][j], removed.get(i, j));
    		}
    	}
    	
    	double [][] integers2 = {{1, 2, 3, 4}, {1, 2, 3, 4}, {1, 2, 3, 4}, {1, 2, 3, 4}};
    	Matrix original2 = new Matrix(integers2);
    	vs = new FastList<Index>();
    	vs.add(Index.valueOf(2));
    	Matrix removed2 = RandomWalkBetweeness.removeGivenRowAndColumnForTest(original2, vs);
    	double [][] expected2 = {{1, 2, 4}, {1, 2, 4}, {1, 2, 4}};
    	for (int i = 0; i < removed2.getRowDimension(); i++)
    	{
    		for (int j = 0; j < removed2.getColumnDimension(); j++)
    		{
    			assertEquals(expected2[i][j], removed2.get(i, j));
    		}
    	}
    	
    	double [][] integers3 = {{1, 2, 2, 1}, {2, 3, 3, 2}, {3, 4, 4, 3}, {4, 1, 1, 4}};
    	Matrix original3 = new Matrix(integers3);
//    	Matrix removed3 = RandomWalkBetweeness.removeGivenRowAndColumnForTest(original3, 0, 2);
//    	double [][] expected3 = {{2, 3, 2}, {3, 4, 3}, {4, 1, 4}};
//    	for (int i = 0; i < removed3.getRowDimension(); i++)
//    	{
//    		for (int j = 0; j < removed3.getColumnDimension(); j++)
//    		{
//    			assertEquals(expected3[i][j], removed3.get(i, j));
//    		}
//    	}
//    	
    	vs = new FastList<Index>();
    	vs.add(Index.valueOf(0));
    	Matrix removed4 = RandomWalkBetweeness.removeGivenRowAndColumnForTest(original3, vs);
    	double [][] expected4 = {{3, 3, 2}, {4, 4, 3}, {1, 1, 4}};
    	for (int i = 0; i < removed4.getRowDimension(); i++)
    	{
    		for (int j = 0; j < removed4.getColumnDimension(); j++)
    		{
    			assertEquals(expected4[i][j], removed4.get(i, j));
    		}
    	}
    	
    	double [][] integers5 = {{1, 2, 5, 1}, {2, 6, 3, 2}, {3, 4, 7, 3}, {4, 9, 1, 4}};
    	Matrix original5 = new Matrix(integers5);
    	vs = new FastList<Index>();
    	vs.add(Index.valueOf(0));	vs.add(Index.valueOf(1)); vs.add(Index.valueOf(3));
    	Matrix removed5 = RandomWalkBetweeness.removeGivenRowAndColumnForTest(original5, vs);
    	double [][] expected5 = {{7}};
    	for (int i = 0; i < removed5.getRowDimension(); i++)
    	{
    		for (int j = 0; j < removed5.getColumnDimension(); j++)
    		{
    			assertEquals(expected5[i][j], removed5.get(i, j));
    		}
    	}
    }
}

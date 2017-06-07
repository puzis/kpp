package tests.randomWalkBetweeness;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javolution.util.FastList;
import javolution.util.Index;
import junit.framework.TestCase;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.randomWalkBetweeness.GroupRandomWalkBetweeness;

public class GRWBTest extends TestCase
{
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	
	public GRWBTest (String arg0)
	{
		super(arg0);
	}
	
	public void setUp()
	{
		m_graph = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int v = 0; v < 11; v++)
			m_graph.addVertex(Index.valueOf(v));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(2));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(3));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(4));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(5));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(6));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(2));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(7));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(8));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(9));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(10));
		
		m_graph.addEdge(Index.valueOf(3), Index.valueOf(4));
		m_graph.addEdge(Index.valueOf(3), Index.valueOf(5));
		m_graph.addEdge(Index.valueOf(3), Index.valueOf(6));
		m_graph.addEdge(Index.valueOf(4), Index.valueOf(5));
		m_graph.addEdge(Index.valueOf(4), Index.valueOf(6));
		m_graph.addEdge(Index.valueOf(5), Index.valueOf(6));
		m_graph.addEdge(Index.valueOf(7), Index.valueOf(8));
		m_graph.addEdge(Index.valueOf(7), Index.valueOf(9));
		m_graph.addEdge(Index.valueOf(7), Index.valueOf(10));
		m_graph.addEdge(Index.valueOf(8), Index.valueOf(9));
		m_graph.addEdge(Index.valueOf(8), Index.valueOf(10));
		m_graph.addEdge(Index.valueOf(9), Index.valueOf(10));
	}
	
	public void testBetweeness()
    {
		ArrayList<FastList<Index>> G = new ArrayList<FastList<Index>>();
		for(int i = 0; i < m_graph.getNumberOfVertices(); i++)
		{
			FastList<Index> neighbors = new FastList<Index>();
			for(AbstractSimpleEdge<Index,BasicVertexInfo> e: m_graph.getOutgoingEdges(Index.valueOf(i)))
			{
				Index n = e.getNeighbor(Index.valueOf(i));
				neighbors.add(n);
			}
			G.add(i, neighbors);
		}
		GroupRandomWalkBetweeness grwb = new GroupRandomWalkBetweeness(G);
		
        NumberFormat formatter = new DecimalFormat("0.000");

        assertEquals(formatter.format(0.670), formatter.format(grwb.getVertexBetweeness(0)));
        assertEquals(formatter.format(0.670), formatter.format(grwb.getVertexBetweeness(1)));
        assertEquals(formatter.format(0.333), formatter.format(grwb.getVertexBetweeness(2)));

        assertEquals(formatter.format(0.269), formatter.format(grwb.getVertexBetweeness(3)));
        assertEquals(formatter.format(0.269), formatter.format(grwb.getVertexBetweeness(4)));
        assertEquals(formatter.format(0.269), formatter.format(grwb.getVertexBetweeness(5)));
        assertEquals(formatter.format(0.269), formatter.format(grwb.getVertexBetweeness(6)));
        assertEquals(formatter.format(0.269), formatter.format(grwb.getVertexBetweeness(7)));
        assertEquals(formatter.format(0.269), formatter.format(grwb.getVertexBetweeness(8)));
        assertEquals(formatter.format(0.269), formatter.format(grwb.getVertexBetweeness(9)));
        assertEquals(formatter.format(0.269), formatter.format(grwb.getVertexBetweeness(10)));
    }
}
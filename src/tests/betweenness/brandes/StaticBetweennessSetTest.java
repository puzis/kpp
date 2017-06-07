package tests.betweenness.brandes;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.Index;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.StaticBetweennessSet;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

public class StaticBetweennessSetTest extends BetweennessTest {

	public StaticBetweennessSetTest(String arg0) {
		super(arg0);
	}
	
	private void singleNode(GraphInterface<Index, BasicVertexInfo> graph)
	{
		DataWorkshop dw = new DataWorkshop(SPALG, graph, new DefaultTrafficMatrix(graph.getNumberOfVertices()), true, new DummyProgress(), 1);
		
		StaticBetweennessSet sbs = new StaticBetweennessSet(dw);
		
		Object[] candidates  = new Object[graph.getNumberOfVertices()];
		int j = 0;
		for (Index v : graph.getVertices())
		{
			candidates[j++] = v;
		}
		CandidatesBasedAlgorithm cbalg = new CandidatesBasedAlgorithm(dw,candidates);
		
		NumberFormat formatter = new DecimalFormat("0.000");
		
		for (int i = 0; i < graph.getNumberOfVertices(); i++) {
			sbs.add(Index.valueOf(i));
			double b = sbs.getGroupCentrality();
			double actual = cbalg.getBetweenness(i);
			assertEquals(formatter.format(b),formatter.format(actual));
			System.out.println(b+","+actual);
			sbs.remove(Index.valueOf(i));
		}
	}
	
	public void testSingleNode()
	{
		singleNode(g_star);
		singleNode(g_line);
		singleNode(g_clique);
		singleNode(g_7_1);
		singleNode(g_7_2);
	}
	
	
	private void group(GraphInterface<Index, BasicVertexInfo> graph)
	{
		DataWorkshop dw = new DataWorkshop(SPALG, graph, new DefaultTrafficMatrix(graph.getNumberOfVertices()), true, new DummyProgress(), 1);
		
		
		Object[] candidates  = new Object[graph.getNumberOfVertices()];
		int j = 0;
		for (Index v : graph.getVertices())
		{
			candidates[j++] = v;
		}
		
		NumberFormat formatter = new DecimalFormat("0.000");
		
		for (int k = 0; k < g_groups.length; k++) {
			CandidatesBasedAlgorithm cbalg = new CandidatesBasedAlgorithm(dw,candidates);
			StaticBetweennessSet sbs = new StaticBetweennessSet(dw);
			for (int k2 = 0; k2 < g_groups[k].length; k2++) {				
				int v = g_groups[k][k2]; 
				sbs.add(Index.valueOf(v));
				cbalg.addMember(v);
			}
			double b = sbs.getGroupCentrality();
			double actual = cbalg.getGroupBetweenness();
			assertEquals(formatter.format(b),formatter.format(actual));
			System.out.println(b+","+actual);
		}
	}
	
	public void testGroups()
	{
		group(g_star);
		group(g_line);
		group(g_clique);
		group(g_7_1);
		group(g_7_2);
	}

}

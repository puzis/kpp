package tests.betweenness.brandes;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.Index;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.DynamicBetweennessSet;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

public class DynamicBetweennessSetTest extends BetweennessTest {

	public DynamicBetweennessSetTest(String arg0) {
		super(arg0);
	}

	private void singleNode(GraphInterface<Index, BasicVertexInfo> graph) {
		DataWorkshop dw = new DataWorkshop(SPALG, graph,
				new DefaultTrafficMatrix(graph.getNumberOfVertices()), true,
				new DummyProgress(), 1);

		DynamicBetweennessSet dbs = new DynamicBetweennessSet(dw);

		Object[] candidates = new Object[graph.getNumberOfVertices()];
		int j = 0;
		for (Index v : graph.getVertices()) {
			candidates[j++] = v;
		}
		CandidatesBasedAlgorithm cbalg = new CandidatesBasedAlgorithm(dw,
				candidates);

		NumberFormat formatter = new DecimalFormat("0.000");

		for (int i = 0; i < graph.getNumberOfVertices(); i++) {
			dbs.add(Index.valueOf(i));
			double b = dbs.getGroupCentrality();
			double expected = cbalg.getBetweenness(i);
			assertEquals(formatter.format(b), formatter.format(expected));
			//System.out.println(b + "," + actual);
			dbs.remove(Index.valueOf(i));
		}
	}

	public void testSingleNode() {
		singleNode(g_star);
		singleNode(g_line);
		singleNode(g_clique);
		singleNode(g_7_1);
		singleNode(g_7_2);
	}

	private void group(GraphInterface<Index, BasicVertexInfo> graph) {
		DataWorkshop dw = new DataWorkshop(SPALG, graph,
				new DefaultTrafficMatrix(graph.getNumberOfVertices()), true,
				new DummyProgress(), 1);

		Object[] candidates = new Object[graph.getNumberOfVertices()];
		int j = 0;
		for (Index v : graph.getVertices()) {
			candidates[j++] = v;
		}

		NumberFormat formatter = new DecimalFormat("0.000");

		for (int k = 0; k < g_groups.length; k++) {
			CandidatesBasedAlgorithm cbalg = new CandidatesBasedAlgorithm(dw,
					candidates);
			DynamicBetweennessSet dbs = new DynamicBetweennessSet(dw);
			for (int k2 = 0; k2 < g_groups[k].length; k2++) {
				int v = g_groups[k][k2];
				dbs.add(Index.valueOf(v));
				cbalg.addMember(v);
			}
			double b = dbs.getGroupCentrality();
			double expected = cbalg.getGroupBetweenness();
			assertEquals(formatter.format(b), formatter.format(expected));
			//System.out.println(b + "," + actual);
		}
	}

	public void testGroups() {
		group(g_star);
		group(g_line);
		group(g_clique);
		group(g_7_1);
		group(g_7_2);
	}

	private void contribution(GraphInterface<Index, BasicVertexInfo> graph) {
		DataWorkshop dw = new DataWorkshop(SPALG, graph,
				new DefaultTrafficMatrix(graph.getNumberOfVertices()), true,
				new DummyProgress(), 1);

		DynamicBetweennessSet dbs = new DynamicBetweennessSet(dw);

		Object[] candidates = new Object[graph.getNumberOfVertices()];
		int j = 0;
		for (Index v : graph.getVertices()) {
			candidates[j++] = v;
		}
		CandidatesBasedAlgorithm cbalg = new CandidatesBasedAlgorithm(dw,
				candidates);

		NumberFormat formatter = new DecimalFormat("0.000");

		for (int i = 0; i < graph.getNumberOfVertices(); i++) {
			double b = dbs.getGroupCentrality();
			cbalg.addMember(i);
			double expected = cbalg.getGroupBetweenness();
			double c = dbs.getContribution(Index.valueOf(i));
			assertEquals(formatter.format(expected),formatter.format(b+c));
			dbs.add(Index.valueOf(i));
			b = dbs.getGroupCentrality();
			assertEquals(formatter.format(expected), formatter.format(b));
			System.out.println(b + "," + expected);
		}
	}
	

	public void testContribution() {
		contribution(g_star);
		contribution(g_line);
		contribution(g_clique);
		contribution(g_7_1);
		contribution(g_7_2);
	}

}

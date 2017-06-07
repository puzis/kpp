package tests.betweenness.brandes;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.OptimizedDynamicBetweennessSet;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

public class OptimizedBetweennessSetTest extends BetweennessTest {

	public OptimizedBetweennessSetTest(String arg0) {
		super(arg0);
	}

	private void singleNode(GraphInterface<Index, BasicVertexInfo> graph) {
		DataWorkshop dw = new DataWorkshop(SPALG, graph,
				new DefaultTrafficMatrix(graph.getNumberOfVertices()), true,
				new DummyProgress(), 1);
 

		FastList<Index> candList = new FastList<Index>();
		Object[] candArray = new Object[graph.getNumberOfVertices()];
		int j = 0;
		for (Index v : graph.getVertices()) {
			candArray[j++] = v;
			candList.add(v);
		}

		NumberFormat formatter = new DecimalFormat("0.000");

		for (int i = 0; i < graph.getNumberOfVertices(); i++) {
			OptimizedDynamicBetweennessSet dbs = new OptimizedDynamicBetweennessSet(dw, candList);
			CandidatesBasedAlgorithm cbalg = new CandidatesBasedAlgorithm(dw,
					candArray);
			dbs.add(Index.valueOf(i));
			cbalg.addMember(i);
			double b = dbs.getGroupCentrality();
			double expected = cbalg.getNormalizedGroupBetweenness();//cbalg.getBetweenness(i);
			System.out.println(b + "," + expected);
			assertEquals(formatter.format(b), formatter.format(expected));
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

		FastList<Index> candList = new FastList<Index>();
		Object[] candArray = new Object[graph.getNumberOfVertices()];
		int j = 0;
		for (Index v : graph.getVertices()) {
			candArray[j++] = v;
			candList.add(v);
		}

		NumberFormat formatter = new DecimalFormat("0.000");

		for (int k = 0; k < g_groups.length; k++) {
			CandidatesBasedAlgorithm cbalg = new CandidatesBasedAlgorithm(dw,
					candArray);
			OptimizedDynamicBetweennessSet dbs = new OptimizedDynamicBetweennessSet(dw, candList);
			for (int k2 = 0; k2 < g_groups[k].length; k2++) {
				int v = g_groups[k][k2];
				dbs.add(Index.valueOf(v));
				cbalg.addMember(v);
			}
			double b = dbs.getGroupCentrality();
			double expected = cbalg.getNormalizedGroupBetweenness();
			System.out.println(b + "," + expected);
			assertEquals(formatter.format(b), formatter.format(expected));
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

		FastList<Index> candList = new FastList<Index>();
		Object[] candArray = new Object[graph.getNumberOfVertices()];
		int j = 0;
		for (Index v : graph.getVertices()) {
			candArray[j++] = v;
			candList.add(v);
		}
		OptimizedDynamicBetweennessSet dbs = new OptimizedDynamicBetweennessSet(dw, candList);
		CandidatesBasedAlgorithm cbalg = new CandidatesBasedAlgorithm(dw,
				candArray);

		NumberFormat formatter = new DecimalFormat("0.000");

		for (int i = 0; i < graph.getNumberOfVertices(); i++) {
			double b = dbs.getGroupCentrality();
			b *= dw.getCommunicationWeight();
			cbalg.addMember(i);
			double expected = cbalg.getGroupBetweenness();
			double c = dbs.getContribution(Index.valueOf(i));
			assertEquals(formatter.format(expected),formatter.format(b+c));
			dbs.add(Index.valueOf(i));
			b = dbs.getGroupCentrality();
			b*=dw.getCommunicationWeight();
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

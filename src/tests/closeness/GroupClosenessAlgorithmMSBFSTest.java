package tests.closeness;

import java.text.DecimalFormat;

import java.text.NumberFormat;
import java.util.Random;

import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.GroupClosenessAlgorithmMSBFS;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

import javolution.util.FastSet;
import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;

public class GroupClosenessAlgorithmMSBFSTest extends TestCase{
	
	private GraphInterface<Index,BasicVertexInfo> m_graph;
    private NumberFormat formatter;
	private Object[] m_group;

	public void setUp() {
        m_graph = new GraphAsHashMap<Index,BasicVertexInfo>();
        formatter = new DecimalFormat("0.0000");

        for (int i = 0; i < 8; i++)
            m_graph.addVertex(Index.valueOf(i), new VertexInfo());
        m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(3), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(5), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(6), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(4), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());

        m_group = new Object[2];
        m_group[0] = Index.valueOf(2);
        m_group[1] = Index.valueOf(4);
	}
	
	public void testCorrectness() {
		
		GraphInterface<Index,BasicVertexInfo> graph = m_graph;
		Object[] group = m_group;
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , new DummyProgress(), 1.0);
		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(graph, formula, new DummyProgress(), 1);
		double oldAlgCloseness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
		
		GroupClosenessAlgorithmMSBFS newAlg = new GroupClosenessAlgorithmMSBFS(graph, formula);
		double newAlgCloseness = newAlg.getGroupCloseness(group);
		
		System.out.println(oldAlgCloseness+" , "+newAlgCloseness);
		assertEquals(formatter.format(oldAlgCloseness),formatter.format(newAlgCloseness));
		
		// making graph unconnected
		// remove edge doesnt work right ?
		//graph.removeEdge(graph.getEdge(Index.valueOf(0), Index.valueOf(1)));
		
        m_graph = new GraphAsHashMap<Index,BasicVertexInfo>();

        for (int i = 0; i < 8; i++)
            m_graph.addVertex(Index.valueOf(i), new VertexInfo());
        m_graph.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(3), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(5), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(6), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(4), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
        
        graph = m_graph;

		dists = DistArrayMatrix.getShortestPathDistances(graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , new DummyProgress(), 1.0);
		formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);

		cAlg = new ClosenessAlgorithm(graph, formula, new DummyProgress(), 1);
		oldAlgCloseness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
		
		newAlg = new GroupClosenessAlgorithmMSBFS(graph, formula);
		newAlgCloseness = newAlg.getGroupCloseness(group);
		System.out.println(oldAlgCloseness+" , "+newAlgCloseness);
		assertEquals(formatter.format(oldAlgCloseness),formatter.format(newAlgCloseness));
		
		// group of vertices from two components
		
		group = new Object[] {Index.valueOf(0),Index.valueOf(2)};
		
		oldAlgCloseness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
		newAlgCloseness = newAlg.getGroupCloseness(group);
		
		System.out.println(oldAlgCloseness+" , "+newAlgCloseness);
		assertEquals(formatter.format(oldAlgCloseness),formatter.format(newAlgCloseness));
	}
	
	public void testRandomCorrectness() {
		
		GraphInterface<Index,BasicVertexInfo> graph = randomGraph(100, 1000);
		Object[] group = randomGroup(100,10);
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , new DummyProgress(), 1.0);
		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(graph, formula, new DummyProgress(), 1);
		
		long oldTime = System.currentTimeMillis();
		double oldAlgCloseness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
		oldTime = System.currentTimeMillis() - oldTime;
		
		GroupClosenessAlgorithmMSBFS newAlg = new GroupClosenessAlgorithmMSBFS(graph, formula);
		
		long newTime = System.currentTimeMillis();
		double newAlgCloseness = newAlg.getGroupCloseness(group);
		newTime = System.currentTimeMillis() - newTime;
		System.out.println("random graph:");
		System.out.println(oldAlgCloseness+" , "+newAlgCloseness);
		System.out.println("time:");
		System.out.println((oldTime/1000.0) + " , " + (newTime/1000.0));
		assertEquals(formatter.format(oldAlgCloseness),formatter.format(newAlgCloseness));
	}
	
	private GraphInterface<Index,BasicVertexInfo> randomGraph(int nodes, int edges) {
		GraphInterface<Index,BasicVertexInfo> graph = new GraphAsHashMap<Index,BasicVertexInfo>();
		
		for (int i=0;i<nodes;i++) {
			graph.addVertex(Index.valueOf(i));
		}
		
		while (edges > 0) {
			Random r = new Random();
			Index v = Index.valueOf(r.nextInt(nodes));
			Index u = Index.valueOf(r.nextInt(nodes));
			if (graph.isEdge(v,u))
				continue;
			graph.addEdge(v, u);
			edges--;
		}
		return graph;
	}
	
	private Object[] randomGroup (int nodeCount, int groupSize){
		FastSet<Object> group = new FastSet<Object>();
		while (groupSize > 0) {
			Random r = new Random();
			Index v = Index.valueOf(r.nextInt(nodeCount));
			if (group.contains(v))
				continue;
			group.add(v);
			groupSize--;
		}
		return group.toArray();
	}
	
}

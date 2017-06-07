package tests.closeness;

import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.closeness.searchAlgorithms.AbsGreedyClosenessNG;
import algorithms.centralityAlgorithms.closeness.searchAlgorithms.AbsGreedyEdgeClosenessNG;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;
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


public class ClosenessSearchTest extends TestCase {
	private GraphInterface<Index,BasicVertexInfo> m_graph;
	private int[] m_vertices;
	private AbstractSimpleEdge<Index,BasicVertexInfo>[] m_edges;
	private FastList<Index> m_candidates_vertices;
	private FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> m_candidates_edges;

	@Override
	public void setUp() {
		m_graph = new GraphAsHashMap<Index,BasicVertexInfo>();
		m_vertices = new int[0];
		m_edges = new AbstractSimpleEdge[0];
		m_candidates_vertices = new FastList<Index>();
		m_candidates_edges = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
	}

	public void testNoVerticesNoLinksContributionVertices() throws Exception {
		Index[] result = null;
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyClosenessNG.findVertices(Algorithm.Contribution, m_candidates_vertices, m_vertices, m_edges, cAlg,
													Bound.GroupSize, 5, new DummyProgress(), 1);
		assertEquals(result.length, m_candidates_vertices.size()); // m_candidates
																	// is an
																	// empty
																	// list
	}

	public void testVerticesNoLinksContributionVertices() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0), new VertexInfo());
		m_graph.addVertex(Index.valueOf(1), new VertexInfo());
		m_candidates_vertices.add(Index.valueOf(0));
		m_candidates_vertices.add(Index.valueOf(1));
		m_vertices = new int[1];
		m_vertices[0] = 0;
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		
		result = AbsGreedyClosenessNG.findVertices(Algorithm.Contribution,
				m_candidates_vertices, m_vertices, m_edges, cAlg,
				Bound.GroupSize, 2, new DummyProgress(), 1);
		compare(result, m_candidates_vertices);
	}

	public void testNoVerticesLinksContributionVertices() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0), new VertexInfo());
		m_graph.addVertex(Index.valueOf(1), new VertexInfo());
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		m_candidates_edges.add(m_graph.getEdge(Index.valueOf(0),
				Index.valueOf(1)));

		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyClosenessNG.findVertices(Algorithm.Contribution,
				m_candidates_vertices, m_vertices, m_edges, cAlg,
				Bound.GroupSize, 5, new DummyProgress(), 1);
		compare(result, m_candidates_vertices); // m_candidates is an empty list
	}

	public void testVerticesLinksContributionVertices() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0), new VertexInfo());
		m_graph.addVertex(Index.valueOf(1), new VertexInfo());
		m_candidates_vertices.add(Index.valueOf(0));
		m_candidates_vertices.add(Index.valueOf(1));
		// m_vertices.add(Index.valueOf(0));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		m_candidates_edges.add(m_graph.getEdge(Index.valueOf(0),
				Index.valueOf(1)));
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		
		result = AbsGreedyClosenessNG.findVertices(Algorithm.Contribution,
				m_candidates_vertices, m_vertices, m_edges, cAlg,
				Bound.GroupSize, 5, new DummyProgress(), 1);
		compare(result, m_candidates_vertices); // m_candidates is an empty list
	}

	public void testNoVerticesNoLinksTopKVertices() throws Exception {
		Object[] result = null;
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyClosenessNG.findVertices(Algorithm.TopK,
				m_candidates_vertices, m_vertices, m_edges, cAlg,
				Bound.GroupSize, 5, new DummyProgress(), 1);
		compare(result, m_candidates_vertices); // m_candidates is an empty list
	}

	public void testVerticesNoLinksTopKVertices() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0));
		m_graph.addVertex(Index.valueOf(1));
		m_candidates_vertices.add(Index.valueOf(0));
		m_candidates_vertices.add(Index.valueOf(1));
		m_vertices = new int[1];
		m_vertices[0] = 0;
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyClosenessNG.findVertices(Algorithm.TopK,
				m_candidates_vertices, m_vertices, m_edges, cAlg,
				Bound.GroupSize, 5, new DummyProgress(), 1);
		for (Object x: result)
			System.out.println(x);
		compare(result, m_candidates_vertices); // m_candidates is an empty list
	}

	public void testNoVerticesLinksTopKVertices() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0), new VertexInfo());
		m_graph.addVertex(Index.valueOf(1), new VertexInfo());
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		m_candidates_edges.add(m_graph.getEdge(Index.valueOf(0),
				Index.valueOf(1)));
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyClosenessNG.findVertices(Algorithm.TopK,
				m_candidates_vertices, m_vertices, m_edges, cAlg,
				Bound.GroupSize, 5, new DummyProgress(), 1);
		compare(result, m_candidates_vertices); // m_candidates is an empty list
	}

	public void testVerticesLinksTopKVertices() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0), new VertexInfo());
		m_graph.addVertex(Index.valueOf(1), new VertexInfo());
		m_candidates_vertices.add(Index.valueOf(0));
		m_candidates_vertices.add(Index.valueOf(1));
		m_vertices = new int[1];
		m_vertices[0] = 0;
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		m_candidates_edges.add(m_graph.getEdge(Index.valueOf(0),
				Index.valueOf(1)));
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		
		result = AbsGreedyClosenessNG.findVertices(Algorithm.TopK,
				m_candidates_vertices, m_vertices, m_edges, cAlg,
				Bound.GroupSize, 5, new DummyProgress(), 1);
		compare(result, m_candidates_vertices); // m_candidates is an empty list
	}

	public void testNoVerticesNoLinksContributionLinks() throws Exception {
		Object[] result = null;
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyEdgeClosenessNG.findEdges(Algorithm.Contribution,
				m_candidates_edges, m_vertices, m_edges, cAlg, Bound.GroupSize,
				5, new DummyProgress(), 1);
		compare(result, m_candidates_edges); // m_candidates is an empty list
	}

	public void testVerticesNoLinksContributionLinks() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0));
		m_graph.addVertex(Index.valueOf(1));
		m_candidates_vertices.add(Index.valueOf(0));
		m_candidates_vertices.add(Index.valueOf(1));
		m_vertices = new int[1];
		m_vertices[0] = 0;
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyEdgeClosenessNG.findEdges(Algorithm.Contribution,
				m_candidates_edges, m_vertices, m_edges, cAlg, Bound.GroupSize,
				5, new DummyProgress(), 1);
		compare(result, m_candidates_edges); // m_candidates is an empty list
	}

	public void testNoVerticesLinksContributionLinks() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0), new VertexInfo());
		m_graph.addVertex(Index.valueOf(1), new VertexInfo());
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		m_candidates_edges.add(m_graph.getEdge(Index.valueOf(0),
				Index.valueOf(1)));
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyEdgeClosenessNG.findEdges(Algorithm.Contribution,
				m_candidates_edges, m_vertices, m_edges, cAlg, Bound.GroupSize,
				5, new DummyProgress(), 1);
		compare(result, m_candidates_edges); // m_candidates is an empty list

	}

	public void testVerticesLinksContributionLinks() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0), new VertexInfo());
		m_graph.addVertex(Index.valueOf(1), new VertexInfo());
		m_candidates_vertices.add(Index.valueOf(0));
		m_candidates_vertices.add(Index.valueOf(1));
		m_vertices = new int[1];
		m_vertices[0] = 0;
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		m_candidates_edges.add(m_graph.getEdge(Index.valueOf(0),
				Index.valueOf(1)));
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyEdgeClosenessNG.findEdges(Algorithm.Contribution,
				m_candidates_edges, m_vertices, m_edges, cAlg, Bound.GroupSize,
				5, new DummyProgress(), 1);
		compare(result, m_candidates_edges); // m_candidates is an empty list
	}

	public void testNoVerticesNoLinksTopKLinks() throws Exception {
		Object[] result = null;
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyEdgeClosenessNG.findEdges(Algorithm.TopK,
				m_candidates_edges, m_vertices, m_edges, cAlg, Bound.GroupSize,
				5, new DummyProgress(), 1);
		compare(result, m_candidates_edges); // m_candidates is an empty list
	}

	public void testVerticesNoLinksTopKLinks() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0));
		m_graph.addVertex(Index.valueOf(1));
		m_candidates_vertices.add(Index.valueOf(0));
		m_candidates_vertices.add(Index.valueOf(1));
		m_vertices = new int[1];
		m_vertices[0] = 0;
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyEdgeClosenessNG.findEdges(Algorithm.TopK,
				m_candidates_edges, m_vertices, m_edges, cAlg, Bound.GroupSize,
				5, new DummyProgress(), 1);
		compare(result, m_candidates_edges); // m_candidates is an empty list
	}

	public void testNoVerticesLinksTopKLinks() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0), new VertexInfo());
		m_graph.addVertex(Index.valueOf(1), new VertexInfo());
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		m_candidates_edges.add(m_graph.getEdge(Index.valueOf(0),
				Index.valueOf(1)));
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		result = AbsGreedyEdgeClosenessNG.findEdges(Algorithm.TopK,
				m_candidates_edges, m_vertices, m_edges, cAlg, Bound.GroupSize,
				5, new DummyProgress(), 1);
		compare(result, m_candidates_edges); // m_candidates is an empty list
	}

	public void testVerticesLinksTopKLinks() throws Exception {
		Object[] result = null;
		m_graph.addVertex(Index.valueOf(0), new VertexInfo());
		m_graph.addVertex(Index.valueOf(1), new VertexInfo());
		m_candidates_vertices.add(Index.valueOf(0));
		m_candidates_vertices.add(Index.valueOf(1));
		m_vertices = new int[1];
		m_vertices[0] = 0;
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		m_candidates_edges.add(m_graph.getEdge(Index.valueOf(0),
				Index.valueOf(1)));
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		
		result = AbsGreedyEdgeClosenessNG.findEdges(Algorithm.TopK,
				m_candidates_edges, m_vertices, m_edges, cAlg, Bound.GroupSize,
				5, new DummyProgress(), 1);
		compare(result, m_candidates_edges); // m_candidates is an empty list
	}

	private void compare(Object[] result, FastList<?> list) {
		assertTrue(list.size() == result.length);
		for (int i = 0; i < result.length; i++) {
			assertTrue(list.contains(result[i]));
		}		
	}	
}
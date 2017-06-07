package tests.closeness;

import common.Pair;

import server.common.DummyProgress;
import topology.BasicVertexInfo;

import topology.GraphInterface;
import javolution.util.Index;

import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

import junit.framework.TestCase;

public class FootPrintTest extends TestCase {

	protected final double PERCISION = 0.001;
	protected final int GRAPH_SIZE = 20;

	protected IClosenessAlgorithm createAlg(AbsTrafficMatrix leg, AbsTrafficMatrix mal, GraphInterface<Index, BasicVertexInfo> graph) {
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , new DummyProgress(), 1.0);
		
		Pair<AbsTrafficMatrix, AbsTrafficMatrix> tmsPair = new Pair<AbsTrafficMatrix, AbsTrafficMatrix>(leg, mal);
		
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_FOOTPRINT, dists, tmsPair);
		
		return new ClosenessAlgorithm(graph, formula, new DummyProgress(), 1);
	}

	public void testGLineSymmetry() {
		LineGraph g_line = new LineGraph(GRAPH_SIZE);
		AbsTrafficMatrix leg = new DenseTrafficMatrix(
				g_line.getNumberOfVertices());
		leg.setAllWeights(1);
		AbsTrafficMatrix mal = new DenseTrafficMatrix(
				g_line.getNumberOfVertices());
		mal.setAllWeights(1);
		IClosenessAlgorithm alg = createAlg(leg, mal, g_line);

		for (int i = 0; i < g_line.getNumberOfVertices(); i++)
			assertTrue(Math.abs(alg.getCloseness(i)
					- alg.getCloseness(g_line.getNumberOfVertices() - 1 - i)) < PERCISION);
	}

	public void testGStar() {

		for (int l = 1; l < 5; l++) {
			int k = 10;
			// int l = 1; // current formula only works for l=1
			double error = 0.00001;
			StarGraph g_star = new StarGraph(k, l);
			AbsTrafficMatrix leg = new DenseTrafficMatrix(
					g_star.getNumberOfVertices());
			leg.setAllWeights(1);
			AbsTrafficMatrix mal = new DenseTrafficMatrix(
					g_star.getNumberOfVertices());
			mal.setAllWeights(1);
			IClosenessAlgorithm alg = createAlg(leg, mal, g_star);

			double expected = starCenterFP(k, l);
			double actual = alg.getCloseness(0);
			System.out.println(expected + "," + actual);
			assertTrue(Math.abs(expected - actual) < error);
		}

	}

	protected double starCenterFP(int k, int l) {
		double expected = 0;
		
		for (int i = 1; i <= l; i++) {
			// v=t (k vertices)
			expected += k * ((1+1)*i+0);
			// v=s (k vertices)
			expected += k * (0+1*i);
			// v<>s & v<>t (k^2 pairs of vertices placed at the end of the ray)
			expected += (k*k) * ((1+1)*i+1*i);
			// v<>s & v<>t (k^2 pairs of vertices placed at the different distances from the center)
			for (int j = 1; j < i; j++){
				expected += (k*k) * ((1+1)*i + 1*j);
				expected += (k*k) * ((1+1)*j + 1*i);
			}
		}
		return 1.0/(double)expected;
	}
}
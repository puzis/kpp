package tests.closeness;

import java.util.Arrays;

import java.util.Random;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DummyProgress;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import topology.AbstractSimpleEdge;
import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.closeness.searchAlgorithms.AbsGreedyClosenessNG;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

public class ClosenessImportanceVectorTest extends AbstractClosenessTest {

	public void testLineGraph() {
		_nodeCount = 20;
		_graph = new LineGraph(_nodeCount);

		double[] importanceVec = new double[_nodeCount];
		Arrays.fill(importanceVec, 0);
		// importanceVec[0] = 1;
		importanceVec[_nodeCount - 1] = 1;
		// importanceVec[(_nodeCount-1)/2] = 1;
		double[][] dists = DistArrayMatrix.getShortestPathDistances(_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, _graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_IV, dists, importanceVec);
		IClosenessAlgorithm alg = new ClosenessAlgorithm(_graph, formula, new DummyProgress(), 1);
		// System.out.println(alg.getGroupCloseness(group));
		FastList<Index> candidates = new FastList<Index>();
		for (int i = 0; i < _nodeCount; i++) {
			candidates.add(Index.valueOf(i));
			if (i > 0)
				assert (alg.getCloseness(i - 1) < alg.getCloseness(i));
			//System.out.println(alg.getCloseness(i));
		}
	}
	
	public void testStarGraph()
	{
		int l = 15;
		int k = 10;
		_nodeCount = l*k+1;
		_graph = new StarGraph(k,l);
		double error = 0.000001;
		double[] importanceVec = new double[_nodeCount];
		Arrays.fill(importanceVec, 0);
		int[] outer = ((StarGraph)_graph).getOuterVertices();
		for (int i = 0; i < outer.length; i++) {			
			importanceVec[outer[i]] = 1;
		}
		double[][] dists = DistArrayMatrix.getShortestPathDistances(_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, _graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_IV, dists, importanceVec);
		IClosenessAlgorithm alg = new ClosenessAlgorithm(_graph, formula, new DummyProgress(), 1);
		
		for (int i = 0; i < _nodeCount; i++) {
			int level = (k-1+i)/k;
			//System.out.println(i+","+level);
			double expected = 1/(double) ((l-level) + (k-1)*(l+level));
			double actual = alg.getCloseness(i);
			//System.out.println(expected+","+actual);
			assertTrue(Math.abs(expected - actual) < error);
			//if (i > 0)
				//assert (alg.getCloseness(i - 1) < alg.getCloseness(i));
			
		}
	}

	public void testGreedySearch() {
		int repeats = 100;
		_nodeCount = 10;
		_graph = new LineGraph(_nodeCount);
		double[] importanceVec = new double[_nodeCount];
		Arrays.fill(importanceVec, 0);
		Random r = new Random();
		int x = r.nextInt(_nodeCount);
		for (int j = 0; j < repeats; j++) {
			
			importanceVec[x] = 0;
			x = r.nextInt(_nodeCount);
			importanceVec[x] = 1;
			
			double[][] dists = DistArrayMatrix.getShortestPathDistances(_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, _graph) , new DummyProgress(), 1.0);
	    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_IV, dists, importanceVec);
	    	
			IClosenessAlgorithm alg = new ClosenessAlgorithm(_graph, formula, new DummyProgress(), 1);
			
			FastList<Index> candidates = new FastList<Index>();
			for (int i = 0; i < _nodeCount; i++) {
				candidates.add(Index.valueOf(i));
				//System.out.println(alg.getCloseness(i));
			}
			int k = 1;
			Index[] result = findVertices(alg, candidates, k);
			assert result[0].intValue() == x;
		}

	}

	private Index[] findVertices(IClosenessAlgorithm msbfsAlg,
			FastList<Index> candidates, int k) {
		Index[] result = null;
		try {
			result = AbsGreedyClosenessNG.findVertices(Algorithm.Contribution,
					candidates, new int[0], new AbstractSimpleEdge[0],
					msbfsAlg, Bound.GroupSize, k, new DummyProgress(), 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}

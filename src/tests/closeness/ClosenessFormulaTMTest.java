package tests.closeness;

import javolution.util.Index;

import server.common.DummyProgress;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.OptimizedGreedyGroupCloseness;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

public class ClosenessFormulaTMTest extends AbstractClosenessTest {
	
	public void testClosenessNormal() throws Exception
	{
		
		double[][] weights = new double[this._nodeCount][this._nodeCount];
		for (int i = 0; i < this._nodeCount; i++) {
			for (int j = 0; j < this._nodeCount; j++) {
				weights[i][j] = 1;
			}
		}
		weights[4][7] = 0.01;
		weights[2][7] = 0.5;
		AbsTrafficMatrix tm = new DenseTrafficMatrix(weights);
		
		double[][] dists = DistArrayMatrix.getShortestPathDistances(this._graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, this._graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_WEIGHTED, dists, tm);
		
		IClosenessAlgorithm alg = new OptimizedGreedyGroupCloseness(this._graph, formula, new DummyProgress(), 1);
		
		Object[] group = new Object[2];
		group[0] = Index.valueOf(2);
		group[1] = Index.valueOf(4);
		
		double closeness = alg.getGroupCloseness(group);
		double expected = 1+1/2.0+1+1/3.0+1/4.0+1/3.0*0.5;
		
		System.out.println(closeness + " =? " +expected);
		assertEquals(_formatter.format(expected), _formatter.format(closeness));
		
	}
}
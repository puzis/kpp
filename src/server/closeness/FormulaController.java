package server.closeness;

import common.Pair;

import javolution.util.Index;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;
import server.common.DataBase;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.GraphInterface;

public class FormulaController {

	public static final String ALIAS = "Formula";
	

	public int createStandard(int netID){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
							ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
							new DummyProgress(), 1.0);
		
		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_STANDARD, dists, null);
		int formulaID = DataBase.putFormula(formula);
		return formulaID;
	}
		
	public int createExponential(int netID, Object immunity){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
							ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
							new DummyProgress(), 1.0);
		
		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_EXPONENTIAL, dists, immunity);
		int formulaID = DataBase.putFormula(formula);
		return formulaID;
	}
	
	public int createReciprocal(int netID){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
							ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
							new DummyProgress(), 1.0);
		
		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		int formulaID = DataBase.putFormula(formula);
		return formulaID;
	}
	
	public int createTM(int netID, int tmID){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
							ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
							new DummyProgress(), 1.0);
		
		AbsTrafficMatrix tm = DataBase.getTrafficMatrix(tmID);
		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_WEIGHTED, dists, tm);
		int formulaID = DataBase.putFormula(formula);
		return formulaID;
	}
	
	public int createIV(int netID, Object[] iv){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
							ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
							new DummyProgress(), 1.0);
		
		double[] ivArray = new double[iv.length];
		for (int i=0; i<iv.length; i++){
			ivArray[i] = (Double)iv[i];
		}
		
		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_IV, dists, ivArray);
		int formulaID = DataBase.putFormula(formula);
		return formulaID;
	}
	
	public int createFootprint(int netID, int tmLegID, int tmMalID, boolean allSources){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
							ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
							new DummyProgress(), 1.0);
		
		AbsTrafficMatrix tmLeg = DataBase.getTrafficMatrix(tmLegID);
		AbsTrafficMatrix tmMal = DataBase.getTrafficMatrix(tmMalID);
		Pair<AbsTrafficMatrix, AbsTrafficMatrix> tmPair = new Pair<AbsTrafficMatrix, AbsTrafficMatrix>(tmLeg, tmMal);
		
		Pair<Boolean, Pair<AbsTrafficMatrix, AbsTrafficMatrix>> param = 
				new Pair<Boolean, Pair<AbsTrafficMatrix, AbsTrafficMatrix>>(allSources, tmPair); 
		
		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_FOOTPRINT, dists, param);
		int formulaID = DataBase.putFormula(formula);
		return formulaID;
	}
	
	/** Releases the pointer to the formula in the DataBase.
     * @param formulaID - The index of the formula in the DataBase.
 	 * @return 0. */
    public int destroy(int formulaID){
    	DataBase.releaseFormula(formulaID);
    	return 0;
    }
}

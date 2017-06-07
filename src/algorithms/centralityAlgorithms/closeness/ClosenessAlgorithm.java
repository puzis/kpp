package algorithms.centralityAlgorithms.closeness;

import javolution.util.FastList;


import javolution.util.Index;
import server.common.DummyProgress;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.closeness.formula.ClosenessIVFrom;
import algorithms.centralityAlgorithms.closeness.formula.Footprint;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

public class ClosenessAlgorithm extends AbsClosenessAlgorithm implements IClosenessAlgorithm {

	public ClosenessAlgorithm(GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage) {
		
		super(graph);
		
		ShortestPathAlgorithmInterface shortestPathAlg = ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, this._graph);
		this._dists = DistArrayMatrix.getShortestPathDistances(graph, shortestPathAlg, progress, percentage);
		this._formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_STANDARD, this._dists, null);
		initSourcesAndTargets();
	}
	
	public ClosenessAlgorithm(GraphInterface<Index,BasicVertexInfo> graph, IClosenessFormula formula, AbstractExecution progress, double percentage) {
		
		super(graph);
		
		this._formula = formula;
		this._dists = this._formula.getDistances();
		initSourcesAndTargets();
	}
	
	private void initSourcesAndTargets(){
		this._targets = new FastList<Index>();
		this._sources = new FastList<Index>();
		
		if (this._formula instanceof Footprint){
			Footprint formula = (Footprint)this._formula;
			
			this._sources = formula.getSources();
			this._targets = formula.getTargets();
		}
		else if (this._formula instanceof ClosenessIVFrom){
			ClosenessIVFrom formula = (ClosenessIVFrom)this._formula;
			
			this._sources = formula.getSources();
			this._targets = formula.getTargets();
		}
		else{
			for (Index i: this._graph.getVertices())
				this._targets.add(i);
			this._sources.add(Index.valueOf(0));
		}
	}
	
	@Override
	public double getCloseness(Index v) {
		return this.getCloseness(v.intValue());
	}

	@Override
	public double getCloseness(AbstractSimpleEdge<Index, BasicVertexInfo> e) {
		return super.getCloseness(e);
	}

	@Override
	public double[] getCloseness() {
		double[] closeness = new double[this._graph.getNumberOfVertices()];
		for (int i = 0; i < closeness.length; i++) {
			closeness[i] = this.getCloseness(i);
		}
		return closeness;
	}

	@Override
	public IClosenessFormula getFormula() {
		return this._formula;
	}

	@Override
	public double getGroupCloseness(Object[] group) {
		return this.calculateMixedGroupCloseness(group, new DummyProgress(), 1.0);
	}

	@Override
	public double[][] getDistanceMatrix() {
		return this._dists;
	}
}
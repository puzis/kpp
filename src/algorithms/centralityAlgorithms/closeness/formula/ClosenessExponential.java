package algorithms.centralityAlgorithms.closeness.formula;

import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public class ClosenessExponential implements IClosenessFormula {
	
	private double[][] _dists;
	
	protected double _immunityP;
	
	public ClosenessExponential(double[][] dists, double immunityProbability) {
		this._dists = dists;
		this._immunityP = immunityProbability;
	}
	
	public ClosenessExponential(double immunityProbability) {
		this._immunityP = immunityProbability;
	}
	
	private double compute (int src, int dst) {
		double dist = this._dists[src][dst];
		return Math.pow(1-this._immunityP, dist);
	}

	@Override
	public double compute(int s, int t, double dist) {
		return Math.pow(1-this._immunityP, dist);
	}

	@Override
	public double compute(int s, int v, int t) {
		return this.compute(v, t);
	}

	@Override
	public double compute(Index s, Index v, Index t) {
		return compute(v.intValue(), t.intValue());
	}

	@Override
	public double compute(Index s, AbstractSimpleEdge<Index, BasicVertexInfo> e, Index t) {
		return -1;
	}

	@Override
	public double getBest(double c0, double c1) {
		return Math.max(c0, c1);
	}

	@Override
	public double getWorstCloseness() {
		return 0;
	}

	@Override
	public double normalize(double c) {
		return c;
	}

	@Override
	public double[][] getDistances() {
		return _dists;
	}

}

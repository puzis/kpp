package algorithms.centralityAlgorithms.closeness.formula;

import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

public class ClosenessTM implements IClosenessFormula {
	
	private AbsTrafficMatrix _tm;
	private double[][] _dists;
	
	public ClosenessTM(AbsTrafficMatrix param, double[][] dists)
	{
		this._dists = dists;
		this._tm = param;
	}
	
	private double compute (int src, int dst) {
		double dist = this._dists[src][dst];
		return dist*this._tm.getWeight(src, dst);
	}

	@Override
	public double compute(int s, int t, double dist) {
		return dist*this._tm.getWeight(s, t);
	}

	@Override
	public double compute(int s, int v, int t) {
		return this.compute(v, t);
	}

	@Override
	public double compute(Index s, Index v, Index t) {
		return this.compute(v.intValue(), t.intValue());
	}

	@Override
	public double compute(Index s, AbstractSimpleEdge<Index, BasicVertexInfo> e, Index t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getBest(double c0, double c1) {
		return Math.min(c0, c1);
	}

	@Override
	public double getWorstCloseness() {
		return Double.MAX_VALUE;
	}

	@Override
	public double normalize(double c) {
		return c==0? 1 : 1/c;
	}
	
	@Override
	public double[][] getDistances() {
		return _dists;
	}

}

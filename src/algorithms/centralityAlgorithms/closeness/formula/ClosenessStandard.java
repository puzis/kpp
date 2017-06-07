package algorithms.centralityAlgorithms.closeness.formula;

import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import javolution.util.Index;

public class ClosenessStandard implements IClosenessFormula {

	private double[][] _dists;

	public ClosenessStandard(double[][] distanceMatrix)
	{
		this._dists = distanceMatrix;
	}
	
	@Override
	public double compute(int s, int v, int t) {
		return this.compute(v,t);
	}

	@Override
	public double compute(Index s, Index v, Index t) {
		return this.compute(v.intValue(), t.intValue());
	}
	
	private double compute(int s, int t)
	{
		return this._dists[s][t];
	}

	@Override
	public double compute(int s, int t, double dist) {
		return dist;
	}

	public double compute(Index s, AbstractSimpleEdge<Index, BasicVertexInfo> e, Index t) {
		int v0 = e.getV0().intValue();
		int v1 = e.getV1().intValue();
		double c0 = this.compute(v0, t.intValue());
		double c1 = this.compute(v1, t.intValue());
		return this.getBest(c0,c1) == c0 ? c1 : c0;
	}

	@Override
	public double getBest(double c0, double c1) {
		return Math.min(c0, c1);
	}

	@Override
	public double getWorstCloseness() {
		return this._dists.length+1;
	}

	@Override
	public double normalize(double c) {
		if (c==0)
			return 1.0;
		return 1.0/c;
	}
	
	@Override
	public double[][] getDistances() {
		return _dists;
	}
}

package algorithms.centralityAlgorithms.closeness.formula;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public class ClosenessIVFrom implements IClosenessFormula {
	
	private double[] _iv;
	private double[][] _dists;
	protected FastList<Index> _malSources;
	protected FastList<Index> _targets;
	
	public ClosenessIVFrom(double[][] dists, double[] iv)
	{
		this._dists = dists;
		this._iv = iv;
		
		_malSources = new FastList<Index>();
		_targets = new FastList<Index>();
		
		for (int i=0; i<this._iv.length; i++){
			if (this._iv[i]>0)
				this._malSources.add(Index.valueOf(i));
		}
		this._targets.add(Index.valueOf(0));
	}
	
	public FastList<Index> getSources(){
		return this._malSources;
	}
	
	public FastList<Index> getTargets(){
		return this._targets;
	}
	
	public double compute (int src, int dst) {
		double dist = this._dists[src][dst];
		return dist*this._iv[src];
	}


	@Override
	public double compute(int s, int t, double dist) {
		return dist*this._iv[s];
	}
	
	@Override
	public double compute(int s, int v, int t) {
		return this.compute(s, v);
	}

	@Override
	public double compute(Index s, Index v, Index t) {
		return this.compute(s.intValue(), v.intValue());
	}

	@Override
	public double compute(Index s, AbstractSimpleEdge<Index, BasicVertexInfo> e, Index t) {
		int v0 = e.getV0().intValue();
		int v1 = e.getV1().intValue();
		double c0 = this.compute(s.intValue(), v0);
		double c1 = this.compute(s.intValue(), v1);
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
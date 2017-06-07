package algorithms.centralityAlgorithms.closeness.formula;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

public class Footprint implements IClosenessFormula{

	protected double[][] _dists;
	protected AbsTrafficMatrix _leg;
	protected AbsTrafficMatrix _mal;
	
	protected FastList<Index> _malSources;
	protected FastList<Index> _targets;
	
	public Footprint(double[][] dists, AbsTrafficMatrix leg, AbsTrafficMatrix mal, boolean allPairs) {
		this._dists = dists;
		this._mal = mal;
		this._leg = leg;
		
		_malSources = new FastList<Index>();
		_targets = new FastList<Index>();
		if (allPairs){
			for (int i=0; i<leg.getDimensions(); i++)
			{
				if (!this._malSources.contains(Index.valueOf(i)))
					this._malSources.add(Index.valueOf(i));
				if (!this._targets.contains(Index.valueOf(i)))
					this._targets.add(Index.valueOf(i));
			}
		}
		else{
			
			for (int i=0; i<dists.length; i++){
				for (int j=0; j<dists.length; j++){
					if (mal.getWeight(i, j)>0.0){
						// Add i to sources
						if (!this._malSources.contains(Index.valueOf(i)))
							this._malSources.add(Index.valueOf(i));
						// Add j to targets
						if (!this._targets.contains(Index.valueOf(j)))
							this._targets.add(Index.valueOf(j));
					}
				}
			}
		}
	}
	
	public FastList<Index> getSources(){
		return this._malSources;
	}
	
	public FastList<Index> getTargets(){
		return this._targets;
	}
	
	public boolean isTarget(int i){
		if (this._targets.contains(Integer.valueOf(i)))
			return true;
		return false;
	}
	
	public boolean isSource(int i){
		if (this._malSources.contains(Integer.valueOf(i)))
			return true;
		return false;
	}
	
	@Override
	public double compute(int s, int v, int t) {
		double dsv = this._dists[s][v];
		double dvt = this._dists[v][t];
		
		return dsv*(this._leg.getWeight(s, t) + this._mal.getWeight(s, t))
			 + dvt*(this._leg.getWeight(s, t));
	}
	
	@Override
	public double compute(Index s, AbstractSimpleEdge<Index, BasicVertexInfo> e, Index t) {
		int u = e.getV0().intValue();
		int v = e.getV1().intValue();
		double du = this._dists[s.intValue()][u];
		double dv = this._dists[s.intValue()][v];
		
		// d = the longer distance
		double d = Math.max(du,dv);
		
		return (d*(this._mal.getWeight(s.intValue(), t.intValue()) + this._leg.getWeight(s.intValue(), t.intValue()))
					+ Math.min(du, dv)*(this._leg.getWeight(s.intValue(), t.intValue())));
	}
	
	@Override
	public double getWorstCloseness() {
		return Double.MAX_VALUE;
	}

	@Override
	public double normalize(double c) {
		return c==0? getWorstCloseness() : 1/c;
//		return c==0? 1 : 1/c;
	}

	@Override
	public double compute(Index s, Index v, Index t) {
		return this.compute(s.intValue(), v.intValue(), t.intValue());
	}

	@Override
	public double getBest(double c0, double c1) {
		return Math.min(c0, c1);
	}

	@Override
	public double compute(int s, int t, double dist) {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public double[][] getDistances() {
		return _dists;
	}
}
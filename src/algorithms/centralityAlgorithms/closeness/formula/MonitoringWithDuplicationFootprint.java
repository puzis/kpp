package algorithms.centralityAlgorithms.closeness.formula;


import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

public class MonitoringWithDuplicationFootprint extends Footprint{

	public MonitoringWithDuplicationFootprint(double[][] dists, AbsTrafficMatrix leg, AbsTrafficMatrix mal, boolean allPairs) {
		super(dists, leg, mal, allPairs);
	}
	
	@Override
	public double compute(int s, int v, int t) {
		
		int dup = findDuplicatingVertex(s, v, t);
		
		double dsv = this._dists[s][v];
		double dvdup = this._dists[v][dup];
		
		double footprint = dsv*(this._leg.getWeight(s, t) + this._mal.getWeight(s, t))
					   + dvdup*(this._leg.getWeight(s, t) + this._mal.getWeight(s, t));	
		
		return footprint;
	}
	
	@Override
	public double compute(Index sIdx, AbstractSimpleEdge<Index, BasicVertexInfo> e, Index tIdx) {
		int s = sIdx.intValue(); int t = tIdx.intValue();
		
		int u = e.getV0().intValue();
		int v = e.getV1().intValue();
		double du = this._dists[s][u];
		double dv = this._dists[s][v];

		double dse = -1;
		int k = -1;
		if (du > dv){
			dse = du;
			k = u;
		}
		else{
			dse = dv;
			k = v;
		}
		int dup = findDuplicatingVertex(s, k, t);
		double dedup = this._dists[k][dup];
		
		double footprint = dse * (this._mal.getWeight(s, t) + this._leg.getWeight(s, t))
					   + dedup * (this._mal.getWeight(s, t) + this._leg.getWeight(s, t));
		
		return footprint;
	}
	
	private int findDuplicatingVertex(int s, int v, int t){
		return -1;
	}
}
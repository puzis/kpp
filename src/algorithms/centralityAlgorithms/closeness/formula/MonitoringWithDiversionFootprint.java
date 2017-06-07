package algorithms.centralityAlgorithms.closeness.formula;


import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

public class MonitoringWithDiversionFootprint extends Footprint{

	public MonitoringWithDiversionFootprint(double[][] dists, AbsTrafficMatrix leg, AbsTrafficMatrix mal, boolean allPairs) {
		super(dists, leg, mal, allPairs);
	}
	
	@Override
	public double compute(int s, int v, int t) {
		double dsv = this._dists[s][v];
		double dvt = this._dists[v][t];

		if (this._dists[s][v]+this._dists[v][t] == this._dists[s][t]){
//			System.out.println("equals");
		}
		else{
//			System.out.println("not equals");
		}
		
		return dsv*(this._leg.getWeight(s, t) + this._mal.getWeight(s, t))
			 + dvt*(this._leg.getWeight(s, t) + this._mal.getWeight(s, t));
	}
	
	@Override
	public double compute(Index s, AbstractSimpleEdge<Index, BasicVertexInfo> e, Index t) {
		int u = e.getV0().intValue();
		int v = e.getV1().intValue();
		double du = this._dists[s.intValue()][u];
		double dv = this._dists[s.intValue()][v];
		
		// d = the longer distance
		double d = Math.max(du,dv);
		
		return          (d*(this._mal.getWeight(s.intValue(), t.intValue()) + this._leg.getWeight(s.intValue(), t.intValue()))
		+ Math.min(du, dv)*(this._mal.getWeight(s.intValue(), t.intValue()) + this._leg.getWeight(s.intValue(), t.intValue())));
	}
}
package algorithms.centralityAlgorithms.closeness.sets;

import java.util.Arrays;

import javolution.util.Index;

import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;

public class DynamicClosenessSet extends BasicSet {
	
	protected IClosenessAlgorithm _alg;
	protected double[][] _cache;
	protected double _groupCloseness;
	
	public DynamicClosenessSet(IClosenessAlgorithm alg) {
		super();
		this._alg = alg;
		this._cache = new double[this._alg.getDistanceMatrix().length][this._alg.getDistanceMatrix().length];
		for (double[] cacheLine : this._cache){
			Arrays.fill(cacheLine, Double.NaN);
		}
		this._groupCloseness = 0.0;
	}
	
	@Override
	public void add(Index v) {
		if (!isMember(v)){
			m_vertices.add(v);
			_groupCloseness = this._alg.addToGroup(v, _groupCloseness, _cache);
		}
	}
	
	@Override
	public double getContribution(Index v) {
		double contr = this._alg.getContribution(v, _groupCloseness, _cache);
		return contr;
//		double current = this.getGroupCentrality();
//		this.add(v);
//		double updated = this.getGroupCentrality();
//		this.remove(v);
//		double oldStyleContr = updated - current;
//		return updated - current;
	}
	
	
	@Override
	public double getContribution(Object[] group) {
		double current = this.getGroupCentrality();
		
		for (int i = 0; i < group.length; i++) {
			this.add((Index)group[i]);
		}
		double updated = this.getGroupCentrality();
		
		for (int i = 0; i < group.length; i++) {
			this.remove((Index)group[i]);
		}
		
		return updated - current;
	}
	
	@Override
	public double getGroupCentrality() {
		return this._alg.getGroupCloseness(this.m_vertices.toArray());
	}
}

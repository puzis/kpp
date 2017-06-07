package algorithms.centralityAlgorithms.closeness.sets;

import javolution.util.Index;

import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;

public class StaticClosenessSet extends BasicSet{

	protected IClosenessAlgorithm _alg;
	
	public StaticClosenessSet(IClosenessAlgorithm alg) {
		super();
		this._alg = alg;
	}
	
	@Override
	public double getContribution(Index v) {
		return this._alg.getCloseness(v);
	}
	
	@Override
	public double getContribution(Object[] group) {
		double contribution = 0;
		for (int i = 0; i < group.length; i++) {
			contribution += this._alg.getCloseness((Index)group[i]);
		}
		return contribution;
	}
	
	@Override
	public double getGroupCentrality() {
		return this._alg.getGroupCloseness(this.m_vertices.toArray());
	}
	
}

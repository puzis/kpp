package algorithms.centralityAlgorithms.closeness.sets;

import javolution.util.Index;

import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.closeness.OptimizedGreedyGroupCloseness;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;

public class OptimizedDynamicClosenessSet extends BasicSet {
	
	protected OptimizedGreedyGroupCloseness m_algorithm = null;

	
	public OptimizedDynamicClosenessSet(GraphInterface<Index,BasicVertexInfo> graph, IClosenessFormula formula) {
		super();
		this.m_algorithm = new OptimizedGreedyGroupCloseness(graph, formula, new DummyProgress(), 1);
	}
	
	public OptimizedDynamicClosenessSet(OptimizedGreedyGroupCloseness alg) {
		super();
		this.m_algorithm = alg;
		for (Index v: alg.getMembers()) {
			super.add(v);
		}
	}
	
	/*
	public OptimizedDynamicClosenessSet(OptimizedDynamicClosenessSet other) {
		super(other);
		this.m_algorithm = new OptimizedGreedyGroupCloseness(other.m_algorithm);
	}
	*/
	
	@Override
	public void add(Index v) {
		super.add(v);
		this.m_algorithm.add(v);
	}
	
	@Override
	public void remove(Index v) {
		super.remove(v);
		this.m_algorithm.remove(v);
	}
	
	@Override
	public double getContribution(Index v) {
		return this.m_algorithm.getContribution(v);
	}
	
	@Override
	public double getContribution(Object[] group) {
		return this.m_algorithm.getContribution((Index[]) group);
	}
	
	@Override
	public double getGroupCentrality() {
		return this.m_algorithm.getGroupCloseness();
	}
}
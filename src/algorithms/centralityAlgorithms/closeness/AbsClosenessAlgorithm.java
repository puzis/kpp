package algorithms.centralityAlgorithms.closeness;


import java.util.Arrays;

import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;

public abstract class AbsClosenessAlgorithm {
	/**
	 * 
	 */
	protected double[][] _dists;
	protected IClosenessFormula _formula;
	protected double[] _closeness;
	protected GraphInterface<Index, BasicVertexInfo> _graph;
	protected FastList<Index> _sources;
	protected FastList<Index> _targets;
	
	public AbsClosenessAlgorithm(GraphInterface<Index,BasicVertexInfo> graph) {
		this._graph = graph;
		this._closeness = new double[this._graph.getNumberOfVertices()];
		Arrays.fill(this._closeness, Double.NaN);
	}

	public void setSources(FastList<Index> sources){
		_sources = sources;
	}
	
	public void setTargets(FastList<Index> targets){
		_targets = targets;
	}
	
	public double getCloseness(int v) {
		if (!Double.isNaN(this._closeness[v]))
			return this._closeness[v];
		
		double c = 0;
		for (Index s : this._sources)
			for (Index t : this._targets)
				c += this._formula.compute(s.intValue(), v, t.intValue());
		
		this._closeness[v] = this._formula.normalize(c); 
		return this._closeness[v];
	}
	
	public double getCloseness(AbstractSimpleEdge<Index, BasicVertexInfo> e) {
		Index v0 = e.getV0();
		Index v1 = e.getV1();
		double c1 = getCloseness(v0.intValue());
		double c2 = getCloseness(v1.intValue());
		double best = this._formula.getBest(c1, c2);
		if (c1 == best)
			return c2;
		else
			return c1;
	}
	
	public double getContribution (Index v, double groupCloseness, double[][] cache){

		if (groupCloseness==0.0){
		
			for (Index s : this._sources)
			{
				for (Index t : this._targets)
				{
					double c = calculateFormula(s, t, v);
					groupCloseness += c;
				}
			}
			return this._formula.normalize(groupCloseness);
		}
		
		double newGC = this._formula.normalize(groupCloseness);
		
		for (Index s : this._sources)
		{
			for (Index t : this._targets)
			{
				double previousC = cache[s.intValue()][t.intValue()];
				if (Double.isNaN(previousC)){
					previousC = this._formula.getWorstCloseness();
				}
				double newC = calculateFormula(s, t, v);
				if (newC == this._formula.getBest(previousC, newC)){
					newGC = newGC - previousC + newC;
				}
			}
		}
		newGC = this._formula.normalize(newGC);
		return newGC - groupCloseness;
	}
	
	public double addToGroup (Index v, double groupCloseness, double[][] cache){
		
		if (groupCloseness==0.0){ // Happens once - with the first member of the group
			
			for (Index s : this._sources)
			{
				for (Index t : this._targets)
				{
					double c = calculateFormula(s, t, v);
					cache[s.intValue()][t.intValue()] = c;
					groupCloseness += c;
				}
			}
			return this._formula.normalize(groupCloseness);
		}
		
		double newGC = this._formula.normalize(groupCloseness);
		
		for (Index s : this._sources)
		{
			for (Index t : this._targets)
			{
				double previousC = cache[s.intValue()][t.intValue()];
				if (Double.isNaN(previousC)){
					previousC = this._formula.getWorstCloseness();
				}
				double newC = calculateFormula(s, t, v);
				if (newC == this._formula.getBest(previousC, newC)){
					cache[s.intValue()][t.intValue()] = newC;
					newGC = newGC - previousC + newC;
				}
			}
		}
		newGC = this._formula.normalize(newGC);
		return newGC;
	}
	
	public double calculateMixedGroupCloseness(Object[] group, AbstractExecution progress, double percentage) {
		double p = progress.getProgress();
		
		double closeness = 0;
		for (Index s : this._sources)
		{
			for (Index t : this._targets)
			{
				double bestC= this._formula.getWorstCloseness();
				
				for (Object wNode : group)
				{
					double c = calculateFormula(s, t, wNode);
					bestC = this._formula.getBest(bestC,c); 
				}
				
				closeness += bestC;
				
				double step = (double)this._sources.size()*this._targets.size();
				p += (1 / step) * percentage;	
				progress.setProgress(p);
			}
		}
		return this._formula.normalize(closeness);
	}

	private double calculateFormula(Index s, Index t, Object wNode) {
		double c = 0;
		if (wNode instanceof Index) {

			Index u = (Index) wNode;
			c = this._formula.compute(s, u, t);

		} else if (wNode instanceof AbstractSimpleEdge) {
			AbstractSimpleEdge<Index, BasicVertexInfo> e = (AbstractSimpleEdge) wNode;
			c = this._formula.compute(s, e, t);
		}
		return c;
	}
}
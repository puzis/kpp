package algorithms.centralityAlgorithms.closeness.searchAlgorithms;

import java.io.Serializable;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.OptimizedGreedyGroupCloseness;
import algorithms.centralityAlgorithms.closeness.sets.DynamicClosenessSet;
import algorithms.centralityAlgorithms.closeness.sets.OptimizedDynamicClosenessSet;

public abstract class AbsGreedyClosenessNG implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected FastList<Index> m_candidates = null;
	protected IClosenessAlgorithm m_closenessAlgorithm = null;
	protected BasicSet _set;
	
	public AbsGreedyClosenessNG(IClosenessAlgorithm closenessAlgorithm, BasicSet set, FastList<Index> candidates, AbstractExecution progress, double percentage) throws Exception
	{
		this.m_candidates = candidates;
		this.m_closenessAlgorithm = closenessAlgorithm;
		this._set = set;
	}
	
	public static Index[] findVertices(Algorithm algType, FastList<Index> candidates, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, IClosenessAlgorithm closenessAlgorithm, Bound boundType, int k, AbstractExecution progress, double percentage) throws Exception
	{
		BasicSet set = null;
		if (closenessAlgorithm instanceof OptimizedGreedyGroupCloseness) {
			set = new OptimizedDynamicClosenessSet((OptimizedGreedyGroupCloseness)closenessAlgorithm);
		} else {
			set = new DynamicClosenessSet(closenessAlgorithm);
		}
		return findVertices(algType, set, candidates, givenVertices, givenEdges, closenessAlgorithm, boundType, k, progress, percentage);
	}
	
	public static Index[] findVertices(Algorithm algType, FastList<Index> candidates, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, OptimizedGreedyGroupCloseness closenessAlgorithm, Bound boundType, int k, AbstractExecution progress, double percentage) throws Exception
	{
		return findVertices(algType, new OptimizedDynamicClosenessSet(closenessAlgorithm), candidates, givenVertices, givenEdges, closenessAlgorithm, boundType, k, progress, percentage);
	}
	
	public static Index[] findVertices(Algorithm algType, BasicSet set, FastList<Index> candidates, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, IClosenessAlgorithm closenessAlgorithm, Bound boundType, int k, AbstractExecution progress, double percentage) throws Exception
	{
		AbsGreedyClosenessNG greedyAlg = null;
		Index[] result = null;
		switch(algType)
		{
			case Contribution:
			{
				greedyAlg = new GreedyClosenessContribution(closenessAlgorithm, set, candidates, progress, percentage);
				break;
			}
			case TopK:
			{
				greedyAlg = new GreedyClosenessTopK(closenessAlgorithm, set, candidates, progress, percentage);
				break;
			}
			default:
				throw new IllegalArgumentException("Invalid closeness algorithm type: " + algType);
		}
		
		switch(boundType)
		{
			case GroupSize:
				result = greedyAlg.getOptimalGroupSizeBounded(k, givenVertices, givenEdges, progress, percentage);
				break;
			case Centrality:
				throw new IllegalArgumentException("Invalid bound type for Greedy Closeness Algorithm: " + boundType);
			default:
			{
				LoggingManager.getInstance().writeSystem("Invalid bound type for Greedy Closeness Algorithm: " + boundType, "AbsGreedyCloseness", "findVertices", null);
				throw new IllegalArgumentException("Invalid bound type for Greedy Closeness Algorithm: " + boundType);
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Index[] getOptimalGroupSizeBounded(int groupSize, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		if (this._set.size() > groupSize) {
			return null;
		}
		Index[] result = new Index[Math.min(groupSize, m_candidates.size())];
		int resultSize = 0;
		
		FastList<Index> unusedCandidates = new FastList<Index>(this.m_candidates);
		for (int i : givenVertices)
		{
			Index idx = Index.valueOf(i);
			unusedCandidates.remove(idx);
			this._set.add(idx);
		}
		
		for ( AbstractSimpleEdge<Index,BasicVertexInfo> e: givenEdges)
		{
			unusedCandidates.remove(e.getV0());
			unusedCandidates.remove(e.getV1());
			
			this._set.add(e);
		}
		
		
		double p = progress.getProgress();
		while (unusedCandidates.size() > 0 && (resultSize < groupSize))
		{
			Index winner = getWinner(unusedCandidates);
			this._set.add(winner);
			unusedCandidates.remove(winner);
			result[resultSize++] = winner;
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public abstract Index getWinner(FastList<Index> unusedCandidates);
	
	public FastList<Index> getCandidates(){ return m_candidates; }
}
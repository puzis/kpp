package algorithms.centralityAlgorithms.closeness.searchAlgorithms;

import java.io.Serializable;

import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public abstract class AbsGreedyEdgeClosenessNG implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> m_candidates = null;
	protected IClosenessAlgorithm m_closenessAlgorithm = null;
	
	public AbsGreedyEdgeClosenessNG(IClosenessAlgorithm closenessAlgorithm, FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, AbstractExecution progress, double percentage) throws Exception
	{
		m_candidates = candidates;
		m_closenessAlgorithm = closenessAlgorithm;
	}
	
	public static AbstractSimpleEdge<Index,BasicVertexInfo>[] findEdges(Algorithm algType, FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, IClosenessAlgorithm closenessAlgorithm, Bound boundType, int k, AbstractExecution progress, double percentage) throws Exception
	{
		AbsGreedyEdgeClosenessNG greedyAlg = null;		
		
		AbstractSimpleEdge<Index,BasicVertexInfo>[] result = null;
		switch(algType)
		{
			case Contribution:
			{
				greedyAlg = new GreedyEdgeClosenessContribution(closenessAlgorithm, candidates, progress, percentage);
				break;
			}
			case TopK:
			{
				greedyAlg = new GreedyEdgeClosenessTopK(closenessAlgorithm, candidates, progress, percentage);
				break;
			}
			default:
				throw new Exception("Invalid closeness algorithm type: " + algType);
		}
		
		switch(boundType)
		{
			case GroupSize:
				result = greedyAlg.getOptimalEdgesSizeBounded(k, givenVertices, givenEdges, progress, percentage);
				break;
			case Centrality:
				throw new IllegalArgumentException("Invalid bound type for Greedy Closeness Algorithm: " + boundType);
			default:
			{
				LoggingManager.getInstance().writeSystem("Invalid bound type for Greedy Closeness Algorithm: " + boundType, "AbsGreedyCloseness", "findEdges", null);
				throw new IllegalArgumentException("Invalid bound type for Greedy Closeness Algorithm: " + boundType);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param graph
	 * @param groupSize
	 * @param givenVertices
	 * @param givenEdges
	 * @param progress
	 * @param percentage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public AbstractSimpleEdge<Index,BasicVertexInfo>[] getOptimalEdgesSizeBounded(int groupSize, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		AbstractSimpleEdge<Index,BasicVertexInfo>[] result = new AbstractSimpleEdge[Math.min(groupSize, m_candidates.size())];
		int resultSize = 0;
		FastList<Object> group = new FastList<Object>();
		
		for (AbstractSimpleEdge<Index,BasicVertexInfo> e : givenEdges)
		{
			m_candidates.remove(e);
			group.add(e);
		}
		for (int i : givenVertices)
		{
			group.add(Index.valueOf(i));
		}
		
		double p = progress.getProgress();
		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> unusedCandidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>(m_candidates);
		while (unusedCandidates.size() > 0 && resultSize < groupSize)
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> winner = getWinner(unusedCandidates, group);
			group.add(winner);
			result[resultSize++] = winner;
			unusedCandidates.remove(winner);
			
			/** Add opposite direction of the edge. */
			if (unusedCandidates.contains(winner.flip()) && resultSize < groupSize)
			{
				result[resultSize++] = winner.flip();
				unusedCandidates.remove(winner.flip());
			}
						
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public abstract AbstractSimpleEdge<Index,BasicVertexInfo> getWinner(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> unusedCandidates, FastList<Object> group);
	
	public FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> getCandidates(){ return m_candidates; }
}
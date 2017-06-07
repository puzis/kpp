package algorithms.centralityAlgorithms.betweenness.brandes;

import javolution.util.FastList;

import javolution.util.Index;
import server.common.LoggingManager;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.BasicSetInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.DynamicBetweennessSet;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.OptimizedDynamicBetweennessSet;

public abstract class AbsGreedyEdgeBetweeness 
{
	protected FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> m_candidates;
	protected DataWorkshop m_dataWorkshop;	
	
	public AbsGreedyEdgeBetweeness(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, DataWorkshop dataWorkshop)
	{
		m_dataWorkshop = dataWorkshop;
		m_candidates = candidates;
	}

	protected AbstractSimpleEdge<Index,BasicVertexInfo> getHighestContribution(BasicSetInterface curSet, FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> competitors)
	{
		FastList.Node<AbstractSimpleEdge<Index,BasicVertexInfo>> head = competitors.head();
		AbstractSimpleEdge<Index,BasicVertexInfo> winner = head.getNext().getValue();
		head = head.getNext();
				
		double winnerContribution = getUtility(winner, curSet);
		
		for (FastList.Node<AbstractSimpleEdge<Index,BasicVertexInfo>> candIndex = competitors.head().getNext(), end = competitors.tail(); (candIndex = candIndex.getNext()) != end;)
		{	
			AbstractSimpleEdge<Index,BasicVertexInfo> candidate = candIndex.getValue();
			double candidateContribution = getUtility(candidate, curSet);
			if (candidateContribution > winnerContribution)
			{
				winner = candidate;
				winnerContribution = candidateContribution;
			}
		}
		return winner;
	}

	public AbstractSimpleEdge<Index,BasicVertexInfo>[] getOptimalSetBetweennessBounded(double betwenessBound, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		return getOptimalSetBetweennessBounded(betwenessBound, new int[0], givenEdges, progress, percentage);
	}
	
	public AbstractSimpleEdge<Index,BasicVertexInfo>[] getOptimalSetBetweennessBounded(double betwenessBound, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		FastList<Index> allVertices = getAllVertices(m_candidates, givenVertices, givenEdges);
		
		BasicSetInterface optSet = new OptimizedDynamicBetweennessSet(m_dataWorkshop, allVertices);
//		BasicSetInterface optSet = new DynamicBetweennessSet(m_dataWorkshop);
		
		AbstractSimpleEdge<Index,BasicVertexInfo>[] result = new AbstractSimpleEdge[m_candidates.size()];
		int resultSize = 0;

		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> unusedCandidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>(m_candidates);
		
		betwenessBound = Math.min(betwenessBound, 1);
		double pExpected = Math.log(1 / (1.01 - betwenessBound));
		
		double oldProgressValue = progress.getProgress();
		double p = 0;
		
		for (int i = 0; i < givenEdges.length && optSet.getGroupCentrality() < betwenessBound; i++)
		{
			/** Add the given vertices to the result set with no order. */
			AbstractSimpleEdge<Index,BasicVertexInfo> e = givenEdges[i];
			unusedCandidates.remove(e);
			optSet.add(e);	
			
			if (optSet.getGroupCentrality() >= betwenessBound)
			{
				progress.setProgress(0.99);
			}
			else
			{		
				double pCur = Math.log(1 / (1.01 - optSet.getGroupCentrality()));			
				p = oldProgressValue + (pCur / pExpected) * percentage;		
				progress.setProgress(p);
			}
		}
		
		for (int i = 0; i < givenVertices.length && optSet.getGroupCentrality() < betwenessBound; i++)	
		{
			Index v = Index.valueOf(givenVertices[i]);
			optSet.add(v);	
			
			if (optSet.getGroupCentrality() >= betwenessBound)
			{
				progress.setProgress(0.99);
			}
			else
			{		
				double pCur = Math.log(1 / (1.01 - optSet.getGroupCentrality()));			
				p = oldProgressValue + (pCur / pExpected) * percentage;		
				progress.setProgress(p);
			}
		}
		
		while ((optSet.getGroupCentrality() < betwenessBound) && (unusedCandidates.size() > 0))
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> e = getHighestContribution(optSet, unusedCandidates);
			unusedCandidates.remove(e);
			optSet.add(e);	
            result[resultSize++] = e;	
			
			if (optSet.getGroupCentrality() >= betwenessBound)
			{
				progress.setProgress(0.99);
			}
			else
			{		
				double pCur = Math.log(1 / (1.01 - optSet.getGroupCentrality()));			
				p = oldProgressValue + (pCur / pExpected) * percentage;	
				progress.setProgress(p);
			}	
		}
		return result;
	}

	public AbstractSimpleEdge<Index,BasicVertexInfo>[] getOptimalSetSizeBounded(int groupSize, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		return getOptimalSetSizeBounded(groupSize, new int[0], givenEdges, progress, percentage);
	}
	
	public AbstractSimpleEdge<Index,BasicVertexInfo>[] getOptimalSetSizeBounded(int groupSize, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		double p = progress.getProgress();
		FastList<Index> allVertices = getAllVertices(m_candidates, givenVertices, givenEdges);
		
//		BasicSetInterface optSet = new OptimizedDynamicBetweennessSet(m_dataWorkshop, allVertices);
		BasicSetInterface optSet = new DynamicBetweennessSet(m_dataWorkshop);
		
		AbstractSimpleEdge<Index,BasicVertexInfo>[] result = new AbstractSimpleEdge[Math.min(groupSize, m_candidates.size())];
		int resultSize = 0;
		
		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> unusedCandidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>(m_candidates);
		
		for (int i = 0; i < givenEdges.length && groupSize > 0; i++)
		{
			/** Add the given vertices to the result set with no order. */
			AbstractSimpleEdge<Index,BasicVertexInfo> e = givenEdges[i];
			unusedCandidates.remove(e);
			optSet.add(e);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		
		for (int i = 0; i < givenVertices.length && groupSize > 0; i++)
		{
			Index v = Index.valueOf(givenVertices[i]);
			optSet.add(v);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		
		while ((resultSize < groupSize) && (unusedCandidates.size() > 0))
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> e = getHighestContribution(optSet, unusedCandidates);
			unusedCandidates.remove(e);
			optSet.add(e);
			result[resultSize++] = e;
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		return result;
	}

	public static AbstractSimpleEdge<Index,BasicVertexInfo>[] findEdges(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenDeployment, DataWorkshop dataWorkshop, Algorithm alg, Bound boundType, double bound,
			AbstractExecution progress, double percentage) throws Exception
	{
		return findEdges(candidates, new int[0], givenDeployment, dataWorkshop, alg, boundType, bound, progress, percentage);
	}
	public static AbstractSimpleEdge<Index,BasicVertexInfo>[] findEdges(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, DataWorkshop dataWorkshop, Algorithm alg, Bound boundType, double bound,
			AbstractExecution progress, double percentage) throws Exception
	{
		AbsGreedyEdgeBetweeness greedyAlg = null;		
		switch(alg)
		{
			case TopK:
				greedyAlg = new GreedyEdgeBetweenessTopK(candidates, dataWorkshop);
				break;
			case Contribution:
				greedyAlg = new GreedyEdgeBetweenessContribution(candidates, dataWorkshop);
				break;
			default:
			{
				LoggingManager.getInstance().writeSystem("Invalid Greedy Link Betweeness Algorithm type: " + alg, "AbsGreedyLinkBetweeness", "findLinks", null);
				throw new IllegalArgumentException("Invalid Greedy Link Betweeness Algorithm type: " + alg);
			}
		}
				
		AbstractSimpleEdge<Index,BasicVertexInfo>[] result = null;
		
		switch(boundType)
		{
			case Centrality:
				result = greedyAlg.getOptimalSetBetweennessBounded(bound, givenVertices, givenEdges, progress, percentage);
				break;
			case GroupSize:
				result = greedyAlg.getOptimalSetSizeBounded((int)bound, givenVertices, givenEdges, progress, percentage);
				break;
			default:
			{
				LoggingManager.getInstance().writeSystem("Invalid bound type for Greedy Link Betweeness Algorithm: " + boundType, "AbsGreedyLinkBetweeness", "findLinks", null);
				throw new IllegalArgumentException("Invalid bound type for Greedy Link Betweeness Algorithm: " + boundType);
			}
		}
		return result;
	}
	
	public abstract double getUtility(AbstractSimpleEdge<Index,BasicVertexInfo> e, BasicSetInterface curGroup);
	
	private FastList<Index> getAllVertices(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> s1, int[] s2, AbstractSimpleEdge<Index,BasicVertexInfo>[] s3)
	{
		FastList<Index> result = new FastList<Index>();
		for (FastList.Node<AbstractSimpleEdge<Index,BasicVertexInfo>> candidate = s1.head(), end = s1.tail(); (candidate = candidate.getNext()) != end;)
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> e = candidate.getValue();
			Index v1 = e.getV0();	Index v2 = e.getV1();
			
			if (!result.contains(v1))
				result.add(v1);
			if (!result.contains(v2))
				result.add(v2);
		}
		
		for (int i : s2)
		{
			Index v = Index.valueOf(i);
			if (!result.contains(v))
				result.add(v);
		}
		
		for (AbstractSimpleEdge<Index,BasicVertexInfo> e : s3)
		{
			Index v1 = e.getV0();	Index v2 = e.getV1();
			
			if (!result.contains(v1))
				result.add(v1);
			if (!result.contains(v2))
				result.add(v2);
		}
		return result;
	}
}
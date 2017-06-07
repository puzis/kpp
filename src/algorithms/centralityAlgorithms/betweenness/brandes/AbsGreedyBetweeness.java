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

public abstract class AbsGreedyBetweeness
{
	protected FastList<Index> m_candidates;
	protected DataWorkshop m_dataWorkshop;	
	
        /**
         * This function makes len( competitors) evaluations of getUtility 
         * and returns the competitor with highest utility.
         * @param curSet
         * @param competitors
         * @return
         */
	public AbsGreedyBetweeness(FastList<Index> candidates, DataWorkshop dataWorkshop)
	{
		m_dataWorkshop = dataWorkshop;
		m_candidates = candidates;
	}
	public AbsGreedyBetweeness(){}
	
	public static Index getHighestContributor(BasicSetInterface curSet, FastList<Index> competitors)
	{
		FastList.Node<Index> head = competitors.head();
		Index winner = head.getNext().getValue();
		head = head.getNext();
				
		double winnerContribution = curSet.getContribution(winner);
		
		for (FastList.Node<Index> candIndex = competitors.head().getNext(), end = competitors.tail(); (candIndex = candIndex.getNext()) != end;)// && !((Thread.currentThread() instanceof ExecutionInterface) && ((ExecutionInterface)Thread.currentThread()).isDone());)
		{	
			Index candidate = candIndex.getValue();
			double candidateContribution = curSet.getContribution(candidate); 
			if (candidateContribution > winnerContribution)
			{
				winner = candidate;
				winnerContribution = candidateContribution;
			}
		}
		return winner;
	}
	
	@SuppressWarnings("unchecked")
	public Index[] getOptimalSetSizeBounded(int groupSize, int[] givenVertices, AbstractExecution progress, double percentage) throws Exception
	{
		return getOptimalSetSizeBounded(groupSize, givenVertices, new AbstractSimpleEdge[0], progress, percentage);
	}
	
	public Index[] getOptimalSetSizeBounded(int groupSize, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		FastList<Index> allVertices = getAllVertices(m_candidates, givenVertices, givenEdges);
		BasicSetInterface optSet = this.createEmptySet(m_dataWorkshop, allVertices);
		
		FastList<Index> unusedCandidates = new FastList<Index>(m_candidates);

		double p = progress.getProgress();
		int resultSize = 0;
		Index[] result = new Index[Math.min(groupSize, m_candidates.size())];
		
		for (int i = 0; i < givenVertices.length && groupSize > 0; i++)
		{
			/** Add the given vertices to the result set with no order. */
			Index v = Index.valueOf(givenVertices[i]);
			unusedCandidates.remove(v);
			optSet.add(v);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}

		for (int i = 0; i < givenEdges.length && groupSize > 0; i++)
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> e = givenEdges[i];
			optSet.add(e);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}

		/** While possible add vertices with highest contribution to the result set. */
		while ((resultSize < groupSize) && (unusedCandidates.size() > 0))
		{
			Index v = getHighestContributor(optSet, unusedCandidates);
			unusedCandidates.remove(v);
			optSet.add(v);
			result[resultSize++] = v;
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		return result;
	}
	
	public Index[] getOptimalSetBetweennessBounded(double betwenessBound, int[] givenVertices, AbstractExecution progress, double percentage) throws Exception
	{
		return getOptimalSetBetweennessBounded(betwenessBound, givenVertices, new AbstractSimpleEdge[0], progress, percentage);
	}
	public Index[] getOptimalSetBetweennessBounded(double betwenessBound, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		FastList<Index> allVertices = getAllVertices(m_candidates, givenVertices, givenEdges);
		
		BasicSetInterface optSet = this.createEmptySet(m_dataWorkshop, allVertices);
		Index[] result = new Index[m_candidates.size()];
		int resultSize = 0;
		FastList<Index> unusedCandidates = new FastList<Index>(m_candidates);
		
		betwenessBound = Math.min(betwenessBound, 1);
		double pExpected = Math.log(1 / (1.01 - betwenessBound));
		double oldProgressValue = progress.getProgress();
		double p = 0;
		
		for (int i = 0; i < givenVertices.length; i++)
		{
			/** Add the given vertices to the result set with no order. */
			Index v = Index.valueOf(givenVertices[i]);
			unusedCandidates.remove(v);

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
		
		for (AbstractSimpleEdge<Index,BasicVertexInfo> e : givenEdges)
		{
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
		
		/** While possible add vertices with highest contribution to the result set. */
		while ((optSet.getGroupCentrality() < betwenessBound) && (unusedCandidates.size() > 0))
		{
			Index v = getHighestContributor(optSet, unusedCandidates);
			unusedCandidates.remove(v);
			optSet.add(v);	
            result[resultSize++] = v;
			
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


	//public abstract double getUtility(int v, BasicSetInterface curGroup);
    public abstract BasicSetInterface createEmptySet(DataWorkshop dw, FastList<Index> candidates) throws Exception;

	public static Index[] findVertices(FastList<Index> candidates, int[] givenDeployment, DataWorkshop dataWorkshop, Algorithm alg, Bound boundType, double bound, 
			AbstractExecution progress, double percentage) throws Exception
	{
		return findVertices(candidates, givenDeployment, new AbstractSimpleEdge[0], dataWorkshop, alg, boundType, bound, progress, percentage);
	}
	
	public static Index[] findVertices(FastList<Index> candidates, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, DataWorkshop dataWorkshop, Algorithm alg, Bound boundType, double bound, 
			AbstractExecution progress, double percentage)
		throws Exception
	{
		AbsGreedyBetweeness greedyAlg = null;		
		switch(alg)
		{
			case TopK:
				greedyAlg = new GreedyBetweenessTopK(candidates, dataWorkshop);
				break;
			case Contribution:
				greedyAlg = new GreedyBetweenessContribution(candidates, dataWorkshop);
				break;
			default:
			{
				LoggingManager.getInstance().writeSystem("Invalid Greedy Beteweeness Algorithm type: " + alg, "AbsGreedyBetweeness", "findGroup", null);
				throw new IllegalArgumentException("Invalid Greedy Algorithm type: " + alg);
			}
		}
				
		Index[] result = null;
		
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
				LoggingManager.getInstance().writeSystem("Invalid bound type for Greedy Beteweeness Algorithm: " + boundType, "AbsGreedyBetweeness", "findGroup", null);
				throw new IllegalArgumentException("Invalid bound type for Greedy Beteweeness Algorithm: " + boundType);
			}
		}
		return result;
	}
	
	private FastList<Index> getAllVertices(FastList<Index> s1, int[] s2, AbstractSimpleEdge<Index,BasicVertexInfo>[] s3)
	{
		FastList<Index> result = new FastList<Index>();
		for (FastList.Node<Index> candidate = s1.head(), end = s1.tail(); (candidate = candidate.getNext()) != end;)
		{
			Index v = candidate.getValue();
			result.add(v);
		}
		
		for (int i = 0; i < s2.length; i++)
		{
			Index v = Index.valueOf(s2[i]);
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
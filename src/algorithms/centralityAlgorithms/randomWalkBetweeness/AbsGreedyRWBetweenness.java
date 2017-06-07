package algorithms.centralityAlgorithms.randomWalkBetweeness;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;


public abstract class AbsGreedyRWBetweenness 
{
	protected FastList<Index> m_candidates;
	protected GroupRandomWalkBetweeness m_alg = null;
	
	public AbsGreedyRWBetweenness(FastList<Index> candidates, GroupRandomWalkBetweeness alg){
		m_alg = alg;
		m_candidates = candidates;
	}
	
	@SuppressWarnings("unchecked")
	public abstract Index getWinner(FastList<Index> unusedCandidates, FastList<Object> group);
	
	@SuppressWarnings("unchecked")
	public Index[] getOptimalSetSizeBounded(int groupSize, int[] givenVertices, 
			AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception{
		int resultSize = 0;
		FastList<Object> group = new FastList<Object>();
		
		for (int i : givenVertices){
			Index idx = Index.valueOf(i);
			m_candidates.remove(idx);
			group.add(idx);
		}
		
		for (AbstractSimpleEdge<Index,BasicVertexInfo> e : givenEdges){
			m_candidates.remove(e.getV0());
			m_candidates.remove(e.getV1());
			
			group.add(e);
		}
		Index[] result = new Index[Math.min(groupSize, m_candidates.size())];
		
		double p = progress.getProgress();
		FastList<Index> unusedCandidates = new FastList<Index>(m_candidates);
		while (unusedCandidates.size() > 0 && resultSize < groupSize){
			Index winner = getWinner(unusedCandidates, group);
			/** NOTICE: Instead of "new Double(0)" there should be the closeness value of the result including the winner.  */
			group.add(winner);
			unusedCandidates.remove(winner);
			result[resultSize++] = winner;
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Index[] getOptimalSetCentralityBounded(double centralityBound, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, 
			double percentage) throws Exception{
		double groupCentrality = 0;
		Index[] result = new Index[m_candidates.size()];
		int resultSize = 0;
		FastList<Object> group = new FastList<Object>();
		
		FastList<Index> unusedCandidates = new FastList<Index>(m_candidates);
		
		centralityBound = Math.min(centralityBound, 1);
		double pExpected = Math.log(1 / (1.01 - centralityBound));
		double oldProgressValue = progress.getProgress();
		double p = 0;
		
		for (int i : givenVertices){
			Index idx = Index.valueOf(i);
			m_candidates.remove(idx);
			group.add(idx);
			groupCentrality += m_alg.getVertexBetweeness(i);
			
			if (groupCentrality >= centralityBound){
				progress.setProgress(0.99);
			}
			else{		
				double pCur = Math.log(1 / (1.01 - groupCentrality));			
				p = oldProgressValue + (pCur / pExpected) * percentage;		
				progress.setProgress(p);
			}
		}
		
		for (AbstractSimpleEdge<Index,BasicVertexInfo> e : givenEdges){
			m_candidates.remove(e.getV0());
			m_candidates.remove(e.getV1());
			
			group.add(e);
			if (groupCentrality >= centralityBound){
				progress.setProgress(0.99);
			}
			else{		
				double pCur = Math.log(1 / (1.01 - groupCentrality));			
				p = oldProgressValue + (pCur / pExpected) * percentage;		
				progress.setProgress(p);
			}
		}
		
		while (unusedCandidates.size() > 0 && groupCentrality < centralityBound){
			Index winner = getWinner(unusedCandidates, group);
			group.add(winner);
			unusedCandidates.remove(winner);
			result[resultSize++] = winner;
			groupCentrality += m_alg.getVertexBetweeness(winner.intValue());
			
			if (groupCentrality >= centralityBound){
				progress.setProgress(0.99);
			}
			else{		
				double pCur = Math.log(1 / (1.01 - groupCentrality));			
				p = oldProgressValue + (pCur / pExpected) * percentage;		
				progress.setProgress(p);
			}
		}
		return result;
	}

	public static Index[] findVertices(GroupRandomWalkBetweeness alg, FastList<Index> candidates, int[] givenDeployment, Algorithm algType, Bound boundType, double bound, 
			AbstractExecution progress, double percentage) throws Exception{
		return findVertices(alg, candidates, givenDeployment, new AbstractSimpleEdge[0], algType, boundType, bound, progress, percentage);
	}
	
	public static Index[] findVertices(GroupRandomWalkBetweeness alg, FastList<Index> candidates, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, 
			Algorithm algType, Bound boundType, double bound, AbstractExecution progress, double percentage) throws Exception{
		AbsGreedyRWBetweenness greedyAlg = null;		
		
		switch(algType){
			case TopK:
				greedyAlg = new GreedyRWBetweennessTopK(candidates, alg);
				break;
			default:
			{
				LoggingManager.getInstance().writeSystem("Invalid Greedy RW Beteweeness Algorithm type: " + alg, "AbsGreedyRWBetweeness", "findVertices", null);
				throw new IllegalArgumentException("Invalid Greedy RW Algorithm type: " + alg);
			}
		}
				
		Index[] result = null;
		
		switch(boundType){
			case GroupSize:
				result = greedyAlg.getOptimalSetSizeBounded((int)bound, givenVertices, givenEdges, progress, percentage);
				break;
			case Centrality:
				result = greedyAlg.getOptimalSetCentralityBounded(bound, givenVertices, givenEdges, progress, percentage);
				break;
			default:
			{
				LoggingManager.getInstance().writeSystem("Invalid bound type for Greedy RW Beteweeness Algorithm: " + boundType, "AbsGreedyRWBetweeness", "findVertices", null);
				throw new IllegalArgumentException("Invalid bound type for Greedy RW Beteweeness Algorithm: " + boundType);
			}
		}
		return result;
	}
}
package algorithms.centralityAlgorithms.degree;

import java.util.Comparator;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

import common.Pair;

public abstract class AbsGreedyDegree 
{
	protected FastList<Index> m_vCandidates = null;
	protected FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> m_eCandidates = null;
	protected DegreeAlgorithm m_degreeAlgorithm = null;
	
	public AbsGreedyDegree(DegreeAlgorithm degreeAlgorithm){
		m_degreeAlgorithm = degreeAlgorithm;
	}
	public void setVerticesCandidates(FastList<Index> candidates){
		m_vCandidates = candidates;
	}
	
	public void setEdgesCandidates(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates){
		m_eCandidates = candidates;
	}
	
	public static Index[] findVertices(DegreeAlgorithm degreeAlgorithm, Algorithm algType, FastList<Index> candidates, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, 
			Bound boundType, int k, AbstractExecution progress, double percentage) throws Exception{
		AbsGreedyDegree greedyAlg = null;
		Index[] result = null;
		switch(algType)
		{
			case Contribution:
			{
				greedyAlg = new GreedyDegreeContribution(degreeAlgorithm);
				greedyAlg.setVerticesCandidates(candidates);
				break;
			}
			case TopK:
			{
				greedyAlg = new GreedyDegreeTopK(degreeAlgorithm);
				greedyAlg.setVerticesCandidates(candidates);
				break;
			}
			default:
				throw new IllegalArgumentException("Invalid degree algorithm type: " + algType);
		}
		
		switch(boundType)
		{
			case GroupSize:
				result = greedyAlg.getOptimalGroupSizeBounded(k, givenVertices, givenEdges, progress, percentage);
				break;
			case Centrality:
				throw new IllegalArgumentException("Invalid bound type for Greedy Degree Algorithm: " + boundType);
			default:
			{
				LoggingManager.getInstance().writeSystem("Invalid bound type for Greedy Degree Algorithm: " + boundType, "AbsGreedyDegree", "findVertices", null);
				throw new IllegalArgumentException("Invalid bound type for Greedy Degree Algorithm: " + boundType);
			}
		}
		
		return result;
	}
	
	public static AbstractSimpleEdge<Index,BasicVertexInfo>[] findEdges(DegreeAlgorithm degreeAlgorithm, Algorithm algType, FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, int[] givenVertices, 
			AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, Bound boundType, int k, AbstractExecution progress, double percentage) throws Exception{
		AbsGreedyDegree greedyAlg = null;		
		
		AbstractSimpleEdge<Index,BasicVertexInfo>[] result = null;
		switch(algType)
		{
			case Contribution:
			{
				greedyAlg = new GreedyDegreeContribution(degreeAlgorithm);
				greedyAlg.setEdgesCandidates(candidates);
				break;
			}
			case TopK:
			{
				greedyAlg = new GreedyDegreeTopK(degreeAlgorithm);
				greedyAlg.setEdgesCandidates(candidates);
				break;
			}
			default:
				throw new Exception("Invalid degree algorithm type: " + algType);
		}
		
		switch(boundType)
		{
			case GroupSize:
				result = greedyAlg.getOptimalEdgesSizeBounded(k, givenVertices, givenEdges, progress, percentage);
				break;
			case Centrality:
				throw new IllegalArgumentException("Invalid bound type for Greedy Degree Algorithm: " + boundType);
			default:
			{
				LoggingManager.getInstance().writeSystem("Invalid bound type for Greedy Degree Algorithm: " + boundType, "AbsGreedyDegree", "findEdges", null);
				throw new IllegalArgumentException("Invalid bound type for Greedy Degree Algorithm: " + boundType);
			}
		}

		return result;
	}

	public abstract Index[] getOptimalGroupSizeBounded(int groupSize, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception;
	public abstract AbstractSimpleEdge<Index,BasicVertexInfo>[] getOptimalEdgesSizeBounded(int groupSize, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception;
	
	public static DegreesComparator getDegreesComparator()
	{
		return new DegreesComparator();
	}
	
	private static class DegreesComparator implements Comparator<Pair<Index, Index>>
	{
		@Override
		public int compare(Pair<Index, Index> o1, Pair<Index, Index> o2)
		{
			return o2.getValue2().intValue() - o1.getValue2().intValue();
		}
	}
}
package algorithms.centralityAlgorithms.degree;

import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public class GreedyDegreeContribution extends AbsGreedyDegree 
{
	GroupDegreeAlgorithm m_groupDegreeAlg = null;
	
	public GreedyDegreeContribution(DegreeAlgorithm degreeAlgorithm)
	{
		super(degreeAlgorithm);
	}
	
	public Index[] getOptimalGroupSizeBounded(int groupSize, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		m_groupDegreeAlg = new GroupDegreeAlgorithm(m_degreeAlgorithm, progress, percentage);
		
		int resultSize = 0;
		Index[] result = new Index[Math.min(groupSize, m_vCandidates.size())];

		FastList<Index> unusedCandidates = new FastList<Index>(m_vCandidates);
		
		double p = progress.getProgress();
		
		/** Add the given vertices to the result set with no order. */
		for (int i = 0; i < givenVertices.length && groupSize > 0; i++)
		{
			int givenVertex = givenVertices[i];
			unusedCandidates.remove(Index.valueOf(givenVertex));
			m_groupDegreeAlg.add(givenVertex);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}

		for ( int i = 0; i < givenEdges.length && groupSize > 0; i++)
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> e = givenEdges[i];
			m_groupDegreeAlg.add(e);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}

		/** While possible add vertices with highest contribution to the result set. */
		while ((resultSize < groupSize) && (unusedCandidates.size() > 0))
		{
			int v = m_groupDegreeAlg.getMaxNeighbors(unusedCandidates);
			unusedCandidates.remove(Index.valueOf(v));
			m_groupDegreeAlg.add(v);
			result[resultSize++] = Index.valueOf(v);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		return result;
	}
	
	public AbstractSimpleEdge<Index,BasicVertexInfo>[] getOptimalEdgesSizeBounded(int groupSize, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception
	{
		m_groupDegreeAlg = new GroupDegreeAlgorithm(m_degreeAlgorithm, progress, percentage);
		
		int resultSize = 0;
		//EdgeInterface<Index>[] result = new IndexEdge[Math.min(groupSize, m_eCandidates.size())];
		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> result = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();

		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> unusedCandidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>(m_eCandidates);
		
		double p = progress.getProgress();
		
		/** Add the given vertices to the result set with no order. */
		for (int i = 0; i < givenVertices.length && groupSize > 0; i++)
		{
			int givenVertex = givenVertices[i];
			m_groupDegreeAlg.add(givenVertex);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}

		for (int i = 0; i < givenEdges.length && groupSize > 0; i++)
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> e = givenEdges[i];
			unusedCandidates.remove(e);
			m_groupDegreeAlg.add(e);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}

		FastList<Index> usedVertices = new FastList<Index>();
		/** While possible add edges with highest contribution to the result set (disjoint edges). */
		while ((resultSize < groupSize) && (unusedCandidates.size() > 0))
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> e = (AbstractSimpleEdge<Index,BasicVertexInfo>)getDisjointEdge(unusedCandidates, usedVertices);
			unusedCandidates.remove(e);
			m_groupDegreeAlg.add(e);
			//result[resultSize++] = e;
			result.add(e);
			resultSize++;
			
			/** Add opposite direction of the edge. */
			AbstractSimpleEdge<Index,BasicVertexInfo> e2 = e.flip();
			if ((resultSize < groupSize) && unusedCandidates.contains(e2))
			{
				unusedCandidates.remove(e2);
				m_groupDegreeAlg.add(e2);
				//result[resultSize++] = e2;
				result.add(e2);
				resultSize++;
				
			}
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		//return result;
		return result.toArray(new AbstractSimpleEdge[Math.min(groupSize, m_eCandidates.size())]);
	}
	
	private AbstractSimpleEdge<Index,BasicVertexInfo> getDisjointEdge(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> unusedCandidates, FastList<Index> usedVertices)
	{
		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>(unusedCandidates);
		
		AbstractSimpleEdge<Index,BasicVertexInfo> e = candidates.removeFirst();
		Index v1 = e.getV0();
		Index v2 = e.getV1();
		while (candidates.size() != 0 && (usedVertices.contains(v1) || usedVertices.contains(v2))) 
		{
			e = candidates.removeFirst();
			v1 = e.getV0();
			v2 = e.getV1();
		}
		usedVertices.add(v1);
		usedVertices.add(v2);
		return e;
	}
}
package algorithms.centralityAlgorithms.degree;

import java.util.Collections;

import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

import common.Pair;

public class GreedyDegreeTopK extends AbsGreedyDegree
{
	public GreedyDegreeTopK(DegreeAlgorithm degreeAlgorithm)
	{
		super(degreeAlgorithm);
	}
	
	@Override
	public Index[] getOptimalGroupSizeBounded(int k, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception 
	{
		Index[] result = new Index[k];
		int resultSize = 0;
		
		for (int i : givenVertices){	m_vCandidates.remove(Index.valueOf(i));	}

		/** NOTHING TO DO WITH GIVEN EDGES IN THIS CASE. */
		
		FastList<Pair<Index, Index>> degrees = m_degreeAlgorithm.getDegreeList();
		
		double p = progress.getProgress();
		
		Collections.sort(degrees, AbsGreedyDegree.getDegreesComparator());
		
		int i = 0;
		while (resultSize < k)
		{
			result[resultSize++] = degrees.get(i).getValue1();
			i += 1;
		}
		
		p += 0.2 * percentage;	
		progress.setProgress(p);
		
		return result;
	}
	
	public AbstractSimpleEdge<Index,BasicVertexInfo>[] getOptimalEdgesSizeBounded(int k, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, AbstractExecution progress, double percentage) throws Exception 
	{
		//EdgeInterface<Index>[] degrees = new IndexEdge[k];
		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> degrees = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
		
		double p = progress.getProgress();
		
		int resultSize = 0;
		int totalSize = m_eCandidates.size() + givenEdges.length;// + givenVertices.size();

		/** NOTHING TO DO WITH GIVEN VERTICES IN THIS CASE. */
		
		for (AbstractSimpleEdge<Index,BasicVertexInfo> e: givenEdges)
		{
			m_eCandidates.remove(e);
			
			p += (1 / (double) (totalSize)) * percentage;	
			progress.setProgress(p);
		}

		/** All edges have the same contribution to the group degree (2), therefore, the order doesn't matter. */
		//FastList<EdgeInterface<Index>> unusedcandidates = new FastList<EdgeInterface<Index>>(m_eCandidates);
		FastList<? extends AbstractSimpleEdge<Index,BasicVertexInfo>> unusedcandidates = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>(m_eCandidates);
		
		while (unusedcandidates.size() > 0)
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> e = (AbstractSimpleEdge<Index,BasicVertexInfo>)unusedcandidates.removeFirst();
			//degrees[resultSize++] = e;
			degrees.add(e);
			resultSize++;
			
			AbstractSimpleEdge<Index,BasicVertexInfo> e2 = e.flip();
			if ((resultSize < k) && unusedcandidates.contains(e2))
			{
				//degrees[resultSize++] = e2;
				degrees.add(e2);
				resultSize++;
				resultSize++;
				unusedcandidates.remove(e2);
			}
			if (resultSize >= k)
			{
				progress.setProgress(0.99);
				break;
			}
			
			p += (1 / (double) (totalSize)) * percentage;	
			progress.setProgress(p);
		}
		
//		for(FastList.Node<Pair<Index, Index>> eNode = m_candidates.head(), end = m_candidates.tail(); (eNode = eNode.getNext()) != end;)
//		{
//			degrees.add(new Pair(eNode.getValue(), new Double(2)));
//			resultSize++;
//			if (resultSize >= k)
//			{
//				progress.setProgress(0.99);
//				break;
//			}
//			
//			p += (1 / (double) (totalSize)) * percentage;	
//			progress.setProgress(p);
//		}
		//return degrees; 
		return degrees.toArray(new AbstractSimpleEdge[k]);
	}
}
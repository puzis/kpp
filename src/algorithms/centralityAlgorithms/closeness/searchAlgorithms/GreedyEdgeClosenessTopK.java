package algorithms.centralityAlgorithms.closeness.searchAlgorithms;

import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public class GreedyEdgeClosenessTopK extends AbsGreedyEdgeClosenessNG 
{
	private static final long serialVersionUID = 1L;

	public GreedyEdgeClosenessTopK(IClosenessAlgorithm closenessAlgorithm, FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, AbstractExecution progress, double percentage) throws Exception
	{
		super(closenessAlgorithm, candidates, progress, percentage);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractSimpleEdge<Index,BasicVertexInfo> getWinner(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> unusedCandidates, FastList<Object> group) 
	{
		/*
		 * IMPORTANT NOTE: Since using ClosenessFormula HIGHER closeness value means BETTER closeness value!
		 */
		double minCloseness = 0;
		AbstractSimpleEdge<Index,BasicVertexInfo> winner = unusedCandidates.getFirst();
		
		for (FastList.Node<AbstractSimpleEdge<Index,BasicVertexInfo>> vNode = unusedCandidates.head(), end = unusedCandidates.tail(); (vNode = vNode.getNext()) != end; )
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> v = vNode.getValue();
			double c = m_closenessAlgorithm.getCloseness(v);
			/*
			 * IMPORTANT NOTE: Since using ClosenessFormula HIGHER closeness value means BETTER closeness value!
			 */
			if (c > minCloseness)
			{
				minCloseness = c;
				winner = v;
			}
		}
		return winner;
	}
}

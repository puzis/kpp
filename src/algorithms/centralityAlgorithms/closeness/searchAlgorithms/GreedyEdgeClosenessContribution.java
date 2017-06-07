package algorithms.centralityAlgorithms.closeness.searchAlgorithms;

import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DummyProgress;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public class GreedyEdgeClosenessContribution extends AbsGreedyEdgeClosenessNG 
{
	private static final long serialVersionUID = 1L;

	public GreedyEdgeClosenessContribution(IClosenessAlgorithm closenessAlgorithm, FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, AbstractExecution progress, double percentage) throws Exception
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
		double closeness = 0;
		AbstractSimpleEdge<Index,BasicVertexInfo> winner = unusedCandidates.getFirst();
		FastList<Object> gUnionCandidate = null;
		
		for (FastList.Node<AbstractSimpleEdge<Index,BasicVertexInfo>> node = unusedCandidates.head(), end = unusedCandidates.tail(); (node = node.getNext()) != end; )
		{
			AbstractSimpleEdge<Index,BasicVertexInfo> candidate = node.getValue();
			
			gUnionCandidate = new FastList<Object>(group);
			gUnionCandidate.add(candidate);
			
			
			//double c = GroupClosenessAlgorithmNG.calculateMixedGroupCloseness(m_closenessAlgorithm.getVertices(), gUnionCandidate, m_closenessAlgorithm.getDistanceMatrix(), new DummyProgress(), 1);
			double c = m_closenessAlgorithm.calculateMixedGroupCloseness(gUnionCandidate.toArray(), new DummyProgress(), 1.0);
			/*
			 * IMPORTANT NOTE: Since using ClosenessFormula HIGHER closeness value means BETTER closeness value!
			 */
			if (c > closeness)
			{
				closeness = c;
				winner = candidate;
			}
		}
		return winner;
	}

}

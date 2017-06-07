package algorithms.centralityAlgorithms.randomWalkBetweeness;

import javolution.util.FastList;
import javolution.util.Index;

public class GreedyRWBetweennessTopK extends AbsGreedyRWBetweenness  
{
	public GreedyRWBetweennessTopK(FastList<Index> candidates, GroupRandomWalkBetweeness alg) 
	{
		super(candidates, alg);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Index getWinner(FastList<Index> unusedCandidates, FastList<Object> group) 
	{
		double maxRWB = Double.MIN_VALUE;
		Index winner = unusedCandidates.getFirst();
		
		for (FastList.Node<Index> vNode = unusedCandidates.head(), end = unusedCandidates.tail(); (vNode = vNode.getNext()) != end; )
		{
			Index v = vNode.getValue();
			double rw = m_alg.getVertexBetweeness(v.intValue());
			if (rw > maxRWB)
			{
				maxRWB = rw;
				winner = v;
			}
		}
		return winner;
	}
}

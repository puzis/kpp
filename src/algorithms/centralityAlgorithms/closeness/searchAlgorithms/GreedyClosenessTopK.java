package algorithms.centralityAlgorithms.closeness.searchAlgorithms;

import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;

public class GreedyClosenessTopK extends AbsGreedyClosenessNG
{
	private static final long serialVersionUID = 1L;

	public GreedyClosenessTopK(IClosenessAlgorithm closenessAlgorithm, BasicSet set, FastList<Index> candidates, AbstractExecution progress, double percentage) throws Exception
	{
		super(closenessAlgorithm, set, candidates, progress, percentage);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Index getWinner(FastList<Index> unusedCandidates) 
	{
		/*
		 * IMPORTANT NOTE: Since using ClosenessFormula HIGHER closeness value means BETTER closeness value!
		 */
		double maxCloseness = 0;
		Index winner = unusedCandidates.getFirst();
		
		for (FastList.Node<Index> vNode = unusedCandidates.head(), end = unusedCandidates.tail(); (vNode = vNode.getNext()) != end; )
		{
			Index v = vNode.getValue();
			double c = m_closenessAlgorithm.getCloseness(v);
			/*
			 * IMPORTANT NOTE: Since using ClosenessFormula HIGHER closeness value means BETTER closeness value!
			 */
			if (c > maxCloseness)
			{
				maxCloseness = c;
				winner = v;
			}
		}
		return winner;
	}
}

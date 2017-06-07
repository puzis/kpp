package algorithms.centralityAlgorithms.closeness.searchAlgorithms;

import javolution.util.FastList;

import javolution.util.Index;
import server.execution.AbstractExecution;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;

public class GreedyClosenessContribution extends AbsGreedyClosenessNG
{
	private static final long serialVersionUID = 1L;

	public GreedyClosenessContribution(IClosenessAlgorithm closenessAlgorithm, BasicSet set, FastList<Index> candidates, AbstractExecution progress, double percentage) throws Exception
	{
		super(closenessAlgorithm, set, candidates, progress, percentage);
	}
	
	@Override
	/**
	 * For every candidate calculates the closeness of the group + candidate.
	 * Returns the candidate for which the closeness value has been the lowest.
	 */
	public Index getWinner(FastList<Index> unusedCandidates)
	{
		/*
		 * IMPORTANT NOTE: Since using ClosenessFormula HIGHER closeness value means BETTER closeness value!
		 */
		double contribution = 0;
		Index winner = unusedCandidates.getFirst();
		
		for (FastList.Node<Index> node = unusedCandidates.head(), end = unusedCandidates.tail(); (node = node.getNext()) != end; )
		{
			Index candidate = node.getValue();
			
			double c = _set.getContribution(candidate);
			/*
			 * IMPORTANT NOTE: Since using ClosenessFormula HIGHER closeness value means BETTER closeness value!
			 */
			if (c > contribution)
			{
				contribution = c;
				winner = candidate;
			}
		}
		return winner;
	}
}

package algorithms.centralityAlgorithms.rbc;

import algorithms.centralityAlgorithms.rbc.sets.DynamicRBCSet;
import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;

public class GreedyContributionRBC {
	
	public static Index[] findVertices(
			DynamicRBCSet set,
			int groupSize,
			int[] givenVertices,
			int[] candidates,
			AbstractExecution progress,
			double percentage)
	{
		
			
		FastList<Index> unusedCandidates = new FastList<Index>();
		for (int i: candidates)
			unusedCandidates.add(Index.valueOf(i));
		
		for (int i : givenVertices)
		{
			Index idx = Index.valueOf(i);
			unusedCandidates.remove(idx);
			set.add(idx);
		}
		
		double p = progress.getProgress();
		while (unusedCandidates.size() > 0 && set.size() < groupSize)
		{
			Index winner = getWinner(unusedCandidates,set);
			set.add(winner);
			unusedCandidates.remove(winner);
			
			p += (1 / (double)groupSize) * percentage;	
			progress.setProgress(p);
		}
		Index[] result = new Index[set.size()];
		int i = 0;
		for (Index v: set.getVertexMembers()) {
			result[i++] = v;
		}
		return result;
	}
	
	public static Index[] findVertices(
			DynamicRBCSet set,
			double groupRBC,
			int[] givenVertices,
			int[] candidates,
			AbstractExecution progress,
			double percentage)
	{
		
			
		FastList<Index> unusedCandidates = new FastList<Index>();
		for (int i: candidates)
			unusedCandidates.add(Index.valueOf(i));
		
		for (int i : givenVertices)
		{
			Index idx = Index.valueOf(i);
			unusedCandidates.remove(idx);
			set.add(idx);
		}
		
		double p = progress.getProgress();
		double pExpected = Math.log(1 / (1.01 - groupRBC));
		double oldProgressValue = progress.getProgress();
		
		while (unusedCandidates.size() > 0 && set.getGroupCentrality() < groupRBC)
		{
			Index winner = getWinner(unusedCandidates,set);
			set.add(winner);
			unusedCandidates.remove(winner);
			
			double pCur = Math.log(1 / (1.01 - set.getGroupCentrality()));			
			p = oldProgressValue + (pCur / pExpected) * percentage;		
			progress.setProgress(p);
		}
		Index[] result = new Index[set.size()];
		int i = 0;
		for (Index v: set.getVertexMembers()) {
			result[i++] = v;
		}
		return result;
	}
	
	private static Index getWinner(FastList<Index> unusedCandidates, DynamicRBCSet set) {
		double bestRBC = 0;
		Index winner = null;
		for (Index i: unusedCandidates)
		{
			double rbc = set.getContribution(i);
			if (rbc >= bestRBC)
			{
				winner = i;
				bestRBC = rbc;
			}
				
		}
		return winner;
	}

}

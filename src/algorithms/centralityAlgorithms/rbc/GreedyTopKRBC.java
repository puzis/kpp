package algorithms.centralityAlgorithms.rbc;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;

public class GreedyTopKRBC {

	public static Index[] findVertices(final FasterGRBC alg, int groupSize,
			int[] givenVertices, int[] candidates, AbstractExecution progress,
			double percentage) {

		FastList<Index> unusedCandidates = new FastList<Index>();
		for (int i : candidates)
			unusedCandidates.add(Index.valueOf(i));

		HashSet<Index> set = new HashSet<Index>();
		for (int i : givenVertices) {
			Index idx = Index.valueOf(i);
			unusedCandidates.remove(idx);
			set.add(idx);
		}

		double p = progress.getProgress();

		int n = unusedCandidates.size();
		int k = groupSize - set.size();
		
		if (k  < (Math.log(n) / Math.log(2)))

		{
		
			while (unusedCandidates.size() > 0 && set.size() < groupSize) {
				Index winner = getWinner(unusedCandidates, alg);
				set.add(winner);
				unusedCandidates.remove(winner);

				p += (1 / (double) groupSize) * percentage;
				progress.setProgress(p);
			}
			
		} else {
			//System.out.println("sorting");
			Collections.sort(unusedCandidates, new Comparator<Index>() {

				@Override
				public int compare(Index o1, Index o2) {
					FastList<Index> ls1 = new FastList<Index>();
					FastList<Index> ls2 = new FastList<Index>();
					ls1.add(o1);
					ls2.add(o2);
					return (int) Math.signum(alg.getBetweeness(ls2) - alg.getBetweeness(ls1)); 
				}
			});
			while (set.size() < groupSize) {
				set.add(unusedCandidates.removeFirst());
				
			}
		}
		
		Index[] result = new Index[set.size()];
		int i = 0;
		for (Index v : set) {
			result[i++] = v;
		}
		return result;
	}

	private static Index getWinner(FastList<Index> unusedCandidates,
			FasterGRBC alg) {
		double bestRBC = 0;
		Index winner = null;
		FastList<Index> ls = new FastList<Index>();
		for (Index i : unusedCandidates) {
			ls.add(i);
			double rbc = alg.getBetweeness(ls);
			if (rbc >= bestRBC) {
				winner = i;
				bestRBC = rbc;
			}
			ls.clear();
		}
		return winner;
	}

}

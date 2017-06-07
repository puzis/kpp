package algorithms.centralityAlgorithms.betweenness.brandes;

import javolution.util.FastList;
import javolution.util.Index;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.StaticBetweennessSet;

public class GreedyBetweenessTopK extends AbsGreedyBetweeness
{ 

	public GreedyBetweenessTopK(FastList<Index> candidates, DataWorkshop dataWorkshop)
	{
		super(candidates, dataWorkshop);
	}

	@Override
	public BasicSet createEmptySet(DataWorkshop dw, FastList<Index> candidates) throws Exception
	{
		return new StaticBetweennessSet(dw);
	}
}

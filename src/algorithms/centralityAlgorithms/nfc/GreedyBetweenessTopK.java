package algorithms.centralityAlgorithms.nfc;

import javolution.util.FastList;
import javolution.util.Index;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.nfc.preprocessing.DataWorkshopNFC;
import algorithms.centralityAlgorithms.nfc.sets.StaticBetweennessSet;

public class GreedyBetweenessTopK extends AbsGreedyBetweeness
{ 

	public GreedyBetweenessTopK(FastList<Index> candidates, DataWorkshopNFC dataWorkshop)
	{
		super(candidates, dataWorkshop);
	}

	@Override
	public BasicSet createEmptySet(DataWorkshopNFC dw, FastList<Index> candidates) throws Exception
	{
		return new StaticBetweennessSet(dw);
	}
}

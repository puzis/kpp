package algorithms.centralityAlgorithms.betweenness.brandes;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.BasicSetInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;

public class GreedyEdgeBetweenessContribution extends AbsGreedyEdgeBetweeness
{
	public GreedyEdgeBetweenessContribution(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, DataWorkshop dataWorkshop)
	{
		super(candidates, dataWorkshop);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getUtility(AbstractSimpleEdge<Index,BasicVertexInfo> e, BasicSetInterface curGroup)
	{
		return curGroup.getContribution(e);
	}
}

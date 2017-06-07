package algorithms.centralityAlgorithms.nfc;

import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.GraphInterface;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.BasicSetInterface;
import algorithms.centralityAlgorithms.nfc.preprocessing.DataWorkshopNFC;
import algorithms.centralityAlgorithms.nfc.sets.DynamicBetweennessSet;

public class GreedyBetweenessContribution extends AbsGreedyBetweeness
{
	private void deploy(GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage) throws Exception
	{
		BasicSetInterface optGroup = new DynamicBetweennessSet(m_dataWorkshop);
		
		FastList<Index> unusedCandidates = new FastList<Index>(m_candidates);
		
		int index = 0;

		while (unusedCandidates.size() > 0)
		{
			Index v = getHighestContributor(optGroup, unusedCandidates);
			unusedCandidates.remove(v);
			optGroup.add(v);

			BasicVertexInfo vInfo = graph.getVertex(v);
			if (VertexFactory.isVertexInfo(vInfo)){
				((VertexInfo)vInfo).setDeployment(index);
				((VertexInfo)vInfo).setBetweeness(m_dataWorkshop.getBetweenness(v.intValue()) / m_dataWorkshop.getCommunicationWeight());
			}
			
			double p = progress.getProgress();
			p += (1 / (double)m_candidates.size()) * percentage;	
			progress.setProgress(p);
			++index;
		}
	}

	public static void writeDeployment(GraphInterface<Index,BasicVertexInfo> graph, DataWorkshopNFC dataWorkshop, AbstractExecution progress, double percentage) throws Exception
	{
		FastList<Index> allVertices = new FastList<Index>();
		
		for (Index v : graph.getVertices()){
        	allVertices.add(v);
        }
		
		GreedyBetweenessContribution alg = new GreedyBetweenessContribution(allVertices, dataWorkshop);
		alg.deploy(graph, progress, percentage);
	}

	public GreedyBetweenessContribution(FastList<Index> candidates, DataWorkshopNFC dataWorkshop)
	{
		super(candidates, dataWorkshop);
	}

    @Override
    public BasicSet createEmptySet(DataWorkshopNFC dw, FastList<Index> candidates) throws Exception 
    {
//    	return new OptimizedDynamicBetweennessSet(dw, candidates);
    	return new DynamicBetweennessSet(dw);
    }
}

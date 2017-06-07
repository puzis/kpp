package algorithms.shortestPath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import javolution.util.Index;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphInterface;
import topology.AbstractHyperEdge;
import topology.HyperGraphInterface;
import topology.MultiWeightedHyperEdge;
import topology.MultiWeightedHyperGraph;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;

import common.Pair;

/**   
 */
public class HyperDijkstraAlgorithm extends AbstractShortestPathAlgorithm  
{

	protected double[] m_vDist;
	protected MultiWeightedHyperGraph<Index,BasicVertexInfo> m_graph;
    protected Map<MultiWeightedHyperEdge<Index,BasicVertexInfo>, Pair<Double,Double>> m_eDist;

    
	public HyperDijkstraAlgorithm(MultiWeightedHyperGraph<Index,BasicVertexInfo> graph) {
		m_vDist = new double [graph.getNumberOfVertices()];
		Arrays.fill(m_vDist, Double.NaN);
		m_eDist = new HashMap<MultiWeightedHyperEdge<Index,BasicVertexInfo>,Pair<Double,Double>>();
		m_graph=graph;
	}

	public void run(int s) 
	{
		
		
		
		PriorityQueue<Pair<Index, Double>> queue = new PriorityQueue<Pair<Index, Double>>(m_graph.getNumberOfVertices(), new NodeComparator());

		Arrays.fill(m_vDist, Double.NaN);
		m_vDist[s]=0;

		/** The cost of a vertex is its latency. */
		BasicVertexInfo sInfo = m_graph.getVertex(Index.valueOf(s));
		double cost=0.0;
		if (VertexFactory.isVertexInfo(sInfo))
			cost = ((VertexInfo)sInfo).getLatency();
		queue.add(new Pair<Index, Double>(Index.valueOf(s), new Double(cost)));

		while(!queue.isEmpty()) 
		{
			Pair<Index, Double> current = (Pair<Index, Double>) queue.poll();			
			double curDist = current.getValue2().doubleValue();			
			Index u = current.getValue1();

			if (curDist>m_vDist[u.intValue()]) continue;

			beforeExpand(u);
			if(isExpandable(u)){			
            	Iterable<? extends MultiWeightedHyperEdge<Index,BasicVertexInfo>> neighbors = m_graph.getOutgoingEdges(u);
            	for  (MultiWeightedHyperEdge<Index,BasicVertexInfo> e : neighbors)
            	{       
            		
            		/**
            		 * farest vertex reachable via e is at distance fardist from source
            		 * nearset vertex belonging to e is at distance neardist from source
            		 * current vertex is at distance vDist
            		 * nearest vertex reachable from v via e is uDistMin
            		 * if uDistMin>fardist there is no point in expanding e  
            		 */
            		double neardist = Double.POSITIVE_INFINITY;
            		double fardist = Double.POSITIVE_INFINITY;
            		if (m_eDist.containsKey(e)){
            			neardist = m_eDist.get(e).getValue1();
            			fardist = m_eDist.get(e).getValue2();
            		}
            		double uDist = m_vDist[u.intValue()];
            		
            		assert uDist == curDist;
            		
            		double uDistMin = uDist + e.getMinLatency(u);
            		double uDistMax = uDist + e.getMaxLatency(u);
            		neardist = Math.min(neardist,uDist);
            		fardist = Math.min(fardist,uDistMax);
        			m_eDist.put(e, new Pair<Double,Double>(neardist,fardist));
        			
        			if (fardist > uDistMin){        			
        				for (Index v : e.getNeighbors(u,fardist-uDist)){
							/** The latency weight of the vertex v. */
        					BasicVertexInfo vInfo = m_graph.getVertex(v);
							double uvCost = e.getLatency(u, v);
							double vCost = 0.0;
							if (VertexFactory.isVertexInfo(vInfo))
        						vCost = ((VertexInfo)vInfo).getLatency();        		
		
							
							/** Relax */
							/** v found for the first time or a shorter path to v via current was found */
							if (Double.isNaN(m_vDist[v.intValue()]) || m_vDist[v.intValue()] > uDist + uvCost + vCost)
							{
								m_vDist[v.intValue()] = uDist + uvCost + vCost;
								queue.add(new Pair<Index, Double>(v, m_vDist[v.intValue()]));
								vertexDiscovered(u, v,e);
							}
							else
							{
								vertexRediscovered(u, v,e, uDist + uvCost + vCost);
							}
        				}
        			}
				}
			}

			afterExpand(u);
		}	
	}

	class NodeComparator implements Comparator<Pair<Index, Double>>
	{
		public int compare(Pair<Index, Double> arg0, Pair<Index, Double> arg1)
		{
			return Double.compare(arg0.getValue2(), arg1.getValue2());
		}
	}

	@Override
	public double[] getDistanceArray() {
		return m_vDist;
	}

	@Override
	public double getDistance(int intValue) {
		return m_vDist[intValue];
	}

}
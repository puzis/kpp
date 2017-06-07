package algorithms.shortestPath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import javolution.util.Index;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphInterface;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;

import common.Pair;

/**   
 */
public class DijkstraAlgorithm extends AbstractShortestPathAlgorithm  
{

	protected double[] m_dist;
	protected GraphInterface<Index,BasicVertexInfo> m_graph;
	
	
	
	public DijkstraAlgorithm(GraphInterface<Index,BasicVertexInfo> graph) {
		m_dist = new double [graph.getNumberOfVertices()];
		Arrays.fill(m_dist, Double.NaN);
		m_graph=graph;
	}

	public void run(int s) 
	{
		PriorityQueue<Pair<Index, Double>> queue = new PriorityQueue<Pair<Index, Double>>(m_graph.getNumberOfVertices(), new NodeComparator());

		Arrays.fill(m_dist, Double.NaN);
		m_dist[s]=0;

		/** The cost of a vertex is its latency. */
		BasicVertexInfo sInfo = m_graph.getVertex(Index.valueOf(s));
		double cost = 0.0; 
		if (VertexFactory.isVertexInfo(sInfo))
			cost = ((VertexInfo)sInfo).getLatency();
		queue.add(new Pair<Index, Double>(Index.valueOf(s), new Double(cost)));

		while(!queue.isEmpty()) 
		{
			Pair<Index, Double> current = (Pair<Index, Double>) queue.poll();			
			double curCost = current.getValue2().doubleValue();			
			Index currentVertex = current.getValue1();

			if (curCost>m_dist[currentVertex.intValue()]) continue;

			beforeExpand(currentVertex);
			if(isExpandable(currentVertex)){			
				for (AbstractSimpleEdge<Index,BasicVertexInfo> e: m_graph.getOutgoingEdges(currentVertex))
				{
					Index v = e.getNeighbor(currentVertex) ;
					/** The latency weight of the vertex v. */
					double vCost = 0.0;
					BasicVertexInfo vInfo = m_graph.getVertex(v);
					if (VertexFactory.isVertexInfo(vInfo))
						vCost = ((VertexInfo)vInfo).getLatency();        		
					EdgeInfo<Index,BasicVertexInfo> eInfo = m_graph.getEdgeWeight(currentVertex, v);
					double dist = vCost + eInfo.getLatency();

					
					/** Relax */
					/** v found for the first time or a shorter path to v via current was found */
					if (Double.isNaN(m_dist[v.intValue()]) || m_dist[v.intValue()] > curCost + dist)
					{
						m_dist[v.intValue()] = curCost + dist;
						queue.add(new Pair<Index, Double>(v, m_dist[v.intValue()]));
						vertexDiscovered(currentVertex, v,e);
					}
					else
					{
						vertexRediscovered(currentVertex, v,e, curCost + dist);
					}

				}
			}

			afterExpand(currentVertex);
		}	
	}

	private class NodeComparator implements Comparator<Pair<Index, Double>>
	{
		public int compare(Pair<Index, Double> arg0, Pair<Index, Double> arg1)
		{
			return Double.compare(arg0.getValue2(), arg1.getValue2());
		}
	}

	@Override
	public double[] getDistanceArray() {
		return m_dist;
	}

	@Override
	public double getDistance(int intValue) {
		return m_dist[intValue];
	}

}
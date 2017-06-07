package algorithms.shortestPath;


import java.util.Arrays;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;

public class BFSAlgorithm extends AbstractShortestPathAlgorithm  
{
	
	
	protected Map<Integer, Double> m_dist = new FastMap<Integer, Double>();
	protected int m_nVertices = 0;
	protected double[] m_distArr;
	protected boolean m_maintainArray = true;
	GraphInterface<Index,BasicVertexInfo> m_graph;
	
	
	public BFSAlgorithm(GraphInterface<Index,BasicVertexInfo> graph){
		this(graph,true);
	}
	
	public BFSAlgorithm(GraphInterface<Index,BasicVertexInfo> graph, boolean maintainArray){
		if(maintainArray)
		{
			m_distArr = new double[graph.getNumberOfVertices()];
			Arrays.fill(m_distArr, Double.NaN);
			m_graph=graph;
		}
		m_maintainArray = maintainArray;
	}

	@Override
	public double[] getDistanceArray(){
		return m_distArr;
	}
	
	
	public void run(int s) 
	{		
		
		FastList<Index> queue = new FastList<Index>();
		m_dist.clear();
		if(m_maintainArray)
			Arrays.fill(m_distArr, Double.NaN);
		setDistance(s, 0);
		m_nVertices = m_graph.getNumberOfVertices();
		
		queue.addLast(Index.valueOf(s));
        while(queue.size() > 0)
        {
            Index v = (Index)queue.removeFirst();
            beforeExpand(v);   
            if(isExpandable(v)){
            	Iterable<? extends AbstractSimpleEdge<Index,BasicVertexInfo>> neighbors = m_graph.getOutgoingEdges(v);
            	for  (AbstractSimpleEdge<Index,BasicVertexInfo> e : neighbors)
            	{
            		Index w = e.getNeighbor(v);
            		/** w found for the first time? */
            		if (!m_dist.containsKey(w.intValue())){
            			setDistance(w.intValue(), getDistance(v.intValue())+1);
            			queue.addLast(w);
            			vertexDiscovered(v,w,e);
            		}
            		else {
            			vertexRediscovered(v,w,e,getDistance(v.intValue()) + 1);
            		}
            	}
            }
            afterExpand(v);
        }
	}

	public double getDistance(int v){
		if(!m_dist.containsKey(v))
			return Double.NaN;
		else
			return m_dist.get(v);
	}
	
	protected void setDistance(int v, double dist){
		if(m_maintainArray)
			m_distArr[v] = dist;
		m_dist.put(v, dist);
	}

}

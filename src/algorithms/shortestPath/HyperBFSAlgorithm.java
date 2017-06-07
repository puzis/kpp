package algorithms.shortestPath;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractHyperEdge;
import topology.BasicVertexInfo;
import topology.HyperGraphInterface;

public class HyperBFSAlgorithm extends AbstractShortestPathAlgorithm  
{
	
	
	protected int m_nVertices = 0;
	protected double[] m_vDistArr;
    protected Map<AbstractHyperEdge<Index,BasicVertexInfo>, Double> m_eDist;
    protected HyperGraphInterface<Index,BasicVertexInfo> m_graph;
    protected boolean m_recordEdgeDistances = true;

    
    protected long m_count_distance_checks = 0;
	
	public HyperBFSAlgorithm(HyperGraphInterface<Index,BasicVertexInfo> graph){
		this(graph,true);
	}
	
	public HyperBFSAlgorithm(HyperGraphInterface<Index,BasicVertexInfo> graph, boolean recordEdgeDistance){
		m_vDistArr = new double[graph.getNumberOfVertices()];
		Arrays.fill(m_vDistArr, Double.NaN);
		m_eDist = new HashMap<AbstractHyperEdge<Index,BasicVertexInfo>,Double>();
		m_graph = graph;
		m_recordEdgeDistances = recordEdgeDistance;
	}

	@Override
	public double[] getDistanceArray(){
		return m_vDistArr;
	}
	
	
	public void run(int s)
	{				
		FastList<Index> queue = new FastList<Index>();
		Arrays.fill(m_vDistArr, Double.NaN);
		setDistance(s, 0);
		m_nVertices = m_graph.getNumberOfVertices();
		m_eDist.clear();
		queue.addLast(Index.valueOf(s));
        while(queue.size() > 0)
        {
            Index v = (Index)queue.removeFirst();
            beforeExpand(v);   
            if(isExpandable(v)){
            	Iterable<? extends AbstractHyperEdge<Index,BasicVertexInfo>> neighbors = m_graph.getOutgoingEdges(v);
            	for  (AbstractHyperEdge<Index,BasicVertexInfo> e : neighbors)
            	{            		
            		if (!m_eDist.containsKey(e) || m_eDist.get(e)>=m_vDistArr[v.intValue()]){
            			if (m_recordEdgeDistances)
            				m_eDist.put(e, m_vDistArr[v.intValue()]);
	            		Iterable<Index> ws = e.getNeighbors(v);
	            		for (Index w :ws){
	            			
	            			m_count_distance_checks++;
	            			
		            		/** w found for the first time? */
		            		if (!(m_vDistArr[w.intValue()]>=0)){
		            			setDistance(w.intValue(), m_vDistArr[v.intValue()]+1);
		            			queue.addLast(w);
		            			vertexDiscovered(v,w,e);
		            		}
		            		else {
		            			vertexRediscovered(v,w,e,getDistance(v.intValue()) + 1);
		            		}
	            		}
            		}
            	}
            }
            afterExpand(v);
        }
	}

	public double getDistance(int v){
		return m_vDistArr[v];
	}
	
	protected void setDistance(int v, double dist){
		m_vDistArr[v] = dist;
	}

	public long getNumberOfDistanceChecks(){
		return m_count_distance_checks;
	}
	
}

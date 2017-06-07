package topology;

import java.util.Arrays;
import java.util.Collection;

import common.KeyValuePair;
import common.Pair;

import javolution.util.FastList;
import javolution.util.FastMap;

public class MultiWeightedHyperEdge<VertexType,VertexInfoStructure> extends HyperEdgeAsSet<VertexType,VertexInfoStructure> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	FastMap<Pair<VertexType,VertexType>,Double> m_latency;
	FastMap<VertexType, Double> m_maxlatency;
	FastMap<VertexType, Double> m_minlatency;
	FastMap<Pair<VertexType,VertexType>,Double> m_multiplicities;
	FastMap<VertexType,Object[]> m_adjacency;
	
	/**
	 * 
	 * 
	 * 
	 * @param vertices a set of vertices connected with each other
	 */
	public MultiWeightedHyperEdge(Iterable<VertexType> vertices) {
		super(vertices);
		m_latency= new FastMap<Pair<VertexType,VertexType>,Double>();
		m_maxlatency = new FastMap<VertexType,Double>();
		m_minlatency = new FastMap<VertexType,Double>();
		m_multiplicities=new FastMap<Pair<VertexType,VertexType>,Double>();
		m_adjacency = new FastMap<VertexType,Object[]>();
		for(VertexType u: m_vertices){
			Object[] adj = new Object[m_vertices.size()];
			int i=0;
			for(VertexType v: m_vertices){
				adj[i] = new KeyValuePair<Double,VertexType>(Double.POSITIVE_INFINITY, v);
				i++;
			}
			Arrays.sort(adj);
			m_adjacency.put(u, adj);
		}
	}
	
	
	public void setLatencies(VertexType s, Collection<Pair<VertexType,Double>> latencies){
		double maxlat = 0;
		double minlat = Double.POSITIVE_INFINITY;		
		for (Pair<VertexType,Double> tl: latencies){
			VertexType t = tl.getValue1();
			Pair<VertexType,VertexType> st = new Pair<VertexType,VertexType>(s,t);
			Double l = tl.getValue2();
			
			l = Math.min(l, m_latency.get(st));
			m_latency.put(st, l);
			
			maxlat = Math.max(l, maxlat);
			minlat = Math.min(l, minlat);
		}
		
		m_minlatency.put(s, minlat);
		m_maxlatency.put(s, maxlat);
		
		Object[] adj = m_adjacency.get(s);
		for (int i=0;i<adj.length;i++){
			KeyValuePair<Double,VertexType> lt = (KeyValuePair<Double,VertexType>)adj[i];
			VertexType t = lt.getValue2();
			Pair<VertexType,VertexType> st = new Pair<VertexType, VertexType>(s, t);
			lt.set(m_latency.get(st),t);
		}
		Arrays.sort(adj);
		
		assert minlat==((KeyValuePair<Double,VertexType>)adj[0]).getValue1().doubleValue();
		assert maxlat==((KeyValuePair<Double,VertexType>)adj[adj.length-1]).getValue1().doubleValue();
	}
	
	public double getLatency(VertexType v0, VertexType v1){
		return m_latency.get(new Pair<VertexType,VertexType>(v0,v1));
	}
	public double getMultiplicity(VertexType v0, VertexType v1){
		return m_multiplicities.get(new Pair<VertexType,VertexType>(v0,v1));
	}
	
	public double getMaxLatency(VertexType s){
		return m_maxlatency.get(s);
	}
	public double getMinLatency(VertexType s){
		return m_minlatency.get(s);
	}
	
	public Collection<VertexType> getNeighbors(VertexType s, double maxlatency) {
		FastList<VertexType> n = new FastList<VertexType>();
		Object[] adj = m_adjacency.get(s);
		int i = 0;
		double lat = 0;
		while(lat<=maxlatency){
			KeyValuePair<Double,VertexType> lt =(KeyValuePair<Double,VertexType>)adj[i];
			lat = lt.getValue1();
			if(lat<=maxlatency)
				n.add(lt.getValue2());
		}
		return n;
	}

}

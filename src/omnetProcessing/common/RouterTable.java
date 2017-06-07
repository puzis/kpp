package omnetProcessing.common;

import omnetProcessing.parsers.RTFileParser.RoutingTableEntry;
import common.Pair;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;

public class RouterTable {

	private int m_vertexNum = -1;
	private FastMap<Pair<Index, Index>, Double> m_rt = null;
	private FastMap<Pair<Index, Index>, FastMap<Double, Double>> m_metrics = null;
	
	public RouterTable(int vertexNum, FastList<Pair<RoutingTableEntry, Double>> weightedRTs){
		m_vertexNum = vertexNum;
		computeWeights(weightedRTs);
	}
	
	private void computeWeights(FastList<Pair<RoutingTableEntry, Double>> rts){
		m_rt = new FastMap<Pair<Index, Index>, Double>();
		m_metrics = new FastMap<Pair<Index, Index>, FastMap<Double, Double>>();
		
		for (FastList.Node<Pair<RoutingTableEntry, Double>> rtNode = rts.head(), rtEnd = rts.tail(); (rtNode = rtNode.getNext()) != rtEnd;) {
			
			Pair<RoutingTableEntry, Double> weightedRouterEntry = rtNode.getValue();
			RoutingTableEntry rtEntry = weightedRouterEntry.getValue1();
			Index destination = rtEntry.getDestination();
			Index nextHop = rtEntry.getNextHop();
			Integer metric = rtEntry.getMetric();
			Double tableWeight = weightedRouterEntry.getValue2();
			
			Pair<Index, Index> pair = new Pair<Index, Index>(destination, nextHop);
			
			if (m_metrics.get(pair)==null){
				m_metrics.put(pair, new FastMap<Double, Double>());
				m_metrics.get(pair).put(metric.doubleValue(), tableWeight);
			}
			else{
				if (m_metrics.get(pair).get(metric.doubleValue())==null){
					m_metrics.get(pair).put(metric.doubleValue(), tableWeight);
				}
				else{
					double weight = m_metrics.get(pair).get(metric.doubleValue());
					m_metrics.get(pair).put(metric.doubleValue(), weight+tableWeight);
				}
			}
			
			Double weight = m_rt.get(pair);  
		    if (weight==null){
		    	m_rt.put(pair, tableWeight);
		    }
		    else{
		      	double updatedWeight = weight.doubleValue() + tableWeight;
		      	m_rt.put(pair, updatedWeight);
		    }
	    }
	}
	
	public int getVertexNumber(){
		return m_vertexNum;
	}

	public FastList<Double> getMetrics(int nextHop, int destination){
		Pair<Index, Index> p = new Pair<Index, Index>(Index.valueOf(destination), Index.valueOf(nextHop));
		FastList<Double> metrics = new FastList<Double>();
		for (Double m : m_metrics.get(p).keySet()){
			metrics.add(m);
		}
		return metrics;
	}
	
	public double getWeight(int destination, int nextHop, double metric){
		Pair<Index, Index> p = new Pair<Index, Index>(Index.valueOf(destination), Index.valueOf(nextHop));
		if (m_rt.get(p)==null){
			return 0;
		}
		double w = m_metrics.get(p).get(metric)/m_rt.get(p);
		return w;
	}
	public double getWeight(int destination, int nextHop){
		Pair<Index, Index> p = new Pair<Index, Index>(Index.valueOf(destination), Index.valueOf(nextHop));
		if (m_rt.get(p)==null){
			return 0;
		}
		double w =m_rt.get(p);
		return w;
	}
}

package omnetProcessing.parsers;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import omnetProcessing.common.IPArray;
import omnetProcessing.common.IpToVertexIndexMap;
import omnetProcessing.common.Utils;


import common.Pair;

import server.common.LoggingManager;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;

public class RTFileParser {

	private FastMap<Index, Index> m_nextHops = null; 
	
	public RTFileParser(FastMap<Index, Index> nextHops) {
		m_nextHops = nextHops;
	}
	
	public FastList<Pair<RoutingTableEntry, Double>> readRoutingTable(InputStream input, Double weight){
		FastList<Pair<RoutingTableEntry, Double>> rtList = new FastList<Pair<RoutingTableEntry, Double>>();
		
		FastMap<Index, Integer> shortestMetric = new FastMap<Index, Integer>();
		FastMap<Index, Pair<Index, Integer>> rt = new FastMap<Index, Pair<Index,Integer>>();
		
		try{
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader reader = new BufferedReader(isr);
			
			String line = null;
			// dest:192.150.3.0  gw:*  mask:255.255.255.0  metric:1 if:eth0  DIRECT OSPF
			while ((line = reader.readLine())!=null){
				String[] tokens = line.split("[\\s\\t]+");
				
				// Ignore if not OSPF protocol
				if (!tokens[6].equals("OSPF"))
					continue;

				String dest = tokens[0].substring(tokens[0].indexOf(":")+1).trim();
				int[] destIP = Utils.parseIP(dest);
				
				// Ignore if the destination is not an end-host.
				if (!IpToVertexIndexMap.isHost(new IPArray(destIP)))
					continue;
				
				Index destVertexNum = IpToVertexIndexMap.getVertexNum(new IPArray(destIP));
				// If the destination is not a router but an end host then ignore.
				if (destVertexNum == null)
					continue;
				
				String metricStr = tokens[3].substring(tokens[3].indexOf(":")+1).trim();
				int metric = Integer.parseInt(metricStr);
				
				int shortest = -1;
				if (shortestMetric.get(destVertexNum) != null){
					shortest = shortestMetric.get(destVertexNum).intValue();
				}
				
				if (metric<shortest || shortest == -1){				
					String ethStr = tokens[4]; // Example: 'if:eth1'
					int eth = Integer.parseInt(ethStr.substring(ethStr.indexOf(":")+4));
					Index nextHopNeighbor = m_nextHops.get(Index.valueOf(eth));
					rt.put(destVertexNum, new Pair<Index, Integer>(nextHopNeighbor, metric));
					shortestMetric.put(destVertexNum, metric);
				}
			}
			isr.close();
			reader.close();
			
			for (FastMap.Entry<Index, Pair<Index, Integer>> rtEntry = rt.head(), rtEnd = rt.tail(); (rtEntry = rtEntry.getNext()) != rtEnd;) {
				Index destination = rtEntry.getKey();
				Pair<Index, Integer> nextHop = rtEntry.getValue();
				
				RoutingTableEntry routerEntry = new RoutingTableEntry(destination, nextHop.getValue1(), nextHop.getValue2());
				rtList.add(new Pair<RoutingTableEntry, Double>(routerEntry, weight));
			}
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "RTFileParser", "readRoutingTable", ex);
		}		
		return rtList;
	}
	
	public class RoutingTableEntry{
		private Index _destination = null;
		private Index _nextHop = null;
		private Integer _metric = 0;
		
		private RoutingTableEntry(Index destination, Index nextHop, Integer metric){
			_destination = destination;
			_nextHop = nextHop;
			_metric = metric;
		}
		
		public Index getDestination(){
			return _destination;
		}
		
		public Index getNextHop(){
			return _nextHop;
		}
		
		public Integer getMetric(){
			return _metric;
		}
	}
}

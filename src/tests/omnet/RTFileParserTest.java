package tests.omnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import omnetProcessing.common.IpToVertexIndexMap;
import omnetProcessing.parsers.RTFileParser;
import omnetProcessing.parsers.RouterFileParser;
import omnetProcessing.parsers.RTFileParser.RoutingTableEntry;

import common.Pair;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;
import junit.framework.TestCase;

public class RTFileParserTest extends TestCase{
	
	FastList<Pair<Index, Pair<Index, Integer>>> expected_routingTable = new FastList<Pair<Index,Pair<Index, Integer>>>();
	FastList<Pair<Index, Pair<Index, Integer>>> expected_fullRT = new FastList<Pair<Index,Pair<Index, Integer>>>();
	
	double expected_time = 5.105481883291;
	
	public void setUp(){
		/* Only three relevant record (those with end-host as destination).
         *
		 * dest:192.150.6.0  gw:*  mask:255.255.255.0  metric:1 if:eth0  DIRECT OSPF
         * dest:192.150.3.0  gw:*  mask:255.255.255.0  metric:2 if:eth1  DIRECT OSPF
         * dest:192.150.9.0  gw:*  mask:255.255.255.0  metric:2 if:eth2  DIRECT OSPF
		 */
//		expected_routingTable.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(1), new Pair<Index, Integer>(Index.valueOf(2), 2)));
		expected_routingTable.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(2), new Pair<Index, Integer>(Index.valueOf(2), 2)));
//		expected_routingTable.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(3), new Pair<Index, Integer>(Index.valueOf(2), 2)));
//		expected_routingTable.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(4), new Pair<Index, Integer>(Index.valueOf(2), 2)));
		expected_routingTable.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(5), new Pair<Index, Integer>(Index.valueOf(5), 1)));
//		expected_routingTable.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(6), new Pair<Index, Integer>(Index.valueOf(2), 2)));
		expected_routingTable.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(8), new Pair<Index, Integer>(Index.valueOf(8), 2)));
//		expected_routingTable.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(9), new Pair<Index, Integer>(Index.valueOf(9), 1)));
		
		
/*
 * <5, 5>, 
 * <5, 5>, 
 * <5, 5>, 
 * <5, 5>
 * <2, 2>, 
 * <8, 8>,
 * <9, 9>, 
 * <2, 2>, 
 * <1, 2>, 
 * <3, 2>, 
 * <4, 2>, 
 * <6, 2>, 
 * <8, 8>, 
 * 
 */
		/*
		 * dest:192.150.1.0  gw:*  mask:255.255.255.0  metric:4 if:eth1  DIRECT OSPF
         * dest:192.150.2.0  gw:*  mask:255.255.255.0  metric:3 if:eth1  DIRECT OSPF
         * dest:192.150.3.0  gw:*  mask:255.255.255.0  metric:2 if:eth1  DIRECT OSPF
         * dest:192.150.4.0  gw:*  mask:255.255.255.0  metric:3 if:eth1  DIRECT OSPF
         * dest:192.150.5.0  gw:*  mask:255.255.255.0  metric:3 if:eth1  DIRECT OSPF
         * dest:192.150.6.0  gw:*  mask:255.255.255.0  metric:1 if:eth0  DIRECT OSPF
         * dest:192.150.7.0  gw:*  mask:255.255.255.0  metric:3 if:eth1  DIRECT OSPF
         * dest:192.150.8.0  gw:*  mask:255.255.255.0  metric:3 if:eth2  DIRECT OSPF
         * dest:192.150.9.0  gw:*  mask:255.255.255.0  metric:2 if:eth2  DIRECT OSPF
         * dest:192.150.10.0  gw:*  mask:255.255.255.0  metric:2 if:eth3  DIRECT OSPF
		 */
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(0), new Pair<Index, Integer>(Index.valueOf(2), 4)));
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(1), new Pair<Index, Integer>(Index.valueOf(2), 3)));
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(2), new Pair<Index, Integer>(Index.valueOf(2), 2)));
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(3), new Pair<Index, Integer>(Index.valueOf(2), 3)));
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(4), new Pair<Index, Integer>(Index.valueOf(2), 3)));
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(5), new Pair<Index, Integer>(Index.valueOf(5), 1)));
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(6), new Pair<Index, Integer>(Index.valueOf(2), 3)));
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(7), new Pair<Index, Integer>(Index.valueOf(8), 3)));
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(8), new Pair<Index, Integer>(Index.valueOf(8), 2)));
		expected_fullRT.add(new Pair<Index, Pair<Index, Integer>>(Index.valueOf(9), new Pair<Index, Integer>(Index.valueOf(9), 2)));
		
	}
	
	public void testRatesParser() throws IOException{
		String dir = "data/omnet/3257_9/test/";
		FastMap<Index, Index> ifcMapping = null;
		for (int i=1; i<11; i++){
			RouterFileParser rParser = new RouterFileParser(dir + "R" + i + ".irt");
			if (i==6)
				ifcMapping = rParser.getInterfacesMapping();
		}
		IpToVertexIndexMap.markInitialized();
		assertEquals(4, ifcMapping.size());
		RTFileParser rtParser = new RTFileParser(ifcMapping);
		FastList<Pair<RoutingTableEntry, Double>> actual_routingTable = rtParser.readRoutingTable(new FileInputStream(new File(dir + "rt/R6_3.txt")), 1.0);
		assertEquals(expected_routingTable.size(), actual_routingTable.size());
		
		Iterator<Pair<RoutingTableEntry, Double>> actItr = actual_routingTable.iterator();
		while (actItr.hasNext()){
			Pair<RoutingTableEntry, Double> actualEntry = actItr.next();
			Index dest = actualEntry.getValue1().getDestination();
			Index hop = actualEntry.getValue1().getNextHop();
			Integer metric = actualEntry.getValue1().getMetric();
			
			Pair<Index, Pair<Index, Integer>> actualPair = new Pair<Index, Pair<Index, Integer>>(
					dest, new Pair<Index, Integer>(hop, metric));
			boolean contains = expected_routingTable.contains(actualPair);
			assertEquals(true,contains);
		}
		
		// Test full routing table of R6
		rtParser = new RTFileParser(ifcMapping);
		actual_routingTable = rtParser.readRoutingTable(new FileInputStream(new File(dir + "rt/R6_full.txt")), 1.0);
		assertEquals(expected_fullRT.size(), actual_routingTable.size());
		
		actItr = actual_routingTable.iterator();
		while (actItr.hasNext()){
			Pair<RoutingTableEntry, Double> actualEntry = actItr.next();
			Index dest = actualEntry.getValue1().getDestination();
			Index hop = actualEntry.getValue1().getNextHop();
			Integer metric = actualEntry.getValue1().getMetric();
			
			Pair<Index, Pair<Index, Integer>> actualPair = new Pair<Index, Pair<Index, Integer>>(
					dest, new Pair<Index, Integer>(hop, metric));
			boolean contains = expected_fullRT.contains(actualPair);
			assertEquals(true,contains);
		}
	}
}

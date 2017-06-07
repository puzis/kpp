package tests.omnet;

import java.text.DecimalFormat;

import java.text.NumberFormat;

import omnetProcessing.OmnetPreprocess;
import javolution.util.Index;

import server.common.DummyProgress;
import server.common.ServerConstants;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphFactory.GraphDataStructure;
import topology.GraphInterface;
import topology.SerializableGraphRepresentation;
import topology.graphParsers.GraphParserFactory;

public class RouterTableTest extends RTFileParserTest {

	private NumberFormat formatter = new DecimalFormat("0.000");
	
	public void testRouterTable(){
		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphDataStructure.GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR + "omnet/3257_9/test/", "omnet/3257_9/test/3257.onet", "", "onet", new DummyProgress(), 1, graph);
		GraphInterface<Index, BasicVertexInfo> g = GraphFactory.copyAsSimple(graph); 
		
		OmnetPreprocess omnet = new OmnetPreprocess("data/omnet/3257_9/test/", g);
		omnet.init(11.0);
		double p1 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 9, 8);
		assertEquals(0.0, p1);
		double p2 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 7, 2);
		assertEquals(0.0, p2);
		double p3 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 5, 5);
		assertEquals(1.0, p3);
		double p4 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 8, 8);
		assertEquals(1.0, p4);
		double p5 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 2, 6);
		assertEquals(1.0, p5);
		double p6 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 2, 2);
		assertEquals(1.0, p6);
		
		double p7 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 8, 7);
		assertEquals(formatter.format(1), formatter.format(p7));
		double p8 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 9, 7);
		assertEquals(formatter.format(0), formatter.format(p8));
		double p9 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 8, 9);
		assertEquals(formatter.format(0), formatter.format(p9));
		double p10 = omnet.getOSPFRoutingFunction().routingProbability(0, 5, 9, 9);
		assertEquals(formatter.format(1), formatter.format(p10));
/*
 * R6_3.txt + R6_full.txt =  
 * <5, 5>, 
 * <5, 5>, 
 * <5, 5>, 
 * <5, 5>
 * <5, 5>
 * <5, 5>, 
 * <5, 5>, 
 * <5, 5>, 
 * <5, 5>
 * 
 * <0, 2>
 * <0, 2>
 * <1, 2>
 * <1, 2>, 
 * <1, 2>
 * <1, 2>, 
 * <2, 2>, 
 * <2, 2>, 
 * <2, 2>
 * <2, 2>
 * <2, 2>
 * <2, 2>
 * <2, 2>, 
 * <2, 2>, 
 * <3, 2>,
 * <3, 2>,  
 * <3, 2>
 * <4, 2>, 
 * <4, 2>
 * <4, 2>, 
 * <6, 2>, 
 * <6, 2>
 * <6, 2>, 
 * <7, 8>
 * <7, 8>
 * <7, 9>
 * <8, 8>
 * <8, 8>, 
 * <8, 8>,
 * <8, 8>,
 * <8, 8>, 
 * <9, 8>
 * <9, 9>, 
 * <9, 9>
 * <9, 9>, 
 */		
	}
}

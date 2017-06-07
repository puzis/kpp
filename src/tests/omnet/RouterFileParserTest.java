package tests.omnet;

import omnetProcessing.common.IPArray;

import omnetProcessing.parsers.RouterFileParser;
import javolution.util.FastMap;
import javolution.util.Index;
import junit.framework.TestCase;

public class RouterFileParserTest extends TestCase{
	
	FastMap<Index, Index> expected_ifcMapping = new FastMap<Index, Index>();
	int[] expected_ip = {192,168,0,8};
	
	FastMap<IPArray, Index> expected_ipToVertexMapping = new FastMap<IPArray, Index>();

	public void setUp(){
		expected_ifcMapping.put(Index.valueOf(0), Index.valueOf(2));
		expected_ifcMapping.put(Index.valueOf(1), Index.valueOf(1));
		expected_ifcMapping.put(Index.valueOf(2), Index.valueOf(3));
		expected_ifcMapping.put(Index.valueOf(3), Index.valueOf(4));
		expected_ifcMapping.put(Index.valueOf(4), Index.valueOf(5));
		expected_ifcMapping.put(Index.valueOf(5), Index.valueOf(6));
		
		// R3: 192.150.3.1, 192.150.3.0, 192.168.0.4, 192.168.0.5, 192.168.0.6, 192.168.0.7, 192.168.0.8
		// R6: 192.150.6.1, 192.150.6.0, 192.168.0.11, 192.168.0.12, 192.167.0.1
		Index r3 = Index.valueOf(2);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,150,3,1}), r3);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,150,3,0}), r3);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,168,0,4}), r3);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,168,0,5}), r3);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,168,0,6}), r3);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,168,0,7}), r3);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,168,0,8}), r3);
		
		Index r6 = Index.valueOf(5);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,150,6,1}), r6);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,150,6,0}), r6);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,168,0,11}), r6);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,168,0,12}), r6);
		expected_ipToVertexMapping.put(new IPArray(new int[]{192,167,0,1}), r6);
	}
	
	public void testRouterParser(){
		RouterFileParser parser = new RouterFileParser("data/omnet/3257_9/test/R3.irt");
		assertEquals(3-1, parser.getVertexNum()); // The base in omnet nets is 1.
		FastMap<Index, Index> actual_mapping = parser.getInterfacesMapping();
		assertEquals(6, actual_mapping.size());
		for (int i=0; i<6; i++){
			Index expected = expected_ifcMapping.get(Index.valueOf(i));
			Index actual = actual_mapping.get(Index.valueOf(i));
			assertEquals(expected.intValue(), actual.intValue());
		}
		int[] actual_ip = parser.getIP();
		for (int i=0; i<4; i++){
			assertEquals(expected_ip[i], actual_ip[i]);
		}
	}
}
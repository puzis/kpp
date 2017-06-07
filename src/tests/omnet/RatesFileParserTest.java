package tests.omnet;

import omnetProcessing.parsers.RatesFileParser;
import junit.framework.TestCase;

public class RatesFileParserTest extends TestCase{
	
	double[] expected_rates = {67.81510914897069, 36.360670190929, 
			21.490489261412673, 118.28317993248311, 
			73.08586490214688, 0.2733035756635458, 
			113.09993500949815, 0.0, 105.71871261635772, 105.71871261635772};

	public void testRatesParser(){
		RatesFileParser parser = new RatesFileParser("data/omnet/3257_9/test/rte/H8.rte", 10);
		assertEquals(8-1, parser.getVertexNum()); // The base in omnet nets is 1.
		double[] actual_rates = parser.getRates();
		assertEquals(10, actual_rates.length);
		for (int i=0; i<10; i++){
			assertEquals(expected_rates[i], actual_rates[i]);
		}
	}
}

package omnetProcessing;

import omnetProcessing.parsers.RatesFileParser;

public class RatesLoader {

	private String _dir = "";
	private int _numberOfVertices = 0;
	
	public RatesLoader(String dir, int numberOfVertices){
		_dir = dir;
		_numberOfVertices = numberOfVertices;
	}
	
	public double[][] loadRates(){
		double[][] rates = new double[_numberOfVertices][_numberOfVertices];
		
		for (int i=1; i<=_numberOfVertices; i++){
			RatesFileParser ratesParser = new RatesFileParser(_dir + "H" + i + ".rte", _numberOfVertices);
			rates[i-1] = ratesParser.getRates();
		}
		return rates;
	}
}

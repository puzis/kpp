package omnetProcessing;

import omnetProcessing.parsers.TrafficMatrixFileParser;

public class TrafficMatrixLoader {

	private String _dir = "";
	private int _numberOfVertices = 0;
	
	public TrafficMatrixLoader(String dir, int numberOfVertices){
		_dir = dir;
		_numberOfVertices = numberOfVertices;
	}
	
	public double [][] loadTrafficMatrix(){
		double[][] tm;
		TrafficMatrixFileParser tmParser = new TrafficMatrixFileParser(_dir, _numberOfVertices);
		tm = tmParser.parse();
		return tm;
	}
}

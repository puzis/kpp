package omnetProcessing;

import java.util.Iterator;


import omnetProcessing.common.RouterTable;
import omnetProcessing.parsers.NetflowFileParser;
import omnetProcessing.parsers.TrafficMatrixFileParser;

import common.MatricesUtils;
import common.ShadowedHistoriedCacheArr;

import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphFactory.GraphDataStructure;
import topology.GraphInterface;
import topology.SerializableGraphRepresentation;
import topology.graphParsers.GraphParserFactory;
import algorithms.centralityAlgorithms.rbc.FasterGRBC;
import algorithms.centralityAlgorithms.rbc.GreedyContributionRBC;
import algorithms.centralityAlgorithms.rbc.NetFlowCoverage;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;
import algorithms.centralityAlgorithms.rbc.routingFunction.OSPFFunction;
import algorithms.centralityAlgorithms.rbc.sets.DynamicRBCSet;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.tmEstimation.NetFlow;
import algorithms.tmEstimation.TrafficMatrixEstimator;
import javolution.util.FastMap;
import javolution.util.Index;

public class OmnetPreprocess {

	private GraphInterface<Index, BasicVertexInfo> _graph = null;
	
	private FastMap<Index, Index>[] _ifcMaps = null;
	private RouterTable[] _rts = null;
	private NetFlow _flows = null;
	
	private AbsRoutingFunction m_ospfRouting = null;
	private double[][] _estimatedTM = null;
	
	private String _dir = null;
	private String _netflowDir = "netflow/";
	private String _ratesDir = "rte/";
	private String _tmDir = "tm/";
	private String _rtDir = "rt/";
	
	@SuppressWarnings("unchecked")
	public OmnetPreprocess(String dir, GraphInterface<Index, BasicVertexInfo> graph){
		_dir = dir;
		_graph = graph;
	}
	
	public void init(double threshold){
		RoutersConfigsLoader irtLoader = new RoutersConfigsLoader(_dir, _graph.getNumberOfVertices());
		_ifcMaps =  irtLoader.loadRouterInterfaces();
		
		RoutersTablesLoader tablesLoader = new RoutersTablesLoader(_dir + _rtDir, _graph.getNumberOfVertices(), _ifcMaps);
		_rts = tablesLoader.loadRouterTables(threshold);
		
		NetflowFileParser netFlowParser = new NetflowFileParser(_dir + _netflowDir);
		_flows = netFlowParser.getNetFlow();
		
		m_ospfRouting = new OSPFFunction(_rts);

		//=============END OF DEBUG==============
//		m_ospfRouting = new ShortestPathRoutingFunction(graph);
		//=============END OF DEBUG==============
	}
	
	public double compareEstimatedTMandActualTM(double confidenceThreshold, int numberOfInspectionPoints){
		_estimatedTM = estimateTM(_dir + _netflowDir, _graph, m_ospfRouting, confidenceThreshold, numberOfInspectionPoints);
		
//		double[][] actualTM = loadTrafficMatrix(_dir + _tmDir, _graph.getNumberOfVertices());
//		double error = MatricesUtils.getPearsonCorrelation(_estimatedTM, actualTM);
		double[][] rates = loadRates(_dir + _ratesDir, _graph.getNumberOfVertices());
		double error = MatricesUtils.getPearsonCorrelation(_estimatedTM, rates);
		return error;
	}
	
	private double [][] loadRates(String dir, int numberOfVertices){
		double[][] rates;
		RatesLoader ratesLoader = new RatesLoader(dir, numberOfVertices);
		rates = ratesLoader.loadRates();
		return rates;
	}
	
	private double [][] loadTrafficMatrix(String dir, int numberOfVertices){
		double[][] tm;
		TrafficMatrixFileParser tmParser = new TrafficMatrixFileParser(dir, numberOfVertices);
		tm = tmParser.parse();
		return tm;
	}
	
	public double[][] estimateTM(double confidenceThreshold, int numberOfInspectionPoints){
		return estimateTM(_dir + _netflowDir, _graph, m_ospfRouting, confidenceThreshold, numberOfInspectionPoints);
	}
	
	public double[][] estimateTM(double confidenceThreshold, int numberOfInspectionPoints, AbsRoutingFunction rf){
		return estimateTM(_dir + _netflowDir, _graph, rf, confidenceThreshold, numberOfInspectionPoints);
	}
	
	private double[][] estimateTM(String dir, GraphInterface<Index, BasicVertexInfo> graph, 
			AbsRoutingFunction rf, double confidenceThreshold, int numberOfInspectionPoints){
		
		try{
			int[] unusedCandidates = new int[graph.getNumberOfVertices()];
			int i=0;
			Iterator<Index> vItr = graph.getVertices().iterator();
			while (vItr.hasNext()){
				unusedCandidates[i] = vItr.next().intValue();
				i++;
			}
			FasterGRBC rbcAlg = new FasterGRBC(graph, rf, new DefaultTrafficMatrix(graph.getNumberOfVertices()), null, ShadowedHistoriedCacheArr.CACHEID) ;
			
			NetFlowCoverage deplAlg = new NetFlowCoverage(graph, rf, new DefaultTrafficMatrix(graph.getNumberOfVertices()), 
					null, ShadowedHistoriedCacheArr.CACHEID, confidenceThreshold) ;
			
			DynamicRBCSet set = new DynamicRBCSet(deplAlg);
			
			Index[] winners = GreedyContributionRBC.findVertices(set, numberOfInspectionPoints, new int[0], unusedCandidates, new DummyProgress(), 1.0);
			
			
			TrafficMatrixEstimator tmEstimator = new TrafficMatrixEstimator(_flows, winners, 
					graph.getNumberOfVertices(), null);
			
			_estimatedTM = tmEstimator.getEstimatedTM();
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "OmnetPreprocess", "estimateTM", ex);
		}
		return _estimatedTM;
	}
	
	public double[][] getEstimatedTM(){
		return _estimatedTM;
	}
	
	public AbsRoutingFunction getOSPFRoutingFunction(){
		return m_ospfRouting;
	}
	
	public static void main(String[] args) {
		String[] nets = {"3257_9", "3257_10", "1755", "4755"};
//		String[] nets = {"3257_10"};
		for (String net:nets){
			processNet(net);
		}
	}
	
	private static void processNet(String net){
		System.out.println(net);
		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphDataStructure.GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR + "omnet/" + net + "/", "omnet/" + net + "/" + net + ".onet", "", "onet", new DummyProgress(), 1, graph);
		GraphInterface<Index, BasicVertexInfo> g = GraphFactory.copyAsSimple(graph);
		
		OmnetPreprocess omnet = new OmnetPreprocess("data/omnet/" + net + "/", g);
		omnet.init(5.0);
		
		int numOfInsPoints = (int) Math.ceil(graph.getNumberOfVertices()/(double)10); 
		System.out.println(numOfInsPoints);
		System.out.println(omnet.compareEstimatedTMandActualTM(0.4, numOfInsPoints));
		
		numOfInsPoints = (int) Math.ceil(graph.getNumberOfVertices()/(double)3);
		System.out.println(numOfInsPoints);
		System.out.println(omnet.compareEstimatedTMandActualTM(0.4, numOfInsPoints));
		
		numOfInsPoints = graph.getNumberOfVertices();
		System.out.println(numOfInsPoints);
		System.out.println(omnet.compareEstimatedTMandActualTM(0.4, numOfInsPoints));
	}
}

package server.mobility;

import javolution.util.Index;
import server.common.DataBase;
import server.common.LoggingManager;
import server.shortestPathBetweenness.BrandesController;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.CongestionAwareMobilityBetweenness;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

public class CongestionAwareBCController {
	public static final String ALIAS = "MobilityBC";

	public int updateNetwork(int netID, int tmID, int steps, double fearFactor,
			double cf0, double cf1, double cf2, double cf3, double cf6) {
		
		return updateNetwork(netID, tmID, steps, fearFactor,
				cf0,cf1, cf2, cf3, cf6,
				null);
		
	}
	public int updateNetwork(int netID, int tmID, int steps, double fearFactor,
			double cf0, double cf1, double cf2, double cf3, double cf6,
			Object[] emissions) {
		CongestionAwareMobilityBetweenness alg = null;
		
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		if (graph == null){
			LoggingManager.getInstance().writeSystem("Graph is NULL.", CongestionAwareBCController.ALIAS, "create", null);
			return -1;
		}
		
		AbsTrafficMatrix tm = DataBase.getTrafficMatrix(tmID);
		if (tm == null){
			LoggingManager.getInstance().writeSystem("Traffic Matrix is NULL.", CongestionAwareBCController.ALIAS, "create", null);
			return -1;
		}
			
//		Index[] idxSources = new Index[sources.length];
//		for (int i=0; i<sources.length; i++){
//			idxSources[i] = Index.valueOf((Integer)sources[i]);
//		}

		if (emissions==null) emissions=new Object[0];
		double[] emissionsArr = new double[emissions.length];
		for (int i = 0; i < emissions.length; i++){
			emissionsArr[i] = ((Double)emissions[i]).doubleValue();
		}

		
		try{
			alg = new CongestionAwareMobilityBetweenness(graph, tm , steps, fearFactor, cf0, cf1, cf2, cf3, cf6, emissionsArr);
			alg.run();
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem("An exception has occured while creating Brandes:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), BrandesController.ALIAS, "create", ex);
		}
		//int algID = DataBase.putAlgorithm(alg, netID);
		//return algID;
		return netID;
	}
	
}

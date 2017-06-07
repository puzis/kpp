package server.shortestPathBetweenness;

import javolution.util.Index;
import server.common.DataBase;
import server.common.LoggingManager;
import topology.BasicVertexInfo;
import topology.HyperGraphInterface;
import algorithms.centralityAlgorithms.betweenness.CentralityAlgorithmInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.HyperBrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.HyperBrandesBCWithBackPhase;
import algorithms.centralityAlgorithms.betweenness.brandes.HyperBrandesBCWithMerging;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class HyperBrandesController{
	public static final String ALIAS = "HyperBrandes";
	public static enum HyperBrandesType{NA,F,FB,MF,MFB,M};
	public static HyperBrandesType algorithm;
	
	public int create(int net_id, String type) {
		return create(net_id,-1,HyperBrandesType.valueOf(type), null,false);
	}
	public int create(int net_id, int tm_id, String type) {
		return create(net_id,tm_id,HyperBrandesType.valueOf(type), null, false);
	}	
	public int create(int net_id, String type, Object[] sources) {
		return create(net_id,-1,HyperBrandesType.valueOf(type), sources, false);
	}
	public int create(int net_id, String type, boolean delayRun) {
		return create(net_id, -1, HyperBrandesType.valueOf(type), null, delayRun);
	}
	public int create(int net_id, int tm_id, String type, Object[] sources) {
		return create(net_id,tm_id,HyperBrandesType.valueOf(type), sources, false);
	}
	public int create(int net_id, int tm_id, String type, boolean delayRun) {
		return create(net_id, tm_id, HyperBrandesType.valueOf(type), null, delayRun);
	}
	public int create(int net_id, String type, Object[] sources, boolean delayRun) {
		return create(net_id, -1, HyperBrandesType.valueOf(type), sources, delayRun);
	}
	public int create(int net_id, int tm_id, String type, Object[] sources, boolean delayRun) {
		return create(net_id, tm_id, HyperBrandesType.valueOf(type), sources, delayRun);		
	}
	protected int create(int net_id, int tm_id, HyperBrandesType type, Object[] sources, boolean delayRun) {	
		HyperGraphInterface<Index,BasicVertexInfo> hgraph = DataBase.getNetwork(net_id).getGraph();

		if (hgraph == null){
			LoggingManager.getInstance().writeSystem("HyperGraph is NULL.", HyperBrandesController.ALIAS, "create", null);
			return -1;
		}
				
		AbsTrafficMatrix tm;
		if (tm_id != -1) {
			if(type==HyperBrandesType.MF || type==HyperBrandesType.MFB) {
				throw new IllegalStateException("MF, MFB do not support arbitrary Traffic Matrix");
			}
			tm = DataBase.getTrafficMatrix(tm_id);
		}
		else {			
			tm = new DefaultTrafficMatrix(hgraph.getNumberOfVertices());
		}
		
		Index[] idxSources = convertSources(sources);
				
		switch(type) {
		case NA://No speedup heuristics
			return createBrandes(net_id, hgraph, tm, idxSources, delayRun, ShortestPathAlg.BFS, null);
		case F: //Only forward phase uses hypergraphs- Run HyperBrandesBC
			return createBrandes(net_id, hgraph, tm, idxSources, delayRun, ShortestPathAlg.HYPERBFS, null);
		case FB: //Forward and backward phases use hypergraphs - Run HyperBrandesBCWithBackPhase
			return createBrandesWithBackPhase(net_id, hgraph,tm, idxSources, delayRun);
		case MF: //Equivalent vertices are merged and forward phase using hypergraphs - Run HyperBrandesBCWithMerging
			return createBrandesWithMerging(net_id, hgraph, false, idxSources, delayRun);
		case MFB: //Equivalent vertices are merged and both phases use hypergraphs
			return createBrandesWithMerging(net_id, hgraph, true, idxSources, delayRun);
		}
		return 0;
	}
	
	protected int create(int net_id, int tm_id, String type, Object[] sources, boolean delayRun, Object[] group) {
		return create(net_id, tm_id, HyperBrandesType.valueOf(type), sources, delayRun, group);
	}
	protected int create(int net_id, int tm_id, HyperBrandesType type, Object[] sources, boolean delayRun, Object[] group) {	
		HyperGraphInterface<Index,BasicVertexInfo> hgraph = DataBase.getNetwork(net_id).getGraph();

		if (hgraph == null){
			LoggingManager.getInstance().writeSystem("HyperGraph is NULL.", HyperBrandesController.ALIAS, "create", null);
			return -1;
		}
				
		AbsTrafficMatrix tm;
		if (tm_id != -1) {
			if(type==HyperBrandesType.MF || type==HyperBrandesType.MFB) {
				throw new IllegalStateException("MF, MFB do not support arbitrary Traffic Matrix");
			}
			tm = DataBase.getTrafficMatrix(tm_id);
		}
		else {			
			tm = new DefaultTrafficMatrix(hgraph.getNumberOfVertices());
		}
		
		Index[] idxSources = convertSources(sources);
		Index[] idxGroup = convertSources(group);
		if(type == HyperBrandesType.F) {
			return createBrandes(net_id, hgraph, tm, idxSources, delayRun, ShortestPathAlg.HYPERBFS, idxGroup);
		}
		else {
			System.out.println("Invalid : Cannot use"+ type +" with GBC");
			return 0;
		}		
	}
	
	protected Index[] convertSources(Object[] sources) {
		if(sources == null)
			return null;
		Index[] idxSources = new Index[sources.length];
		for (int i=0; i<sources.length; i++){
			idxSources[i] = Index.valueOf((Integer)sources[i]);
		}
		return idxSources;
	}
	
	/**
	 * Sets the sources of Algorithm alg_id
	 * @param alg_id
	 * @return 0 if successful, -1 otherwise
	 */
	public int setSources(int alg_id, Object[] sources) {
		try{
			Index[] idxSources = convertSources(sources);
			BrandesInterface hbrandes = (BrandesInterface)DataBase.getAlgorithm(alg_id);
			hbrandes.setSources(idxSources);
		}
		catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	/**
	 * Used to merge in the MF and MFB algorithms
	 * Should not be used if create() is called with F or FB algorithm type
	 * @param alg_id
	 * @return
	 */
	public int merge(int alg_id) {
		try {
			BrandesInterface hbrandes = (BrandesInterface)DataBase.getAlgorithm(alg_id);
			if(hbrandes instanceof HyperBrandesBCWithMerging) {
				((HyperBrandesBCWithMerging) hbrandes).merge();
			}
			else {
				throw new IllegalStateException("Can only merge on M, MF and MFB algorithms");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	/**
	 * Used to run the algorithm if it wasn't run during creation or if sources were changed
	 * @param alg_id
	 * @return 0 if successful, -1 otherwise
	 */
	public int run(int alg_id) {
		try {
		BrandesInterface hbrandes = (BrandesInterface)DataBase.getAlgorithm(alg_id);
		hbrandes.run();
		}
		catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	
	public int getNumberOfVertices(int algID){
		BrandesInterface pu = (BrandesInterface)DataBase.getAlgorithm(algID);
		return pu.getGraph().getNumberOfVertices();
	}
	
	public int getNumberOfEdges(int algID){
		BrandesInterface pu = (BrandesInterface)DataBase.getAlgorithm(algID);
		return pu.getGraph().getNumberOfEdges();
	}
	
	public double getNumberOfDiscovered(int algID) {
		BrandesInterface alg = (BrandesInterface)DataBase.getAlgorithm(algID);
		return alg.getShortestPathAlgorithm().getNumberOfDiscovered();
	}

	public double getNumberOfRediscovered(int algID) {
		BrandesInterface alg = (BrandesInterface)DataBase.getAlgorithm(algID);
		return alg.getShortestPathAlgorithm().getNumberOfRediscovered();
	}

	public double getNumberOfExpanded(int algID) {
		BrandesInterface alg = (BrandesInterface)DataBase.getAlgorithm(algID);
		return alg.getShortestPathAlgorithm().getNumberOfExpanded();
	}

	public int resetCounters(int algID) {
		BrandesInterface alg = (BrandesInterface)DataBase.getAlgorithm(algID);
		alg.getShortestPathAlgorithm().resetCounters();
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private int createBrandes(int net_id, HyperGraphInterface<Index,BasicVertexInfo> hgraph, AbsTrafficMatrix communicationWeights, Index[] sources, boolean delayRun, ShortestPathAlg spalg, Index[] group) {
		HyperBrandesBC hbrandes = null;
		try{
			hbrandes = new HyperBrandesBC(hgraph, communicationWeights, sources, spalg, group); 
			if(!delayRun)
				hbrandes.run();
//			System.out.println("F : Forward: "+hbrandes.getForwardCount()+", Back: "+hbrandes.getBackCount());
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem("An exception has occured while creating HyperBrandes:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), HyperBrandesController.ALIAS, "create", ex);
		}
		int alg_id = DataBase.putAlgorithm(hbrandes, net_id);
		return alg_id;
	}
	private int createBrandesWithMerging(int net_id, HyperGraphInterface<Index,BasicVertexInfo> hgraph, boolean usingBack, Index[] sources, boolean delayRun) {
		HyperBrandesBCWithMerging hbrandes = null;
		try {
		hbrandes = new HyperBrandesBCWithMerging(hgraph,usingBack, sources);
		if(!delayRun) {
			hbrandes.merge();
			hbrandes.run();
		}
//		if(usingBack)
//			System.out.println("MFB : Forward: "+hbrandes.getForwardCount()+", Back: "+hbrandes.getBackCount());
//		else 
//			System.out.println("MF : Forward: "+hbrandes.getForwardCount()+", Back: "+hbrandes.getBackCount());
		}	
		catch(Exception e) {
			LoggingManager.getInstance().writeSystem("An exception has occured while creating HyperBrandesBCWithMerging:\n"+e.getMessage()+"\n"+LoggingManager.composeStackTrace(e), HyperBrandesController.ALIAS, "createBrandesWithMerging", e);
		}
		int alg_id = DataBase.putAlgorithm(hbrandes, net_id);
		return alg_id;
	}
	private int createBrandesWithBackPhase(int net_id, HyperGraphInterface<Index,BasicVertexInfo> hgraph, AbsTrafficMatrix communicationWeights, Index[] sources, boolean delayRun) {
		HyperBrandesBCWithBackPhase hbrandes = null;
		try{
			hbrandes = new HyperBrandesBCWithBackPhase(hgraph, communicationWeights, sources); 
			if(!delayRun)
				hbrandes.run();
//			System.out.println("FB : Forward: "+hbrandes.getForwardCount()+", Back: "+hbrandes.getBackCount());
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem("An exception has occured while creating HyperBrandesWithBackPhase:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), HyperBrandesController.ALIAS, "createBrandesWithBackPhase", ex);
		}
		int alg_id = DataBase.putAlgorithm(hbrandes, net_id);
		return alg_id;
	}
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}
	
	public double getGBC(int algID){
		HyperBrandesBC pu = (HyperBrandesBC)DataBase.getAlgorithm(algID);
		return pu.getGBC();
	}
	
}

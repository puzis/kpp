package server.shortestPathBetweenness;

import java.util.Arrays;
import java.util.Set;

import javolution.util.FastSet;
import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import server.execution.AbstractExecution;
import server.shortestPathBetweenness.executions.BrandesBCExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.TrafficMatrixBC;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;


public class BrandesController implements BrandesAPI {
	public static final String ALIAS = "Brandes";
	

	@Override
    public int create(int netID, String communicationWeightsStr){
    	return create(netID, communicationWeightsStr, ShortestPathAlgorithmInterface.DEFAULT.name());
    }

	/** Creates Brandes algorithm with the given network and communication weights (the weights may also be an empty String or null).
	 * @param network index
	 * @param communication weights String
	 * @return Index of the algorithm in the Database */
	@Override
    public int create(int netID, String communicationWeightsStr, String shortestPathAlg){
		
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		AbsTrafficMatrix communicationWeights = null;
		
		if (graph == null){
			LoggingManager.getInstance().writeSystem("Graph is NULL.", BrandesController.ALIAS, "create", null);
			return -1;
		}
		
		if (communicationWeightsStr != null && !communicationWeightsStr.isEmpty())
			communicationWeights = new DenseTrafficMatrix(communicationWeightsStr, graph.getNumberOfVertices());
		else
			communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices());
		
		
		return createBrandes(netID, graph, communicationWeights, null, ShortestPathAlgorithmInterface.ShortestPathAlg.valueOf(shortestPathAlg),false, null); 
	}

	/** Default traffic matrix is assumed. */
	@Override
    public int create(int netID){
		return create(netID, -1); 
	}
	public int create(int netID, boolean delayExecution) {
		return create(netID, -1, delayExecution);
	}
	
	@Override
    public int create(int netID, int tmID){
		return create(netID,tmID,false);
	}
	
    public int create(int netID, int tmID, boolean delayExecution){
    	return create(netID, tmID, null, ShortestPathAlgorithmInterface.DEFAULT.name(),delayExecution);
	}	
	
	@Override
    public int create(int netID, int tmID, Object[] sources){
		return create(netID,tmID,sources, ShortestPathAlgorithmInterface.DEFAULT.name());	
	}
	
	
	/**
	 * Same as create(int, int, Object[]) but shortest path algorithm implementation can be selected.
	 * 
	 * When using DIJKSTRA, custom property "latency" of edges and vertices is used to guide the algorithm.
	 * If an edge has no latency defined unity (1) is assumed.
	 * If a vertex has no latency defined zero (0) is assumed.
	 *  
	 * @param netID
	 * @param tmID
	 * @param sources
	 * @param shortestPathAlg BFS or DIJKSTRA. 
	 * @return the algorithm handler
	 */
	@Override
    public int create(int netID, int tmID, Object[] sources, String shortestPathAlg){
		return create(netID,tmID,sources,shortestPathAlg, false);
	}

	
	/**
	 * Same as create(int, int, Object[], String) but you can choose whether to run the algorithm or not.
	 * If delayExecution is set top true the method run() must be invoked before attempting to retrieve 
	 * any results from the algorithm.   
	 *  
	 * @param netID
	 * @param tmID
	 * @param sources
	 * @param shortestPathAlg BFS or DIJKSTRA. 
	 * @return the algorithm handler
	 */
	@Override
    public int create(int netID, int tmID, Object[] sources, String shortestPathAlg, boolean delayExecution){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		if (graph == null){
			LoggingManager.getInstance().writeSystem("Graph is NULL.", BrandesController.ALIAS, "create", null);
			return -1;
		}
		
		AbsTrafficMatrix tm;
		if (tmID != -1)
			tm = DataBase.getTrafficMatrix(tmID);
		else
			tm = new DefaultTrafficMatrix(graph.getNumberOfVertices());
		
		Index[] idxSources = convertSources(sources);		
		return createBrandes(netID, graph, tm, idxSources, ShortestPathAlg.valueOf(shortestPathAlg),delayExecution, null); 
	}

	
	/**
	 * Same as create(int, int, Object[], String, boolean) 
	 * but getGBC() will return the GBC of the group.
	 *  
	 * @param netID
	 * @param tmID
	 * @param sources
	 * @param shortestPathAlg BFS or DIJKSTRA. 
	 * @return the algorithm handler
	 */
    public int create(int netID, int tmID, Object[] sources, String shortestPathAlg, boolean delayExecution, Object[] group){
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		if (graph == null){
			LoggingManager.getInstance().writeSystem("Graph is NULL.", BrandesController.ALIAS, "create", null);
			return -1;
		}
		
		AbsTrafficMatrix tm;
		if (tmID != -1)
			tm = DataBase.getTrafficMatrix(tmID);
		else
			tm = new DefaultTrafficMatrix(graph.getNumberOfVertices());
		
		Index[] idxSources = convertSources(sources);
		Index[] idxGroup = convertSources(group);
		return createBrandes(netID, graph, tm, idxSources, ShortestPathAlg.valueOf(shortestPathAlg),delayExecution, idxGroup); 
	}
	
	
	
	protected Index[] convertSources(Object[] sources) {
		Index[] idxSources; 
		if (sources!= null)		
			idxSources = new Index[sources.length];
		else
			idxSources = new Index[0];
			
		for (int i=0; i<idxSources.length; i++){
			idxSources[i] = Index.valueOf((Integer)sources[i]);
		}
		return idxSources;
	}

	private int createBrandes(int netID, GraphInterface<Index,BasicVertexInfo> graph, AbsTrafficMatrix communicationWeights, Index[] sources, ShortestPathAlg spAlg, boolean delayExecution, Index[] idxGroup) {
		TrafficMatrixBC brandes = null;
		try{			
			
			brandes = new TrafficMatrixBC(spAlg, graph, communicationWeights, sources, idxGroup, new DummyProgress(), 1);
			if (!delayExecution)
				brandes.run();
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem("An exception has occured while creating Brandes:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), BrandesController.ALIAS, "create", ex);
		}
		int algID = DataBase.putAlgorithm(brandes, netID);
		return algID;
	}
	
	/** Starts an execution that creates Brandes algorithm with the given network and communication weights (the weights may also be an empty String or null).
	 * @param network index
	 * @param communication weights String
	 * @return Execution index in the Database */
	@Override
    public int createAsynch(int netID, String communicationWeightsStr){
		LoggingManager.getInstance().writeTrace("Starting creating Brandes.", BrandesController.ALIAS, "createAsynch", null);
    	
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
		AbstractExecution exe = new BrandesBCExecution(ShortestPathAlgorithmInterface.DEFAULT, netID, communicationWeightsStr);
    	return createBrandesAsynch(exe);
	}

	@Override
    public int createAsynch(int netID, int tmID){
		LoggingManager.getInstance().writeTrace("Starting creating Brandes.", BrandesController.ALIAS, "createAsynch", null);
    	
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
		AbstractExecution exe = new BrandesBCExecution(ShortestPathAlgorithmInterface.DEFAULT, netID, tmID);
    	return createBrandesAsynch(exe);
	}
	
	@Override
    public int createAsynch(int netID){
		return createAsynch(netID, -1);
	}
	
	private int createBrandesAsynch(AbstractExecution exe) {
		int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, BrandesController.ALIAS, "createAsynch", null);
    	return exeID;
	}
	
	/** Removes the given Brandes algorithm from the Database maps.
	 * @param algorithm index
	 * @return 0 */
	@Override
    public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}	
	
	/** Returns the betweenness value of the given vertex in the given Brandes algorithm instance.
	 * @param algorithm index
	 * @param vertex
	 * @return betweenness value */
	@Override
    public double getBetweenness(int algID, int vertex){
		return ((BrandesBC)DataBase.getAlgorithm(algID)).getCentrality(vertex);
	}
	
	
	public double getGBC(int algID){
		BrandesBC pu = (BrandesBC)DataBase.getAlgorithm(algID);
		return pu.getGBC();
	}
	
	/** Returns an array of betweenness values of the given vertices in the given Brandes algorithm instance.
	 * The order of the betweenness values in the array corresponds to the order of the given vertices.
	 * @param algorithm index
	 * @param array of vertices
	 * @return array of betweenness values */
	@Override
    public Object[] getBetweenness(int algID, int[] vertices){
		Object [] betweennessValues = new Object [vertices.length];
		BrandesBC pu = (BrandesBC)DataBase.getAlgorithm(algID);
		for (int i = 0; i < vertices.length; i++)
			betweennessValues[i] = pu.getCentrality(vertices[i]);
		return betweennessValues;
	}
	
	/** Returns an array of betweenness values of all vertices in the given Brandes algorithm instance.
	 * The order of the betweenness values in the array corresponds to the order of the vertices in the graph.
	 * @param algorithm index
	 * @return array of betweenness values */
	@Override
    public Object[] getBetweenness(int algID){
		BrandesBC pu = (BrandesBC)DataBase.getAlgorithm(algID);
		double[] bc = pu.getCentralitites();
		Object[] bVals = new Object[bc.length];
		for (int i = 0; i < bVals.length; i++)
			bVals[i] = new Double(bc[i]);
		return bVals;
	}
	
	public int getNumberOfVertices(int algID){
		BrandesBC pu = (BrandesBC)DataBase.getAlgorithm(algID);
		return pu.getGraph().getNumberOfVertices();
	}
	
	public int getNumberOfEdges(int algID){
		BrandesBC pu = (BrandesBC)DataBase.getAlgorithm(algID);
		return pu.getGraph().getNumberOfEdges();
	}
	
	/** 
	 * Manipulates the graph algorithm by setting the source vertices for the BFS/Dijkstra traversal. 
	 * Setting the sources does not change the betweenness values computed beforehand. In order to 
	 * update the betweenness value the algorithm should be executed again using the run() method.  
	 * @param algorithm index
	 * @param source vertices
	 * @return 0 if successful */
	@Override
    public int setSources(int algID, Object[] sources){
		BrandesBC alg = (BrandesBC)DataBase.getAlgorithm(algID);
		Index[] idxSources = convertSources(sources);
		alg.setSources(idxSources);
		return 0;
	}
	
	
	/** 
	 * This method should be called before attempting to retrieve any results 
	 * if delayExecution was set to true in the respective create(...) or if 
	 * setSources was used 
	 * @param algorithm index
	 * @return 0 if successful */
	@Override
    public int run(int algID){
		BrandesBC alg = (BrandesBC)DataBase.getAlgorithm(algID);
		alg.run();
		return 0;
	}

	@Override
	public double getNumberOfDiscovered(int algID) {
		BrandesBC alg = (BrandesBC)DataBase.getAlgorithm(algID);
		return alg.getShortestPathAlgorithm().getNumberOfDiscovered();
	}

	@Override
	public double getNumberOfRediscovered(int algID) {
		BrandesBC alg = (BrandesBC)DataBase.getAlgorithm(algID);
		return alg.getShortestPathAlgorithm().getNumberOfRediscovered();
	}

	@Override
	public double getNumberOfExpanded(int algID) {
		BrandesBC alg = (BrandesBC)DataBase.getAlgorithm(algID);
		return alg.getShortestPathAlgorithm().getNumberOfExpanded();
	}

	@Override
	public int resetCounters(int algID) {
		BrandesBC alg = (BrandesBC)DataBase.getAlgorithm(algID);
		alg.getShortestPathAlgorithm().resetCounters();
		return 0;
	}
	
}
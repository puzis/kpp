package server.shortestPathBetweenness;

import java.util.Iterator;
import java.util.LinkedList;

import javolution.util.Index;
import server.common.DataBase;
import server.common.LoggingManager;
import server.common.Network;
import server.common.ServerConstants;
import server.common.ServerConstants.BCCalculatorAlgorithm;
import server.execution.AbstractExecution;
import server.shortestPathBetweenness.executions.FasterBCExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.bcc.BCCAlgorithm;
import algorithms.bcc.BiConnectedComponent;
import algorithms.centralityAlgorithms.betweenness.bcc.BCCalculatorInterface;
import algorithms.centralityAlgorithms.betweenness.bcc.BetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.bcc.EvenFasterBetweenness;
import algorithms.centralityAlgorithms.betweenness.bcc.TMBetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

public class FasterBCController 
{
	public static final String ALIAS = "BCC";
	
	/**
	 * This method constructs a BCC based Betweenness calculator that accepts arbitrary Traffic Matrices.
	 * Accepting arbitrary Traffic Matrices as an input argument causes awful running times. 
	 * This algorithm should only be used if the network is almost a tree.   
	 * @param netID
	 * @param tmID
	 * 
	 * @return
	 */
	public int create(int netID, int tmID){
		int algID = -1;
		try{
			GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
			EvenFasterBetweenness fasterBC = null;
			BCCAlgorithm bccAlg = new BCCAlgorithm(graph);
			AbsTrafficMatrix communicationWeights = DataBase.getTrafficMatrix(tmID);
			BCCalculatorInterface ebcc = new TMBetweennessCalculator(bccAlg, communicationWeights);

			fasterBC = new EvenFasterBetweenness(graph, ebcc);	
	    	
			fasterBC.run();
	    	
			algID = DataBase.putAlgorithm(fasterBC, netID);
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem("An exception has occured while creating faster BC algorithm\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), FasterBCController.ALIAS, "create", ex);
		}
		return algID; 
	}
	
	/**
	 * This method constructs a BCC based Betweenness calculator that assumes 
	 * a default traffic matrix. It works by partitioning the network into 
	 * bi-connected components and computing betweenness separately within 
	 * each component. Its running times are almost always better than of 
	 * the Brandes (2001) algorithm.    
	 * @param netID
	 * @param tmID
	 * @return
	 */
	public int create(int netID){
		int algID = -1;
		try{
			GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
			EvenFasterBetweenness fasterBC = null;
			BCCAlgorithm bccAlg = new BCCAlgorithm(graph);
			BCCalculatorInterface ebcc = new BetweennessCalculator(bccAlg);

			fasterBC = new EvenFasterBetweenness(graph, ebcc);	
	    	
			fasterBC.run();
	    	
			algID = DataBase.putAlgorithm(fasterBC, netID);
		}
		catch(Exception ex){	
			LoggingManager.getInstance().writeSystem("An exception has occured while creating faster BC algorithm\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), FasterBCController.ALIAS, "create", ex);
		}
		return algID; 
	}

	/**
	 * This method constructs a BCC based Betweenness calculator that assumes 
	 * that the network AND the Traffic Matrix were created using 
	 * SE.createUnifiedNetwork(..) and SE.createUnifiedCW(..) respectively.
	 * It works by partitioning the unified network into bi-connected 
	 * components and computing betweenness separately within each component.
	 * Its running time is almost always better than of the Brandes (2001) 
	 * algorithm.    
	 * @param netID created by Structural Equivalence unifier.
	 * @param tmID created by Structural Equivalence unifier.
	 * @return algID
	 */
	public int createFromSE(int netID, int tmID){
		int algID = -1;
		try{
			GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
			EvenFasterBetweenness fasterBC = null;
			BCCAlgorithm bccAlg = new BCCAlgorithm(graph);
			AbsTrafficMatrix communicationWeights = DataBase.getTrafficMatrix(tmID);
			BCCalculatorInterface ebcc = new BetweennessCalculator(bccAlg, communicationWeights);

			fasterBC = new EvenFasterBetweenness(graph, ebcc);	
	    	
			fasterBC.run();
	    	
			algID = DataBase.putAlgorithm(fasterBC, netID);
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem("An exception has occured while creating faster BC algorithm\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), FasterBCController.ALIAS, "create", ex);
		}
		return algID; 
	}
	
	
	/** Starts an execution that creates Faster BC algorithm with the given network.
	 * @param network index
	 * @return Execution index in the Database */
	public int createAsynch(int netID)
	{
		LoggingManager.getInstance().writeTrace("Starting creating FasterBCAlgorithm.", FasterBCController.ALIAS, "createAsynch", null);
    	
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
		AbstractExecution exe = new FasterBCExecution(netID);
		return createFasterBCAsynch(exe);
	}
	
	/** Starts an execution that creates Faster BC algorithm with the given network and traffic matrix.
	 * The algorithm created uses assumes BCCalculatorAlgorithm.TRAFIC_MATRIX_BC
	 * @param network index
	 * @param tmID - Traffic Matrix index
	 * @return Execution index in the Database */
	public int createAsynch(int netID, int tmID)
	{
		LoggingManager.getInstance().writeTrace("Starting creating FasterBCAlgorithm.", FasterBCController.ALIAS, "createAsynch", null);
    	
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
		AbstractExecution exe = new FasterBCExecution(netID, tmID);
		return createFasterBCAsynch(exe);
	}

	/** Starts an execution that creates Faster BC algorithm with the given network, traffic matrix, and
	 * bcCalculatorType. If DEFAULT is given then the network and the traffic matrix must have been created
	 * using SE.createUnifiedNetwork(..) and SE.createUnifiedCW(..) respectively.
	 * @param netID - network index
	 * @param tmID - Traffic Matrix index
	 * @param bcCalculatorType - DEFAULT = 0, TRAFIC_MATRIX_BC = 1
	 * @return Execution index in the Database */
	public int createAsynch(int netID, int tmID, int bcCalculatorType){
		LoggingManager.getInstance().writeTrace("Starting creating FasterBCAlgorithm.", FasterBCController.ALIAS, "createAsynch", null);
    	
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
		AbstractExecution exe = new FasterBCExecution(netID, tmID, BCCalculatorAlgorithm.make(bcCalculatorType));
    	return createFasterBCAsynch(exe);
	}
	
	
	private int createFasterBCAsynch(AbstractExecution exe){
		int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, FasterBCController.ALIAS, "createAsynch", null);
    	return exeID;
	}
	
	/** Removes the given Faster BC algorithm from the Database maps.
	 * @param algorithm index
	 * @return 0 */
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}	
	
	/** Returns the betweenness value of the given vertex in the given Faster BC algorithm instance.
	 * @param algorithm index
	 * @param vertex
	 * @return betweenness value */
	public double getBetweenness(int algID, int vertex){
		EvenFasterBetweenness fasterBC = (EvenFasterBetweenness)DataBase.getAlgorithm(algID); 
		return fasterBC.getCentrality(vertex);
	}
	
	/** Returns an array of betweenness values of the given vertices in the given Faster BC algorithm instance.
	 * The order of the betweenness values in the array corresponds to the order of the given vertices.
	 * @param algorithm index
	 * @param array of vertices
	 * @return array of betweenness values */
	public Object[] getBetweenness(int algID, int[] vertices){
		Object [] betweennessValues = new Object [vertices.length];
		EvenFasterBetweenness fasterBC = (EvenFasterBetweenness)DataBase.getAlgorithm(algID);
		for (int i = 0; i < vertices.length; i++)
			betweennessValues[i] = fasterBC.getCentrality(vertices[i]);
		return betweennessValues;
	}
	
	/** Returns an array of betweenness values of all vertices in the given Faster BC algorithm instance.
	 * The order of the betweenness values in the array corresponds to the order of the vertices in the graph.
	 * @param algorithm index
	 * @return array of betweenness values */
	public Object[] getBetweenness(int algID){
		EvenFasterBetweenness fasterBC = (EvenFasterBetweenness)DataBase.getAlgorithm(algID);
		double[] bc = fasterBC.getCentralitites();
		Object[] bVals = new Object[bc.length];
		for (int i = 0; i < bVals.length; i++)
			bVals[i] = new Double(bc[i]);
		return bVals;
	}
	
	/** Returns the sum of betweenness values of the given vertices in the given Faster BC algorithm instance.
	 * @param algorithm index
	 * @param array of vertices
	 * @return betweenness value */
	public double getSumGroup(int algID, Object[] vertices){
		EvenFasterBetweenness fasterBC = (EvenFasterBetweenness)DataBase.getAlgorithm(algID);
		double gFasterBC = 0;
		try{
			for (int i = 0; i < vertices.length; i++){
				gFasterBC += fasterBC.getCentrality(((Integer)vertices[i]).intValue());
	    	}
		}
		catch(RuntimeException ex){
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured during getSumGroup.", FasterBCController.ALIAS, "getSumGroup", ex);
		}
		LoggingManager.getInstance().writeTrace("Finishing getSumGroup.", FasterBCController.ALIAS, "getSumGroup", null);
		return gFasterBC;
	}
	
	/** Returns an array with statistical values of the given Faster BC algorithm instance.
	 * statistics[0] is number of components
	 * statistics[1] is avg component size
	 * statistics[2] is max component size
	 * @param algorithm index 
	 * @return statistics array **/
	public Object[] getComponentsStatistics(int algID){
		EvenFasterBetweenness fbc = (EvenFasterBetweenness)DataBase.getAlgorithm(algID);
		Object[] statistics = new Object[3];
		statistics[0] = new Integer(fbc.getComponentsCounter());
		statistics[1] = new Double(fbc.getAvgComponentSize());
		statistics[2] = new Integer(fbc.getMaxComponentSize());
		return statistics;
	}

	/**
	 * @author Rami 
	 * Creates remotely accessible network objects from the bi-connected components.  
	 * @param algorithm index 
	 * @return array of network identifiers
	 **/
	public Object[] createNetworksFromComponents(int algID){
		EvenFasterBetweenness fbc = (EvenFasterBetweenness)DataBase.getAlgorithm(algID);

		
		LinkedList<Integer> netids = new LinkedList<Integer>(); 
		
		
		int i = 0;
		Iterator<BiConnectedComponent> bccitr=fbc.getSubGraphs();
		while (bccitr.hasNext()){
			BiConnectedComponent bcc=bccitr.next();
			GraphInterface<Index,BasicVertexInfo> bccgraph = bcc.getComponent();			
			Network network = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID));			
			Network unifiedNetwork = new Network(network.getName().concat("_component_").concat(String.valueOf(i)), bccgraph);
			netids.add(DataBase.putNetwork(unifiedNetwork));
		}
		
		return netids.toArray();
	}
	

	/**@author Rami 
	 * Returns the bi-connected components as a two dimensional array. 
	 * @param algorithm index 
	 * @return bcc array **/
	public Object[] getComponents(int algID){
		EvenFasterBetweenness fbc = (EvenFasterBetweenness)DataBase.getAlgorithm(algID);
		Index[][] components = fbc.getComponents();
		Integer[][] result = new Integer[components.length][]; 		                         
		for (int i=0;i<components.length;i++){		
			result[i]=new Integer[components[i].length];
			for(int j=0;j<components[i].length;j++)
				result[i][j]=components[i][j].intValue();
		}
		return result;
	}
	
}
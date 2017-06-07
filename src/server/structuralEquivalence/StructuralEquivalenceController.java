package server.structuralEquivalence;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DataBase;
import server.common.LoggingManager;
import server.common.Network;
import server.common.ServerConstants;
import server.execution.AbstractExecution;
import server.structuralEquivalence.executions.StructuralEquivalenceExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.GraphPrinter;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.structuralEquivalence.StructuralEquivalenceUnifier;

import common.FastListNG;

public class StructuralEquivalenceController {

	public static final String ALIAS = "SE";
	
	/** Creates StructuralEquivalenceUnifier algorithm with the given network.
	 *  @param network index
	 *  @return Index of the algorithm in the Database */
	public int create(int netID)
	{
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		StructuralEquivalenceUnifier seUnifier = null;
		
		if (graph != null){
			seUnifier = new StructuralEquivalenceUnifier(graph);
			seUnifier.run();
		}
		int algID = DataBase.putAlgorithm(seUnifier, netID);
	
		return algID; 
	}
	
	public int create(int netID, String tm){
		return create(netID);
	}
	
	/** Starts an execution that creates StructuralEquivalenceUnifier algorithm with the given network.
	 * @param network index
	 * @return Execution index in the Database */
	public int createAsynch(int netID)
	{
		LoggingManager.getInstance().writeTrace("Starting creating StructuralEquivalenceUnifier.", StructuralEquivalenceController.ALIAS, "createAsynch", null);
    	
    	/** Create new execution (Runnable).  
    	 *  Store execution into database. 
    	 *  Create new thread, give it the execution and start it.
    	 *  Return exeID. 
    	 *  (At the end of run() the execution parameters of progress and success are updated.)
    	 */
		AbstractExecution exe = new StructuralEquivalenceExecution(netID);
    	int exeID = DataBase.putExecution(exe);
    	exe.setID(exeID);
    	Thread t = new Thread(exe);
    	t.start();
    	
    	LoggingManager.getInstance().writeTrace(ServerConstants.RETURNING_TO_CLIENT, StructuralEquivalenceController.ALIAS, "createAsynch", null);
    	return exeID;
	}
	
	public int createAsynch(int netID, String tm){
		return createAsynch(netID);
	}
	
	public Object[] getEquivalenceClasses(int algID){
		StructuralEquivalenceUnifier se = (StructuralEquivalenceUnifier)DataBase.getAlgorithm(algID);
		FastListNG<FastListNG<Index>> eClasses = se.getEquivalenceClasses();
		
		Object[] equivalenceClasses = new Object[eClasses.size()];
		int i=0;
		for(FastListNG.Node<FastListNG<Index>> cNode=eClasses.head(), end=eClasses.tail(); (cNode = cNode.getNext())!=end; i++){
			FastList<Index> unifiedVertices = cNode.getValue();
			Object [] eClass = new Object[unifiedVertices.size()];
			int j = 0;
			for(FastList.Node<Index> vNode=unifiedVertices.head(), vEnd=unifiedVertices.tail(); (vNode = vNode.getNext())!=vEnd; j++){
				eClass[j] = new Integer(vNode.getValue().intValue());
			}
			equivalenceClasses[i] = eClass;
		}
		return equivalenceClasses;
	}
	
	public Object[] getUnifiedEdges(int algID){
		StructuralEquivalenceUnifier se = (StructuralEquivalenceUnifier)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> unifiedGraph = se.getUnifiedGraph();
		Object[] unifiedEdges = new Object[unifiedGraph.getNumberOfEdges()*2];

		int i=0;
		for(AbstractSimpleEdge<Index,BasicVertexInfo> e : unifiedGraph.getEdges()) {
			unifiedEdges[i++] = new Integer(((Index)e.getV0()).intValue());
			unifiedEdges[i++] = new Integer(((Index)e.getV1()).intValue());
		}
		return unifiedEdges;
	}
	
	/** Creates and stores the unified traffic matrix of the given structural equivalence algorithm in the database.
	 * 
	 * @param algID - given structural equivalence algorithm
	 * @return index of the unified traffic matrix in the database.
	 */
	public int createUnifiedCW(int algID){
		StructuralEquivalenceUnifier se = (StructuralEquivalenceUnifier)DataBase.getAlgorithm(algID);
		AbsTrafficMatrix unifiedCW = se.getUnifiedCW();
		int tmID = DataBase.putTrafficMatrix(unifiedCW);
		
		return tmID;
	}
	
	public Object[] getUnifiedCW(int algID){
		StructuralEquivalenceUnifier se = (StructuralEquivalenceUnifier)DataBase.getAlgorithm(algID);
		AbsTrafficMatrix unifiedCW = se.getUnifiedCW();
		Object [][] tmObj = new Object[unifiedCW.getDimensions()][unifiedCW.getDimensions()];
		
		for (int i=0; i<unifiedCW.getDimensions(); i++)
			for (int j=0; j<unifiedCW.getDimensions(); j++)
				tmObj[i][j] = new Double(unifiedCW.getWeight(i, j));
		
		return tmObj;
	}
	
	public String getUnifiedGraphRepr(int algID){
		StructuralEquivalenceUnifier se = (StructuralEquivalenceUnifier)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> graph = se.getUnifiedGraph();
		GraphPrinter printer = new GraphPrinter(graph);
		return printer.getVerticesStr()+printer.getEdgesStr();
	}
	
	/** Creates and stores the unified graph of the given structural equivalence algorithm in the database.
	 * 
	 * @param algID - given structural equivalence algorithm
	 * @return index of the unified graph in the database.
	 */
	public int createUnifiedNetwork(int algID){
		StructuralEquivalenceUnifier se = (StructuralEquivalenceUnifier)DataBase.getAlgorithm(algID);
		GraphInterface<Index,BasicVertexInfo> unifiedGraph = se.getUnifiedGraph();
		
		Network network = DataBase.getNetwork(DataBase.getNetworkOfAlgorithm(algID));
		Network unifiedNetwork = new Network(network.getName().concat("unified"), unifiedGraph);
		int netID = DataBase.putNetwork(unifiedNetwork);
		
		return netID;
	}
	
	/** Removes the given Brandes algorithm from the Database maps.
	 * @param algorithm index
	 * @return 0 */
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}
}
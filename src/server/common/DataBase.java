package server.common;

import javolution.util.FastMap;
import javolution.util.Index;
import server.execution.AbstractExecution;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

public class DataBase 
{
	private static FastMap<Index, Network> m_networks = new FastMap<Index, Network>();
	private static FastMap<Index, AbstractExecution> m_executions = new FastMap<Index, AbstractExecution>();
	private static FastMap<Index, Object> m_algorithms = new FastMap<Index, Object>();
	private static FastMap<Index, AbsTrafficMatrix> m_trafficMatrices = new FastMap<Index, AbsTrafficMatrix>();
	private static FastMap<Index, IClosenessFormula> m_formulas = new FastMap<Index, IClosenessFormula>();
	
	/** Key - algID, Value - netID */
	private static FastMap<Index, Index> m_netsOfAlgs = new FastMap<Index, Index>();
	
	private static int m_networkCounter = -1;
	private static int m_executionsCounter = -1;
	private static int m_algorithmsCounter = -1;
	private static int m_tmCounter = -1;
	private static int m_formulasCounter = -1;
	
	/** Returns the index of the network mapped to the given algorithm index. 
	 * @param algID
	 * @return netID
	 */
	public static int getNetworkOfAlgorithm(int algID){
		return m_netsOfAlgs.get(Index.valueOf(algID)).intValue();
	}

	/** Puts the given network into a map and returns its index in the map.
	 * @param network
	 * @return network index
	 */
	public static int putNetwork(Network network){
		m_networks.put(Index.valueOf(++m_networkCounter), network);
		return m_networkCounter;
	}
	
	/** Puts the given algorithm into a map, 
	 * maps its index in the map to the given network index, 
	 * and returns the algorithm's index in the map.
	 * @param algorithm
	 * @return algorithm index
	 * @return netID
	 */
	public static int putAlgorithm(Object algorithm, int netId){
		m_algorithms.put(Index.valueOf(++m_algorithmsCounter), algorithm);
		m_netsOfAlgs.put(Index.valueOf(m_algorithmsCounter), Index.valueOf(netId));
		return m_algorithmsCounter;
	}
	
	/** Puts the given traffic matrix into a map and returns its index in the map.
	 * @param traffic matrix
	 * @return traffic matrix index
	 */
	public static int putTrafficMatrix(AbsTrafficMatrix trafficMatrix){
		m_trafficMatrices.put(Index.valueOf(++m_tmCounter), trafficMatrix);
		return m_tmCounter;
	}
	
	/** Puts the given formula into a map and returns its index in the map.
	 * @param formula
	 * @return formula index
	 */
	public static int putFormula(IClosenessFormula formula){
		m_formulas.put(Index.valueOf(++m_formulasCounter), formula);
		return m_formulasCounter;
	}
	
	/** Puts the given execution into a map and returns its index in the map.
	 * @param execution
	 * @return execution index
	 */
	public static int putExecution(AbstractExecution execution){
		m_executions.put(Index.valueOf(++m_executionsCounter), execution);
		return m_executionsCounter;
	}
	
	/** Retrieves from the networks' map the network object with the given index.
	 * @param index
	 * @return Network
	 */
	public static Network getNetwork(int index){
		return m_networks.get(Index.valueOf(index));
	}
    public static int getNetworkCounter()
    {
        return m_networkCounter;
    }
    public static int getAlgCounter()
    {
        return m_algorithmsCounter;
    }
	
	/**
	 * Checks if the network index is known to the server.
	 * @param index
	 * @return
	 */
	public static boolean networkExists(int index){
		return m_networks.containsKey(Index.valueOf(index));
	}
	
	/** Retrieves from the executions' map the execution object with the given index.
	 * @param index
	 * @return AbstractExecution
	 */
	public static AbstractExecution getExecution(int index){
		return m_executions.get(Index.valueOf(index));
	}
	
	/** Retrieves from the algorithms' map the algorithm object with the given index.
	 * @param index
	 * @return Algorithm
	 */
	public static Object getAlgorithm(int index){
		return m_algorithms.get(Index.valueOf(index));
	}
	
	/** Retrieves from the traffic-matrices' map the traffic matrix object with the given index.
	 * @param index
	 * @return Traffic matrix
	 */
	public static AbsTrafficMatrix getTrafficMatrix(int index){
		return m_trafficMatrices.get(Index.valueOf(index));
	}
	
	/** Retrieves from the formulas' map the formula object with the given index.
	 * @param index
	 * @return Formula
	 */
	public static IClosenessFormula getFormula(int index){
		return m_formulas.get(Index.valueOf(index));
	}
	
	/** Removes from the networks' map the network object with the given index.
	 * 	Removes also the mapping from the network to a traffic matrix.
	 * @param index
	 */
	public static void releaseNetwork(int index){
		Index net = Index.valueOf(index);
		m_networks.remove(net);
	}
	
	/** Removes from the executions' map the execution object with the given index.
	 * @param index
	 */
	public static void releaseExecution(int index){
		m_executions.remove(Index.valueOf(index));
	}
	
	/** Removes from the algorithms' map the algorithm object with the given index.
	 * Removes also the mapping from the algorithm to a network.
	 * @param index
	 */
	public static void releaseAlgorithm(int index){
		Index alg = Index.valueOf(index);
		if (m_netsOfAlgs.containsKey(alg)) m_netsOfAlgs.remove(alg);
		m_algorithms.remove(alg);
	}
	
	/** Removes from the traffic-matrices' map the traffic-matrix object with the given index.
	 * @param index
	 */
	public static void releaseTrafficMatrix(int index){
		m_trafficMatrices.remove(Index.valueOf(index));
	}
	
	/** Removes from the formulas' map the formula object with the given index.
	 * @param index
	 */
	public static void releaseFormula(int index){
		m_formulas.remove(Index.valueOf(index));
	}
}

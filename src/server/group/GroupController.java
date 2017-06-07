package server.group;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.Network;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.BasicSetInterface;
import algorithms.centralityAlgorithms.SetWithMemorization;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.DynamicBetweennessSet;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.OptimizedDynamicBetweennessSet;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.StaticBetweennessSet;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.closeness.sets.OptimizedDynamicClosenessSet;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

public class GroupController 
{
	public static final String ALIAS = "Group";
	
	
	public int createClosenessGroup(int netId) {
		Network net = (Network)DataBase.getNetwork(netId);
		GraphInterface<Index,BasicVertexInfo> graph = net.getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
				ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
				new DummyProgress(), 1.0);

		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_STANDARD, dists, null);
		OptimizedDynamicClosenessSet group = new OptimizedDynamicClosenessSet(graph, formula);
		return DataBase.putAlgorithm(group, netId);
	}
	
	public int createClosenessGroupReciprocal(int netId) {
		Network net = (Network)DataBase.getNetwork(netId);
		GraphInterface<Index,BasicVertexInfo> graph = net.getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
				ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
				new DummyProgress(), 1.0);

		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
		OptimizedDynamicClosenessSet group = new OptimizedDynamicClosenessSet(graph, formula);
		return DataBase.putAlgorithm(group, netId);
	}
	
	public int createClosenessGroupExponential(int netId, double immunity) {
		Network net = (Network)DataBase.getNetwork(netId);
		GraphInterface<Index,BasicVertexInfo> graph = net.getGraphSimple();
		double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, 
				ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , 
				new DummyProgress(), 1.0);

		IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_EXPONENTIAL, dists, immunity);
		OptimizedDynamicClosenessSet group = new OptimizedDynamicClosenessSet(graph, formula);
		return DataBase.putAlgorithm(group, netId);
	}
	
	
	
	/** Creates a Group instance with the given candidate vertices and the given dataworkshop.
	 * @param dataworkshop index
	 * @param array of candidates
	 * @return group index in the database */
	public int createGBCOptimizedDynamicSet(int dwID, Object[] candidatesObj){
		int netId = DataBase.getNetworkOfAlgorithm(dwID);
		Network net = (Network)DataBase.getNetwork(netId);
		int numberOfVertices = net.getGraphSimple().getNumberOfVertices();
		
		FastList<Index> candidates = new FastList<Index>(candidatesObj.length);
    	for (int i = 0; i < candidatesObj.length; i++) 
    		candidates.add(Index.valueOf(((Integer)candidatesObj[i]).intValue()));    	
    	DataWorkshop dw = (DataWorkshop)DataBase.getAlgorithm(dwID);    	
    	BasicSetInterface group = new OptimizedDynamicBetweennessSet(dw, candidates);
    	group = new SetWithMemorization(group,numberOfVertices);
    	return DataBase.putAlgorithm(group, netId);
	}
	
	/** Creates a Group instance with the given dataworkshop.
	 * Contributions of vertices to the GBC of the group are computed as their individual BC.
	 * @param dataworkshop index
	 * @return group index in the database */
	public int createGBCStaticSet(int dwID){
		int netId = DataBase.getNetworkOfAlgorithm(dwID);
		Network net = (Network)DataBase.getNetwork(netId);
		int numberOfVertices = net.getGraphSimple().getNumberOfVertices();

		DataWorkshop dw = (DataWorkshop)DataBase.getAlgorithm(dwID);    	
    	BasicSetInterface group = new StaticBetweennessSet(dw);
    	group = new SetWithMemorization(group,numberOfVertices);
    	return DataBase.putAlgorithm(group, DataBase.getNetworkOfAlgorithm(dwID));
	}
	
	/** Creates a Group instance with the given dataworkshop.
	 * @param dataworkshop index
	 * @return group index in the database */
	public int createGBCDynamicSet(int dwID){
		int netId = DataBase.getNetworkOfAlgorithm(dwID);
		Network net = (Network)DataBase.getNetwork(netId);
		int numberOfVertices = net.getGraphSimple().getNumberOfVertices();

		DataWorkshop dw = (DataWorkshop)DataBase.getAlgorithm(dwID);    	
    	BasicSetInterface group = new DynamicBetweennessSet(dw);
    	group = new SetWithMemorization(group,numberOfVertices);
    	return DataBase.putAlgorithm(group, DataBase.getNetworkOfAlgorithm(dwID));
	}

	/** Creates a Basic Group instance.
	 * Group centrality is it's size. 
	 * @param network index
	 * @return group index in the database */
	public int createBasicSet(int netId){
    	BasicSetInterface group = new BasicSet();
    	return DataBase.putAlgorithm(group,netId);
	}

	/** Returns the group members of the given group.
	 * @param group index
	 * @return array of vertices */
	public Object[] getMembers(int algID){
		Object[] members = ((BasicSetInterface)DataBase.getAlgorithm(algID)).getVertexMembers().toArray();
		Integer[] result = new Integer[members.length];
		for (int i=0;i<members.length;i++)
			result[i] = ((Index)members[i]).intValue();
		return result;
	}
	
	/** Returns the betweenness value of the given vertex in the given group.
	 * @param group index
	 * @param vertex
	 * @return betweenness value */
	public double getContribution(int algID, int v){	
		return ((BasicSetInterface)DataBase.getAlgorithm(algID)).getContribution(Index.valueOf(v));	
	}
	
	/** Adds the given vertex to the given group.
	 * @param group index
	 * @param vertex */
	public int add(int algID, int v){
		BasicSetInterface group = ((BasicSetInterface)DataBase.getAlgorithm(algID));
		group.add(Index.valueOf(v));
		return group.size();
	}
	
	/** Adds the given vertices to the given group.
	 * @param group index
	 * @param array of vertices */
	public int add(int algID, int[] vertices){
		FastList<Index> indxVertices = new FastList<Index>();
		for (int i: vertices)
			indxVertices.add(Index.valueOf(i));
		BasicSetInterface group = ((BasicSetInterface)DataBase.getAlgorithm(algID));
		group.add(indxVertices);
		//group.add(Index.valueOf(v));
		return group.size();
	}
	
	/** Returns the group betweenness value of all vertices in the given group.
	 * @param group index
	 * @return betweenness value */
	public double getGroupCentrality(int algID){
		return ((BasicSetInterface)DataBase.getAlgorithm(algID)).getGroupCentrality(); 	
	}
	
	/** Returns k vertices with the highest betweenness values in the given group.
	 * @param group index
	 * @param k
	 * @return array of k vertices */
	public Object[] getTopK(int algID, int k){
		throw new NotImplementedException();
		//return ((GroupAdapter)DataBase.getAlgorithm(algID)).getTopK(k);
		/*
			Object[] setSizeK = null;
			try{
				FastList<Index> unusedCandidates = new FastList<Index>(m_candidates);
				int resultingGroupSize = 0;
				FastList<Pair<Index, Double>> result = new FastList<Pair<Index, Double>>();
				
				while ((resultingGroupSize < k) && (unusedCandidates.size() > 0))
				{
					int v = AbsGreedyBetweeness.getHighestContribution(m_setImpl, unusedCandidates);
					unusedCandidates.remove(Index.valueOf(v));
					resultingGroupSize++;
		            result.add(new Pair<Index, Double>(Index.valueOf(v), Double.valueOf(0)));
				}
			
				setSizeK = new Object[result.size()];
				int i = 0;
				for (FastList.Node<Pair<Index, Double>> member = result.head(), end = result.tail(); (member = member.getNext()) != end; i++)
				{
					setSizeK[i] = member.getValue().getValue1().intValue();
				}
			}
			catch(Exception ex)
			{
				LoggingManager.getInstance().writeSystem("Couldn't get optimal set of size: " + k + ".", "Group", "getTopKFromGroup", ex);
			}
			return setSizeK;	
		}
		*/
		  
	}
	
	
	
	/** Removes the given group from the Database maps.
	 * @param group index
	 **/
	public int close(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}
}
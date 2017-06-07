package server.dfbnb;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import javolution.util.Index;
import server.common.DataBase;
import server.common.LoggingManager;
import server.common.Network;
import server.common.ServerConstants.Centrality;
import algorithms.centralityAlgorithms.BasicSetInterface;

public class DfbnbController 
{
	
	//TODO: refactor: make dfbnb consistent with the Create/Destroy interface of other controllers.
	public static final String ALIAS = "Dfbnb";
	
	/**
	 * @deprecated 
	 * DEFAULT TRAFFIC MATRIX IS ASSUMED. */
	public boolean dfbnbInit( int networkIndex, int centrality, int setType, double budget, int elementOrdering, int utilityHeuristic, int costHeuristic, int openList) throws Exception {
		Network network = DataBase.getNetwork(networkIndex);
		Centrality cType = convertToEnum(centrality);
		int candidateNum = network.getGraphSimple().getNumberOfVertices();
		Index[] candidates = new Index[candidateNum];
		for (int i = 0; i < candidateNum; i++)
			candidates[i] = Index.valueOf(i);
		
		Dfbnb instance = new Dfbnb(cType,setType, elementOrdering, utilityHeuristic, costHeuristic, candidates, network, budget, openList);
		
		Dfbnb.networks.put(Index.valueOf(networkIndex), instance);
		return true;
	}

	/** 
	 * @deprecated
	 * DEFAULT TRAFFIC MATRIX IS ASSUMED. */
	public boolean dfbnbInit( int networkIndex, int centrality, int setType,Double budget, int elementOrdering, int utilityHeuristic, int costHeuristic, int [] candidates, int openList) throws Exception {		
		Network network = DataBase.getNetwork(networkIndex);		
		Centrality cType = convertToEnum(centrality);

		Index[] candidatesArr = new Index[candidates.length];
		for (int i = 0; i < candidates.length; i++)
			candidatesArr[i] = Index.valueOf(candidates[i]);
		
		Dfbnb instance = new Dfbnb(cType, setType, elementOrdering, utilityHeuristic, costHeuristic, candidatesArr, network, budget, openList);		
		Dfbnb.networks.put(Index.valueOf(networkIndex), instance);
		return true;
	}


	/** 
	 * 
	 **/
	public boolean dfbnbInitUsingGroups(int networkIndex, int uGID, int cGID, double budget, int elementOrdering, int utilityHeuristic, int costHeuristic, Object [] candidates, int openList) throws Exception {
		Network network = DataBase.getNetwork(networkIndex);		
		BasicSetInterface uGroup = ((BasicSetInterface)DataBase.getAlgorithm(uGID));
		BasicSetInterface cGroup = ((BasicSetInterface)DataBase.getAlgorithm(cGID));
		
		Index[] candidatesArr = new Index[candidates.length];
		for (int i = 0; i < candidates.length; i++)
			candidatesArr[i] = Index.valueOf((Integer)candidates[i]);

		Dfbnb instance = new Dfbnb(uGroup,cGroup, elementOrdering, utilityHeuristic, costHeuristic, candidatesArr, network, budget, openList);
		Dfbnb.networks.put(Index.valueOf(networkIndex), instance);
		return true;
	}
	
	
	public int dfbnbCalculate(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.execute();
	}

	public int dfbnbCalculate(int networkIndex, int numberOfSteps) throws Exception {
		LoggingManager.getInstance().writeMessage("Calculating DFBnB...", this.getClass().getName(), "dfbnbCalculate", null);
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.execute(numberOfSteps);
	}

	/**
	 * A time delimited run of DFBnB
	 * @param networkIndex
	 * @param minNumOfMillis
	 * @param numberOfStepsIncrement
	 * @return
	 * @throws Exception
	 */
	public int dfbnbTimeLimitedCalculate(int networkIndex, int minNumOfMillis, int numberOfStepsIncrement) throws Exception
	{
		LoggingManager.getInstance().writeMessage("Calculating DFBnB...", this.getClass().getName(), "dfbnbCalculate", null);
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		Calendar calendar = Calendar.getInstance();
		long startTime = calendar.getTimeInMillis();
		int steps=-1;
		do{
			steps = instance.execute(numberOfStepsIncrement);
			if (steps<numberOfStepsIncrement)
				return steps;
		}while(calendar.getTimeInMillis()-startTime<minNumOfMillis);
		return steps;
	}
	
	public double dfbnbGetCertificate(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.getCertificate();
	}

	public boolean dfbnbRemove(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		Dfbnb.networks.remove(Index.valueOf(networkIndex));
		instance.clear();
		System.gc();
		return true;
	}

	public boolean dfbnbIsSearchDone(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.isSearchDone();
	}


	public String dfbnbGetBestSolutionDescription(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		StringBuffer description = new StringBuffer("");
		for (Index item : instance.getBestSolution()){
			description.append(item.intValue());
			description.append(",");
		}
		return description.toString();                            
	}	

	public Integer[] dfbnbGetBestSolution(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		StringBuffer description = new StringBuffer("");
		Index[] solution = instance.getBestSolution();
		Integer[] result = new Integer[solution.length];
		for (int i=0;i<solution.length;i++){
			result[i] = solution[i].intValue();
		}
		return result;                            
	}	
	
	/**
	 * Analyze the heuristics after a complete solution
	 * @param networkIndex
	 *
	public Vector<Double> analyzeHeuristic(int networkIndex)
		throws Exception {
		Network network = DataBase.getNetwork(networkIndex);		
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));		
		Pair<double[],double[]> results = instance.analyzeHeuristic(network);
		
		// Convert pair of arrays to long array in order to be passable in XMLRPC 
		// (although I believe that there is probably a way to pass it somehow)
		double[] value1 = results.getValue1();
		double[] value2 = results.getValue2();
		if (value1.length != value2.length)
			throw new IllegalStateException("Analysis should return the same values for h and optimal h");
		
		Vector<Double> passableResults = new Vector<Double>();
		for(int i=0;i<value1.length;i++){
			passableResults.add(value1[i]);
			passableResults.add(value2[i]);
		}
		return passableResults;
	}
	*/
	
	public Integer[] dfbnbGetCurrentSearchNode(int networkIndex)
			throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		Index[] result = instance.getCurrentSearchNode();
		Integer[] resultInt = new Integer[result.length];
		for (int i = 0; i<result.length; i++){
			resultInt[i] = result[i].intValue();
		}
		return resultInt;
	}

	public double dfbnbGetBestUtility(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.getBestUtility();
	}

	public double dfbnbGetBestCost(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.getBestCost();
	}

	public double dfbnbGetCurrentUtility(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.getCurrentUtility();
	}

	public double dfbnbGetCurrentCost(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.getCurrentCost();
	}
	
	public double dfbnbGetUnprunedExpandedNodes(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.getCountUnprunedExpandedNodes();
	}
	
	public double dfbnbGetAcceptedNodes(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.getCountAcceptedNodes();
	}

	public double dfbnbGetRejectedNodes(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.getCountRejectedNodes();
	}
	
	public int dfbnbGetOpenListSize(int networkIndex) throws Exception {
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		return instance.getOpenListSize();
	}
	
	public boolean dfbnbStore(int networkIndex, String name) throws Exception {
		boolean result = false;
		Dfbnb instance = Dfbnb.networks.get(Index.valueOf(networkIndex));
		if (instance == null)
			throw new Exception("Dfbnb: network not initialized as dfbnb!");
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream(name + "." + ALIAS);
			out = new ObjectOutputStream(fos);
			out.writeObject(instance);
			out.close();
			result = true;
		}
		finally
		{
			if (out != null)
				out.close();
			if (fos != null)
				fos.close();
		}
		return result;
	}

	public boolean dfbnbLoad(int networkIndex, String name) throws Exception {
		boolean result = false;
		
		FileInputStream fos = null;
		ObjectInputStream out = null;
		try
		{
			fos = new FileInputStream(name + "." + ALIAS);
			out = new ObjectInputStream(fos);
			Dfbnb instance = (Dfbnb)out.readObject();
			out.close();
			Network network = DataBase.getNetwork(networkIndex);
			instance.setNetwork(network, true);
			Dfbnb.networks.put(Index.valueOf(networkIndex), instance);
			result = true;
		}
		finally
		{
			if (out != null)
				out.close();
			if (fos != null)
				fos.close();
		}
		return result;
	}
	
	private Centrality convertToEnum(int centrality) {
		Centrality cType = Centrality.Betweeness;
		switch (centrality){
		case 0:
			cType = Centrality.Betweeness;
			break;
		case 1:
			cType = Centrality.Degree;
			break;
		case 2:
			cType = Centrality.Closeness;
			break;
		case 3:
			cType = Centrality.RandomWalk;
			break;
		case 4:
			cType = Centrality.FasterBC;
			break;
		}
		return cType;
	}
}
package server.trafficMatrix;

import server.common.DataBase;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.centralityAlgorithms.tm.PartialTrafficMatrix;
import algorithms.centralityAlgorithms.tm.SparseTrafficMatrix;

public class TrafficMatrixController {

	public static final String ALIAS = "TM";
	
	/** Creates default traffic-matrix object with the given number of vertices.
	 *  @param numOfVertices number of vertices in the corresponding graph
	 *  @return Index of the traffic matrix in the Database */
	public int createDefault(int numOfVertices){
		AbsTrafficMatrix tm = new DefaultTrafficMatrix(numOfVertices); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
		int tmID = DataBase.putTrafficMatrix(tm);

		return tmID;
	}
	
	/** Creates default traffic-matrix object with the given number of vertices
	 *  where only part (the first numOfCommVertices) of the vertices communicate with each other.
	 *  @param numOfVertices number of vertices in the corresponding graph
	 *  @param numOfCommVertices number of vertices that communicate with each other. 
	 *  @return Index of the traffic matrix in the Database */
	public int createPartial(int numOfVertices, int numOfCommVertices){
		AbsTrafficMatrix tm = new PartialTrafficMatrix(numOfVertices, numOfCommVertices); 
		int tmID = DataBase.putTrafficMatrix(tm);

		return tmID;
	}

	
	/** Creates dense traffic-matrix object with the given number of vertices and traffic matrix string representation 
	 *  where only part of the vertices communicate with each other.
	 *  If the traffic matrix string is an empty String or null, then DefaultTrafficMatrix will be created.
	 *  
	 *  @param numOfVertices number of vertices in the corresponding graph
	 *  @param numOfCommVertices number of vertices that communicate with each other. 
	 *  @param traffic matrix String
	 *  @return Index of the traffic matrix in the Database */
	public int createPartialDense(int numOfVertices, int numOfCommVertices, String tmStr){
		AbsTrafficMatrix tm = null;
		if (tmStr != null && !tmStr.isEmpty())
			tm = new DenseTrafficMatrix(tmStr, numOfCommVertices);
		else
			tm = new DefaultTrafficMatrix(numOfCommVertices); 
		
		tm = new PartialTrafficMatrix(numOfVertices, numOfCommVertices, tm);
		
		int tmID = DataBase.putTrafficMatrix(tm);
		
		return tmID;
	}
	
	
	/** Creates dense traffic-matrix object with the given number of vertices and traffic matrix string representation (the traffic matrix may also be an empty String or null).
	 *  If the traffic matrix string is an empty String or null, then DefaultTrafficMatrix will be created.
	 *  
	 *  @param numOfVertices number of vertices in the corresponding graph
	 *  @param traffic matrix String
	 *  @return Index of the traffic matrix in the Database */
	public int createDense(int numOfVertices, String tmStr){
		AbsTrafficMatrix tm = null;
		if (tmStr != null && !tmStr.isEmpty())
			tm = new DenseTrafficMatrix(tmStr, numOfVertices); // WeightsLoader.loadWeightsFromString(tmStr, graph.getNumberOfVertices());
		else
			tm = new DefaultTrafficMatrix(numOfVertices); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
		
		int tmID = DataBase.putTrafficMatrix(tm);
		
		return tmID;
	}
	
	/** Creates sparse traffic-matrix object with the given number of vertices and traffic matrix string representation (the traffic matrix may also be an empty String or null).
	 *  If the traffic matrix string is an empty String or null, then DefaultTrafficMatrix will be created. 
	 *  @param numOfVertices number of vertices in the corresponding graph
	 *  @param traffic matrix String
	 *  @return Index of the traffic matrix in the Database */
	public int createSparse(int numOfVertices, String tmStr){
		AbsTrafficMatrix tm = null;
		if (tmStr != null && !tmStr.isEmpty())
			tm = new SparseTrafficMatrix(tmStr, numOfVertices); // WeightsLoader.loadWeightsFromString(tmStr, graph.getNumberOfVertices());
		else
			tm = new DefaultTrafficMatrix(numOfVertices); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
		
		int tmID = DataBase.putTrafficMatrix(tm);
		
		return tmID;
	}
	
	public Object[] getTrafficMatrix(int tmID){
		AbsTrafficMatrix tm = (AbsTrafficMatrix) DataBase.getTrafficMatrix(tmID);
		Object [][] tmObj = new Object[tm.getDimensions()][tm.getDimensions()];
		
		for (int i=0; i<tm.getDimensions(); i++)
			for (int j=0; j<tm.getDimensions(); j++)
				tmObj[i][j] = new Double(tm.getWeight(i, j));
		
		return tmObj;
	}
	
	/** Releases the pointer to the traffic matrix in the DataBase.
     * @param tmID - The index of the traffic matrix in the DataBase.
 	 * @return 0. */
    public int destroy(int tmID){
    	DataBase.releaseTrafficMatrix(tmID);
    	return 0;
    }

	/** Multiplies the TM by a constant factor. 
     * @param tmID - The index of the traffic matrix in the DataBase.
 	 * @return 0. */
    public int mul(int tmID, double a){
		AbsTrafficMatrix tm = (AbsTrafficMatrix) DataBase.getTrafficMatrix(tmID);
		tm.mul(a);
    	return 0;
    }

}
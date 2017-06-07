package server.sato;


import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.Network;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.TrafficMatrixBC;
import algorithms.centralityAlgorithms.sato.SatoGraphBuilder;
import algorithms.centralityAlgorithms.sato.TrastBC;
import algorithms.centralityAlgorithms.sato.TrastUpperBoundBC;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.clustering.Clustering;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class TrastBCController {

	public static final String ALIAS = "TrastBC";

	//TODO Separate out sato construction
	public int createBC(int clusteringID) throws Exception {
		Clustering<Index,BasicVertexInfo> c = (Clustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(clusteringID);
		SatoGraphBuilder sgb = new SatoGraphBuilder(c, ShortestPathAlg.BFS);
		GraphInterface<Index,BasicVertexInfo> sato = sgb.buildSATOGraph();
		AbsTrafficMatrix tm = sgb.getTrafficMatrix();
		TrafficMatrixBC tmbc = new TrafficMatrixBC(ShortestPathAlg.DIJKSTRA, sato, tm, new DummyProgress(), 0);
		tmbc.run();
		int algid = DataBase.putAlgorithm(tmbc, DataBase.getNetworkOfAlgorithm(clusteringID));
		return algid;
	}

	/** 
	 * Creates Trast betweenness centrality algorithm.
	 */	
	@Deprecated
	public int create(int clusteringId) throws Exception
	{

		Clustering<Index,BasicVertexInfo> c = (Clustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(clusteringId);
		TrastBC bcAlg = null;
		if(c!=null)
			bcAlg = new TrastBC(c, ShortestPathAlg.BFS);
		int algID = DataBase.putAlgorithm(bcAlg, DataBase.getNetworkOfAlgorithm(clusteringId));
		return algID;
	}

	/**
	 * Calculates upper bound on the BC of border vertices.
	 * Precondition: The network netID is a TRAST network
	 * @param netID
	 * @return
	 */
	public int createUpperBoundBC(int netID) {
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
		BrandesBC upperBC = new TrastUpperBoundBC(graph);
		upperBC.run();
		int algID = DataBase.putAlgorithm(upperBC, netID);
		return algID;
	}

	/**
	 * Creates a TRAST network from the input clustering and outputs its ID
	 * @param clusteringID
	 * @return
	 */
	public int createTrast(int clusteringID) throws Exception{
		Clustering<Index,BasicVertexInfo> c = (Clustering<Index,BasicVertexInfo>)DataBase.getAlgorithm(clusteringID);
		SatoGraphBuilder sgb = new SatoGraphBuilder(c, ShortestPathAlg.BFS);
		GraphInterface<Index,BasicVertexInfo> sato = sgb.buildSATOGraph();
		AbsTrafficMatrix tm = sgb.getTrafficMatrix();
		int netID = DataBase.putNetwork(new Network("satoGraph", sato));
		int tmID = DataBase.putTrafficMatrix(tm);
		return netID;
	}

	public int run(int algId){
		BrandesBC bcAlg = (BrandesBC) DataBase.getAlgorithm(algId);
		bcAlg.run();
		return algId;
	}

	public int getSatoNoOfEdges(int algId){
		BrandesBC bcAlg = (BrandesBC) DataBase.getAlgorithm(algId);
		return bcAlg.getGraph().getNumberOfEdges();
	}

	public int getSatoNoOfTransitEdges(int algID, int clusterId) {
		BrandesBC bcAlg = (BrandesBC) DataBase.getAlgorithm(algID);
		int i = 0;
		for(AbstractSimpleEdge<Index,BasicVertexInfo> e: bcAlg.getGraph().getEdges()){
			if ( VertexFactory.isVertexInfo(bcAlg.getGraph().getVertex(e.getV0())) 
					&& VertexFactory.isVertexInfo(bcAlg.getGraph().getVertex(e.getV1())) 
					&&((VertexInfo)bcAlg.getGraph().getVertex(e.getV0())).isBorder() 
					&& ((VertexInfo)bcAlg.getGraph().getVertex(e.getV1())).isBorder()  )
				i++;
		}
		return i;
	}

	/** Returns the betweenness value of the given vertex in the given Brandes algorithm instance.
	 * @param algorithm index
	 * @param vertex
	 * @return betweenness value */
	public double getBetweenness(int algId, int v){
		BrandesBC bcAlg = (BrandesBC) DataBase.getAlgorithm(algId);
		return bcAlg.getCentrality(v);
	}

	/** Returns an array of betweenness values of the given vertices in the given Brandes algorithm instance.
	 * The order of the betweenness values in the array corresponds to the order of the given vertices.
	 * @param algorithm index
	 * @param array of vertices
	 * @return array of betweenness values */
	public Object[] getBetweenness(int algID, int[] vertices){
		Object [] betweennessValues = new Object [vertices.length];
		BrandesBC bcAlg = (BrandesBC)DataBase.getAlgorithm(algID);
		for (int i = 0; i < vertices.length; i++)
			betweennessValues[i] = bcAlg.getCentrality(vertices[i]);
		return betweennessValues;
	}

	/** Returns an array of betweenness values of all vertices in the given Brandes algorithm instance.
	 * The order of the betweenness values in the array corresponds to the order of the vertices in the graph.
	 * @param algorithm index
	 * @return array of betweenness values */
	public Object[] getBetweenness(int algID){
		BrandesBC bcAlg = (BrandesBC)DataBase.getAlgorithm(algID);
		double[] bc = bcAlg.getCentralitites();
		Object[] bVals = new Object[bc.length];
		for (int i = 0; i < bVals.length; i++)
			bVals[i] = new Double(bc[i]);
		return bVals;
	}

	/** Removes the given algorithm from the Database maps.
	 * @param algorithm index
	 * @return 0 */
	public int destroy(int algID){
		DataBase.releaseAlgorithm(algID);
		return 0;
	}

}
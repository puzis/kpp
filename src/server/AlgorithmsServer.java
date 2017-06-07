package server;

import java.io.BufferedOutputStream;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.xmlrpc.metadata.XmlRpcSystemImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import server.closeness.ClosenessController;
import server.closeness.FormulaController;
import server.closeness.GroupClosenessController;
import server.clustering.BudgetedGreedyClusteringController;
import server.common.LoggingManager;
import server.common.ServerConstants;
import server.degree.DegreeController;
import server.degree.GroupDegreeController;
import server.dfbnb.DfbnbController;
import server.execution.ExecutionController;
import server.group.GroupController;
import server.mobility.CongestionAwareBCController;
import server.network.NetworkController;
import server.randomWalkBetweenness.RWBController;
import server.rbc.ContributionVRBCController;
import server.rbc.FasterGRBCController;
import server.rbc.FasterSRBCController;
import server.rbc.GRBCController;
import server.rbc.SRBCController;
import server.rbc.StatefullVRBCController;
import server.rbc.VRBCController;
import server.saritKraus.SKController;
import server.sato.TrastBCController;
import server.shortestPathBetweenness.BrandesController;
import server.shortestPathBetweenness.FasterBCController;
import server.shortestPathBetweenness.GBCController;
import server.shortestPathBetweenness.HyperBrandesController;
import server.sssp.SSSPController;
import server.structuralEquivalence.StructuralEquivalenceController;
import server.trafficMatrix.TrafficMatrixController;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class AlgorithmsServer 
{
	public static final String ALIAS = "Server";
	private static String m_version = "2.1.7";
	private static WebServer m_webServer = null;
	private static ShortestPathAlg m_shortestPathAlgType = ShortestPathAlg.BFS;

	/** Loads server's configurations from the given file.
	 * The configurations include the port the server listens on and the shortest path algorithm type. 
	 * 
	 * @param filename
	 * @throws IOException */
	
	private static void loadServerProperties(String filename) throws IOException
	{
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream(filename));
		String portStr = properties.getProperty(ServerConstants.PORT, "8080");
		String mathKernelPath = properties.getProperty(ServerConstants.MATHKERNEL_ABS_PATH, "");//"C:/Program Files/Wolfram Research/Mathematica/8.0"
		ServerConstants.setMathKernelPath(mathKernelPath);
		ServerConstants.setPort(Integer.parseInt(portStr));

		//		int shortestPathAlgorithm = Integer.parseInt(properties.getProperty(ServerConstants.SHORTEST_PATH_ALG, Integer.toString(ShortestPathAlgorithmInterface.BFS_ALGORITHM)));
		//		m_shortestPathAlgType = shortestPathAlgorithm;
	}

	/** Saves server's configurations to the given file.
	 * The configurations include the port the server listens on. 
	 * 
	 * @param filename
	 * @throws IOException */
	private static void saveServerProperties(String filename) throws IOException
	{
		Properties properties = new Properties();
		properties.setProperty(ServerConstants.PORT, Integer.toString(ServerConstants.getPort()));
		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(filename));
		properties.storeToXML(os, "", ServerConstants.UTF_ENCODING);
		os.close();
	}

	/** Stops the web server.
	 *  @return True.*/
	public boolean stopServer()
	{
		System.out.println("Server is going down.. :)");
		m_webServer.shutdown();
		return true;
	}

	/** @return The memory usage measured in MB. Implemented as totalMemory - freeMemory. */
	public int getMemoryConsumption()
	{
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		long result = (runtime.totalMemory() - runtime.freeMemory())/new Double(Math.pow(2, 20)).longValue();
		return (new Long(result).intValue()); 
	}
	
	public String getCWD() {
		try {
			return new File(".").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isAlive(){	return true;	}

	public String getDataDirectory() {
		return ServerConstants.DATA_DIR;
	}
	/** @return The current version of AlgorithmsServer. */
	public String getVersion(){	return m_version;	}

	public static void main (String [] args) {

		//java.util.logging.Logger.getAnonymousLogger().setLevel(Level.OFF);
		try 
		{
			saveServerProperties(ServerConstants.SERVER_PROPS_XML);
			// Invoke me as <http://localhost:8080/RPC2>.

			/** Loads the type of Shortest path algorithm and sets it as the global Shortest Path Algorithm Type. */
			loadServerProperties(ServerConstants.SERVER_PROPS_XML);
			m_webServer = new WebServer(ServerConstants.getPort());

			XmlRpcServer xmlRpcServer = m_webServer.getXmlRpcServer();

			PropertyHandlerMapping phm = new PropertyHandlerMapping();

			phm.addHandler(StructuralEquivalenceController.ALIAS, StructuralEquivalenceController.class);
			/** Single Source Shortest Path solvers*/
			phm.addHandler(SSSPController.ALIAS, SSSPController.class);			
			/** ShortestPathBetweenness Controllers */
			phm.addHandler(BrandesController.ALIAS, BrandesController.class);
			phm.addHandler(GBCController.ALIAS, GBCController.class);
			phm.addHandler(FasterBCController.ALIAS, FasterBCController.class);
			phm.addHandler(GroupController.ALIAS, GroupController.class);
			phm.addHandler(CongestionAwareBCController.ALIAS, CongestionAwareBCController.class);
			/** RBC Controllers */
			phm.addHandler(FasterGRBCController.ALIAS, FasterGRBCController.class);
			phm.addHandler(GRBCController.ALIAS, GRBCController.class);
			phm.addHandler(FasterSRBCController.ALIAS, FasterSRBCController.class);
			phm.addHandler(SRBCController.ALIAS, SRBCController.class);
			phm.addHandler(ContributionVRBCController.ALIAS, ContributionVRBCController.class);
			phm.addHandler(StatefullVRBCController.ALIAS, StatefullVRBCController.class);
			phm.addHandler(VRBCController.ALIAS, VRBCController.class);
			/** Closeness Controllers */
			phm.addHandler(ClosenessController.ALIAS, ClosenessController.class);
			phm.addHandler(GroupClosenessController.ALIAS, GroupClosenessController.class);
			/** Degree Controllers **/
			phm.addHandler(DegreeController.ALIAS, DegreeController.class);
			phm.addHandler(GroupDegreeController.ALIAS, GroupDegreeController.class);
			/** Random Walk Betweenness Controller **/
			phm.addHandler(RWBController.ALIAS, RWBController.class);
			/** Sarit Kraus Algorithm Controller **/
			phm.addHandler(SKController.ALIAS, SKController.class);

			//** Heuristic Search Controllers *//*
			phm.addHandler(DfbnbController.ALIAS, DfbnbController.class);
			//** Network Controller *//*
			phm.addHandler(NetworkController.ALIAS, NetworkController.class);
			//** Traffic Matrix Controller *//*
			phm.addHandler(TrafficMatrixController.ALIAS, TrafficMatrixController.class);
			//** Closeness Formula Controller *//*
			phm.addHandler(FormulaController.ALIAS, FormulaController.class);
			//** Execution Controller *//*
			phm.addHandler(ExecutionController.ALIAS, ExecutionController.class);
			//** General Controller *//*
			phm.addHandler(AlgorithmsServer.ALIAS, AlgorithmsServer.class);

			phm.addHandler(TrastBCController.ALIAS, TrastBCController.class);
			phm.addHandler(BudgetedGreedyClusteringController.ALIAS, BudgetedGreedyClusteringController.class);
			phm.addHandler(HyperBrandesController.ALIAS, HyperBrandesController.class);
			
			XmlRpcSystemImpl.addSystemHandler(phm);

			xmlRpcServer.setHandlerMapping(phm);

			XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
			serverConfig.setContentLengthOptional(false);

			File dataDir = new File(ServerConstants.DATA_DIR);
			if (!dataDir.exists())
				dataDir.mkdir();

			m_webServer.start();


			System.out.println("Server is up and waiting..");
			LoggingManager.getInstance().writeTrace("Server is up and waiting..", "CentralityAlgorithmsServer", "main", null);
		} catch (Exception exception) 
		{
			LoggingManager.getInstance().writeSystem(exception.toString(), ServerConstants.ALGORITHMS_SERVER, "main", exception);
		}

	}
}
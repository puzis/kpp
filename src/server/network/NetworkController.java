package server.network;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.Network;
import server.common.ServerConstants;
import server.execution.AbstractExecution;
import server.network.executions.ImportExecution;
import server.network.executions.LoadExecution;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.GraphPrinter;
import topology.HyperGraphInterface;

public class NetworkController {
	public static final String ALIAS = "Network";
	private static final String DOT = ".";

	/**
	 * Initializes a Network instance and loads the already parsed graph file.
	 * 
	 * @param filename
	 *            - The name of the network to load.
	 * @return index of the load execution.
	 */
	public int loadNetworkAsynch(String filename) {
		LoggingManager.getInstance().writeTrace("Starting network loading.",
				NetworkController.ALIAS, ServerConstants.LOAD_NETWORK, null);
		Network network = new Network(filename);

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new LoadExecution(network);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, NetworkController.ALIAS,
				ServerConstants.LOAD_NETWORK, null);
		return exeID;
	}

	/**
	 * Initializes a Network instance and loads the already parsed graph file.
	 * 
	 * @param filename
	 *            - The name of the network to load.
	 * @return index of the network in the DataBase.
	 */
	public int loadNetwork(String filename) {
		LoggingManager.getInstance().writeTrace("Starting network loading.",
				NetworkController.ALIAS, ServerConstants.LOAD_NETWORK, null);
		String fileName_no_ext = new String(filename);
		if (filename.indexOf(DOT) != -1)
			fileName_no_ext = filename.substring(0, filename.lastIndexOf(DOT));

		Network network = new Network(fileName_no_ext);

		network.loadNetwork(new DummyProgress());

		/** The newly added network is added to the networks map. */
		int netID = DataBase.putNetwork(network);

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, NetworkController.ALIAS,
				ServerConstants.LOAD_NETWORK, null);
		return netID;
	}

	/**
	 * Initializes a Network instance. If the file contents are given then
	 * parses them into a graph. Otherwise, loads the given file and
	 * parses it into a graph.
	 * 
	 * The graph parser is chosen according to the file extension:
	 * *.net -- Pajek graph file with vertex list and edges list.
	 * *.net0 -- Pajek format with zero-based vertex indices 
	 * *.txt -- CAIDA.ORG style AS relationship files
	 * *.sel -- Simple edge list. Every line represents an edge and contains two space separated integers. "#" indicates a comment. 
	 * 
	 * @param filename
	 *            - The name of the network file to parse. The filename must have a valid extension.
	 * @param importedNet
	 *            - The contents of the network file to parse.
	 * @return index of the import execution.
	 */
	public int importNetworkAsynch(String filename, String importedNet) {
		LoggingManager.getInstance().writeTrace("Starting to import network.",
				NetworkController.ALIAS, ServerConstants.IMPORT_NETWORK, null);
		String fileName_no_ext = new String(filename);

		if (filename.indexOf(DOT) != -1)
			fileName_no_ext = filename.substring(0, filename.lastIndexOf(DOT));

		Network network = new Network(fileName_no_ext);

		/**
		 * Create new execution (Runnable). Store execution into database.
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
		AbstractExecution exe = new ImportExecution(network, filename,
				importedNet);
		int exeID = DataBase.putExecution(exe);
		exe.setID(exeID);
		Thread t = new Thread(exe);
		t.start();

		LoggingManager.getInstance().writeTrace(
				ServerConstants.RETURNING_TO_CLIENT, NetworkController.ALIAS,
				ServerConstants.IMPORT_NETWORK, null);
		return exeID;
	}

	/**
	 * Initializes a Network instance. If the file contents are given then
	 * parses them into a graph. Otherwise, loads the given file and
	 * parses it into a graph.
	 * The network format is determined by the file name extension.
	 * NOTE: Current implementation supports directed networks only in Pajek file format. 
	 * @param filename
	 *            - The name of the network file to parse. Filename should have no extension.
	 * @param importedNet
	 *            - The contents of the network file to parse.
	 * @param ext - the extension of the filename representing the file format.
	 * The graph parser is chosen according to the file extension:
	 * *.net -- Pajek graph file with vertex list and edges list.
	 * *.net0 -- Pajek format with zero-based vertex indices 
	 * *.txt -- CAIDA.ORG style AS relationship files
	 * *.sel -- Simple edge list. Every line represents an edge and contains two space separated integers. "#" indicates a comment.
	 *  
	 * @param graphDataStructure
	 *            - Indicates whether the input .net network should be treated as directed or not.
	 *              and also include the graph data structure. 
	 *              this is enum that can be: DI_GRAPH_AS_HASH_MAP/GRAPH_AS_HASH_MAP/OPTIMIZED_GRAPH_AS_ARRAY
	 *              or DEFAULT (that is the GRAPH_AS_HASH_MAP)
	 * @param vertexStructure - The vertex Structure:
     * 		"Vertex" - m_vertexNum,m_label and the coordinates
     * 		"VertexInfo" - will include all possible information about the vertex  
	 * @return index of the network in the DataBase.
	 */
	public int importNetwork(String filename, String importedNet, String ext, String graphDataStructure, String vertexInfoType) {
		LoggingManager.getInstance().writeTrace("Starting to import network.",
				NetworkController.ALIAS, ServerConstants.IMPORT_NETWORK, null);
		String fileName_no_ext = new String(filename);
		int netID =-1; 

		if (ext!=null && ext.length()>0 && filename.lastIndexOf(ext)!=-1)
			fileName_no_ext = filename.substring(0, filename.lastIndexOf(ext)-1);
		else if (filename.indexOf(DOT) != -1)
			fileName_no_ext = filename.substring(0, filename.lastIndexOf(DOT));

		Network network = new Network(fileName_no_ext);
		try
		{
			network.importNetwork(
					new DummyProgress(), 
					filename, 
					importedNet, 
					ext, 
					GraphFactory.GraphDataStructure.valueOf(graphDataStructure),
					GraphFactory.VertexInfoType.valueOf(vertexInfoType)
				);
	
			/** The newly added network is added to the networks map. */
			netID = DataBase.putNetwork(network);
	
			LoggingManager.getInstance().writeTrace(
					ServerConstants.RETURNING_TO_CLIENT, NetworkController.ALIAS,
					ServerConstants.IMPORT_NETWORK, null);
		}
		catch (IllegalArgumentException ex)
		{
			LoggingManager.getInstance().writeSystem("Couldn't import, illegalArgument graph data structure " + filename + "\n" + importedNet, ServerConstants.NETWORK, ServerConstants.IMPORT_NETWORK, ex);
		}
		return netID;
			
	}
	/**
	 * Initializes a Network instance. If the file contents are given then
	 * parses them into a graph. Otherwise, loads the given file and
	 * parses it into a graph.
	 * 
	 * The graph parser is chosen according to the file extension:
	 * *.net -- Pajek graph file with vertex list and edges list.
	 * *.net0 -- Pajek format with zero-based vertex indices 
	 * *.txt -- CAIDA.ORG style AS relationship files
	 * *.sel -- Simple edge list. Every line represents an edge and contains two space separated integers. "#" indicates a comment. 
	 * 
	 * @param filename
	 * 				File name to import (including extension). 
	 * @param importedNet 
	 * 				The file contents.
	 * @return
	 */
	public int importNetwork(String filename, String importedNet) {
		return importNetwork(filename, importedNet, false);
	}
	
	public int importNetwork(String filename, String importedNet, boolean directed) {
		String ext = null;
		if (filename.lastIndexOf(DOT) != -1)
			ext = filename.substring(filename.lastIndexOf(DOT)+1);
		if (!directed)
			return importNetwork(filename, importedNet, ext, "GRAPH_AS_HASH_MAP");
		else
			return importNetwork(filename, importedNet, ext, "DI_GRAPH_AS_HASH_MAP");
	}
	
	/**
	 * Initializes a Network instance. If the file contents are given then
	 * parses them into a graph. Otherwise, loads the given file and
	 * parses it into a graph.
	 * 
	 * 
	 * @param filename
	 * 				File name to import (without extension). 
	 * @param importedNet 
	 * 				The file contents.
	 * @param ext
	 * 				The graph parser is chosen according to the file extension:
	 * 				*.net -- Pajek graph file with vertex list and edges list.
	 * 				*.net0 -- Pajek format with zero-based vertex indices 
	 * 				*.txt -- CAIDA.ORG style AS relationship files
	 * 				*.sel -- Simple edge list. Every line represents an edge and contains two space separated integers. "#" indicates a comment. 
	 * @return
	 */
	public int importNetwork(String filename, String importedNet, String ext) {
		return importNetwork(filename, importedNet, ext, "GRAPH_AS_HASH_MAP");
	}
	
	/***
	 * "VertexInfo" the default vertex structure 
	 * @return
	 */
	public int importNetwork(String filename, String importedNet, String ext, String graphDataStructure) {
		return importNetwork(filename,importedNet, ext, graphDataStructure, "VERTEX_INFO") ;
	}

	
	/**
	 * Checks if the network ID is known to the server
	 * @param netID
	 * @return 0 if network exists, -1 otherwise.
	 */
	public int networkExists(int netID){
		if (DataBase.networkExists(netID))
			return 0;
		return -1;
	}

	/**
	 * Returns the string representation of the graph stored in the network with
	 * the given index in the database.
	 * 
	 * @param netID
	 *            - given network
	 * @return string representation of the network graph.
	 */
	public String getGraphRepr(int netID) {
		GraphPrinter printer = new GraphPrinter(DataBase.getNetwork(netID)
				.getGraphSimple());
		String res = printer.getVerticesStr() + printer.getEdgesStr(); 
		return res;
	}
	
	/***
	 * will create graph with pajek representation and will restore it in text file
	 * at data directory.
	 * the file name is the network name 
	 * the extention is .net0 (with 0-base)
	 * @param netID 
	 * 		-given network
	 * @return
	 * 		-String with graph at pajek representation
	 */
	public String storeGraphPajek(int netID) {
		String str="";
		try
		{
			GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(netID).getGraphSimple();
			String fileName = DataBase.getNetwork(netID).getName();
			GraphPrinter printer = new GraphPrinter(graph);
			StringBuilder res = new StringBuilder();
			res.append(printer.getVerticesStr()).append(printer.getEdgesStr());

			File simulationFile = new File(ServerConstants.DATA_DIR + fileName +".net0");
			BufferedOutputStream out = null;
			try{
				out = new BufferedOutputStream(new FileOutputStream(simulationFile));
				out.write(res.toString().getBytes());
				out.flush();
			}
			catch(IOException ex){
				LoggingManager.getInstance().writeTrace("An IOException has occured while trying to save the analysis to file.", "GraphAnalyzer", "storeAnalysis", ex);
				throw new IOException("An IOException has occured while trying to save the analysis to file.\n" + ex + "\n" + LoggingManager.composeStackTrace(ex));
			}
			finally{
				try{
					if (out != null)
					{
						str = res.toString();
						out.flush();
						out.close();	
					}
				}
				catch(IOException ex)
				{
					LoggingManager.getInstance().writeSystem("An IOException has occured while trying to close the output stream after writting the file.", "GraphAnalyzer", "storeAnalysis", ex);
					throw new IOException("An IOException has occured while trying to close the output stream after writting the file.\n" + ex + "\n" + LoggingManager.composeStackTrace(ex));
				}
			}
			
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		return str;
	}
	
	/***
	 * 
	 * @param netID - ID of the original graph
	 * @param targetGraphDataStructure - the desired graph data structure 
	 * @return newNetID - ID of the new copy
	 * 
	 * this method will create new instance of with the desired Graph Data Structure. 
	 * converting undirected graph into directed graph will need special treatment: 
	 * 		for each undirected edge at the oldGraph (from U to v),
	 * 		will be added second edge at the opposite direction (from V to U)  
	 * 		for example, the undirected edge:
	 * 		[u,v]  -> will becomes into two edges [u,v][v,u]
	 * 
	 * conversion from simple (directed / undirected) graphs to Hyper Graphs is
	 * done by treating every edge as a set of size 2. Note that duplicate edge 
	 * may exist if Directed Graph is copied to Uniderected Hyper Graph.
	 * 
	 * during conversion from hyper graph to simple graph every hyper edge
	 * is replaced by a clique. 
	 */
	public int createCopy(int netID, String targetGDSName){
		GraphFactory.GraphDataStructure targetGDS = GraphFactory.GraphDataStructure.valueOf(targetGDSName);
		
		Network oldNet = DataBase.getNetwork(netID);
		
		HyperGraphInterface<Index,BasicVertexInfo> newGraph = oldNet.getGraphAs(targetGDS);
		
		Network newNet = new Network(targetGDS.toString()+ "_" +DataBase.getNetwork(netID).getName(),newGraph);
		newNet.loadNetwork(new DummyProgress());//What is it for?
		int newNetID = DataBase.putNetwork(newNet);
		
		return newNetID;
	}
	

	/**
	 * Stores the already parsed graph in a file with extension .graph.
	 * 
	 * @param netID
	 *            - The index of the network in the DataBase.
	 * @return index of the store execution.
	 */
	public int storeGraphAsynch(int netID) {
		LoggingManager.getInstance().writeTrace("Starting saving network graph.",
				NetworkController.ALIAS, ServerConstants.STORE_GRAPH, null);
		Network network = DataBase.getNetwork(netID);

		/**
		 * Create new execution (Runnable). Store execution into database
		 * Create new thread, give it the execution and start it. Return exeID.
		 * (At the end of run() the execution parameters of progress and success
		 * are updated.)
		 */
//		AbstractExecution exe = new SaveAnalysisExecution(network);
//		int exeID = DataBase.putExecution(exe);
//		exe.setID(exeID);
//		Thread t = new Thread(exe);
//		t.start();
//
//		LoggingManager.getInstance().writeTrace(
//				ServerConstants.RETURNING_TO_CLIENT, NetworkController.ALIAS,
//				ServerConstants.STORE_GRAPH, null);
//		return exeID;
		LoggingManager.getInstance().writeMessage("SaveAnalysisExecution class not in SVN", 
				NetworkController.ALIAS, ServerConstants.STORE_GRAPH, null);
		throw new NotImplementedException();
	}

	/**
	 * Stores the already parsed graph in a file with extension .graph.
	 * 
	 * @param netID
	 *            - The index of the network in the DataBase.
	 * @return true if the operation has been successful, and false otherwise.
	 */
	public boolean storeGraph(int netID) {
		return DataBase.getNetwork(netID).storeGraph();
	}

	/**
	 * Retrieves the number of vertices in the network. This method rebuilds the
	 * graph on every call.
	 * 
	 * @param netID
	 *            - The index of the network in the DataBase.
	 * @return the number of vertices.
	 */
	public int getNumberOfVertices(int netID) {
		return DataBase.getNetwork(netID).getNumberOfVertices();
	}

	public Object[] getVertices(int netID) {
		Network n = DataBase.getNetwork(netID);
		
		HyperGraphInterface<Index, BasicVertexInfo> g = n.getGraph();
		Integer indices[] = new Integer[g.getNumberOfVertices()];
		int i = 0;
		for(Index v : g.getVertices()) {
			indices[i++] = v.intValue();
		}
		return indices;
	}
	/**
	 * Retrieves the number of edges in the network. This method rebuilds the
	 * graph on every call.
	 * 
	 * @param netID
	 *            - The index of the network in the DataBase.
	 * @return the number of edges.
	 */
	public int getNumberOfEdges(int netID) {
		return DataBase.getNetwork(netID).getNumberOfEdges();
	}

	/**
	 * Releases the pointer to the network in the DataBase.
	 * 
	 * @param netID
	 *            - The index of the network in the DataBase.
	 * @return 0.
	 */
	public int destroy(int netID) {
		DataBase.releaseNetwork(netID);
		return 0;
	}
	
}
package server.common;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;

import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.HyperGraphInterface;
import topology.graphParsers.GraphParserFactory;
import topology.graphParsers.common.FileRegularExpression;

public class Network implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_networkName = null;
	private HyperGraphInterface  <Index,BasicVertexInfo> m_serGraph = null;
	
	/** CONSTANTS */
	private static final String _GRAPH = ".graph";
	private static final String NEW_LINE = "\n";
	
	/** The constructor sets the given name.
	 * @param networkName - name that represent the network
	 */
	public Network(String networkName){
		this.m_networkName = networkName;
	}

    /** The constructor sets the given name and creates the Serializable graph representation
     * @param networkName - name that represent the network
     * @param graph - the graph object
     */
	public Network(String networkName, HyperGraphInterface<Index,BasicVertexInfo> graph){
		m_networkName = networkName;
		m_serGraph = GraphFactory.copy(graph);
	}
	
	/** Loads from the data directory the already parsed graph file (which has the network's name).
	 * During the loading, updates the given AbstractExecution object.
	 * @param exe - represent the status
	 * @return True if the load has been successful and False otherwise.
	 */
	public boolean loadNetwork(AbstractExecution exe)
	{
		reportSuccess(exe, AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		boolean success ;
		try{
			File graphFile = new File(ServerConstants.DATA_DIR + m_networkName + _GRAPH);
		
			if (graphFile.exists()){
				try{
					
					ObjectInputStream in = null;
					try{
						in = new ObjectInputStream(new FileInputStream(graphFile));
						m_serGraph = (HyperGraphInterface<Index, BasicVertexInfo>)in.readObject();
			        }
					catch(ClassNotFoundException ex){
						LoggingManager.getInstance().writeSystem("A ClassNotFoundException has occured while trying to read the graph from file " + graphFile.getName(), ServerConstants.NETWORK, ServerConstants.LOAD_NETWORK, ex);
						throw new Exception("A ClassNotFoundException has occured while trying to read the graph from file " + graphFile.getName() + "\n" + ex.getMessage());
					}
					catch(IOException ex){
						LoggingManager.getInstance().writeSystem("An IOException has occured while trying to read the graph from file " + graphFile.getName(), ServerConstants.NETWORK, ServerConstants.LOAD_NETWORK, ex);
						throw new Exception("An IOException has occured while trying to read the graph from file " + graphFile.getName() + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex));
					}
					finally{
						try{
							if (in != null)
								in.close();
						}
						catch(IOException ex){
							LoggingManager.getInstance().writeSystem("An IOException has occured while trying to close the input stream after reading the file: " + graphFile.getName(), ServerConstants.NETWORK, ServerConstants.LOAD_NETWORK, ex);
							throw new Exception("An IOException has occured while trying to close the input stream after reading the file: " + graphFile.getName() + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex));
						}
					}
					
					reportSuccess(exe, AbstractExecution.PHASE_SUCCESS, AbstractExecution.PHASE_COMPLETE);
					success = true;
				}
				catch(Exception ex){
					reportSuccess(exe, AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
					LoggingManager.getInstance().writeSystem("Couldn't load " + m_networkName + ".graph.", ServerConstants.NETWORK, ServerConstants.LOAD_NETWORK, ex);
					success = false;
				}
			}
			else{
				reportSuccess(exe, AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
				LoggingManager.getInstance().writeSystem("The file " +  ServerConstants.DATA_DIR + m_networkName + ".graph doesn't exist.", ServerConstants.NETWORK, ServerConstants.LOAD_NETWORK, null);
				success = false;
			}
		}
		catch(RuntimeException ex){
			reportSuccess(exe, AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
			LoggingManager.getInstance().writeSystem("The file " +  ServerConstants.DATA_DIR + m_networkName + ".graph doesn't exist.", ServerConstants.NETWORK, ServerConstants.LOAD_NETWORK, null);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing graph loading.", ServerConstants.NETWORK, ServerConstants.LOAD_NETWORK, null);
		return success;
	}

    /**
     * if the user didn't specified the extension and
     * didn't specified the Vertex structure - the default is "VertexInfo", most generic Vertex.
     * if the file is oregon-file -> group 2 should be null
     * if the file is not oregon-file -> group 1 should be null
     * @param exe - represent the status
     * @param filename_with_extension - filename.extension
     * @param importedNet - the content of the network
     * @param graphDataStructure - graph structure
     * @return True if the import has been successful and False otherwise.
     */
	public boolean importNetwork(AbstractExecution exe, String filename_with_extension, String importedNet, GraphFactory.GraphDataStructure graphDataStructure) {
        String ext = FileRegularExpression.setExt( filename_with_extension);
		return importNetwork(exe,filename_with_extension,importedNet,ext,graphDataStructure);
	}

    /**
     * if the user didn't specified the Vertex structure the default is "VertexInfo", most generic Vertex
     * @param exe - represent the status
     * @param filename_with_extension - filename.extension
     * @param importedNet - the content of the network
     * @param ext - file extension
     * @param graphDataStructure -  graph structure
     * @return True if the import has been successful and False otherwise.
     */
	public boolean importNetwork(AbstractExecution exe, String filename_with_extension, String importedNet, String ext, GraphFactory.GraphDataStructure graphDataStructure){
		return importNetwork( exe,  filename_with_extension,  importedNet,  ext,  graphDataStructure, GraphFactory.DEFAULT_VERTEX_INFO_TYPE);
	}

	
	/** If the given file contents String is not null and not empty, then the contents are parsed into a graph.
     * Otherwise, a file with the given name is loaded from the data directory and is parsed into a graph.
     * During the loading, updates the given AbstractExecution object.
     * @param filename_with_extension - The name of the network file to parse.
     * @param importedNet - The contents of the network file to parse.
     * @param vertexInfoType - The vertex Structure:
     * 		"Vertex" - m_vertexNum,m_label and the coordinates
     * 		"VertexInfo" - will include all possible information about the vertex  
     * @return True if the import has been successful and False otherwise.
     */
	public boolean importNetwork(AbstractExecution exe, String filename_with_extension, String importedNet, String ext, GraphFactory.GraphDataStructure gds, GraphFactory.VertexInfoType vertexInfoType)
	{
		reportSuccess(exe, AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		boolean success ;
		try{
			setPhaseProgress(exe, 0);

            m_serGraph = GraphFactory.createGraph(gds);

			GraphParserFactory.getGraph(ServerConstants.DATA_DIR, filename_with_extension, importedNet, ext, exe, 1, m_serGraph,vertexInfoType);
			
			reportSuccess(exe, AbstractExecution.PHASE_SUCCESS, AbstractExecution.PHASE_COMPLETE);
			success = true;
		}
		catch(RuntimeException ex){
			reportSuccess(exe, AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
			LoggingManager.getInstance().writeSystem("Couldn't import " + m_networkName + NEW_LINE + importedNet, ServerConstants.NETWORK, ServerConstants.IMPORT_NETWORK, ex);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing network importing.", ServerConstants.NETWORK, ServerConstants.IMPORT_NETWORK, null);
		return success;
	}
	
	/** Writes the graph to a file with the network's name and saves it into data directory. 
	 * @return True if the storing has been successful and False otherwise.
	 */
	public boolean storeGraph()
	{
		boolean success = true;
		String graphFileName = m_networkName + _GRAPH;
		
		try{
			File outFile = new File(ServerConstants.DATA_DIR + graphFileName);
			ObjectOutputStream out = null;
			try{
				out = new ObjectOutputStream(new FileOutputStream(outFile));
		        out.writeObject(m_serGraph);
			}
			catch(IOException ex){
				LoggingManager.getInstance().writeSystem("An IOException has occured while trying to save graph representation to file " + outFile.getName(), ServerConstants.NETWORK, ServerConstants.STORE_GRAPH, ex);
				throw new IOException("An IOException has occured while trying to save graph representation to file " + outFile.getName() + "\n" + ex.getMessage());
			}
			finally{
				try{
					if (out != null){
						out.flush();
						out.close();
					}
				}
				catch(IOException ex){
					LoggingManager.getInstance().writeSystem("An IOException has occured while trying to close the output stream after writting the file: " + outFile.getName(), ServerConstants.NETWORK, ServerConstants.STORE_GRAPH, ex);
					throw new IOException("An IOException has occured while trying to close the output stream after writting the file: " + outFile.getName() + "\n" + ex.getMessage());
				}
			}
		}
		catch (RuntimeException rex){
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured while storing graph representation.", ServerConstants.NETWORK, ServerConstants.STORE_GRAPH, rex);
			success = false;
		}
		catch (Exception ex){
			LoggingManager.getInstance().writeSystem("An exception has occured while storing graph representation:\n" + ex.getMessage() + NEW_LINE + ex.getStackTrace(), ServerConstants.NETWORK, ServerConstants.STORE_GRAPH, ex);
			success = false;
		}
		LoggingManager.getInstance().writeTrace("Finishing storing graph representation.", ServerConstants.NETWORK, ServerConstants.STORE_GRAPH, null);
		return success;
	}
	


	
	/** 
	 * @return reference to the network's graph or a new simple graph representation. 
	 **/
	public GraphInterface<Index,BasicVertexInfo> getGraphSimple(){
		if (GraphFactory.isSimple(m_serGraph.getType()))
			return (GraphInterface<Index,BasicVertexInfo>)m_serGraph;
		else
			return GraphFactory.copyAsSimple(m_serGraph);	
	}
	
	/** 
	 * @return reference to the network's graph. 
	 **/
	public HyperGraphInterface<Index,BasicVertexInfo> getGraph(){
		return m_serGraph;			
	}
	/** 
	 * @return copy the network's graph. 
	 **/
	public HyperGraphInterface<Index,BasicVertexInfo> getGraphAs(GraphFactory.GraphDataStructure gds){
		return GraphFactory.copyAs(m_serGraph, gds);
	}
	
	/** @return the network's name. */
	public String getName(){	return m_networkName;	}

    /**@return number of edges*/
	public int getNumberOfEdges(){return m_serGraph.getNumberOfEdges();}
    /**@return number of vertices*/
	public int getNumberOfVertices(){return m_serGraph.getNumberOfVertices();}
	
	private void setPhaseProgress(AbstractExecution exe, double progress){	exe.setProgress(progress);	}
	private void setPhaseSuccess(AbstractExecution exe, int success){	exe.setSuccess(success);	}
	private void reportSuccess(AbstractExecution exe, int success, double progress)
	{
		setPhaseProgress(exe, progress);
		setPhaseSuccess(exe, success);
	}
}
package topology.graphParsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;

import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.EdgeInfo;
import topology.GraphFactory;
import topology.GraphDataInterface;
import topology.GraphRegularExpressions;

import topology.BasicVertexInfo;
import topology.VertexFactory;

public class OmnetNetworkParser extends NetworkGraphParser {

    private static final String EXTENSION = "onet";
	private static final int _base = 1;


	@Override
	public void analyzeFile(BufferedReader reader, AbstractExecution progress, double percentage, GraphDataInterface<Index,BasicVertexInfo> graph, GraphFactory.VertexInfoType vertexInfoType) {
		BasicVertexInfo vInfo ;
		int numOfVertices = readFirstLine(reader);
		String line;
		try{
			// Read vertices
			for (int i=0; i<numOfVertices; i++){
				line = reader.readLine();
				String[] tokens = line.split(" ");
				int vertexNum = Integer.parseInt(tokens[0]);
				String label = tokens[1].substring(1, tokens[1].length()-1);
				vInfo = VertexFactory.createVertexStructure(vertexInfoType, vertexNum, label);
				graph.addVertex(Index.valueOf(vertexNum - _base), vInfo);
                updateLoadProgress(progress, percentage);
			}
			// Read edges
			line = reader.readLine(); // Read line '*Arcs'
			while (!(line = reader.readLine()).equalsIgnoreCase("*Edges")){
				String[] tokens = line.split("[\\s]+");
				int vertexNum0 = Integer.parseInt(tokens[0]);
				int vertexNum1 = Integer.parseInt(tokens[1]);
				int weight = Integer.parseInt(tokens[2]);

				Index[] vertices = new Index[]{
						Index.valueOf(vertexNum0 - _base),
						Index.valueOf(vertexNum1 - _base)};				
                graph.addEdge(Arrays.asList(vertices), new EdgeInfo<Index,BasicVertexInfo>(weight));
                updateLoadProgress(progress, percentage);
			}
		}
		catch(IOException ex){
			
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "OmnetNetworkParser", "readVertices", ex);
		}
		finally{
			cleanClose(reader);
		}
	}
	
	private void cleanClose(BufferedReader reader){
    	try{
    		if (reader != null)
    			reader.close();
        }
        catch(IOException ex){
        	LoggingManager.getInstance().writeSystem("An exception has occured while closing BufferedReader.\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "OmnetNetworkParser", "cleanClose", ex);
        }
    }

	private int readFirstLine(BufferedReader reader){
    	try{
    		String firstLine = reader.readLine();
    		Matcher verticesNumber = GraphRegularExpressions.VERTICES_BEGINING.matcher(firstLine);
        	if (verticesNumber.find())
        		return Integer.parseInt(verticesNumber.group(1));
        	else 
        	{
        		LoggingManager.getInstance().writeSystem("Couldn't read fisrt line of the network file.\nFirst line: " + firstLine , "OmnetNetworkParser", "readFirstLine", null);
        		return -1;
        	}
    	}
    	catch(IOException ex){
    		LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "OmnetNetworkParser", "readFirstLine", ex);
    		return -1;
    	}
    }
	
	@Override
	public String getextension() {
		return EXTENSION;
	}


}

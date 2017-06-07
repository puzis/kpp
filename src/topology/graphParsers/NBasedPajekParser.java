package topology.graphParsers;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Matcher;

import javolution.util.FastMap;
import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.EdgeInfo;
import topology.GraphDataInterface;
import topology.GraphFactory;
import topology.GraphRegularExpressions;
import topology.BasicVertexInfo;
import topology.VertexFactory;

import common.Pair;

/**
 * This class loads graphs from pajek formated files with node indices
 * starting at <i>base</i> (constructor parameter).
 * Most of the code taken as is from NetFileParser and only modified to use base as starting index.
 * @author Yuri Bakulin
 *
 */
public class NBasedPajekParser extends NetworkGraphParser {

	private final int _base;


    public  NBasedPajekParser(int base) {
		this._base = base;
	}
	
	
	protected int readFirstLine(BufferedReader reader)
    {
    	try{
    		String firstLine = reader.readLine();
    		Matcher verticesNumber = GraphRegularExpressions.VERTICES_BEGINING.matcher(firstLine);
        	if (verticesNumber.find())
        		return Integer.parseInt(verticesNumber.group(1));
        	else 
        	{
        		LoggingManager.getInstance().writeSystem("Couldn't read fisrt line of the network file.\nFirst line: " + firstLine , "GraphLoader", "readFirstLine", null);
        		return -1;
        	}
    	}
    	catch(IOException ex){
    		LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "readFirstLine", ex);
    		return -1;
    	}
    }
	
	protected int readVertices(int numOfVertices, BufferedReader reader, GraphDataInterface<Index,BasicVertexInfo> graph, int base,GraphFactory.VertexInfoType vertexInfoType)
    {
    	/** KEY - label name in lowercase, VALUE - <label name, label value> */
        FastMap<String, Pair<String, String>> m_lastInfo = new FastMap<String, Pair<String, String>>();
        BasicVertexInfo vInfo;
        String line;
    	try{
    		for (int i = 0; i < numOfVertices && ((line = reader.readLine()) != null); i++)
    		{
    			Matcher verticesLine = GraphRegularExpressions.VERTICES_LINE.matcher(line);
            	if (verticesLine.find())
            	{
            		int vertexNum = Integer.parseInt(verticesLine.group(1));
            		String label = verticesLine.group(2);
            		double x = Double.parseDouble(verticesLine.group(3));
            		double y = Double.parseDouble(verticesLine.group(4));
            		double z = Double.parseDouble(verticesLine.group(5));
            		
            		FastMap<String, Pair<String, String>> info = new FastMap<String, Pair<String, String>>();
            		//int infoIndex = verticesLine.end() + 1;
            		int infoIndex = verticesLine.end();
            		if (line.length() > infoIndex)
            		{
            			String infoStr = line.substring(infoIndex);
            			Matcher vertexInfo = GraphRegularExpressions.OPTIONAL_INFO.matcher(infoStr);
            			while (vertexInfo.find())
            			{
            				String labelName = vertexInfo.group(1);
            				String labelValue = vertexInfo.group(2);

            				if (labelName != null)
            					info.put(labelName.trim().toLowerCase(), new Pair<String, String>(labelName.trim() ,labelValue));
            			}
            		}
            		m_lastInfo.putAll(info);
                    info.putAll(m_lastInfo);
                    vInfo = VertexFactory.createVertexStructure(vertexInfoType,vertexNum - base, label, x, y, z, info);
            		graph.addVertex(Index.valueOf(vertexNum - base), vInfo);
            	}
            	else 
            	{
            		verticesLine = GraphRegularExpressions.VERTICES_LINE_VERSION_2.matcher(line);
            		if (verticesLine.find())
                	{
                		int vertexNum = Integer.parseInt(verticesLine.group(1));
                		vInfo = VertexFactory.createVertexStructure(vertexInfoType, vertexNum, "", 0, 0, 0,  new FastMap<String, Pair<String, String>>());
                		graph.addVertex(Index.valueOf(vertexNum - base), vInfo);
                	}
            	}
    		}
    		return 1;
    	}catch(IOException ex){
    		LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "readVertices", ex);
    		return -1;
    	}
    }
	
	protected int readEdges(BufferedReader reader, GraphDataInterface<Index,BasicVertexInfo> graph, int base)
    {
    	/** KEY - label name in lowercase, VALUE - <label name, label value> */
        FastMap<String, String> m_lastInfo = new FastMap<String, String>();
    	String line;
    	try{
    		while ((line = reader.readLine()) != null)
    		{
    			Matcher edgesLine = GraphRegularExpressions.EDGE_LINE.matcher(line);
            	if (edgesLine.find())
            	{
            		FastMap<String, String> info = new FastMap<String, String>();
            		// If edge data contains only the from/to nodes, assume a weight of one, otherwise parse the data 
            		int infoIndex = edgesLine.end() + 1;
            		if (line.length() > infoIndex)
            		{
            			String infoStr = line.substring(infoIndex);
            			Matcher edgeInfo = GraphRegularExpressions.OPTIONAL_INFO.matcher(infoStr);
            			while (edgeInfo.find())
            			{
            				String labelName = edgeInfo.group(1);
            				String labelValue = edgeInfo.group(2);

            				if (labelName != null)
            					info.put(labelName.trim().toLowerCase(), labelValue);
            			}
            		}
            		m_lastInfo.putAll(info);
                    info.putAll(m_lastInfo);
                    Index[] vertices = new Index[]{
                    		Index.valueOf(Integer.parseInt(edgesLine.group(1)) - base),
                    		Index.valueOf(Integer.parseInt(edgesLine.group(2)) - base)};
            		/** (vertex1, vertex2, width) */
                	graph.addEdge(Arrays.asList(vertices),new EdgeInfo<Index,BasicVertexInfo>(Double.parseDouble(edgesLine.group(3)), info));
            	}
            	else{
            		// Try a simpler format with only the from and to nodes
            		edgesLine = GraphRegularExpressions.SIMPLE_EDGE_LINE.matcher(line);
            		if (edgesLine.find()){
                        Index[] vertices = new Index[]{
                        		Index.valueOf(Integer.parseInt(edgesLine.group(1)) - base),
                        		Index.valueOf(Integer.parseInt(edgesLine.group(2)) - base)};
            			graph.addEdge(Arrays.asList(vertices), new EdgeInfo<Index,BasicVertexInfo>());
            		}
            	}
    		}
    		return 1;
    	}catch(IOException ex){
    		LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "readEdges", ex);
    		return -1;
    	}
    }
	
    private void cleanClose(BufferedReader reader, String msg)
    {
    	System.err.println(msg);
    	try{
    		if (reader != null)
    			reader.close();
        }
        catch(IOException ex)
        {
        	LoggingManager.getInstance().writeSystem("An exception has occured while closing BufferedReader.\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "cleanClose", ex);
        }
    	
//    	System.exit(1);
    }



	@Override
	 public void analyzeFile(BufferedReader reader, AbstractExecution progress, double percentage, GraphDataInterface<Index,BasicVertexInfo> graph,GraphFactory.VertexInfoType vertexInfoType)
	    {
	        int numOfVertices = readFirstLine(reader);
	        	
	        if (numOfVertices == -1){
	        	cleanClose(reader, "Could not read the first line of the graph description properly, exiting the program.");
	        	graph = null;
	        }
	        	
	        int res = readVertices(numOfVertices, reader, graph,this._base,vertexInfoType);
	        if (res == -1){
	        	cleanClose(reader, "Could not read the vertices' description properly, exiting the program.");
	        	graph = null;
	        }
	        updateLoadProgress(progress, percentage);
	        	
	        res = readEdges(reader, graph,this._base);
	        if (res == -1){
	        	cleanClose(reader, "Could not read the edges' description properly, exiting the program.");
	        	graph = null;
	        }
	        updateLoadProgress(progress, percentage);
	        
	        try{
	        	if (reader != null)
	        		reader.close();
	        }
	        catch(IOException ex)
	        {
	        	LoggingManager.getInstance().writeSystem("An exception has occured while closing BufferedReader.\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "analyzeFile", ex);
	        }
	    }

    public void analyzeFile(String filename, AbstractExecution progress, double percentage, GraphDataInterface<Index,BasicVertexInfo> graph){
        analyzeFile(filename, progress, percentage, graph, GraphFactory.DEFAULT_VERTEX_INFO_TYPE);
    }


    public void analyzeFile(String filename, AbstractExecution progress, double percentage, GraphDataInterface<Index,BasicVertexInfo> graph, GraphFactory.VertexInfoType vertexInfoType)
    {
        File file = new File(filename);
        FileInputStream fin = null;
        BufferedReader reader ;
        try
        {
            fin = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fin));
            analyzeFile(reader, progress, percentage, graph, vertexInfoType);
        }
        catch(FileNotFoundException ex)
        {
            LoggingManager.getInstance().writeSystem("Couldn't find the network file: " + filename + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "loadFile", ex);
        }
        finally
        {
            try{
                if (fin != null)
                    fin.close();
            }
            catch(IOException ex)
            {
                LoggingManager.getInstance().writeSystem("Couldn't close FileInputStream to: " + file.getAbsoluteFile() + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "loadFile", ex);
            }
        }
    }


	@Override
	public String getextension() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
}

/**
 * 
 */
package topology.graphParsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.regex.Matcher;

import javolution.util.FastMap;
import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.EdgeInfo;
import topology.GraphFactory;
import topology.GraphDataInterface;
import topology.GraphRegularExpressions;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;



public class SimpleEdgeListParser extends NetworkGraphParser {

	final private static String EXTENSION="sel";



    /** (non-Javadoc)
	 * @see topology.graphParsers.NetworkGraphParser#getextension()
	 **/


	public String getextension() {
		return EXTENSION;
	}


	/** (non-Javadoc)
	 * @see topology.graphParsers.NetworkGraphParser#updateLoadProgress(server.ExecutionProgress, double)
	 **/

	
	
	/**counts the lines in the file
	 * @param filename name of file
	 */
	public int lineCount (String filename){
		int count=0;
		try
        {
         	RandomAccessFile randFile = new RandomAccessFile(filename,"r");
        	long lastRec=randFile.length();
        	randFile.close();
        	FileReader fileRead = new FileReader(filename);
        	LineNumberReader lineRead = new LineNumberReader(fileRead);
        	lineRead.skip(lastRec);
        	count=lineRead.getLineNumber()-1;
        	fileRead.close();
        	lineRead.close();
        	randFile.close();
        }
		catch(IOException ex){
				LoggingManager.getInstance().writeSystem("Couldn't Read from " + filename + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "AsRelationshipParser", "LineCount", ex);		
		}
		return count+1;		
	}


	@Override
	public void analyzeFile(BufferedReader reader,
			AbstractExecution progress, double precentage,
			GraphDataInterface<Index,BasicVertexInfo> graph, GraphFactory.VertexInfoType vertexInfoType) {
		try {
			String line= reader.readLine();
			int i = 0;
			FastMap<Integer,Index> nodes = new FastMap<Integer, Index>();
			BasicVertexInfo vInfo; 
			while (line != null) {
				if (line.charAt(0)=='#') { //skipping comments
					line=reader.readLine();
					continue;
					}
				Matcher verticesLine = GraphRegularExpressions.SIMPLE_EDGE_LINE.matcher(line);
				if (verticesLine.find()) {
					String arg1=verticesLine.group(1);
					String arg2=verticesLine.group(2);
					
					int v1 = Integer.valueOf(arg1);
					if (!nodes.containsKey(v1)) {
						nodes.put(v1, Index.valueOf(i++));
					}
					Index idx1 = nodes.get(v1);
					vInfo = VertexFactory.createVertexStructure(vertexInfoType, idx1.intValue(), arg1);
					graph.addVertex(idx1, (VertexInfo)vInfo);

					
					int v2 = Integer.valueOf(arg2);
					if (!nodes.containsKey(v2)) {
						nodes.put(v2, Index.valueOf(i++));
					}
					Index idx2 = nodes.get(v2);
					vInfo = VertexFactory.createVertexStructure(vertexInfoType,idx2.intValue(),arg1);
					graph.addVertex(idx2,(VertexInfo)vInfo);
					
					graph.addEdge(Arrays.asList(new Index[]{idx1,idx2}),new EdgeInfo<Index,BasicVertexInfo>());
					graph.addEdge(Arrays.asList(new Index[]{idx2,idx1}),new EdgeInfo<Index,BasicVertexInfo>());
					
				}
				line = reader.readLine();
			}
		} catch(IOException ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "AsRelationshipParser", "FillASRelationsList", ex);
		}
    	
        updateLoadProgress(progress, precentage);
	}


}

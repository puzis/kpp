package topology.graphParsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;

import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphDataInterface;
import topology.GraphFactory;
import topology.GraphRegularExpressions;
import topology.VertexFactory;

/**
 * @author Omer Zohar
 *
 */
public class ASOregonParser extends NetworkGraphParser{
	final static private String EXTENSION="oregon";
	//counts the lines in the file



	@Override
	public String getextension() {
		return EXTENSION;
	}



	@Override
	public void analyzeFile(BufferedReader reader,
			AbstractExecution progress, double percentage,
			GraphDataInterface<Index,BasicVertexInfo> graph,GraphFactory.VertexInfoType vertexInfoType) {
		
		String line;
		try {
			line=reader.readLine();
			while (line!=null)
			{
				while (line.charAt(0)=='#'){line=reader.readLine();}//skipping notes
				Matcher verticesLine = GraphRegularExpressions.ASOREGON.matcher(line);
				if (verticesLine.find())//parsing data coloums
				{
					String s1 = verticesLine.group(1);
					Index idx1 = Index.valueOf(Integer.valueOf(s1));
					if (!graph.isVertex(idx1))
						graph.addVertex(idx1, VertexFactory.createVertexStructure(vertexInfoType, graph.getNumberOfVertices(), s1));
					
					String s2 = verticesLine.group(2);
					Index idx2 = Index.valueOf(Integer.valueOf(s2));
					if (!graph.isVertex(idx2))
						graph.addVertex(idx2, VertexFactory.createVertexStructure(vertexInfoType, graph.getNumberOfVertices(), s2));


					graph.addEdge(Arrays.asList(new Index[]{idx1,idx2}),new EdgeInfo<Index,BasicVertexInfo>());
					graph.addEdge(Arrays.asList(new Index[]{idx2,idx1}),new EdgeInfo<Index,BasicVertexInfo>());
				}
				line=reader.readLine();
			}
		}
		catch(IOException ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "AsRelationshipParser", "FillASRelationsList", ex);
		}
	}
	
}

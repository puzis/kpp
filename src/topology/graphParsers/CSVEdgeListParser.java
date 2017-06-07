/**
 * 
 */
package topology.graphParsers;

import java.io.BufferedReader;
import java.io.IOException;

import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphDataInterface;
import topology.GraphFactory;
import topology.VertexFactory;



public class CSVEdgeListParser extends NetworkGraphParser {

	final private static String EXTENSION="e.csv";

    /** (non-Javadoc)
	 * @see topology.graphParsers.NetworkGraphParser#getextension()
	 **/

    @Override
    public String getextension() {
		return EXTENSION;
	}



	@Override
	public void analyzeFile(BufferedReader reader,
			AbstractExecution progress, double percentage,
			GraphDataInterface<Index,BasicVertexInfo> graph, GraphFactory.VertexInfoType vertexInfoType) {
		try {
			String line= reader.readLine();
			int i = 0;
			FastMap<String,Index> nodes = new FastMap<String, Index>();
			BasicVertexInfo vInfo; 
			while (line != null && (!"".equals(line))) {
				if (line.charAt(0)=='#') { //skipping comments
					line=reader.readLine();
					continue;
					}
				
				String[] vertices = line.split(",");
				FastSet<Index> e = new FastSet<Index>();
				for (int j =0;j<vertices.length;j++){
					String vName=vertices[j].trim();
					if (j==vertices.length-1 & vName.length()==0) //Last vertex is simply a newline - ignore it.
						break;

					if (!nodes.containsKey(vName)) {
						Index idx = Index.valueOf(i++);
						nodes.put(vName, idx);
						vInfo = VertexFactory.createVertexStructure(vertexInfoType, idx.intValue(), vName);
						graph.addVertex(idx, vInfo);
					}
					e.add(nodes.get(vName));
				}
				if (e.size()>=2) {
					graph.addEdge(e,new EdgeInfo<Index,BasicVertexInfo>());					
				}
				line = reader.readLine();
                updateLoadProgress(progress, percentage);
            }
		} catch(IOException ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "CSVEdgeListParser", "FillASRelationsList", ex);
		}
    	
	}


}

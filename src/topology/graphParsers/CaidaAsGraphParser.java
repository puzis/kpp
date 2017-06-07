package topology.graphParsers;

import java.io.BufferedReader;
import java.io.IOException;
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

public class CaidaAsGraphParser extends NetworkGraphParser {

    final static private String EXTENSION = "txt";



    @Override
    public void analyzeFile(BufferedReader reader, AbstractExecution progress, double percentage, GraphDataInterface<Index,BasicVertexInfo> graph, GraphFactory.VertexInfoType vertexInfoType) {
        try {
            String line = reader.readLine();
            int i = 0;
            FastMap<Integer, Index> nodes = new FastMap<Integer, Index>();
            BasicVertexInfo vInfo;
            while (line != null) {
                if (line.charAt(0) == '#') { //skipping comments
                    line = reader.readLine();
                    continue;
                }

                Matcher verticesLine = GraphRegularExpressions.ASRELATIONSHIP.matcher(line);
                if (verticesLine.find()) {
                    String arg1 = verticesLine.group(1);
                    String arg2 = verticesLine.group(2);

                    int v1 = Integer.valueOf(arg1);
                    if (!nodes.containsKey(v1)) {
                        nodes.put(v1, Index.valueOf(i++));
                    }
                    Index idx1 = nodes.get(v1);

                    vInfo = VertexFactory.createVertexStructure(vertexInfoType, idx1.intValue(), arg1);
                    graph.addVertex(idx1, vInfo);

                    int v2 = Integer.valueOf(arg2);
                    if (!nodes.containsKey(v2)) {
                        nodes.put(v2, Index.valueOf(i++));
                    }
                    Index idx2 = nodes.get(v2);

                    vInfo = VertexFactory.createVertexStructure(vertexInfoType, idx2.intValue(), arg2);
                    graph.addVertex(idx2, vInfo);

                    int direction = Integer.parseInt(verticesLine.group(3));
                    Index[] index12 = new Index[]{idx1, idx2};
                    Index[] index21 = new Index[]{idx2, idx1};
                    switch (direction) { //arcs will be added only when were sure the vertices exist.
                        case -1: {
                            graph.addEdge(Arrays.asList(index21), new EdgeInfo<Index, BasicVertexInfo>());
                            break;
                        }
                        case 1: {
                            graph.addEdge(Arrays.asList(index12), new EdgeInfo<Index, BasicVertexInfo>());
                            break;
                        }
                        default: {
                            graph.addEdge(Arrays.asList(index21), new EdgeInfo<Index, BasicVertexInfo>());
                            graph.addEdge(Arrays.asList(index12), new EdgeInfo<Index, BasicVertexInfo>());
                        }
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException ex) {
            LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), "AsRelationshipParser", "FillASRelationsList", ex);
        }

        updateLoadProgress(progress, percentage);
    }

    @Override
    public String getextension() {
        return EXTENSION;
    }



}

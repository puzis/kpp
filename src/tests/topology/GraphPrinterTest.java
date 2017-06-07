package tests.topology;

import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import javolution.util.Index;
import org.junit.Before;
import org.junit.Test;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import topology.*;
import topology.graphParsers.NetFileParser;
import topology.graphParsers.NetworkGraphParser;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: danahend
 * Date: 24/09/12
 * Time: 14:51
 */
public class GraphPrinterTest {

    NetworkGraphParser loader;
    String network;
    SerializableGraphRepresentation snet;
    GraphPrinter printer;


    @Before
    public void setUp(){
        loader = new NetFileParser();
        network = "defaultnet";
        snet = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP);
    }

    @Test
    public void test(){
        File file = new File(ServerConstants.DATA_DIR + network + ".net");
        FileInputStream fin;
        BufferedReader reader ;
        try{
            fin = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fin));
            loader.analyzeFile(reader,new DummyProgress(),1,snet,GraphFactory.DEFAULT_VERTEX_INFO_TYPE);
            GraphInterface<Index,BasicVertexInfo> graph = GraphFactory.copyAsSimple(snet);

            printer = new GraphPrinter(graph);
            String verticesStr = printer.getVerticesStr();
            DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, true, new DummyProgress(), 1);
            String edgesStr = printer.getEdgesStr(dw);
            String distancesStr = printer.getDistanceMatrix(dw.getDistanceMatrix());
            String routingTableStr = printer.getRoutingTableStr(dw.getRoutingTable());
            String betweenessStr = printer.getVertexBetweenessStr(dw);
            String dtVertices = printer.getDT_Vertices();
            System.out.print(verticesStr);
            System.out.print(edgesStr);
            System.out.print(distancesStr);
            System.out.print(routingTableStr);
            System.out.print(betweenessStr);
            System.out.print(dtVertices);

            String res = printer.getAnalyzedFile(dw);


            File simulationFile = new File(ServerConstants.DATA_DIR + network + ".txt");
            BufferedOutputStream out = null;
            try{
                out = new BufferedOutputStream(new FileOutputStream(simulationFile));
                out.write(res.getBytes());
                out.flush();
            }
            catch(IOException ex)
            {
                LoggingManager.getInstance().writeSystem("An IOException has occured while trying to save the analysis to file.", "GraphAnalyzer", "storeAnalysis", ex);
                throw new IOException("An IOException has occured while trying to save the analysis to file.\n" + ex + "\n" + LoggingManager.composeStackTrace(ex));
            }
            finally{
                try{
                    if (out != null)
                    {
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
        }catch(Exception ex)
           {
                System.out.print(ex);
            }
    }
}


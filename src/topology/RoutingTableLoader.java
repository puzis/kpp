package topology;

import common.IndexFastList;

import algorithms.centralityAlgorithms.betweenness.brandes.WeightedUlrikNG;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import javolution.util.FastList;
import javolution.util.Index;
import server.common.DummyProgress;
import server.common.ServerConstants;
import topology.GraphFactory.GraphDataStructure;
import topology.graphParsers.GraphParserFactory;

public class RoutingTableLoader {

    public static FastList<Index>[][] loadRoutingTable(String routingTableStr, int numberOfVertices) {
        IndexFastList[][] routingTable = new IndexFastList[numberOfVertices][numberOfVertices];

        String routingTableStrTrimmed = routingTableStr.substring(routingTableStr.indexOf("[") + 1, routingTableStr.lastIndexOf("]"));
        String[] lines = routingTableStrTrimmed.split("]]\\[\\[");
        for (int i = 0; i < numberOfVertices; i++) {
            String[] nextHops = lines[i].split("]\\[");

            for (int j = 0; j < numberOfVertices; j++) {
                String nextHop = nextHops[j].replaceAll("[]\\[]", "");
                String[] hops = nextHop.split(",");
                routingTable[i][j] = new IndexFastList();
                for (String hop : hops) {
                    routingTable[i][j].add(Index.valueOf(Integer.parseInt(hop.trim())));
                }
            }
        }

        return routingTable;
    }

    public static void main(String[] args) {
        String net = "3257_v3";//"3257_R9_fail";//"1755";//"4755";//"3257";//
        SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphDataStructure.GRAPH_AS_HASH_MAP);
        GraphParserFactory.getGraph(ServerConstants.DATA_DIR + "omnet/" + net + "/", "omnet/" + net + "/" + net + ".onet", "", "onet", new DummyProgress(), 1, graph);
        GraphInterface<Index, BasicVertexInfo> g = GraphFactory.copyAsSimple(graph);

        WeightedUlrikNG uAlg = new WeightedUlrikNG(ShortestPathAlgorithmInterface.DEFAULT, g, true, new DummyProgress(), 0);
        uAlg.run();
        FastList<Index>[][] routingTable = uAlg.getRoutingTable();

        GraphPrinter printer = new GraphPrinter(g);
        String routingTableStr = printer.getRoutingTableStr(routingTable);

        RoutingTableLoader.loadRoutingTable(routingTableStr, g.getNumberOfVertices());
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.dfbnb;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.Index;
import server.common.DummyProgress;
import server.common.Network;
import server.common.ServerConstants;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

/**
 *
 * @author Matt
 */
public class NetworkBuilder {

    private static final String _NET = ".net";
    private Network m_network;
    private boolean networkReady;

    public boolean isNetworkReady() {
        return networkReady;
    }

    public Network getNetwork() {
        return m_network;
    }
    private GraphInterface<Index,BasicVertexInfo> graph;

    public NetworkBuilder(String networkName) {

        File f = new File(ServerConstants.DATA_DIR + networkName + _NET);
        if (f.exists()) {

            m_network = new Network(networkName);

            m_network.importNetwork(new DummyProgress(), networkName + _NET, null, GraphFactory.DEFAULT_GRAPH_TYPE);
            graph = m_network.getGraphSimple();
            
//            NetFileParser parser = new NetFileParser();
//            graph = parser.analyzeFile(ServerConstants.DATA_DIR + networkName + _NET, new PhaseProgress(), 1);

            try {//TODO: check if this initialization is correct
                DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, true, new DummyProgress(), 1);
                dw.saveToDisk(ServerConstants.DATA_DIR + networkName + ".dw", new DummyProgress(), 1);
//                networkReady = m_network.loadNetwork();
                networkReady = true;

            } catch (Exception ex) {
                Logger.getLogger(NetworkBuilder.class.getName()).log(Level.SEVERE, null, ex);
                networkReady = false;
            }
        } else {
            System.out.println("Error: " + f + " is missing ");
            networkReady = false;
        }
    }

    public int getNumberOfVertices() {
        return graph.getNumberOfVertices();
    }

    public int getNumberOfEdges() {
        return graph.getNumberOfEdges();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.dfbnb.samples;

import java.io.File;

import javolution.util.Index;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.Network;
import server.common.ServerConstants;
import server.dfbnb.NetworkBuilder;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

/**
 *
 * @author Matt
 */
public class NetworkMain {

    public static void main(String[] args) {

//        NetworkBuilder nb = new NetworkBuilder("testNetwork", numOfVertices);
        NetworkBuilder nb = new NetworkBuilder("smallnet");
        if (nb.isNetworkReady()) {
        	
//            int numOfVertices = nb.getNumberOfVertices();
//            Network m_network = nb.getNetwork();
//
//            int[] verticeArray = new int[numOfVertices];
//            int[] linkArray = new int[0];
//
//            for (int i = 0; i < verticeArray.length; i++) {
//                verticeArray[i] = i;
//
//            }
//
//
//            int centrality = Centrality.Betweeness;
//            if (m_network.evaluateGroup(verticeArray, linkArray, centrality)) {
//                System.out.println("evaluation result: " + m_network.evaluationResult());
//            } else {
//                System.out.println("evaluate failed");
//            }
        	
            int numOfVertices = nb.getNumberOfVertices();
            Network m_network = nb.getNetwork();
            DataWorkshop dw = null;
            try{
    			File dwFile = new File(ServerConstants.DATA_DIR + m_network.getName() + ".dw");
    			if (dwFile.exists()){
    				dw = new DataWorkshop();
    				try{
    					dw.loadFromDisk(dwFile, new DummyProgress(), 1);
    				}
    				catch(Exception ex){
    					LoggingManager.getInstance().writeSystem("Couldn't load " + m_network.getName() + ".dw.", "NetworkMain", "main", ex);
    				}
    			}else{
    				GraphInterface<Index,BasicVertexInfo> graph = m_network.getGraphSimple();
    				AbsTrafficMatrix communicationWeights = null;
    				if (graph != null){
    					communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices());// MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
    				
    					try{
    						dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, communicationWeights, true, new DummyProgress(), 1);
    					}
    					catch(Exception ex){
    						LoggingManager.getInstance().writeSystem("An exception has occured while creating dataWorkshop:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), "NetworkMain", "main", ex);
    					}
    				}
    			}
    		}
            catch(RuntimeException ex){
    			LoggingManager.getInstance().writeSystem("The file " +  ServerConstants.DATA_DIR + m_network.getName() + ".dw doesn't exist.", "NetworkMain", "main", null);
            }
    		
            Object[] group = new Object[numOfVertices];
            for (int i=0;i<numOfVertices;i++){
            	group[i] = Index.valueOf(i);
            }

            double evaluationResult = -1;
            evaluationResult = CandidatesBasedAlgorithm.calculateGB(dw, group, new DummyProgress(), 1);
            if (evaluationResult!=-1) {
            	System.out.println("evaluation result: " + evaluationResult);
            } else {
            	System.out.println("evaluate failed");
            }
        } else {
            System.out.println("network not ready");
        }
    }
}

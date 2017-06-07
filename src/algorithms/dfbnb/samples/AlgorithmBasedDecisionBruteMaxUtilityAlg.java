/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.dfbnb.samples;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.Index;
import server.common.Network;
import server.common.ServerConstants.Centrality;
import server.dfbnb.NetworkBuilder;
import algorithms.dfbnb.BruteForceUtilitySearch;
import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;
import algorithms.dfbnb.Node;

/**
 *
 * @author Matt
 */
public class AlgorithmBasedDecisionBruteMaxUtilityAlg {

    public static void main(String[] args) {
        NetworkBuilder nb = new NetworkBuilder("smallnet");
        if (nb.isNetworkReady()) {
            Network network = nb.getNetwork();
            DynamicSet_GBC_Size groupMember = new DynamicSet_GBC_Size(network,Centrality.Betweeness);
            Vector<Index> candidates = new Vector<Index>();
            for (int i = 0; i < nb.getNumberOfVertices(); i++) {
                candidates.add(Index.valueOf(i));
            }
            InfNode<Index> root = new Node<Index>(candidates, groupMember);
            BruteForceUtilitySearch<Index> maxSearch = new BruteForceUtilitySearch<Index>(root);
            InfGroup<Index> bestGroup = maxSearch.execute();
            System.out.println("Best Utility: " + bestGroup.getUtility());
            System.out.println("Best Node group members: ");
            for (int i = 0; i < bestGroup.getGroupSize(); i++) {
                try {
                    System.out.print(bestGroup.getElementAt(i) + " , ");
                } catch (Exception ex) {
                    Logger.getLogger(AlgorithmBasedDecisionBruteMaxUtilityAlg.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println();
        } else {
            System.out.println("network not ready");
        }
    }
}

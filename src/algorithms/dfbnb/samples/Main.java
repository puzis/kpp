package algorithms.dfbnb.samples;

import java.util.Vector;

import javolution.util.Index;
import server.common.Network;
import server.common.ServerConstants.Centrality;
import server.dfbnb.NetworkBuilder;
import algorithms.dfbnb.CertifiedUtilitySearch;
import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;
import algorithms.dfbnb.Node;

public class Main {

    public static void main(String[] args) {
        Double budget = 50.0;
        NetworkBuilder nb = new NetworkBuilder("test_graph_100");
        if (nb.isNetworkReady()) {
            Network network = nb.getNetwork();
            DynamicSet_GBC_Size groupMember = new DynamicSet_GBC_Size(network,Centrality.Betweeness);
            Vector<Index> candidates = new Vector<Index>();
            for (int i = 0; i < nb.getNumberOfVertices(); i++) {
                candidates.add(Index.valueOf(i));
            }
            InfNode<Index> root = new Node<Index>(candidates, groupMember);
            CertifiedUtilitySearch<Index> heuristicMax = 
            	new CertifiedUtilitySearch<Index>(root, budget);
            InfGroup<Index> bestGroup = heuristicMax.execute();
            System.out.println("Result: " + bestGroup.getUtility() + " size: " +bestGroup.getGroupSize());
        } else {
            
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input;

import algorithms.featuredep.gbc.VertexGroup;
import javolution.util.Index;
import topology.AbstractUndirectedGraph;

/**
 *
 * @author bennyl
 */
public interface DeployableTopologyGraph {

    /**
     * @return the number of nodes available in the graph the nodes ids are
     * guaranteed to be unique for each node and in the range[0,number-of-nodes
     * -1]
     */
    int getNumberOfNodes();

    /**
     * @return the deployable nodes
     */
    VertexGroup getDeployableNodes();
    
    /**
     * @param nodeId
     * @return the node with the given id.
     */
    DeployableVertexInfoIfc getNode(int nodeId);

    /**
     * @return the topology that this object wraps
     */
    AbstractUndirectedGraph<Index, DeployableVertexInfo> getBaseTopology();
    
}

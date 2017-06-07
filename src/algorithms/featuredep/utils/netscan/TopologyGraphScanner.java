/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.featuredep.utils.netscan.GraphElement.EdgeElement;
import algorithms.featuredep.utils.netscan.GraphElement.VertexElement;
import topology.AbstractUndirectedGraph;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;

/**
 *
 * @author bennyl
 */
public class TopologyGraphScanner implements GraphElement.GraphElementVisitor{

    AbstractUndirectedGraph topology = new GraphAsHashMap();
    
    @Override
    public void visit(VertexElement e) {
        topology.addVertex(e.getId(), new BasicVertexInfo(e.getId(), e.getName()));
    }

    @Override
    public void visit(EdgeElement e) {
        topology.addEdge(e.getVertexFromId(), e.getVertexToId());
    }

    public AbstractUndirectedGraph getResultedTopology() {
        return topology;
    }
    
}

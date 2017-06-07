/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input.impl;

import algorithms.featuredep.input.DeployableTopologyGraph;
import algorithms.featuredep.input.DeployableVertexInfo;
import algorithms.featuredep.gbc.VertexGroup;
import common.Factory;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import javolution.util.Index;
import topology.AbstractUndirectedGraph;
import topology.BANetworkGenerator;

/**
 *
 * @author bennyl
 */
public final class RandomDeployableTopologyGraph implements DeployableTopologyGraph {

    private final AbstractUndirectedGraph<Index, DeployableVertexInfo> topology;
    private final VertexGroup deployableNodes;

    public RandomDeployableTopologyGraph(long seed, int numNodes, double averageDegree, int numDepNodes, final int numProtocols) {
        final Random r = new Random(seed);
        
        // generate initial topology structure
        BANetworkGenerator<DeployableVertexInfo> baGenerator = new BANetworkGenerator(averageDegree);
        topology = baGenerator.generate(numNodes, new Factory<DeployableVertexInfo, Index>() {
            @Override
            public DeployableVertexInfo construct(Index argument) {
                DeployableVertexInfo n = new DeployableVertexInfo(argument.intValue(), null, numProtocols);
                n.setImportanceIn(Math.abs(r.nextDouble()));
                n.setImportanceOut(Math.abs(r.nextDouble()));

                for (int p = 0; p < numProtocols; p++) {
                    n.setClientOf(p, r.nextBoolean());
                    n.setServerOf(p, r.nextBoolean());
                }

                return n;
            }
        }, r.nextLong());

        //select deployable nodes
        int[] all = new int[getNumberOfNodes()];
        for (int i = 0; i < getNumberOfNodes(); i++) {
            all[i] = i;
        }
        Collections.shuffle(Arrays.asList(all), r);
        
        //update the deployable group
        deployableNodes = new VertexGroup(numDepNodes);
        for (int i=0; i<numDepNodes; i++){
            deployableNodes.addVertex(all[i], 0);
            getNode(all[i]).setDeployable(true);
        }
    }

    @Override
    public int getNumberOfNodes() {
        return topology.getNumberOfVertices();
    }

    @Override
    public DeployableVertexInfo getNode(int nodeId) {
        return (DeployableVertexInfo) topology.getVertex(Index.valueOf(nodeId));
    }

    @Override
    public AbstractUndirectedGraph<Index, DeployableVertexInfo> getBaseTopology() {
        return topology;
    }

    @Override
    public VertexGroup getDeployableNodes() {
        return deployableNodes;
    }
}

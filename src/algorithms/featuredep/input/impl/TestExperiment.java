/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input.impl;

import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.featuredep.GBCCalculator;
import algorithms.featuredep.gbc.DeploymentGBC;
import algorithms.featuredep.gbc.VertexGroup;
import algorithms.featuredep.input.DeployableVertexInfo;
import algorithms.featuredep.utils.TrafficMatrixUtils;
import javolution.util.Index;
import topology.AbstractUndirectedGraph;
import topology.GraphAsHashMap;

/**
 *
 * @author bennyl
 */
public class TestExperiment {

    public static void main(String[] args) {
        int p = 1;
        AbstractUndirectedGraph<Index, DeployableVertexInfo> topology = new GraphAsHashMap<Index, DeployableVertexInfo>();
        topology.addVertex(Index.valueOf(0), new DeployableVertexInfo(0, "0", p));
        topology.addVertex(Index.valueOf(1), new DeployableVertexInfo(1, "1", p));
        topology.addVertex(Index.valueOf(2), new DeployableVertexInfo(2, "2", p));
        topology.addVertex(Index.valueOf(3), new DeployableVertexInfo(3, "3", p));

        topology.addEdge(Index.valueOf(1), Index.valueOf(2));
        topology.addEdge(Index.valueOf(2), Index.valueOf(3));
        topology.addEdge(Index.valueOf(1), Index.valueOf(3));

        DenseTrafficMatrix matrix = new DenseTrafficMatrix(3 + 1);
        matrix.setWeight(1, 2, 10);
        matrix.setWeight(1, 3, 20);
        matrix.setWeight(2, 3, 1);
        matrix.setWeight(2, 1, 1);
        matrix.setWeight(3, 1, 30);
        matrix.setWeight(3, 2, 1);
        TrafficMatrixUtils.normalize(matrix);

        { //only for debugging
            VertexGroup group = new VertexGroup(2);
            group.addVertex(2, 0.1);
            group.addVertex(3, 1);
            DeploymentGBC gbc = new DeploymentGBC(topology, matrix, group, true);
            System.out.println("GBC for group " + group + " is " + gbc.calculate(group));
        }
        for (int i = 0; i < 3; i++) {
            VertexGroup group = new VertexGroup(1);
            group.addVertex(i + 1, 1);
            DeploymentGBC gbc = new DeploymentGBC(topology, matrix, group, true);
            System.out.println("GBC for group " + group + " is " + gbc.calculate(group));
        }

        System.out.println("-----------------------------");

        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 3; j++) {
                VertexGroup group = new VertexGroup(2);
                group.addVertex(i + 1, 1);
                group.addVertex(j + 1, 1);
                DeploymentGBC gbc = new DeploymentGBC(topology, matrix, group, true);
                System.out.println("GBC for group " + group + " is " + gbc.calculate(group));
            }
        }

    }
}

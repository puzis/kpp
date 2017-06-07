/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input.impl;

import algorithms.featuredep.input.Input;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public class RandomInputGenerator {

    private long seed;
    private int numberOfNodesInTopologyGraph = 70;
    private double averageDegreeOfNode = 1.8;
    private int numberOfDeployableNodes = 6;
    private int numberOfProtocols = 1;
    private int numberOfFeatures = 3;
    private long maximalDamageAssesment = 100000;
    private long minimalDamageAssesment = 1000;

    public RandomInputGenerator(long seed) {
        this.seed = seed;
    }

    public Input generate() {
        Input in = new Input();

        Random r = new Random(seed);
        final RandomDeployableTopologyGraph randomDeployableTopologyGraph = new RandomDeployableTopologyGraph(r.nextLong(), numberOfNodesInTopologyGraph, averageDegreeOfNode, numberOfDeployableNodes, numberOfProtocols);
        in.setTopology(randomDeployableTopologyGraph);
        in.setFeatureSet(new RandomFeatureSet(r.nextLong(), numberOfFeatures, numberOfDeployableNodes));
        in.setSupportedProtocols(numberOfProtocols);
        in.setTrafficMatrix(new RandomTrafficMatrix(r.nextLong(), numberOfNodesInTopologyGraph, true));
        in.setDamageAssesment(new RandomAttackDamageAssesmentFunction(r.nextLong(), maximalDamageAssesment, minimalDamageAssesment));
        return in;
    }

    public int getNumberOfNodesInTopologyGraph() {
        return numberOfNodesInTopologyGraph;
    }

    public void setNumberOfNodesInTopologyGraph(int numberOfNodesInTopologyGraph) {
        this.numberOfNodesInTopologyGraph = numberOfNodesInTopologyGraph;
    }

    public double getAverageDegreeOfNode() {
        return averageDegreeOfNode;
    }

    public void setAverageDegreeOfNode(double averageDegreeOfNode) {
        this.averageDegreeOfNode = averageDegreeOfNode;
    }

    public int getNumberOfDeployableNodes() {
        return numberOfDeployableNodes;
    }

    public void setNumberOfDeployableNodes(int numberOfDeployableNodes) {
        this.numberOfDeployableNodes = numberOfDeployableNodes;
    }

    public void setMaximalDamageAssesment(long maximalDamageAssesment) {
        this.maximalDamageAssesment = maximalDamageAssesment;
    }

    public void setMinimalDamageAssesment(long minimalDamageAssesment) {
        this.minimalDamageAssesment = minimalDamageAssesment;
    }

    public long getMaximalDamageAssesment() {
        return maximalDamageAssesment;
    }

    public long getMinimalDamageAssesment() {
        return minimalDamageAssesment;
    }

    public int getNumberOfProtocols() {
        return numberOfProtocols;
    }

    public void setNumberOfProtocols(int numberOfProtocols) {
        this.numberOfProtocols = numberOfProtocols;
    }

    public int getNumberOfFeatures() {
        return numberOfFeatures;
    }

    public void setNumberOfFeatures(int numberOfFeatures) {
        this.numberOfFeatures = numberOfFeatures;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input.impl;

import algorithms.featuredep.input.FeatureSet;
import algorithms.featuredep.utils.BinUtils;
import algorithms.featuredep.utils.RandomSequance;

/**
 *
 * @author bennyl
 */
public class RandomFeatureSet implements FeatureSet {

    int numFeatures;
    RandomSequance rsq;
    
    public RandomFeatureSet(long seed, int numFeatures, int numDeploymentNodes) {
        this.numFeatures = numFeatures;
        this.rsq = new RandomSequance(seed);
    }

    @Override
    public int getNumberOfFeatures() {
        return numFeatures;
    }

    @Override
    public double calcUtility(boolean[] nodeAssignment, int protocol) {
//        double sum = 0;
//        for (int i = 0; i < nodeAssignment.length; i++) {
//            if (nodeAssignment[i]) {
//                sum += rsq.getIthDouble(i * nodeAssignment.length + protocol);
//            }
//        }
//        return sum / ((double) nodeAssignment.length);

        double sum = 0;
        for (int j = 0; j < nodeAssignment.length; j++) {
            if (nodeAssignment[j]) {
                sum = Math.max(rsq.getIthDouble(j * nodeAssignment.length + protocol), sum);
            }
        }

        return sum;
    }

    @Override
    public double calcCost(boolean[][] assignment) {
        double sum = 0;
        for (int i = 0; i < assignment.length; i++) {
            for (int j = 0; j < assignment[i].length; j++) {
                if (assignment[i][j]) {
                    sum += rsq.getIthPositiveInteger(j) % 999 + 1;
                }
            }
        }

        return sum;
    }
}

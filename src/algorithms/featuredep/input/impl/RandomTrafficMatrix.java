/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input.impl;

import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.featuredep.utils.RandomSequance;
import algorithms.featuredep.utils.ValidationUtils;

/**
 *
 * @author bennyl
 */
public class RandomTrafficMatrix extends AbsTrafficMatrix {

    RandomSequance rsq;
    int dimentions;
    double normalizingFactor;
    double maxValue;

    public RandomTrafficMatrix(long seed, int dimentions, double maxValue) {
        this.rsq = new RandomSequance(seed);
        this.dimentions = dimentions;
        this.maxValue = maxValue;
        this.normalizingFactor = 1.0;
    }

    public RandomTrafficMatrix(long seed, int dimentions, boolean normalized) {
        this(seed, dimentions, 1.0);

        double nfact = 0;
        if (normalized) {
            for (int i = 0; i < dimentions; i++) {
                for (int j = 0; j < dimentions; j++) {
                    nfact += getWeight(i, j);
                }
            }
            
            normalizingFactor = nfact;
        } else {
            normalizingFactor = 1;
        }
    }

    @Override
    public double getWeight(int i, int j) {
        if (i == j) {
            return 0;
        }
        return (rsq.getIthDouble(i * dimentions + j) * maxValue) / normalizingFactor;
    }

    @Override
    public void setWeight(int i, int j, double w) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setAllWeights(double w) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void mul(double a) {
        throw new UnsupportedOperationException("Not supported.");
    }
}

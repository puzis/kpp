/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input.impl;

import algorithms.featuredep.input.AttackDamageAssesmentFunction;
import algorithms.featuredep.utils.RandomSequance;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public class RandomAttackDamageAssesmentFunction implements AttackDamageAssesmentFunction {

    RandomSequance r;
    long maxAmount;
    long minAmount;

    public RandomAttackDamageAssesmentFunction(long seed, long maxAmount, long minAmount) {
        this.r = new RandomSequance(seed);
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
    }

    @Override
    public double assest(int protocol) {
        return r.getIthDouble(protocol)*(maxAmount-minAmount) + minAmount;
    }
}

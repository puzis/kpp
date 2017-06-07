/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input.impl;

import algorithms.featuredep.input.HardConstraintSet;
import algorithms.featuredep.utils.BinUtils;
import algorithms.featuredep.utils.RandomSequance;

/**
 *
 * @author bennyl
 */
public class RandomHardConstraintSet implements HardConstraintSet {

    RandomSequance rsq;

    public RandomHardConstraintSet(long seed) {
        rsq = new RandomSequance(seed);
    }

    @Override
    public boolean isConsistent(boolean[] nodeAssignment) {
        if (1==1)
         return true; //temporary for testing 
        if (nodeAssignment.length > 64) {
            throw new UnsupportedOperationException("RandomHardConstraintSet supports up to 64 features");
        }
        
        return rsq.getIthBoolean(BinUtils.convertToLong(nodeAssignment));
    }

    @Override
    public boolean isConsistent(boolean[][] fullAssignment) {
        for (boolean[] a : fullAssignment) {
            if (!isConsistent(a)) {
                return false;
            }
        }

        return true;
    }

}

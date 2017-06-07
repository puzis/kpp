/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.algos;

import algorithms.featuredep.Assignment;
import algorithms.featuredep.FDProblem;
import algorithms.featuredep.input.Input;
import algorithms.featuredep.input.impl.RandomInputGenerator;
import java.math.BigInteger;

/**
 *
 * @author bennyl
 */
public class FullSearch {

    private FDProblem p;
    private Assignment best = null;
    private double bestQuality = Double.MIN_VALUE;

    public Assignment solve(FDProblem p) {
        this.p = p;
        Assignment cpa = new Assignment(p);
        cpa.setAllAssignmentTo(false);

        int cpos = 0;
        while (cpos < p.getNumberOfFeatures() * p.getNumberOfDeployableNodes()) {
            int dnode = cpos / p.getNumberOfFeatures();
            int feature = cpos - dnode * p.getNumberOfFeatures();

            boolean bit = cpa.getValue(dnode, feature);
            if (bit) {
                cpa.assign(dnode, feature, false);
                cpos++;
            } else {
                cpa.assign(dnode, feature, true);
//                System.out.println("Looking at: " + cpa.toString(p));
                cpos = 0;
                double quality = cpa.calcQuility(p);
                if (quality > bestQuality) {
                    System.out.println("found new best cost: " + quality);
                    System.out.println("with assignment: " + cpa.toString(p));
//                    System.out.println("Taken!");
                    best = cpa.deepCopy();
                    bestQuality = quality;
                }
            }
        }

        return best;
    }

    public static void main(String[] args) {
//        int i = 0;

        RandomInputGenerator rgen = new RandomInputGenerator(1111);
//        rgen.setNumberOfProtocols(3);
//        rgen.setNumberOfDeployableNodes(10);

        Input in = rgen.generate();
        FDProblem prob = new FDProblem();
        prob.reduce(in);

        new FullSearch().solve(prob);
//        for (int i = 0; i < 10; i++) {
//            System.out.println("------------- solving problem: " + i + "-------------------");
//            
//        }
    }
}

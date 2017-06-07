/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.algos;

import algorithms.featuredep.Assignment;
import algorithms.featuredep.FDProblem;
import algorithms.featuredep.input.Input;
import algorithms.featuredep.input.impl.RandomInputGenerator;
import java.util.Arrays;

/**
 *
 * @author bennyl
 */
public class GreedyWithBudget {

    /**
     *
     * @param p
     * @param budget in $
     * @return
     */
    public static Assignment topDownSolve(FDProblem p, double budget) {
        Assignment a = new Assignment(p);
        a.setAllAssignmentTo(false);

        while (true) { //maybe allow to take chances if bellow the budget but store the best result before the chance taken.
            long best = a.findMaximalQualityContinuesIndexToSwitch(p, true, budget);            
            if (best != -1) { //found
                a.assignContinues(best, true);
                System.out.println("Improved to " + a.calcQuility(p));
            } else { //not found then done.
                System.out.println("found solution : " + a.toString(p));
                return a;
            }
        }
    }

    /**
     *
     * @param p
     * @param budget in $
     * @return
     */
    public static Assignment bottomUpSolve(FDProblem p, double budget) {
        Assignment a = new Assignment(p);
        a.setAllAssignmentTo(true);
        double price = a.calcDeploymentPrice(p);

        while (price > budget) {
            long best = a.findMaximalQualityContinuesIndexToSwitch(p, false, -1);

            if (best != -1) { //found
                a.assignContinues(best, false);
                price = a.calcDeploymentPrice(p);
                System.out.println("Improved to " + a.toString(p));
            } else { //not found then done.
                break;
            }
        }

        System.out.println("found solution: " + a.toString(p));
        return a;
    }

    public static void main(String[] args) {
        RandomInputGenerator rgen = new RandomInputGenerator(1111);
        Input in = rgen.generate();
        FDProblem prob = new FDProblem();
        prob.reduce(in);

        System.out.println("Top Down: ");
        topDownSolve(prob, 400);
        System.out.println("Buttom up: ");
        bottomUpSolve(prob, 400);
    }
}

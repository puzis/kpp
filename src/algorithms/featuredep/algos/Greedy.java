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
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author bennyl
 */
public class Greedy {

    public static Assignment solve(FDProblem p) {
        Queue<Integer> o = new LinkedList<Integer>(Arrays.asList(p.getDeploymentPointsOrderedByGBC()));
        Assignment a = new Assignment(p).setAllAssignmentTo(false);
        while (!o.isEmpty()){
            Integer d = o.remove();
            a.assignBestLocalDeployment(d, p);
        }
        
        System.out.println("found solution: " + a.toString(p));
        return a;
    }

    public static void main(String[] args) {
        RandomInputGenerator rgen = new RandomInputGenerator(1111);
        Input in = rgen.generate();
        FDProblem prob = new FDProblem();
        prob.reduce(in);

        solve(prob);
    }
}

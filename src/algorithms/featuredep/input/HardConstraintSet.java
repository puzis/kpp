/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input;

/**
 *
 * @author bennyl
 */
public interface HardConstraintSet {
    boolean isConsistent(boolean[] nodeAssignment);
    boolean isConsistent(boolean[][] fullAssignment);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input;

import java.util.List;

/**
 *
 * represents a set of feature and access to their metadata related to the 
 * utilities and costs of deployment
 * 
 * @author bennyl
 */
public interface FeatureSet {
    /**
     * @return the number of available features
     */
    int getNumberOfFeatures();
    /**
     * @param nodeAssignment
     * @param protocol
     * @return the utility of assigning the given set of features on a single node in the 
     * topology graph for the given protocol
     */
    double calcUtility(boolean[] nodeAssignment, int protocol);
    
    /**
     * @param assignment
     * @return the *normalized* cost 
     */
    double calcCost(boolean[][] assignment);
}

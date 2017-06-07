/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.centralityAlgorithms.rbc.routingFunction;

import javolution.util.FastList;

/**
 *
 * @author ishayp
 */
public abstract class AbsRoutingFunction {
        
    public abstract double routingProbability(int s, int u, int v, int t);
    
    public abstract FastList<Double> routesMetrics(int u, int v, int t);
}
/*

*/
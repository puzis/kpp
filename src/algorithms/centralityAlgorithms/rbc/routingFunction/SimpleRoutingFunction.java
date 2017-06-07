/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.centralityAlgorithms.rbc.routingFunction;

import javolution.util.FastList;

import javolution.util.Index;

/**
 *
 * @author ishayp
 */
public class SimpleRoutingFunction extends AbsRoutingFunction{

	private FastList<Index>[][] _routingTable;
    
    public SimpleRoutingFunction(FastList<Index>[][] routingTable){
        _routingTable = routingTable;
    }
    
    @Override
    public double routingProbability(int s, int u, int v, int t) {
        FastList<Index> uChildren = _routingTable[u][t];
        if  (uChildren.contains(Index.valueOf(v)))
        	return (1.0/uChildren.size());
        else
        	return 0.0;
    }
    
    public FastList<Double> routesMetrics(int u, int v, int t){
    	return null;
    }
}
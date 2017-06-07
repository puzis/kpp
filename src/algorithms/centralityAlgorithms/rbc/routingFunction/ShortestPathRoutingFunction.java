/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.centralityAlgorithms.rbc.routingFunction;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.WeightedUlrikNG;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

/**
 *
 * @author ishayp
 */
public class ShortestPathRoutingFunction extends AbsRoutingFunction{

	private WeightedUlrikNG uAlg;
    @SuppressWarnings("unchecked")
	private FastList<Index>[][] routingTable;
    private double[][] distance;
   
    public ShortestPathRoutingFunction(GraphInterface<Index,BasicVertexInfo> G){
        try {
            uAlg = new WeightedUlrikNG(ShortestPathAlgorithmInterface.DEFAULT,G, true, new  DummyProgress(), 0);
            uAlg.run();
            routingTable = uAlg.getRoutingTable();
            distance = uAlg.getDistance();
        } 
        catch (Exception e) {
        	e.printStackTrace();
            throw new IllegalArgumentException("Invalid graph provided");
        }
    }
    
    public ShortestPathRoutingFunction(GraphInterface<Index,BasicVertexInfo> G, ShortestPathAlg shortestPathAlgorithm){
        try {
            uAlg = new WeightedUlrikNG(shortestPathAlgorithm,G, true, new  DummyProgress(), 0);
            uAlg.run();
            routingTable = uAlg.getRoutingTable();
            distance = uAlg.getDistance();
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
            throw new IllegalArgumentException("Invalid graph provided");
        }
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public double routingProbability(int s, int u, int v, int t) {
        FastList<Index> uChildren = new FastList<Index>();
        if (distance[s][t] == distance[s][u]+distance[u][v]+distance[v][t]){
            uChildren = routingTable[u][t];
            if  (uChildren.contains(Index.valueOf(v)))
                return (1.0/uChildren.size());
            else
                return 0.0;
        }
        else
            return 0.0;
    }
    
    public FastList<Double> routesMetrics(int u, int v, int t){
    	FastList<Double> metrics = new FastList<Double>();
    	metrics.add(distance[u][v]+distance[v][t]);
    	return metrics;
    }
}
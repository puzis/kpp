/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.centralityAlgorithms.rbc;

import javolution.util.FastList;

import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;

/**
 *
 * @author ishayp
 */
public class VRBCAlgorithm extends AbsBetweenessAlgorithm<Index>{
    public VRBCAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction, AbsTrafficMatrix cw){
        super(G,routingFunction,cw);
    }
    
    public VRBCAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw){
        super(G, cw);
    }

    @Override
    public double getDelta(Index s, Index v, Index t) {
    	
        double result=0;
        if (s.intValue() == v.intValue() || v.intValue() == t.intValue())
            return 1.0;
        else{
            for(AbstractSimpleEdge<Index,BasicVertexInfo> e: G.getIncomingEdges(v)){
                Index u = e.getNeighbor(v) ;
                double RFCalculation=this.RF.routingProbability(s.intValue(), u.intValue(), v.intValue(), t.intValue());
                if (RFCalculation!=0){
                	FastList<Double> metrics = this.RF.routesMetrics(u.intValue(), v.intValue(), t.intValue());
                    result = result+RFCalculation*getDelta(s,u,t, metrics);
                }
            }
            return result;
        }
    }
    
    public double getDelta(Index s, Index v, Index t, FastList<Double> metrics) {
    	
        double result=0;
        if (s.intValue() == v.intValue() || v.intValue() == t.intValue())
            return 1.0;
        else{
            for(AbstractSimpleEdge<Index,BasicVertexInfo> e: G.getIncomingEdges(v)){
            	Index u = e.getNeighbor(v) ;
                double RFCalculation=this.RF.routingProbability(s.intValue(), u.intValue(), v.intValue(), t.intValue());
                if (RFCalculation!=0){
                	// Check if u does not cause a loop.
                	boolean found = false; // If found then proceeding to u does not mean looping back.
                	FastList<Double> nextMetrics = this.RF.routesMetrics(u.intValue(), v.intValue(), t.intValue());
                	for (Double nextMetric : nextMetrics){
                		for (Double metric : metrics){
                			if (nextMetric.doubleValue()>metric.doubleValue())
                				found = true;
                			break;
                		}
                		if (found)
                			break;
                	}
                	if (found)
                		result = result+RFCalculation*getDelta(s,u,t, nextMetrics);
                }
            }
            return result;
        }
    }
}

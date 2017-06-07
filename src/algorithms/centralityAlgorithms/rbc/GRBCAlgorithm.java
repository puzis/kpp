/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.centralityAlgorithms.rbc;

import java.util.HashMap;

import java.util.LinkedList;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;

import common.Pair;

/**
 *
 * @author Ishay Peled, Omer zohar
 */
public class GRBCAlgorithm extends AbsBetweenessAlgorithm<FastList<Index>>{
    public GRBCAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction, AbsTrafficMatrix cw){
        super(G,routingFunction,cw);
    }
    
    public GRBCAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw){
        super(G, cw);
    }

    @Override
    public double getDelta(Index s, FastList<Index> group, Index t) {
        if (group==null || s==null || t==null){
            //new AssertionError("Null value(s) for getDelta in groupRBC");
            return 0.0;
        }
        for (int i=0;i<group.size();i++){
            if (group.get(i).intValue() == s.intValue() || group.get(i).intValue() == t.intValue())
                return 1.0;
        }
        LinkedList<Index> Q = new LinkedList<Index>();
        Index v;
        Double groupDelta = 0.0;
        Q.add(s);
        HashMap<Index, Double> delta = new HashMap<Index, Double>();
        delta.put(s, 1.0);
        while (Q.size()>0){
            v=Q.pop();
            LinkedList<Pair<Index, Double>> RFCalculation = new LinkedList<Pair<Index, Double>>();  
            for (AbstractSimpleEdge<Index,BasicVertexInfo> e: G.getOutgoingEdges(v)){
            	Index current = e.getNeighbor(v) ;
                Double calc = this.RF.routingProbability(s.intValue(), v.intValue(), current.intValue() , t.intValue());
                if (calc>0)
                    RFCalculation.add(new Pair<Index,Double>(current, calc));
            }
            for (int i=0;i<RFCalculation.size();i++){
                Index u = RFCalculation.get(i).getValue1();
                //Double pu = delta.get(u);
                Double pu = RFCalculation.get(i).getValue2();
                if (pu==null)
                    pu=0.0;
                Double dv = delta.get(v);
                if (dv == null)
                    dv = 0.0;
                if (group.contains(u))
                    groupDelta = groupDelta + pu*dv;
                else{
                    delta.put(u, pu*dv);
                    Q.addLast(u);
                }
            }
        }
        return groupDelta;
    }
}  
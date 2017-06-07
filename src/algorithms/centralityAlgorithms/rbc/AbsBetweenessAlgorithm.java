/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.centralityAlgorithms.rbc;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;
import algorithms.centralityAlgorithms.rbc.routingFunction.ShortestPathRoutingFunction;

/**
 *
 * @author ishayp
 */
public abstract class AbsBetweenessAlgorithm<SubjectType> {
    protected GraphInterface<Index,BasicVertexInfo> G;
    protected AbsRoutingFunction RF;
    protected AbsTrafficMatrix communicationWeights;
    protected int numberOfVertices;
    protected Iterable<Index> vertices;
    
    public AbsBetweenessAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction, AbsTrafficMatrix cw){
        this.G=G;
        this.RF=routingFunction;
        this.communicationWeights = cw;
        this.numberOfVertices = G.getNumberOfVertices();
        this.vertices = G.getVertices();
    }
    
    public AbsBetweenessAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw){
        this.G=G;
        this.RF=new ShortestPathRoutingFunction(G);
        this.communicationWeights = cw;
        this.numberOfVertices = G.getNumberOfVertices();
        this.vertices = G.getVertices();
    }
    
    public abstract double getDelta(Index s, SubjectType v, Index t);
    
    public double getTargetDependency(SubjectType v, Index t){
        double result=0.0;
        for(Index w : G.getVertices()){
            result+=getDelta(w, v, t)*communicationWeights.getWeight(w.intValue(), t.intValue());//[w.intValue()][t.intValue()];
        }
        return result;
    }
    
    public double getSourceDependency(Index s, SubjectType v){
        double result=0.0;
        for(Index w : G.getVertices()){
            result+=getDelta(s, v, w)*communicationWeights.getWeight(s.intValue(), w.intValue()); //[s.intValue()][w.intValue()];
        }
        return result;
    }
    
    public double getBetweeness (SubjectType v){
        double result=0;
        for(Index t : G.getVertices()){
            for(Index s : G.getVertices()){
                result+=getDelta(s, v, t)*communicationWeights.getWeight(t.intValue(), s.intValue()); //[t.intValue()][s.intValue()];
            }
        }
        return result;
    }
}
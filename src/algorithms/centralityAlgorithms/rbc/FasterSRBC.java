package algorithms.centralityAlgorithms.rbc;

import javolution.util.FastList;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;

public class FasterSRBC extends SRBCAlgorithm {
    
    public FasterSRBC(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw) {
        super(G,cw);
    }
    public FasterSRBC(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw,int cachetype) {
        super(G,cw,cachetype);
    }
    public FasterSRBC(GraphInterface<Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction, AbsTrafficMatrix cw){
        super(G, routingFunction, cw);
    }
    public FasterSRBC(GraphInterface<Index,BasicVertexInfo> G,AbsRoutingFunction routingFunction, AbsTrafficMatrix cw,VRBCAlgorithm vrbcAlg) {
    	super (G,routingFunction,cw,vrbcAlg);
    }
    
    @Override
    public double getTargetDependency (FastList<Index> sequence, Index t){
    	
    	Index v0= sequence.removeFirst();
    	double res=this.getM_vrbc().getTargetDependency(v0, t)*this.getDelta(v0,sequence, t);
    	sequence.addFirst(v0);
    	return res;
    }
 
    @Override
    public double getBetweeness (FastList<Index> sequence){
    	double res=0.0;
		for (Index t : G.getVertices()){
    		double a=this.getTargetDependency(sequence, t);
    		res+=a;
    	}
    	return res;
    }
}
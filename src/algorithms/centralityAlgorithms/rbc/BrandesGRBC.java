package algorithms.centralityAlgorithms.rbc;

import java.util.Arrays;


import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import topology.AbstractDirectedGraph;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.GraphUtils;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;

public class BrandesGRBC extends GRBCAlgorithm{

	public BrandesGRBC(GraphInterface<Index,BasicVertexInfo> G,	AbsRoutingFunction routingFunction, AbsTrafficMatrix cw) {
		super(G, routingFunction, cw);
	}
	
	public BrandesGRBC(GraphInterface<Index,BasicVertexInfo> G,	AbsTrafficMatrix cw) {
		super(G, cw);
	}
	/**
	 * class FasterGRBC(GRBC):
    """
    computes betweenness of groups
    Precondition: routingFunction is loop-free and source independent
    """    
    
    def __init__(self,G,routingFunction=None,CW=DefaultCommMatrix()):
        super(FasterGRBC,self).__init__(G,routingFunction,CW)
        
    def getTargetDependency(self,group,t):
        """
        Running time: O(m)
        """
        if not iterable(group):group=frozenset([group])
        gtd=0
        t=int(t)            
        #t defines a DAG
        DAG = nxRoutingDAG(self._G._G,self._RF,t)
        L = NX.dag.topological_sort(DAG)
        
        delta=dict([(int(v),0.0) for v in self._V])
        for v in L:
            delta[v]+=self._CW[v,t]
            if (v in group):
                gtd+=delta[v]
                delta[v]=0
            for u in DAG.successors(v):
                delta[u]+=delta[v]*self._RF(v,v,u,t)
            pass
        return gtd

    def getBetweenness(self,group):
        """
        Running time: O(nm)
        """
        if not iterable(group):group=frozenset([group])
        grb=0
        for t in self._V:
            grb+=self.getTargetDependency(group,t)
        return grb
	 */	
	@Override
	public double getTargetDependency(FastList<Index> group, Index t){
		double gtd = 0;
		AbstractDirectedGraph<Index,BasicVertexInfo> dag = GraphUtils.getDAG(G, t, this.RF);	/** O(m) */
		AbstractDirectedGraph<Index,BasicVertexInfo> copyDag = GraphUtils.copy(dag);			/** O(n+m) */
		FastList<Index> topologicalSort = null;
		try{
			topologicalSort = GraphUtils.topologicalSort(copyDag);				/** O(n) */
		}catch(Exception ex){
			LoggingManager.getInstance().writeSystem("Topological sort has failed.", "BrandesGRBC", "getTargetDependency", null);
		}
		double[] delta = new double [G.getNumberOfVertices()];
		Arrays.fill(delta, 0.0);
		while (topologicalSort.size()>0){
			Index v = topologicalSort.removeFirst();
			delta[v.intValue()]+= this.communicationWeights.getWeight(v.intValue(), t.intValue()); //[v.intValue()][t.intValue()];
		
			if (group.contains(v)){
				gtd += delta[v.intValue()];
				delta[v.intValue()]=0;
			}
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e:dag.getOutgoingEdges(v)){
				Index u = e.getNeighbor(v);
				delta[u.intValue()]+=delta[v.intValue()]*this.RF.routingProbability(v.intValue(), v.intValue(), u.intValue(), t.intValue());
			}
		}
		return gtd;
	}
	
	@Override
	public double getBetweeness(FastList<Index> group){
		double grb = 0;
		
        for(Index t : G.getVertices()){
			grb += getTargetDependency(group, t);
		}
		return grb;
	}
}
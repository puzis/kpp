package algorithms.centralityAlgorithms.rbc;

import java.util.Arrays;


import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractDirectedGraph;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.GraphUtils;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;

public class FasterVRBC extends StatefullVRBCAlgorithm {

	public FasterVRBC(GraphInterface <Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction, AbsTrafficMatrix cw, boolean shadowable, boolean historyable) 
	throws Exception{
		super(G, routingFunction, cw, shadowable, historyable);
		init(G, cw);
	}

	public FasterVRBC(GraphInterface <Index,BasicVertexInfo> G, AbsTrafficMatrix cw) throws Exception{
		super(G, cw);
		init(G, cw);
	}
	
	public FasterVRBC(GraphInterface <Index,BasicVertexInfo> G, AbsTrafficMatrix cw, AbsRoutingFunction routingFunction) throws Exception{
		super(G, routingFunction, cw);
		init(G, cw);
	}

	public FasterVRBC(GraphInterface <Index,BasicVertexInfo> G, AbsTrafficMatrix cw,int cachetype) throws Exception{
		super(G, cw, cachetype);
		init(G, cw);
	}
	
	public FasterVRBC(GraphInterface <Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction, AbsTrafficMatrix cw, boolean shadowable, boolean historyable,int cachetype) 
	throws Exception{
		super(G, routingFunction, cw, shadowable,historyable,cachetype);
		init(G, cw);
	}
	
	public FasterVRBC(GraphInterface <Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction, AbsTrafficMatrix cw, boolean shadowable, boolean historyable, int cachetype,int candsize,int[] candarray) 
	throws Exception{
		super(G, routingFunction, cw, shadowable, historyable, cachetype, candsize, candarray);
		init(G, cw);
	}

	//init() fills the cache of target dependency and betweenness
	/**
	 * def _computeBetweenness(self):
     *   rb=dict([(int(v),0.0) for v in self._V])
     *   for t in self._V:
     *       t=int(t)            
     *       #each t defines a DAG
     *       DAG = nxRoutingDAG(self._G._G,self._RF,t)
     *       L = NX.dag.topological_sort(DAG)
     *       
     *       delta=dict([(int(v),0.0) for v in self._V])
     *       for v in L:
     *           delta[v]+=self._CW[v,t]
     *           for u in DAG.successors(v):
     *               delta[u]+=delta[v]*self._RF(v,v,u,t)
     *           rb[v]+=delta[v]
     *           pass
     *       
     *       for v in self._V:
     *           self._doCache(lambda x,y: delta[v],v=v,t=t)
     *   for v in self._V:
     *       self._doCache(lambda x: rb[v],v=v)
	 */
	private void init(GraphInterface <Index,BasicVertexInfo> G, AbsTrafficMatrix cw) throws Exception{
		
		double[] RB = new double[G.getNumberOfVertices()];
		Arrays.fill(RB, 0.0);
		
		for (Index t : G.getVertices()){
			AbstractDirectedGraph<Index,BasicVertexInfo> dag = GraphUtils.getDAG(G, t, this.RF);
			AbstractDirectedGraph<Index,BasicVertexInfo> copyDag = GraphUtils.copy(dag);
			FastList<Index> topologicalSort = GraphUtils.topologicalSort(copyDag);
			
			double[] delta = new double [G.getNumberOfVertices()];
			Arrays.fill(delta, 0.0);
			while (topologicalSort.size()>0){
				Index v = topologicalSort.removeFirst();
				delta[v.intValue()]+= this.communicationWeights.getWeight(v.intValue(), t.intValue()); //[v.intValue()][t.intValue()];
				for (AbstractSimpleEdge<Index,BasicVertexInfo> e: dag.getOutgoingEdges(v)){
					Index u = e.getNeighbor(v);
					delta[u.intValue()]+=delta[v.intValue()]*this.RF.routingProbability(v.intValue(), v.intValue(), u.intValue(), t.intValue());
				}
				RB[v.intValue()]+=delta[v.intValue()];
			}
			for (int i = 0; i<G.getNumberOfVertices(); i++){
				this.mCache.put(mDONTCARE, i, t.intValue(), delta[i]);
			}
		} 
		for (int i = 0; i<G.getNumberOfVertices(); i++){
			this.mCache.put(mDONTCARE, i, mDONTCARE, RB[i]);
		}
	}
}
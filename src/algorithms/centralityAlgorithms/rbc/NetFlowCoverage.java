package algorithms.centralityAlgorithms.rbc;

import javolution.util.FastList;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;

public class NetFlowCoverage extends FasterGRBC{

	private double _confidenceThreshold = 0;
	
	public NetFlowCoverage(GraphInterface<Index, BasicVertexInfo> G,
			AbsRoutingFunction routingFunction, AbsTrafficMatrix cw,
			FastList<Index> candidates, int cachetype, double confidenceThreshold) {
		super(G, routingFunction, cw, candidates, cachetype);
		_confidenceThreshold = confidenceThreshold;
	}

	public NetFlowCoverage(GraphInterface<Index,BasicVertexInfo> G, 
			AbsTrafficMatrix cw, FastList<Index> candidates,int cachetype,
			double confidenceThreshold) {
		super(G, cw, candidates, cachetype);
		_confidenceThreshold = confidenceThreshold;
	}
	
	/**
	 * @Pre group,s is a member of candidates
	 */
	@Override
	public double getDelta(Index s, FastList<Index> group, Index t) {
		
		double delta_rbc = super.getDelta(s, group, t);
		if (delta_rbc >= _confidenceThreshold)
			return 1.0;
		
		return 0.0;
	}
}

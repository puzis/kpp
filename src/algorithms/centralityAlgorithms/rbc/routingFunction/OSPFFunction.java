package algorithms.centralityAlgorithms.rbc.routingFunction;

import javolution.util.FastList;
import omnetProcessing.common.RouterTable;

public class OSPFFunction extends AbsRoutingFunction {

	private RouterTable[] m_rts = null;
	
	public OSPFFunction(RouterTable[] rts){
		m_rts = rts;
	}
	
	@Override
	public double routingProbability(int s, int u, int v, int t) {
		return m_rts[u].getWeight(t, v);
	}
	
	public FastList<Double> routesMetrics(int u, int v, int t){
    	return m_rts[u].getMetrics(v, t);
    }
}

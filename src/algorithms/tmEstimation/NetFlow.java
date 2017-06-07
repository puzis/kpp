package algorithms.tmEstimation;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;

public class NetFlow {

	private FastMap<Index, FastList<Flow>> _flows = null;
	
	public NetFlow(){
		_flows = new FastMap<Index, FastList<Flow>>();
	}
	
	public void add(Index v, Index s, Index t, long weight){
		if (_flows.get(v) == null)
			_flows.put(v, new FastList<Flow>());
		
		_flows.get(v).add(new Flow(v, s, t, weight));
	}
	
	public FastList<Flow> getFlows(Index v){
		return _flows.get(v);
	}
}

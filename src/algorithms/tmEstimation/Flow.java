package algorithms.tmEstimation;

import javolution.util.Index;

public class Flow {

	private Index _v = null;
	private Index _s = null;
	private Index _t = null;
	private long _weight = -1;
	
	public Flow(Index v, Index s, Index t, long weight){
		_v = v;
		_s = s;
		_t = t;
		_weight = weight;
	}
	
	public Index getVertex(){
		return _v;
	}
	
	public Index getS(){
		return _s;
	}
	
	public Index getT(){
		return _t;
	}
	
	public long getWeight(){
		return _weight;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(_v).append(" (").append(_s).append(",").append(_t).append(")")
		.append(" delta=").append(_weight);
		return sb.toString();
	}
}

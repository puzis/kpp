package algorithms.centralityAlgorithms.rbc;

import javolution.util.FastList;

import javolution.util.FastMap;
import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;



public class StatefullFasterSRBC extends FasterSRBC {
	
	private FastMap<Integer,Double> m_cache;
	public StatefullFasterSRBC(GraphInterface<Index,BasicVertexInfo> G,
    		AbsRoutingFunction routingFunction,
    							AbsTrafficMatrix cw,
    						   	VRBCAlgorithm vrbcAlg ) {
    	super (G,routingFunction,cw,vrbcAlg);
    	m_cache = new FastMap<Integer,Double> (2);
    }
    
    private Double CheckCache (FastList<Index> sequence){
		Double res=Double.NaN;
		int key = 0;
		int j=1;
		for (Index i: sequence)	{
			key+=i.intValue()*j;
			j++;
		}
		if (this.m_cache.containsKey(key));
			res=this.m_cache.get(key);
		return res;
	}
    
    private void InsertCache (FastList<Index> sequence,double val){
		int key = 0;
		int j=1;
		for (Index i: sequence)	{
			key+=i.intValue()*j;
			j++;
		}
		m_cache.put(key, val);
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
    	double res=CheckCache(sequence);
    	if (Double.isNaN(res)){
	    	for (Index t:G.getVertices()){
	    		res+=this.getTargetDependency(sequence, t);
	    	}
	    	InsertCache(sequence, res);
    	}
    	return res;
    }
}
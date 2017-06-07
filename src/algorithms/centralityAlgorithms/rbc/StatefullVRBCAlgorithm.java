/**
 * 
 */
package algorithms.centralityAlgorithms.rbc;


import javolution.util.FastMap;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;

import common.ShadowedHistoriedCache;
import common.ShadowedHistoriedCacheArr;
import common.ShadowedHistoriedCacheInterface;
import common.Triple;

/**
 * @author zoharo
 *
 */
public class StatefullVRBCAlgorithm extends VRBCAlgorithm {
	
	
	protected ShadowedHistoriedCacheInterface mCache;
	protected int mDONTCARE;
	
	/**
	 * @param G
	 * @param routingFunction
	 * @param dw
	 * @param shadowable TODO
	 * @param historyable TODO
	 */
	public StatefullVRBCAlgorithm(GraphInterface <Index,BasicVertexInfo> G,
			AbsRoutingFunction routingFunction, AbsTrafficMatrix cw, boolean shadowable, boolean historyable) {
		super(G, routingFunction, cw);
		this.mCache=new ShadowedHistoriedCache(numberOfVertices,shadowable,historyable);
		mDONTCARE=mCache.DONTCARE();
	}

	/**
	 * @param G
	 * @param dw
	 */
	public StatefullVRBCAlgorithm(GraphInterface <Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction, AbsTrafficMatrix cw) {
		super(G, routingFunction, cw);
		this.mCache=new ShadowedHistoriedCache(numberOfVertices,false,false);
		mDONTCARE=mCache.DONTCARE();
	}
	
	public StatefullVRBCAlgorithm(GraphInterface <Index,BasicVertexInfo> G, AbsTrafficMatrix cw) {
		super(G, cw);
		this.mCache=new ShadowedHistoriedCache(numberOfVertices,false,false);
		mDONTCARE=mCache.DONTCARE();
	}
	
	/**
	 * @param G
	 * @param dw
	 */
	public StatefullVRBCAlgorithm(GraphInterface <Index,BasicVertexInfo> G, AbsTrafficMatrix cw,int cachetype) {
		super(G, cw);
		switch (cachetype){
			case ShadowedHistoriedCache.CACHEID: this.mCache=new ShadowedHistoriedCache (numberOfVertices,false,false);break;
			default: case ShadowedHistoriedCacheArr.CACHEID:this.mCache=new ShadowedHistoriedCacheArr (numberOfVertices,false,false);break;
		}
		mDONTCARE=mCache.DONTCARE();
	}
	
	/**
	 * @param G
	 * @param routingFunction
	 * @param dw
	 * @param shadowable TODO
	 * @param historyable TODO
	 */
	public StatefullVRBCAlgorithm(GraphInterface <Index,BasicVertexInfo> G,
			AbsRoutingFunction routingFunction, AbsTrafficMatrix cw, boolean shadowable, boolean historyable,int cachetype) {
		this(G, routingFunction, cw, shadowable,historyable,cachetype,-1,null);
	}
	
	public StatefullVRBCAlgorithm(GraphInterface <Index,BasicVertexInfo> G,
			AbsRoutingFunction routingFunction, AbsTrafficMatrix cw, boolean shadowable, boolean historyable,
			int cachetype,int candsize,int[] candarray) {
		super(G, routingFunction, cw);
		switch (cachetype){
			case ShadowedHistoriedCache.CACHEID: this.mCache=new ShadowedHistoriedCache (numberOfVertices,shadowable,historyable);break;
			default: case ShadowedHistoriedCacheArr.CACHEID:this.mCache=new ShadowedHistoriedCacheArr (numberOfVertices,shadowable,historyable,candsize,candarray);break;
		}
		mDONTCARE=mCache.DONTCARE();
	}
	
	/**
	 * checks the cache to see if that's calculation' been done before
	 * @param a are vertices.
	 * @param b
	 * @param c
	 * @return the result of the calculation if founf, NAN if not.
	 */
	private Double CheckCache (int a, int b, int c){
		Double res=Double.NaN;
		if (this.mCache.containsKey(a,b,c))
			res=this.mCache.get(a,b,c);
		return res;
	}
	
	
	@Override
    public double getDelta(Index s, Index v, Index t){
		double res=this.CheckCache(s.intValue(), v.intValue(), t.intValue());
		if (Double.isNaN(res)){
			res=super.getDelta(s, v, t);
			this.mCache.put(s.intValue(),v.intValue(),t.intValue(),res);
		}
		assert !Double.isNaN(res);
		return res;
	}
	
	@Override
	 public double getTargetDependency(Index v, Index t){
		double res=this.CheckCache(mDONTCARE, v.intValue(), t.intValue());
		if (Double.isNaN(res)){
//			if (FasterGRBC.INITFINISH)
//				LoggingManager.getInstance().writeTrace("cace miss: "+v+","+t, this.getClass().getCanonicalName(), "put",null );		

			res=super.getTargetDependency(v, t);
			this.mCache.put(mDONTCARE,v.intValue(),t.intValue(),res);
		}
		assert !Double.isNaN(res);
		return res;
     }
	@Override    
    public double getSourceDependency(Index s, Index v){
		double res=this.CheckCache(s.intValue(), v.intValue(), mDONTCARE);
		if (Double.isNaN(res)){
			res=super.getSourceDependency(s, v);
			this.mCache.put(s.intValue(),v.intValue(),mDONTCARE,res);
		}
		assert !Double.isNaN(res);
		return res;
	}
	
	@Override
    public double getBetweeness (Index v){
		Double res=this.CheckCache(mDONTCARE, v.intValue(), mDONTCARE);
		if (Double.isNaN(res)){
			res=super.getBetweeness(v);
			this.mCache.put(mDONTCARE, v.intValue() ,mDONTCARE,res);
			
		}
		return res;
    }
	
	public static void main (String [] args){
		Triple<Integer,Integer,Integer> s1 = new Triple <Integer,Integer,Integer> (Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(6));
		Triple<Integer,Integer,Integer> s2 = new Triple <Integer,Integer,Integer> (Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(6));
		FastMap<Triple<Integer, Integer, Integer>, Double>  m1=new FastMap<Triple<Integer,Integer,Integer>, Double>();
		m1.put(new Triple <Integer,Integer,Integer> (Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(6)), 5.0);
		System.out.println(m1.containsKey(new Triple <Integer,Integer,Integer> (Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(6))));
		
		
		FastMap<String, Double>g5= new FastMap<String, Double> ();
		g5.put(new String("mooki"), 5.0);
		System.out.println(g5.containsKey(new String ("mooki")));
		
		System.out.println(s1.equals(s2));
		
		
		}
	

}

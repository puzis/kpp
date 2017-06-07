/**
 * 
 */
package algorithms.centralityAlgorithms.rbc;

import java.util.Iterator;

import java.util.Random;

import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;
import algorithms.centralityAlgorithms.rbc.routingFunction.ShortestPathRoutingFunction;

import common.ShadowedHistoriedCache;
import common.ShadowedHistoriedCacheArr;
import common.ShadowedHistoriedCacheInterface;

/**
 * @author zoharo
    Accepts a predefined set of vertices (candidates) (subset of or equal to V).
    Maintains a set of vertices (M) and a corresponding data structure of size O(|V|*|candidates|^2)
    M can be updated only by accepting new vertices. Call add(v) to add v to M.
    All methods compute SRBC with respect to M. That is accounting for all communication paths that do not traverse M.
    In all methods the seq argument must be subset of candidates.
    In all methods the s argument must be in candidates
    In all methods the t argument must be in V 
    In all methods the v argument must be in candidates 
 */
public class ContributionVRBC extends VRBCAlgorithm {

	private int [] m_candidates_array = null; //optimization for precondition check speedup
	
	private final FastList<Index> m_candidates;
	
	private FastSet<Index> M;

	private SRBCAlgorithm m_srbc;//for calculation sequences, uses the same cache as this.
	
	private boolean mInitDone=false;
	
	protected ShadowedHistoriedCacheInterface mCache;
	private int mDONTCARE;
	//////////
	// support for state saving data structures, for fasterGRBC implementation 
	private FastSet<Index> state_set=new FastSet<Index>();
	boolean mSavedState=false;
///////////////////////////////////////////
//// construction
///////////////////////////////////////////
	
	/**
	 * Constructor
	 * @param G
	 * @param routingFunction
	 * @param dw
	 * @param candidates list of vertices to add. null means default candidates (all vertices)
	 * @param cachetype
	 */
	public ContributionVRBC(
			GraphInterface<Index,BasicVertexInfo> G ,
			AbsRoutingFunction routingFunction, 
			AbsTrafficMatrix cw,
			FastList<Index> candidates,
			int cachetype) 
	{
		//super(G, routingFunction, dw, true, true,cachetype,candidates.size(),m_candidates_array);//activating shadowing and history
		super(G,routingFunction, cw);
		m_srbc=new FasterSRBC (G,routingFunction,cw,this);
		M=new FastSet<Index>();//starting of with new set
		
		//build candidates structures
		m_candidates_array=new int [this.G.getNumberOfVertices()];
		if (null!=candidates){
			m_candidates=candidates;
			for (int i=0;i<m_candidates_array.length;i++)
				m_candidates_array[i]=-1;
			int i=0;
			for (Index v:candidates)
				m_candidates_array[v.intValue()]=i++;//putting the the ith candidate in the ith place with ith value
		}
		else {//default candidates all of set (graph) 
			m_candidates=new FastList<Index>(this.G.getNumberOfVertices());
			for (int i=0;i<m_candidates_array.length;i++){
				m_candidates_array[i]=i;
				m_candidates.add(Index.valueOf(i));
			}
		}
		
		//initializing the cache
		switch (cachetype){
			case ShadowedHistoriedCache.CACHEID: this.mCache=new ShadowedHistoriedCache (G.getNumberOfVertices(),true,true);break;
			default: case ShadowedHistoriedCacheArr.CACHEID:this.mCache=new ShadowedHistoriedCacheArr (G.getNumberOfVertices(),true,true,null==candidates?G.getNumberOfVertices():candidates.size(),m_candidates_array);break;			
		}
		mDONTCARE=mCache.DONTCARE();
		
		init ();
		mInitDone=true;
	}
	
	/**
	 * choose candidates and cache type. uses default routing function
	 * @param G
	 * @param dw
	 * @param candidates
	 * @param cachetype
	 */
	public ContributionVRBC(GraphInterface<Index,BasicVertexInfo> G,
			AbsTrafficMatrix cw,FastList<Index> candidates,int cachetype) {
		this(G,new ShortestPathRoutingFunction(G),cw,candidates,cachetype);
	}
	
	/**
	 * no candidates - uses the whole graph as candidate set. uses default routing function
	 * @param G
	 * @param dw
	 * @param cachetype
	 */
	public ContributionVRBC(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw ,int cachetype) {
		this (G,cw,null,cachetype);
	}

	
	
	/**
	 * inits the cache
	 */
	private void init (){
		for (Index u : m_candidates){
			for (Index t : G.getVertices()){
				for (Index s : m_candidates){
					this.getDelta(s,u,t);
				}
				this.getTargetDependency(u,t);
			}
			this.getBetweeness(u);
		}
		
	}
	
	
	

///////////////////////////////////////////
////	Public methods
///////////////////////////////////////////
	/**
	 * add a new vertex to group, uses entire candidates group
	 * @param v - a vertex
	 * @pre v must be a member of candidates
	 * 		group is partial to candidates
	 */
	public void add (Index v){
		this.add(v,this.m_candidates);
	}
	
	/**
	 * add a new vertex to group
	 * @param v - a vertex
	 * @param group - group of candidates
	 * @pre v must be a member of candidates
	 * 		group is partial to candidates
	 */
	public void add (Index v,FastList<Index> group) /*throws Exception*/{
		assert mSavedState;
		assert this.m_candidates_array[v.intValue()]>=0;
		this.mCache.StartShadowing();
		//System.out.println("entered add with v=:"+v.intValue() + "group: "+ group);
//		((ShadowedHistoriedCacheArr)this.mCache).printAllcachedim(mDONTCARE);
		FastList <Index> seq = new FastList <Index>(2); 
		for (Index u : group){
			if (v.intValue()==u.intValue()){// case of v is in candidates, setting cache to 0
				this.mCache.put(mDONTCARE, u.intValue(), mDONTCARE,0.0); 
				for (Index t : G.getVertices()){//for (Index t : group){
					this.mCache.put(mDONTCARE, u.intValue(), t.intValue(),0.0); 
					for (Index s : group){
						this.mCache.put(s.intValue(), u.intValue(), t.intValue(),0.0);
					}
				}
			}
			else { //v is not this candidate
				double val = 
					this.getBetweeness(u) - 
					this.m_srbc.getBetweeness(makeDoubleSequence(seq,u,v)) - 
					this.m_srbc.getBetweeness(makeDoubleSequence(seq,v,u));
				this.mCache.put(mDONTCARE, u.intValue(), mDONTCARE ,val);
				assert val>=0;
				for (Index t : G.getVertices()){
					val=this.getTargetDependency(u,t) -   
						this.m_srbc.getTargetDependency(makeDoubleSequence(seq,u,v),t) - 
						this.m_srbc.getTargetDependency(makeDoubleSequence(seq,v,u),t);
					this.mCache.put(mDONTCARE, u.intValue(), t.intValue() ,val);

					for (Index s : group){
						val=this.getDelta(s,u,t) - 
							this.m_srbc.getDelta(s,makeDoubleSequence(seq,u,v),t) - 
							this.m_srbc.getDelta(s,makeDoubleSequence(seq,v,u),t);
						if (val<0.0){
							System.out.println("BUG!!!");
						}
						this.mCache.put(s.intValue(), u.intValue(), t.intValue() ,val);
					}
				}
			}
		}
		this.mCache.StopShadowing();  //updating the main cache 
		this.M.add(v); //adding v to the group
	}
	
	
	/**
	 * get group members
	 * @return iterator of the group
	 */
	public Iterator<Index> getMembers (){
		return this.M.iterator();
	}
	
	
	/**
	 * @Pre v is a member of  candidates
	 */
	@Override
	public double getBetweeness(Index v) {
		if (mInitDone) assert this.m_candidates_array[v.intValue()]>=0;
		Double res=this.CheckCache(mDONTCARE, v.intValue(), mDONTCARE);
		if (Double.isNaN(res)){
//			if (mInitDone) {System.out.println("cache insertion after init");assert false;}
			res=super.getBetweeness(v);
			this.mCache.put(mDONTCARE, v.intValue() ,mDONTCARE,res);
		}
		return res;
	}

	/**
	 * @Pre v,s is a member of  candidates
	 */
	@Override
	public double getDelta(Index s, Index v, Index t) {
		if (mInitDone) {
			assert this.m_candidates_array[v.intValue()]>=0;
			assert this.m_candidates_array[s.intValue()]>=0;
		}
		/*if (this.G.getDegree(v) <2) //optimization: vertices with degree < 2 are faster to just calculate
			return super.getDelta(s, v, t);*/
		double res=this.CheckCache(s.intValue(), v.intValue(), t.intValue());
		if (Double.isNaN(res)){
			if (mInitDone) {System.out.println("cache insertion after init");assert false;}
			res=super.getDelta(s, v, t);
			this.mCache.put(s.intValue(),v.intValue(),t.intValue(),res);
		}
		assert !Double.isNaN(res);
		return res;
	}
	
	/**
	 * @Pre v,s is a member of candidates
	 */
	@Override
	public double getSourceDependency(Index s, Index v) {
		if (mInitDone) {		
			assert this.m_candidates_array[v.intValue()]>=0;
			assert this.m_candidates_array[s.intValue()]>=0;
		}
		double res=this.CheckCache(s.intValue(), v.intValue(), mDONTCARE);
		if (Double.isNaN(res)){
			res=super.getSourceDependency(s, v);
			this.mCache.put(s.intValue(),v.intValue(),mDONTCARE,res);
		}
		assert !Double.isNaN(res);
		return res;
	}
	
	/**
	 * @Pre v is a member of  candidates
	 */
	@Override
	public double getTargetDependency(Index v, Index t) {
		if (mInitDone) assert this.m_candidates_array[v.intValue()]>=0;
		double res=this.CheckCache(mDONTCARE, v.intValue(), t.intValue());
		if (Double.isNaN(res)){
			res=super.getTargetDependency(v, t);
			this.mCache.put(mDONTCARE,v.intValue(),t.intValue(),res);
		}
		assert !Double.isNaN(res);
		return res;
	}

	/**
	 * saves the current state of the object's cache and group, for later restoration
	 */
	public void saveState (){
		if (!mSavedState){
			this.mCache.saveState();
			this.state_set.addAll(this.M);
			mSavedState=true;
		}
	}
	/**
	 * restore the state of the object
	 * @pre saveState must be called before invoking this method.
	 */
	public void restoreState (){
		if (mSavedState){
			this.mCache.restoreState();
			this.M=state_set;
			this.state_set.clear();
			this.mSavedState=false;
		}
	}
	
///////////////////////////////////////////
////Private/service methods
///////////////////////////////////////////
	
	/**
	 * checks the cache for value
	 * @param a
	 * @param b
	 * @param c
	 * @return value in the cache, NAN if otherwise
	 */
	private Double CheckCache (int a, int b, int c){
		Double res=Double.NaN;
		if (this.mCache.containsKey(a,b,c))
			res=this.mCache.get(a,b,c);
		return res;
	}
	
	private FastList<Index> makeDoubleSequence (FastList<Index> res,Index v1, Index v2){
		res.clear();
		res.add(v1);
		res.add(v2);
		return res;
	}
	
	public static double nanosToSecs(long c) {
        return ((double)c)/1e9;}
	
///////////////////////////////////////////
////Main
///////////////////////////////////////////	
	public static void main (String [] args){
		Random generator = new Random();
        GraphInterface<Index,BasicVertexInfo> graph = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 20; v++)
        	graph.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 20; v++){
        	int t1=generator.nextInt(20);
        	int t2=0;
        	while ((t2=generator.nextInt(20))==t1) {}
        	if (!graph.isEdge(Index.valueOf(t1), Index.valueOf(v)))
        		graph.addEdge(Index.valueOf(t1), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
        	if (!graph.isEdge(Index.valueOf(t2), Index.valueOf(v)))
        		graph.addEdge(Index.valueOf(t2), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
        }
//        DataWorkshop dw=null;
//        try {
//		 dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT,graph, true, new DummyProgress(), 1.0);
//        }catch (Exception e) {
//			// TODO: handle exception
//		}
        AbsTrafficMatrix cw = new DefaultTrafficMatrix(graph.getNumberOfVertices());
        
		ContributionVRBC moshe =new ContributionVRBC(graph,cw,1);	
		long tic = System.nanoTime();
		//double f1=moshe.getDelta(Index.valueOf(0), Index.valueOf(4), Index.valueOf(5));
		double f1=moshe.getBetweeness(Index.valueOf(4));
		System.out.print ("fast: "+nanosToSecs(System.nanoTime()-tic)+" res:"+f1+"| ");
		//moshe.add(Index.valueOf(14));
	}

	public ShadowedHistoriedCacheInterface getMCache() {
		return mCache;
	}

	public int getMDONTCARE() {
		return mDONTCARE;
	}

}

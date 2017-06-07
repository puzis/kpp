package algorithms.centralityAlgorithms.rbc;

import java.util.Arrays;

import java.util.Random;

import javolution.util.FastList;
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

import common.ShadowedHistoriedCacheArr;

public class ArticleFasterGRBC {
	double [][][] pdepOriginal;
	double [][][] pdep;
	double [][][] npdep;
	double [][] tdepOriginal;
	double [][] tdep;
	double [][] ntdep;
	
	private int [] m_candidates_array = null; //optimization for precondition check speedup
	private final FastList<Index> m_candidates;
	/// absBetweeness
	private GraphInterface<Index,BasicVertexInfo> G;
	private AbsRoutingFunction RF;
	private AbsTrafficMatrix communicationWeights;
	
	//private int mDONTCARE;
	private boolean mInitDone=false;
	
	
	public ArticleFasterGRBC (GraphInterface<Index,BasicVertexInfo> G ,
		AbsRoutingFunction routingFunction, 
		AbsTrafficMatrix cw,
		FastList<Index> candidates){
		this.G=G;
        this.RF=routingFunction;
        this.communicationWeights = cw;
        
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
		int numvertices = G.getNumberOfVertices();
		int candsize = null==candidates?G.getNumberOfVertices():candidates.size();
		pdepOriginal = new double [candsize][candsize][numvertices];
		pdep = new double [candsize][candsize][numvertices];
		npdep = new double [candsize][candsize][numvertices];
		tdepOriginal = new double [candsize][numvertices];
		tdep = new double [candsize][numvertices];
		ntdep = new double [candsize][numvertices];
		
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
	public ArticleFasterGRBC(GraphInterface<Index,BasicVertexInfo> G,
			AbsTrafficMatrix cw, FastList<Index> candidates) {
		this(G,new ShortestPathRoutingFunction(G),cw,candidates);
	}
	
	private void init (){
		for (Index u : m_candidates){
			for (Index t :G.getVertices()){
				for (Index s : m_candidates){
					pdepOriginal[s.intValue()][u.intValue()][t.intValue()]=this.getDelta(s,u,t);
				}
				tdepOriginal[u.intValue()][t.intValue()]=this.getTargetDependency(u,t);
			}
		}
		
	}
	
	private void TripleArrayCopy (double [][][] src, double [][][] dst){
		for (int i=0;i<src.length-1;i++)
			for (int j=0;j<src.length-1;j++)
				dst[i][j] = Arrays.copyOf(src[i][j],src.length); 
	}
	private void DoubleArrayCopy (double [][] src, double [][] dst){
		for (int i=0;i<src.length-1;i++)
			dst[i] = Arrays.copyOf(src[i],src.length); 
	}
	
	public double getGroupBetweeness(FastList<Index> group) {
		TripleArrayCopy (pdepOriginal,pdep);
		DoubleArrayCopy (tdepOriginal,tdep);
		double res = 0.0;
		for (Index iv: group){
			int v = iv.intValue();
			for (Index ti : G.getVertices()){
				int t=ti.intValue();
				res +=tdep[v][t];
				for (Index iu: group){
					int u = iu.intValue();
					if (u==v)
						ntdep[u][t]=0.0;
					else
						ntdep[u][t]=
							tdep[u][t] - 
							tdep [u][t]*pdep[u][v][t] - 
							tdep [v][t]*pdep[v][u][t];
					for (Index is: group){
						int s = is.intValue();
						if (u==v)
							npdep [s][u][t] = 0.0;
						else
							npdep[s][u][t]=
								pdep[s][u][t] - 
								pdep [s][u][t]*pdep[u][v][t] - 
								pdep [s][v][t]*pdep[v][u][t];
					}
				}
			}
			double [][][] tmppdep = pdep;
			pdep = npdep;
			npdep = tmppdep;
			
			double [][] tmptdep = tdep;
			tdep = ntdep;
			ntdep = tmptdep;
		}
		return res;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @Pre v is a member of  candidates
	 */
/*	private double getBetweeness(Index v) {
		Double res=Double.NaN;
		if (mInitDone) {
			assert this.m_candidates_array[v.intValue()]>=0;
			res=this.CheckCache(mDONTCARE, v.intValue(), mDONTCARE);
		}
		else {
			if (Double.isNaN(res)){
	//			if (mInitDone) {System.out.println("cache insertion after init");assert false;}
				Iterator<Index> vertsOuter = G.getVertices();
		        res=0.0;
		        while (vertsOuter.hasNext()){
		        	Iterator<Index> vertsInner = G.getVertices();
		            Index t = vertsOuter.next();
		            while (vertsInner.hasNext()){
		                Index s = vertsInner.next();
		                res+=getDelta(s, v, t)*communicationWeights[t.intValue()][s.intValue()];
		            }
		        }
				this.mCache.put(mDONTCARE, v.intValue() ,mDONTCARE,res);
			}
		}
		return res;
	}*/

	/**
	 * @Pre v,s is a member of  candidates
	 */
	private double getDelta(Index s, Index v, Index t) {
		double res=Double.NaN;
		if (mInitDone) {
			assert this.m_candidates_array[v.intValue()]>=0;
			assert this.m_candidates_array[s.intValue()]>=0;
		}
		if (Double.isNaN(res)){
			if (mInitDone) {
				System.out.println("cache insertion after init");assert false;}
	        if (s.intValue() == v.intValue() || v.intValue() == t.intValue())
	            res = 1.0;
	        else{
	        	res=0.0;
	            for (topology.AbstractSimpleEdge<Index,BasicVertexInfo> e: G.getIncomingEdges(v)){
	                Index u = e.getNeighbor(v) ;
	                double RFCalculation=this.RF.routingProbability(s.intValue(), u.intValue(), v.intValue(), t.intValue());
	                if (RFCalculation!=0)
	                    res = res+RFCalculation*getDelta(s,u,t);             
	            }
	        }
		}
		assert !Double.isNaN(res);
		return res;
	}
	
/*	*//**
	 * @Pre v,s is a member of candidates
	 *//*
	private double getSourceDependency(Index s, Index v) {
		if (mInitDone) {		
			assert this.m_candidates_array[v.intValue()]>=0;
			assert this.m_candidates_array[s.intValue()]>=0;
		}
		double res=this.CheckCache(s.intValue(), v.intValue(), mDONTCARE);
		if (Double.isNaN(res)){
			Iterator<Index> verts = G.getVertices();
			res=0.0;
	        while (verts.hasNext()){
	            Index w = verts.next();
	            res+=getDelta(s, v, w)*communicationWeights[s.intValue()][w.intValue()];
	        }
			this.mCache.put(s.intValue(),v.intValue(),mDONTCARE,res);
		}
		assert !Double.isNaN(res);
		return res;
	}*/
	
	/**
	 * @Pre v is a member of  candidates
	 */
	private double getTargetDependency(Index v, Index t) {
		if (mInitDone) assert this.m_candidates_array[v.intValue()]>=0;
		double res= Double.NaN;
		if (Double.isNaN(res)){
			res=0.0;
	        for(Index w : G.getVertices()){
	            res+=getDelta(w, v, t)*communicationWeights.getWeight(w.intValue(), t.intValue());
	        }
		}
		assert !Double.isNaN(res);
		return res;
	}	
	
	
	
///////////////////////////////////////////
////Main
///////////////////////////////////////////	
	public static void main(String[] args) {
		Random generator = new Random();
        GraphInterface<Index,BasicVertexInfo> graph = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 80; v++)
        	graph.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 80; v++){
        	int t1=0;
        	while ((t1=generator.nextInt(80))==v) {}
        	int t2=0;
        	while ((t2=generator.nextInt(80))==t1 || t2==v) {}
        	
        	if (!graph.isEdge(Index.valueOf(t1), Index.valueOf(v)))
        		graph.addEdge(Index.valueOf(t1), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
        	if (!graph.isEdge(Index.valueOf(t2), Index.valueOf(v)))
        		graph.addEdge(Index.valueOf(t2), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
        }
        AbsTrafficMatrix cw = new DefaultTrafficMatrix(graph.getNumberOfVertices());
        
        System.out.println(graph.toString());
        FastList<Index> cands=null;
        FasterGRBC fastgrbc =new FasterGRBC(graph,cw,cands,ShadowedHistoriedCacheArr.CACHEID);
        ArticleFasterGRBC exactfastgrbc =new ArticleFasterGRBC(graph,cw,cands);	
        VeryFastAndAwsomeGRBC awsomefastgrbc =new VeryFastAndAwsomeGRBC(graph,cw,cands,ShadowedHistoriedCacheArr.CACHEID);
		GRBCAlgorithm slowgrbc = new GRBCAlgorithm(graph,cw);
		FastList<Index> group = new FastList<Index> ();
		for (int i=0;i<10;i++)
			group.add(Index.valueOf(i));
		for (int i = 0 ; i < 5 ; i++){
			long tic = System.nanoTime();
			double f3=fastgrbc.getBetweeness(group);//;(Index.valueOf(3), group, Index.valueOf(80));
			//double f1=vrbc.getBetweeness(Index.valueOf(4));
			System.out.print ("fast: "+nanosToSecs(System.nanoTime()-tic)+" res:"+f3+"| ");
			tic = System.nanoTime();
			double f1=exactfastgrbc.getGroupBetweeness(group);//;(Index.valueOf(3), group, Index.valueOf(80));
			//double f1=vrbc.getBetweeness(Index.valueOf(4));
			System.out.print ("ExactGRBC: "+nanosToSecs(System.nanoTime()-tic)+" res:"+f1+"| ");
			tic = System.nanoTime();
			double f2=slowgrbc.getBetweeness(group);
			System.out.print ("slow: "+nanosToSecs(System.nanoTime()-tic)+" res:"+f2+"| ");
			tic = System.nanoTime();
			double f4=awsomefastgrbc.getGroupBetweeness(group);
			System.out.println ("awsome: "+nanosToSecs(System.nanoTime()-tic)+" res:"+f4+"| ");
			tic = System.nanoTime();
		}
	}
	
	public static double nanosToSecs(long c) {
        return ((double)c)/1e9;}
}
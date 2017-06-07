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
import java.util.Map;

/**
 * @author zoharo Accepts a predefined set of vertices (candidates) (subset of
 * or equal to V). Maintains a set of vertices (M) and a corresponding data
 * structure of size O(|V|*|candidates|^2) M can be updated only by accepting
 * new vertices. Call add(v) to add v to M. All methods compute SRBC with
 * respect to M. That is accounting for all communication paths that do not
 * traverse M. In all methods the seq argument must be subset of candidates. In
 * all methods the s argument must be in candidates In all methods the t
 * argument must be in V In all methods the v argument must be in candidates
 */
public class VeryFastAndAwsomeGRBC {

    private int[] m_candidates_array = null; //optimization for precondition check speedup
    private final FastList<Index> m_candidates;
    private FastSet<Index> M;
    //private SRBCAlgorithm m_srbc;//for calculation sequences, uses the same cache as this.
    private boolean mInitDone = false;
    protected ShadowedHistoriedCacheInterface mCache;
    private int mDONTCARE;
    //////////
    // support for state saving data structures, for fasterGRBC implementation 
    private FastSet<Index> state_set = new FastSet<Index>();
    boolean mSavedState = false;
    /// absBetweeness
    private GraphInterface<Index, BasicVertexInfo> G;
    private AbsRoutingFunction RF;
    private AbsTrafficMatrix communicationWeights;

///////////////////////////////////////////
//// construction
///////////////////////////////////////////
    /**
     * Constructor
     *
     * @param G
     * @param routingFunction
     * @param dw
     * @param candidates list of vertices to add. null means default candidates
     * (all vertices)
     * @param cachetype
     */
    public VeryFastAndAwsomeGRBC(
            GraphInterface<Index, BasicVertexInfo> G,
            AbsRoutingFunction routingFunction,
            AbsTrafficMatrix cw,
            FastList<Index> candidates,
            int cachetype) {
        //super(G, routingFunction, dw, true, true,cachetype,candidates.size(),m_candidates_array);//activating shadowing and history
        //super(G,routingFunction, dw);
        //m_srbc=new FasterSRBC (G,routingFunction,dw,this);
        this.G = G;
        this.RF = routingFunction;
        this.communicationWeights = cw;

        M = new FastSet<Index>();//starting of with new set

        //build candidates structures
        m_candidates_array = new int[this.G.getNumberOfVertices()];
        if (null != candidates) {
            m_candidates = candidates;
            for (int i = 0; i < m_candidates_array.length; i++) {
                m_candidates_array[i] = -1;
            }
            int i = 0;
            for (Index v : candidates) {
                m_candidates_array[v.intValue()] = i++;//putting the the ith candidate in the ith place with ith value
            }
        } else {//default candidates all of set (graph) 
            m_candidates = new FastList<Index>(this.G.getNumberOfVertices());
            for (int i = 0; i < m_candidates_array.length; i++) {
                m_candidates_array[i] = i;
                m_candidates.add(Index.valueOf(i));
            }
        }

        //initializing the cache
        switch (cachetype) {
            case ShadowedHistoriedCache.CACHEID:
                this.mCache = new ShadowedHistoriedCache(G.getNumberOfVertices(), true, true);
                break;
            default:
            case ShadowedHistoriedCacheArr.CACHEID:
                this.mCache = new ShadowedHistoriedCacheArr(G.getNumberOfVertices(), true, true, null == candidates ? G.getNumberOfVertices() : candidates.size(), m_candidates_array);
                break;
        }
        mDONTCARE = mCache.DONTCARE();

        init();
        mInitDone = true;
    }

    /**
     * choose candidates and cache type. uses default routing function
     *
     * @param G
     * @param dw
     * @param candidates
     * @param cachetype
     */
    public VeryFastAndAwsomeGRBC(GraphInterface<Index, BasicVertexInfo> G,
            AbsTrafficMatrix cw, FastList<Index> candidates, int cachetype) {
        this(G, new ShortestPathRoutingFunction(G), cw, candidates, cachetype);
    }

    /**
     * no candidates - uses the whole graph as candidate set. uses default
     * routing function
     *
     * @param G
     * @param dw
     * @param cachetype
     */
    public VeryFastAndAwsomeGRBC(GraphInterface<Index, BasicVertexInfo> G, AbsTrafficMatrix cw, int cachetype) {
        this(G, cw, null, cachetype);
    }

    /**
     * inits the cache
     */
    private void init() {
        for (Index u : m_candidates) {
            for (Index t : G.getVertices()) {
                for (Index s : m_candidates) {
                    this.getDelta(s, u, t);
                }
                this.getTargetDependency(u, t);
            }
            this.getBetweeness(u);
        }

    }

///////////////////////////////////////////
////	Public methods
///////////////////////////////////////////
    /**
     * add a new vertex to group, uses entire candidates group
     *
     * @param v - a vertex
     * @pre v must be a member of candidates group is partial to candidates
     */
    public void add(Index v) {
        this.add(v, this.m_candidates);
    }

    /**
     * add a new vertex to group
     *
     * @param v - a vertex
     * @param group - group of candidates
     * @pre v must be a member of candidates group is partial to candidates
     */
    private void add(Index v, FastList<Index> group) /*throws Exception*/ {
        assert mSavedState;
        assert this.m_candidates_array[v.intValue()] >= 0;
        this.mCache.StartShadowing();
        for (Index u : group) {
            if (v.intValue() == u.intValue()) {// case of v is in candidates, setting cache to 0
                this.mCache.put(mDONTCARE, u.intValue(), mDONTCARE, 0.0);
                for (Index t : G.getVertices()) {
                    this.mCache.put(mDONTCARE, u.intValue(), t.intValue(), 0.0);
                    for (Index s : group) {
                        this.mCache.put(s.intValue(), u.intValue(), t.intValue(), 0.0);
                    }
                }
            } else { //v is not this candidate
                double val =
                        this.getBetweeness(u)
                        - this.getBetweeness(u, v)
                        - this.getBetweeness(v, u);
                this.mCache.put(mDONTCARE, u.intValue(), mDONTCARE, val);
                assert val >= 0;
                for (Index t : G.getVertices()) {
                    val = this.getTargetDependency(u, t)
                            - this.getTargetDependency(u, v, t)
                            - this.getTargetDependency(v, u, t);
                    this.mCache.put(mDONTCARE, u.intValue(), t.intValue(), val);

                    for (Index s : group) {
                        val = this.getDelta(s, u, t)
                                - this.getDelta(s, u, v, t)
                                - this.getDelta(s, v, u, t);
                        this.mCache.put(s.intValue(), u.intValue(), t.intValue(), val);
                    }
                }
            }
        }
        this.mCache.StopShadowing();  //updating the main cache 
        this.M.add(v); //adding v to the group
    }

    /**
     * get group members
     *
     * @return iterator of the group
     */
    public Iterator<Index> getMembers() {
        return this.M.iterator();
    }

    /**
     * @Pre v is a member of candidates
     */
    private double getBetweeness(Index v) {
        Double res = Double.NaN;
        if (mInitDone) {
            assert this.m_candidates_array[v.intValue()] >= 0;
            res = this.CheckCache(mDONTCARE, v.intValue(), mDONTCARE);
        } else {
            if (Double.isNaN(res)) {
                //			if (mInitDone) {System.out.println("cache insertion after init");assert false;}
                res = 0.0;
                for (Index t : G.getVertices()) {
                    for (Index s : G.getVertices()) {
                        res += getDelta(s, v, t) * communicationWeights.getWeight(t.intValue(), s.intValue()); //[t.intValue()][s.intValue()];
                    }
                }
                this.mCache.put(mDONTCARE, v.intValue(), mDONTCARE, res);
            }
        }
        return res;
    }

    /**
     * @Pre v,s is a member of candidates
     */
    private double getDelta(Index s, Index v, Index t) {
        double res = Double.NaN;
        if (mInitDone) {
            assert this.m_candidates_array[v.intValue()] >= 0;
            assert this.m_candidates_array[s.intValue()] >= 0;
            res = this.CheckCache(s.intValue(), v.intValue(), t.intValue());
        } else {
            /*if (this.G.getDegree(v) <2) //optimization: vertices with degree < 2 are faster to just calculate
             return super.getDelta(s, v, t);*/
            if (Double.isNaN(res)) {
                if (mInitDone) {
                    System.out.println("cache insertion after init");
                    assert false;
                }
                if (s.intValue() == v.intValue() || v.intValue() == t.intValue()) {
                    res = 1.0;
                } else {
                    res = 0.0;
                    for (topology.AbstractSimpleEdge<Index, BasicVertexInfo> e : G.getIncomingEdges(v)) {
                        Index u = e.getNeighbor(v);
                        double RFCalculation = this.RF.routingProbability(s.intValue(), u.intValue(), v.intValue(), t.intValue());
                        if (RFCalculation != 0) {
                            res = res + RFCalculation * getDelta(s, u, t);
                        }
                    }
                }
                this.mCache.put(s.intValue(), v.intValue(), t.intValue(), res);
            }
        }
        assert !Double.isNaN(res);
        return res;
    }

    /**
     * @Pre v,s is a member of candidates
     */
    private double getSourceDependency(Index s, Index v) {
        if (mInitDone) {
            assert this.m_candidates_array[v.intValue()] >= 0;
            assert this.m_candidates_array[s.intValue()] >= 0;
        }
        double res = this.CheckCache(s.intValue(), v.intValue(), mDONTCARE);
        if (Double.isNaN(res)) {
            res = 0.0;
            for (Index w : G.getVertices()) {
                res += getDelta(s, v, w) * communicationWeights.getWeight(s.intValue(), w.intValue()); //[s.intValue()][w.intValue()];
            }
            this.mCache.put(s.intValue(), v.intValue(), mDONTCARE, res);
        }
        assert !Double.isNaN(res);
        return res;
    }

    /**
     * @Pre v is a member of candidates
     */
    private double getTargetDependency(Index v, Index t) {
        if (mInitDone) {
            assert this.m_candidates_array[v.intValue()] >= 0;
        }
        double res = this.CheckCache(mDONTCARE, v.intValue(), t.intValue());
        if (Double.isNaN(res)) {
            res = 0.0;
            for (Index w : G.getVertices()) {
                res += getDelta(w, v, t) * communicationWeights.getWeight(w.intValue(), t.intValue()); //[w.intValue()][t.intValue()];
            }
            this.mCache.put(mDONTCARE, v.intValue(), t.intValue(), res);
        }
        assert !Double.isNaN(res);
        return res;
    }

    /**
     * *******************************
     * SRBC
     */
    private double getDelta(Index s, Index s1, Index s2, Index t) {
        double result = 1.0;
        result *= this.getDelta(s, s1, t);
        result *= this.getDelta(s1, s2, t);
        return result;
    }

    private double getTargetDependency(Index s1, Index s2, Index t) {
        double res = this.getTargetDependency(s1, t) * this.getDelta(s1, s2, t);
        return res;
    }

    private double getBetweeness(Index s1, Index s2) {
        double res = 0.0;
        for (Index t : G.getVertices()) {
            res += this.getTargetDependency(s1, s2, t);
        }
        return res;
    }

    /**
     * saves the current state of the object's cache and group, for later
     * restoration
     */
    private void saveState() {
        if (!mSavedState) {
            this.mCache.saveState();
            this.state_set.addAll(this.M);
            mSavedState = true;
        }
    }

    /**
     * restore the state of the object
     *
     * @pre saveState must be called before invoking this method.
     */
    private void restoreState() {
        if (mSavedState) {
            this.mCache.restoreState();
            this.M = state_set;
            this.state_set.clear();
            this.mSavedState = false;
        }
    }

///////////////////////////////////////////
////Private/service methods
///////////////////////////////////////////
    /**
     * checks the cache for value
     *
     * @param a
     * @param b
     * @param c
     * @return value in the cache, NAN if otherwise
     */
    private Double CheckCache(int a, int b, int c) {
        Double res = Double.NaN;
        if (this.mCache.containsKey(a, b, c)) {
            res = this.mCache.get(a, b, c);
        }
        return res;
    }

    public static double nanosToSecs(long c) {
        return ((double) c) / 1e9;
    }

    /**
     * * FASTER GRBC **
     */
    public double getGroupBetweeness(FastList<Index> group) {
        return getGroupBetweeness(group, null);
    }

    public double getGroupBetweeness(FastList<Index> group, Map<Integer, Double> samples) {
        double result = 0.0;
        this.saveState();
        for (Index v : group) {
            double res = this.getBetweeness(v);
            result += res * (samples != null ? samples.get(v.intValue()) : 1.0);
            this.add(v, group);
        }
        this.restoreState();
        return result;
    }

    public double getGroupDelta(Index s, FastList<Index> group, Index t) {
        double result = 0.0;
        this.saveState();
        for (Index v : group) {
            result += this.getDelta(s, v, t);
            this.add(v);
        }
        this.restoreState();
        return result;
    }

    /**
     * @Pre group,s is a member of candidates
     */
    public double getGroupSourceDependency(Index s, FastList<Index> group) {
        double result = 0.0;
        this.saveState();
        for (Index v : group) {
            result += this.getSourceDependency(s, v);
            this.add(v);
        }
        this.restoreState();
        return result;
    }

    /**
     * @Pre group is a member of candidates
     */
    public double getGroupTargetDependency(FastList<Index> group, Index t) {
        double result = 0.0;
        this.saveState();
        for (Index v : group) {
            result += this.getTargetDependency(v, t);
            this.add(v);
        }
        this.restoreState();
        return result;
    }

///////////////////////////////////////////
////Main
///////////////////////////////////////////	
    public static void main(String[] args) {
        Random generator = new Random();
        GraphInterface<Index, BasicVertexInfo> graph = new GraphAsHashMap<Index, BasicVertexInfo>();
        for (int v = 0; v < 80; v++) {
            graph.addVertex(Index.valueOf(v), new VertexInfo());
        }
        for (int v = 0; v < 80; v++) {
            int t1 = 0;
            while ((t1 = generator.nextInt(80)) == v) {
            }
            int t2 = 0;
            while ((t2 = generator.nextInt(80)) == t1 || t2 == v) {
            }

            if (!graph.isEdge(Index.valueOf(t1), Index.valueOf(v))) {
                graph.addEdge(Index.valueOf(t1), Index.valueOf(v), new EdgeInfo<Index, BasicVertexInfo>());
            }
            if (!graph.isEdge(Index.valueOf(t2), Index.valueOf(v))) {
                graph.addEdge(Index.valueOf(t2), Index.valueOf(v), new EdgeInfo<Index, BasicVertexInfo>());
            }
        }
//        graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index>());
//        graph.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index>());
//        graph.addEdge(Index.valueOf(1), Index.valueOf(4), new EdgeInfo<Index>());
//        graph.addEdge(Index.valueOf(1), Index.valueOf(3), new EdgeInfo<Index>());
//        graph.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index>());
//        graph.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo<Index>());
//        graph.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index>());
//        graph.addEdge(Index.valueOf(3), Index.valueOf(4), new EdgeInfo<Index>());

//        DataWorkshop dw=null;
//        try {
//         dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT,graph, true, new DummyProgress(), 1.0);
//        }catch (Exception e) {
//			// TODO: handle exception
//		}
        AbsTrafficMatrix cw = new DefaultTrafficMatrix(graph.getNumberOfVertices());
        System.out.println(graph.toString());
        FastList<Index> cands = null;/*new FastSet<Index> ();
         cands.add(Index.valueOf(0));
         cands.add(Index.valueOf(1));
         cands.add(Index.valueOf(3));
         cands.add(Index.valueOf(4));*/
        FasterGRBC fastgrbc = new FasterGRBC(graph, cw, cands, ShadowedHistoriedCacheArr.CACHEID);
        VeryFastAndAwsomeGRBC fastAwsomegrbc = new VeryFastAndAwsomeGRBC(graph, cw, cands, ShadowedHistoriedCacheArr.CACHEID);
        GRBCAlgorithm slowgrbc = new GRBCAlgorithm(graph, cw);
        FastList<Index> group = new FastList<Index>();
        for (int i = 0; i < 10; i++) {
            group.add(Index.valueOf(i));
        }
        long tic = System.nanoTime();
        double f3 = fastgrbc.getBetweeness(group);//;(Index.valueOf(3), group, Index.valueOf(80));
        //double f1=vrbc.getBetweeness(Index.valueOf(4));
        System.out.print("fast: " + nanosToSecs(System.nanoTime() - tic) + " res:" + f3 + "| ");
        tic = System.nanoTime();
        double f1 = fastAwsomegrbc.getGroupBetweeness(group);//;(Index.valueOf(3), group, Index.valueOf(80));
        //double f1=vrbc.getBetweeness(Index.valueOf(4));
        System.out.print("fastAwsome: " + nanosToSecs(System.nanoTime() - tic) + " res:" + f1 + "| ");
        tic = System.nanoTime();
        double f2 = slowgrbc.getBetweeness(group);
        System.out.println("slow: " + nanosToSecs(System.nanoTime() - tic) + " res:" + f2 + "| ");
        tic = System.nanoTime();

        tic = System.nanoTime();
        f1 = fastgrbc.getBetweeness(group);//;(Index.valueOf(3), group, Index.valueOf(5));
        System.out.print("fast: " + nanosToSecs(System.nanoTime() - tic) + " res:" + f1 + "| ");
        f1 = fastAwsomegrbc.getGroupBetweeness(group);//;(Index.valueOf(3), group, Index.valueOf(5));
        System.out.print("fastAwsome: " + nanosToSecs(System.nanoTime() - tic) + " res:" + f1 + "| ");
        tic = System.nanoTime();
        f2 = slowgrbc.getBetweeness(group);
        System.out.println("slow: " + nanosToSecs(System.nanoTime() - tic) + " res:" + f2 + "| ");

//		assert f1==f2;
	/*	for (Iterator<Index> it_u=graph.getVertices();it_u.hasNext();){
         Index u=it_u.next();
         for (Iterator<Index> it_t=graph.getVertices();it_t.hasNext();){
         Index t=it_t.next();
         FastList<Index> group = new FastList<Index> ();
         for (Iterator<Index> it_s=graph.getVertices();it_s.hasNext();){
         Index s=it_s.next();
         group.add(s);
					
         //					assert f1==f2;
         }
         //				fastgrbc.getTargetDependency(group,t);
         }
         //fastgrbc.getBetweeness(u);*/
    }
    //moshe.add(Index.valueOf(14));

    public ShadowedHistoriedCacheInterface getMCache() {
        return mCache;
    }

    public int getMDONTCARE() {
        return mDONTCARE;
    }
}

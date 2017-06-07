/**
 * 
 */
package algorithms.centralityAlgorithms.rbc;

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

import common.ShadowedHistoriedCacheArr;

/**
 * @author zoharo
 *
 */
public class FasterGRBC extends AbsBetweenessAlgorithm<FastList<Index>> {
	private ContributionVRBC m_cvrbc;
	
	/**
	 * @param G
	 * @param routingFunction
	 * @param dw
	 */
	public FasterGRBC(GraphInterface<Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction,
			AbsTrafficMatrix cw,FastList<Index> candidates,int cachetype) {
		super(G, routingFunction, cw);
		m_cvrbc=new ContributionVRBC(G,routingFunction,cw,candidates,cachetype);
	}

	/**
	 * @param G
	 * @param dw
	 */
	public FasterGRBC(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw, FastList<Index> candidates,int cachetype) {
		super(G, cw);
		
		m_cvrbc=new ContributionVRBC(G,this.RF,cw,candidates,cachetype);
	}

	/**
	 * @Pre group,s is a member of candidates
	 */
	@Override
	public double getDelta(Index s, FastList<Index> group, Index t) {
		double result=0.0;
		m_cvrbc.saveState();
			for (Index v: group){
				result += m_cvrbc.getDelta(s, v, t);
				m_cvrbc.add(v);
			}
		m_cvrbc.restoreState();
		return result;
	}
	
	/**
	 * @Pre group,s is a member of candidates
	 */
	@Override
	public double getSourceDependency(Index s, FastList<Index> group) {
		double result=0.0;
		m_cvrbc.saveState();
			for (Index v: group){
				result += m_cvrbc.getSourceDependency(s, v);
				m_cvrbc.add(v);
			}
		m_cvrbc.restoreState();
		return result;
	}
	
	/**
	 * @Pre group is a member of  candidates
	 */
	@Override
	public double getTargetDependency(FastList<Index> group, Index t) {
		double result=0.0;
		m_cvrbc.saveState();
			for (Index v: group){
				result += m_cvrbc.getTargetDependency(v, t);
				m_cvrbc.add(v);
			}
		m_cvrbc.restoreState();
		return result;
	}

	/**
	 * @Pre group is a member of  candidates
	 */
	public double getBetweeness(FastList<Index> group) {
		double result=0.0;
		m_cvrbc.saveState();
			for (Index v: group){
				double res=m_cvrbc.getBetweeness(v);
				result += res;
				m_cvrbc.add(v,group);
			}
		m_cvrbc.restoreState();
		return result;
	}

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random generator = new Random();
        GraphInterface<Index,BasicVertexInfo> graph = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 100; v++)
        	graph.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 100; v++){
        	int t1=0;
        	while ((t1=generator.nextInt(100))==v) {}
        	int t2=0;
        	while ((t2=generator.nextInt(100))==t1 || t2==v) {}
        	
        	if (!graph.isEdge(Index.valueOf(t1), Index.valueOf(v)))
        		graph.addEdge(Index.valueOf(t1), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
        	if (!graph.isEdge(Index.valueOf(t2), Index.valueOf(v)))
        		graph.addEdge(Index.valueOf(t2), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
        }
//        graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo(new Double(1)));
//        graph.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo(new Double(1)));
//        graph.addEdge(Index.valueOf(1), Index.valueOf(4), new EdgeInfo(new Double(1)));
//        graph.addEdge(Index.valueOf(1), Index.valueOf(3), new EdgeInfo(new Double(1)));
//        graph.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo(new Double(1)));
//        graph.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo(new Double(1)));
//        graph.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo(new Double(1)));
//        graph.addEdge(Index.valueOf(3), Index.valueOf(4), new EdgeInfo(new Double(1)));
        
//        DataWorkshop dw=null;
//        try {
//         dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT,graph, true, new DummyProgress(), 1.0);
//        }catch (Exception e) {
//			// TODO: handle exception
//		}
        AbsTrafficMatrix cw = new DefaultTrafficMatrix(graph.getNumberOfVertices());
        
        System.out.println(graph.toString());
        FastList<Index> cands=null;/*new FastSet<Index> ();
        cands.add(Index.valueOf(0));
        cands.add(Index.valueOf(1));
        cands.add(Index.valueOf(3));
        cands.add(Index.valueOf(4));*/
        FasterGRBC fastgrbc =new FasterGRBC(graph,cw,cands,ShadowedHistoriedCacheArr.CACHEID);	
		GRBCAlgorithm slowgrbc = new GRBCAlgorithm(graph,cw);
		FastList<Index> group = new FastList<Index> ();
		for (int i=0;i<10;i++)
			group.add(Index.valueOf(i));
		long tic = System.nanoTime();
		double f1=fastgrbc.getBetweeness(group);//;(Index.valueOf(3), group, Index.valueOf(100));
		//double f1=vrbc.getBetweeness(Index.valueOf(4));
		System.out.print ("fast: "+nanosToSecs(System.nanoTime()-tic)+" res:"+f1+"| ");
		tic = System.nanoTime();
		double f2=slowgrbc.getBetweeness(group);
		System.out.println ("slow: "+nanosToSecs(System.nanoTime()-tic)+" res:"+f2+"| ");
		tic = System.nanoTime();
		f1=fastgrbc.getBetweeness(group);//;(Index.valueOf(3), group, Index.valueOf(5));
		System.out.print ("fast: "+nanosToSecs(System.nanoTime()-tic)+" res:"+f1+"| ");
		tic = System.nanoTime();
		f2=slowgrbc.getBetweeness(group);
		System.out.println ("slow: "+nanosToSecs(System.nanoTime()-tic)+" res:"+f2+"| ");
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
	
	public static double nanosToSecs(long c) {
        return ((double)c)/1e9;}

}

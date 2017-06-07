package tests.rbc;


import java.util.Random;
import java.util.Vector;

import javolution.util.FastList;
import javolution.util.Index;
import junit.framework.TestCase;

import org.junit.Before;

import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.rbc.FasterSRBC;
import algorithms.centralityAlgorithms.rbc.SRBCAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

import common.Pair;

public class SRBCPerformanceTest extends TestCase{
	private Vector<GraphInterface<Index,BasicVertexInfo>> m_graph_tests;
	private Vector<Pair<Double,Double>> m_results_TargetDependency ,m_results_Betweeness;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		m_graph_tests=new Vector<GraphInterface<Index,BasicVertexInfo>>();
		m_results_TargetDependency=new Vector<Pair<Double,Double>>(); 
		m_results_Betweeness=new Vector<Pair<Double,Double>>(); 
		
		// generating graphs with average degree of 2 with 10-100 vertices
        for (int i=10;i<=100;i+=10){
	        Random generator = new Random();
	        GraphInterface<Index,BasicVertexInfo> graph = new GraphAsHashMap<Index,BasicVertexInfo>();
	        for (int v = 0; v < i; v++)
	        	graph.addVertex(Index.valueOf(v), new VertexInfo());
	        for (int v = 0; v < i; v++){
	        	int t1=generator.nextInt(i);
	        	int t2=0;
	        	while ((t2=generator.nextInt(i))==t1) {}
	        	if (!graph.isEdge(Index.valueOf(t1), Index.valueOf(v)))
	        		graph.addEdge(Index.valueOf(t1), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
	        	if (!graph.isEdge(Index.valueOf(t2), Index.valueOf(v)))
	        		graph.addEdge(Index.valueOf(t2), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
	        }
	        m_graph_tests.add(graph);
        }
	
	}
	
	public void testVRBComparisongetDelta() throws Exception{
		Random generator = new Random();
		
		for (GraphInterface<Index,BasicVertexInfo> graph : m_graph_tests){
			AbsTrafficMatrix cw = new DefaultTrafficMatrix(graph.getNumberOfVertices());
			SRBCAlgorithm SRBCalg=new SRBCAlgorithm(graph, cw);
			SRBCAlgorithm FasterSRBCalg=new FasterSRBC(graph, cw);;
			double res=0;
			double [] T_first= {0.0,0.0};//number of test functions
			double [] T_second= {0.0,0.0};
			int numvertices=graph.getNumberOfVertices();
			
			for (int t=0;t<3;t++){
				//prepearing the sequence
				FastList<Index> sequence =new FastList<Index> ();
				for (int i=0;i<3;i++){
					sequence.add(Index.valueOf(generator.nextInt(numvertices)));				   
				}
				/**************
				 test getTargetDependency
				***************/
				for (int i=0;i<2;i++){
					long tic = System.nanoTime();
					res=SRBCalg.getTargetDependency(sequence, Index.valueOf(0));
					T_first[0]+=nanosToSecs(System.nanoTime()-tic);
					
					tic = System.nanoTime();
					res-=FasterSRBCalg.getTargetDependency(sequence, Index.valueOf(0));
					T_second[0]+=nanosToSecs(System.nanoTime()-tic);
					assertEquals(0.0, res); //making sure we get the same results
				}
					
				/**************
				 test getBetweeness
				***************/
				for (int i=0;i<2;i++){
					long tic = System.nanoTime();
					res=SRBCalg.getBetweeness(sequence);
					T_first[1]+=nanosToSecs(System.nanoTime()-tic);
					
					tic = System.nanoTime();
					res-=FasterSRBCalg.getBetweeness(sequence);
					T_second[1]+=nanosToSecs(System.nanoTime()-tic);
					assertEquals(0.0, res); //making sure we get the same results
				}
			}
			m_results_TargetDependency.add(new Pair<Double, Double>(T_first[0],T_second[0]));
			m_results_Betweeness.add(new Pair<Double, Double>(T_first[1],T_second[1]));
		}
		System.out.println("TargetDependency: "+m_results_TargetDependency);
		System.out.println("getBetweeness: "+m_results_Betweeness);
	}
	
	public static double nanosToSecs(long c) {
        return ((double)c)/1e9;
	}
}
/**
 * 
 */
package tests.rbc;


import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import javolution.util.Index;
import junit.framework.TestCase;

import org.junit.Before;

import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.rbc.AbsBetweenessAlgorithm;
import algorithms.centralityAlgorithms.rbc.StatefullVRBCAlgorithm;
import algorithms.centralityAlgorithms.rbc.VRBCAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

import common.Pair;
import common.Triple;

/**
 * @author omer zohar
 *
 */
public class VRBCPerformanceTest extends TestCase{
	private Vector<GraphInterface<Index,BasicVertexInfo>> m_graph_tests;
	private Vector<Pair<Double,Double>> m_results_delta,m_results_TargetDependency ,m_results_SourceDependency ,m_results_Betweeness;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		m_graph_tests=new Vector<GraphInterface<Index,BasicVertexInfo>>();
		m_results_delta=new Vector<Pair<Double,Double>>();
		m_results_TargetDependency=new Vector<Pair<Double,Double>>(); 
		m_results_SourceDependency=new Vector<Pair<Double,Double>>(); 
		m_results_Betweeness=new Vector<Pair<Double,Double>>(); 
		
		
		GraphInterface<Index,BasicVertexInfo> graph = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 20; v++)
        	graph.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 20; v++)
            for (int u = 0; u < 20; u++)
                if (u != v)
                {
                	if (!graph.isEdge(Index.valueOf(u), Index.valueOf(v)))
                		graph.addEdge(Index.valueOf(u), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
                }
        m_graph_tests.add(graph);
        
        graph = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 50; v++)
        	graph.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 50; v++)
            for (int u = 0; u < 50; u++)
                if (u != v)
                {
                	if (!graph.isEdge(Index.valueOf(u), Index.valueOf(v)))
                		graph.addEdge(Index.valueOf(u), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
                }
        m_graph_tests.add(graph);
/*		
		NetFileParser gl = new NetFileParser();
		graph=gl.analyzeFile("res\\test_graph_100.net", new PhaseProgress(), 1);
		m_graph_tests.add(graph);

		gl = new NetFileParser();
		graph=gl.analyzeFile("res\\test_graph_500.net", new PhaseProgress(), 1);
		m_graph_tests.add(graph);
	*/	
		
	}
	
	public void testVRBComparisongetDelta() throws Exception{
		Random generator = new Random();
		
		for (GraphInterface<Index,BasicVertexInfo> graph : m_graph_tests){
			AbsTrafficMatrix cw = new DefaultTrafficMatrix(graph.getNumberOfVertices());
			AbsTrafficMatrix cw2 = new DefaultTrafficMatrix(graph.getNumberOfVertices());
			AbsBetweenessAlgorithm<Index> VRBCalg1=new VRBCAlgorithm(graph, cw);
			AbsBetweenessAlgorithm<Index> VRBCalg2=new StatefullVRBCAlgorithm(graph, cw2);;
			double res=0;
			double [] T_first= {0.0,0.0,0.0,0.0};//number of test functions
			double [] T_second={0.0,0.0,0.0,0.0};
			int numvertices=graph.getNumberOfVertices();
			
			LinkedList <Triple<Index, Index, Index>> test_subjects =new LinkedList<Triple<Index,Index,Index>> ();
			for (int i=0;i<numvertices*3;i++){
				test_subjects.add(new Triple<Index, Index, Index>
				   (Index.valueOf(generator.nextInt(numvertices/3)),
				    Index.valueOf(1),
				    Index.valueOf(2)));
			}
			while (!test_subjects.isEmpty()){
				/**************
				 test getDelta
				***************/
				Triple<Index, Index, Index> testedSVT=test_subjects.removeFirst();
				long tic = System.nanoTime();
					res=VRBCalg1.getDelta(testedSVT.getValue1(), testedSVT.getValue2(), testedSVT.getValue3());
				T_first[0]+=nanosToSecs(System.nanoTime()-tic);
				
				tic = System.nanoTime();
				res-=VRBCalg2.getDelta(testedSVT.getValue1(), testedSVT.getValue2(), testedSVT.getValue3());
				T_second[0]+=nanosToSecs(System.nanoTime()-tic);
				assertEquals(0.0, res); //making sure we get the same results
				
				/**************
				 test getTargetDependency
				***************/
				tic = System.nanoTime();
				res=VRBCalg1.getTargetDependency(testedSVT.getValue1(), testedSVT.getValue2());
				T_first[1]+=nanosToSecs(System.nanoTime()-tic);
				
				tic = System.nanoTime();
				res-=VRBCalg2.getTargetDependency(testedSVT.getValue1(), testedSVT.getValue2());
				T_second[1]+=nanosToSecs(System.nanoTime()-tic);
				assertEquals(0.0, res); //making sure we get the same results
				
				/**************
				 test getSourceDependency
				***************/
				tic = System.nanoTime();
				res=VRBCalg1.getSourceDependency(testedSVT.getValue1(), testedSVT.getValue2());
				T_first[2]+=nanosToSecs(System.nanoTime()-tic);
				
				tic = System.nanoTime();
				res-=VRBCalg2.getSourceDependency(testedSVT.getValue1(), testedSVT.getValue2());
				T_second[2]+=nanosToSecs(System.nanoTime()-tic);
				assertEquals(0.0, res); //making sure we get the same results
				
				/**************
				 test getBetweeness
				***************/
				tic = System.nanoTime();
				res=VRBCalg1.getBetweeness(testedSVT.getValue1());
				T_first[3]+=nanosToSecs(System.nanoTime()-tic);
				
				tic = System.nanoTime();
				res-=VRBCalg2.getBetweeness(testedSVT.getValue1());
				T_second[3]+=nanosToSecs(System.nanoTime()-tic);
				assertEquals(0.0, res); //making sure we get the same results
			}
			m_results_delta.add(new Pair<Double, Double>(T_first[0],T_second[0]));
			m_results_TargetDependency.add(new Pair<Double, Double>(T_first[1],T_second[1]));
			m_results_SourceDependency.add(new Pair<Double, Double>(T_first[2],T_second[2]));
			m_results_Betweeness.add(new Pair<Double, Double>(T_first[3],T_second[3]));
		}
		System.out.println("getdelta: "+m_results_delta);
		System.out.println("TargetDependency: "+m_results_TargetDependency);
		System.out.println("getSourceDependency: "+m_results_SourceDependency);
		System.out.println("getBetweeness: "+m_results_Betweeness);
	}
	
	public static double nanosToSecs(long c) {
        return ((double)c)/1e9;
	}
}
package tests.topology;

import javolution.util.Index;

import org.junit.Before;
import org.junit.Test;

import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.HyperBrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.HyperBrandesBCWithBackPhase;
import algorithms.centralityAlgorithms.betweenness.brandes.HyperBrandesBCWithMerging;
import algorithms.centralityAlgorithms.betweenness.brandes.TrafficMatrixBC;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.MultiplicityTrafficMatrix;
import algorithms.shortestPath.HyperBFSAlgorithm;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

import server.common.DataBase;
import server.common.DummyProgress;
import server.common.Network;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.GraphUtils;
import topology.HyperGraphInterface;

public class GraphUtilsTest extends junit.framework.TestCase{
	Network network;
	HyperGraphInterface<Index,BasicVertexInfo> hgraph;
	GraphInterface<Index,BasicVertexInfo> graph;
	@Before
	public void setUp() throws Exception {
		network = new Network("dblp");
		System.out.println("Calling setup");
		boolean success = network.importNetwork(
				new DummyProgress(), 
				"dblp-Y1990.e.csv", 
				"", 
				"e.csv", 
				GraphFactory.GraphDataStructure.HYPER_GRAPH_AS_HASH_MAP,
				GraphFactory.VertexInfoType.VERTEX_INFO
				);		

		if(!success) {
			System.out.println("Couldn't import");
		}
		hgraph = network.getGraph();
		graph = (GraphAsHashMap<Index,BasicVertexInfo>)network.getGraphAs(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP);
	}

	//	@Test
	//	public void testMerging() {
	//		HyperGraphInterface<Index,BasicVertexInfo> hgraph = network.getGraph();
	//		System.out.println("Testing merging equivalent vertices of hypergraph");
	//		GraphUtils.mergeEquivalentVertices(hgraph);
	//		assertEquals(4,hgraph.getNumberOfEdges());
	//		assertEquals(10,hgraph.getNumberOfVertices());
	//		assertEquals(3,hgraph.getVertex(Index.valueOf(0)).getMultiplicity());
	//		assertEquals(2,hgraph.getVertex(Index.valueOf(7)).getMultiplicity());
	//		assertTrue(hgraph.isVertex(Index.valueOf(2)));
	//		assertEquals("{0 3 4 }{3 4 5 6 }{4 7 9 }{6 9 }",hgraph.toString());
	//				
	//	}

	@Test
	public void testBetweenness() {
		HyperBrandesBC hbrandes2;// = new HyperBrandesBC(hgraph, null);
		hbrandes2 = new HyperBrandesBC(hgraph, null);
		long t2 = System.currentTimeMillis();
		hbrandes2.run();		
		t2 = System.currentTimeMillis() - t2;
		System.out.println("Original hypertime : "+t2);
		
		System.gc();
		

		HyperBrandesBCWithMerging hbrandes3 ;//= new HyperBrandesBCWithMerging(hgraph);
		hbrandes3 = new HyperBrandesBCWithMerging(hgraph,false);
		long t3 = System.currentTimeMillis();
		hbrandes3.run();
		t3 = System.currentTimeMillis() - t3;
		System.out.println("Merged hypertime : "+t3);
		
		System.gc();
		HyperBrandesBCWithMerging hbrandes5 ;//= new HyperBrandesBCWithMerging(hgraph);
		hbrandes5 = new HyperBrandesBCWithMerging(hgraph,true);
		long t5 = System.currentTimeMillis();
		hbrandes5.run();
		t5 = System.currentTimeMillis() - t5;
		System.out.println("Merged+back hypertime : "+t5);
		
		
		System.gc();
		HyperBrandesBCWithBackPhase hbrandes4 ;//= new HyperBrandesBCWithMerging(hgraph);
		hbrandes4 = new HyperBrandesBCWithBackPhase(hgraph, null);
		long t4 = System.currentTimeMillis();
		hbrandes4.run();
		t4 = System.currentTimeMillis() - t4;
		System.out.println("Backphase hypertime : "+t4);
		System.gc();
		
		
		BrandesBC brandes1 = new TrafficMatrixBC(ShortestPathAlg.BFS, graph, new DummyProgress(), 0);
		long t1 = System.currentTimeMillis();
		brandes1.run();
		t1 = System.currentTimeMillis() - t1;
		System.out.println("Time for brandes : "+t1);
		System.gc();
				
		
		for(int i=0;i<hgraph.getNumberOfVertices();i++) {
			assertEquals(hbrandes4.getCentrality(i), hbrandes5.getCentrality(i), 0.000001);
			assertEquals(hbrandes3.getCentrality(i), hbrandes4.getCentrality(i), 0.000001);
			assertEquals(hbrandes2.getCentrality(i), hbrandes3.getCentrality(i), 0.000001);
			assertEquals(brandes1.getCentrality(i), hbrandes2.getCentrality(i), 0.000001);
		}
	}
}

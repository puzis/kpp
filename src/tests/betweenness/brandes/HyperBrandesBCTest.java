package tests.betweenness.brandes;

import java.util.Date;
import java.util.Random;

import javolution.util.Index;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import server.common.DummyProgress;
import server.common.Network;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.HyperGraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.HyperBrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.HyperBrandesBCWithMerging;
import algorithms.centralityAlgorithms.betweenness.brandes.TrafficMatrixBC;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class HyperBrandesBCTest extends TestCase{
	Network network;
	@Before
	public void setUp() throws Exception {
		network = new Network("dblp");
		boolean success = network.importNetwork(
				new DummyProgress(), 
//				"hyperbackpropagation.e.csv",
//				"dblp-Y1980.e.csv", 
				"venues-1974.e.csv",
				"", 
				"e.csv", 
				GraphFactory.GraphDataStructure.HYPER_GRAPH_AS_HASH_MAP,
				GraphFactory.VertexInfoType.VERTEX_INFO
			);
		if(!success) {
			System.out.println("Couldn't import");
		}
	}
	@Test
	public void testHyperBC_vs_SimpleBC() {
		GraphInterface<Index,BasicVertexInfo> graph = (GraphAsHashMap<Index,BasicVertexInfo>)network.getGraphAs(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP);
		BrandesBC brandes = new TrafficMatrixBC(ShortestPathAlg.BFS, graph, new DummyProgress(), 0);

		long t1 = System.currentTimeMillis();
		brandes.run();
		t1 = System.currentTimeMillis() - t1;
		
		System.out.println("Time for brandes : "+t1);
		
		HyperGraphInterface<Index, BasicVertexInfo> hgraph = network.getGraph();
		HyperBrandesBC hbrandes = new HyperBrandesBC(hgraph, null);

		long t = System.currentTimeMillis();
		hbrandes.run();
		t = System.currentTimeMillis() - t;
		
		System.out.println("Time for hbrandes : "+t);
		
		
		for(int i=0;i<graph.getNumberOfVertices();i++) {
			assertEquals(brandes.getCentrality(i), hbrandes.getCentrality(i),0.000001);
			//System.out.println(hbrandes.getCentrality(i)+" : "+brandes.getCentrality(i));
		}
	}
	
	@Test
	public void testBCWithHyperBFS_vs_BFS() {
		HyperGraphInterface<Index, BasicVertexInfo> hgraph = network.getGraph();
		long t;
//		long c1,c2;
		
		Index[] sources = new Index[10];
		Random r = new Random(System.currentTimeMillis());
		for(int i=0;i<10;i++) {
			sources[i] = Index.valueOf(r.nextInt(50)+1);
		}
		sources = null;
		HyperBrandesBC hbrandes = new HyperBrandesBC(hgraph, null, sources, ShortestPathAlg.HYPERBFS);
		t = System.currentTimeMillis();
		hbrandes.run();
		t = System.currentTimeMillis() - t;
		System.out.println("Time for hbrandes : "+t);

		
		HyperBrandesBC brandesonh = new HyperBrandesBC(hgraph, null,sources,ShortestPathAlg.BFS);
		t = System.currentTimeMillis();
		brandesonh.run();
		t = System.currentTimeMillis() - t;		
		System.out.println("Time for brandes on hgraph: "+t );
		
		HyperBrandesBCWithMerging mfhbrandes = new HyperBrandesBCWithMerging(hgraph, false, sources);
		mfhbrandes.merge();
		t = System.currentTimeMillis();
		mfhbrandes.run();
		t = System.currentTimeMillis() - t;
		System.out.println("Time for mfhbrandes: "+t );
		
		for(int i=0;i<hgraph.getNumberOfVertices();i++) {
			assertEquals(hbrandes.getCentrality(i), brandesonh.getCentrality(i),0.000001);
			assertEquals(hbrandes.getCentrality(i), mfhbrandes.getCentrality(i),0.000001);
			//assert hbrandes.getCentrality(i) >= mfhbrandes.getCentrality(i);
		}
	}
//	@Test
//	public void testOn() {
//		HyperGraphInterface<Index,BasicVertexInfo> hgraph = network.createGraph();
////		System.out.println(hgraph.getNumberOfVertices());
////		System.out.println(hgraph.getNumberOfEdges());
//		HyperBrandesBC hbrandes = new HyperBrandesBC(hgraph, null);
//		long t = System.currentTimeMillis();
//		hbrandes.run();
//		t = System.currentTimeMillis() - t;
//		System.out.println("Time for hbrandes : "+t);
////		for(double c :hbrandes.getCentralitites()) {
////			System.out.println(c);
////		}
//		
//		GraphInterface<Index,BasicVertexInfo> graph = (GraphAsHashMap<Index,BasicVertexInfo>)network.getGraphAs(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP);
////		System.out.println("Received simple graph: ");
////		System.out.println("Vertices: "+graph.getVertices());
////		System.out.println(graph.getEdgeWeight(Index.valueOf(1), Index.valueOf(2)).getMultiplicity());
////		System.out.println("Edges: "+graph.getEdges());
//		BrandesBC brandes = new TrafficMatrixBC(ShortestPathAlg.BFS, graph, new DummyProgress(), 0);
//		t = System.currentTimeMillis();
//		brandes.run();
//		t = System.currentTimeMillis() - t;
//		System.out.println("Time for brandes : "+t);
//		boolean correct = true;
//		for(int i=0;i<graph.getNumberOfVertices();i++) {
//			//System.out.println(hbrandes.getCentrality(i) + ", " + brandes.getCentrality(i));
////			if(hbrandes.getCentrality(i) != brandes.getCentrality(i)) {
////				System.out.println(hbrandes.getCentrality(i)+","+brandes.getCentrality(i));
////				correct = false;				
////			}
//			assertEquals(brandes.getCentrality(i), hbrandes.getCentrality(i));
//		}		
//	}
}

package tests.clustering;

import java.util.Set;

import javolution.util.Index;
import junit.framework.Assert;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.Network;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.sato.SatoGraphBuilder;
import algorithms.clustering.BudgetedGreedyClustering;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class BudgetedGreedyClusteringTest extends TestCase {
	BudgetedGreedyClustering<Index,BasicVertexInfo> m_clustering;
	
	public void setUp() throws Exception{
		Network network = new Network("optimizedGraphTest");
		network.importNetwork(new DummyProgress(), "greedyClustering.net", "", "net",GraphFactory.DEFAULT_GRAPH_TYPE );
		GraphInterface<Index,BasicVertexInfo> g = network.getGraphSimple();
		m_clustering = new BudgetedGreedyClustering<Index,BasicVertexInfo>(g, 0, 5 );
		m_clustering.generateClusters();		
	}
	
	public void testLargeGraph() throws Exception{
		Assert.assertTrue(false);
		//TODO: restore test
		/*
		Network network = new Network("BudgetedGreedyClusteringTest");
		network.importNetwork(new DummyProgress(), "snap-01-as20000102.txt.sel", "", "sel",GraphInterface.DEFAULT );
		GraphInterface<Index> g = network.getGraph();
		GraphInterface<Index> graph = new OptimizedGraphAsArray<Index>(g);
		m_clustering = new BudgetedGreedyClustering<Index>(graph, 0, 1000 );
		m_clustering.generateClusters();
		System.out.println("Clustering complete");
		System.out.println("No Of Borders : "+m_clustering.getBorderVertices().size());
		SatoGraphBuilder sgb = new SatoGraphBuilder(m_clustering, ShortestPathAlg.BFS);
		GraphInterface<Index> sato =  sgb.buildSATOGraph();
		System.out.println("Sato # Edges : "+sato.getNumberOfEdges());
		*/
	}
	public void testTest() throws Exception{
		SatoGraphBuilder sgb = new SatoGraphBuilder(m_clustering, ShortestPathAlg.BFS);
		GraphInterface<Index,BasicVertexInfo> sato =  sgb.buildSATOGraph();
		//System.out.println(sato.getNumberOfEdges());
	}
	
	public void testConstruction() {
		String s = m_clustering.getGraph().toString();
		String sExp = "0 1\n0 2\n0 3\n0 4\n1 0\n1 2\n1 3\n1 4\n1 8\n2 0\n2 1\n2 3\n2 4\n3 0\n3 1\n3 2\n3 4\n4 0\n4 1\n4 2\n4 3\n5 6\n5 7\n5 8\n6 5\n6 7\n6 8\n7 5\n7 6\n7 8\n8 1\n8 5\n8 6\n8 7\n"; 
		assertEquals(sExp,s);
	}
	
	public void testClustering() {
		m_clustering.generateClusters();
		String s = m_clustering.toString();
		String sExp = " {0 1 2 3 4 }  {5 6 7 8 } ";
		assertEquals(sExp, s);		
	}
	
	public void testBorders() {
		Set<Index> b = m_clustering.getBorderVertices();
		String sExp = "[1, 8]";
		assertEquals(sExp,b.toString());
		
		for(int c : m_clustering.getClusterIds()) {
			b = m_clustering.getBorderVertices(c);
			switch(c){
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				sExp = "[1]";
				break;
			case 5:
			case 6:
			case 7:
			case 8:
				sExp = "[8]";
				break;
			}
			assertEquals(sExp, b.toString());
		}
		
		assertEquals(true,m_clustering.isBorder(Index.valueOf(1)));
		assertEquals(true,m_clustering.isBorder(Index.valueOf(8)));
		assertEquals(false,m_clustering.isBorder(Index.valueOf(0)));
		assertEquals(false,m_clustering.isBorder(Index.valueOf(2)));
		assertEquals(false,m_clustering.isBorder(Index.valueOf(3)));
		assertEquals(false,m_clustering.isBorder(Index.valueOf(4)));
		assertEquals(false,m_clustering.isBorder(Index.valueOf(5)));
		assertEquals(false,m_clustering.isBorder(Index.valueOf(6)));
		assertEquals(false,m_clustering.isBorder(Index.valueOf(7)));
	}
	
	public void testNumberOfClusters() {
		assertEquals(2,m_clustering.getNoOfClusters());
	}
	
	public void testGetClusters() {
		int c0 = m_clustering.getClusters(Index.valueOf(0)).get(0);
		int c1 = m_clustering.getClusters(Index.valueOf(1)).get(0);
		int c2 = m_clustering.getClusters(Index.valueOf(2)).get(0);
		int c3 = m_clustering.getClusters(Index.valueOf(3)).get(0);
		int c4 = m_clustering.getClusters(Index.valueOf(4)).get(0);
		int c5 = m_clustering.getClusters(Index.valueOf(5)).get(0);
		int c6 = m_clustering.getClusters(Index.valueOf(6)).get(0);
		int c7 = m_clustering.getClusters(Index.valueOf(7)).get(0);
		int c8 = m_clustering.getClusters(Index.valueOf(8)).get(0);
		assert c0==c1 && c0==c2 &&c0==c3&&c0==c4;
		assert c5==c8&&c5==c7&&c5==c6;
		assert m_clustering.getClusters(Index.valueOf(0)).size() == 1;
		assert m_clustering.getClusters(Index.valueOf(1)).size() == 1;
		assert m_clustering.getClusters(Index.valueOf(2)).size() == 1;
		assert m_clustering.getClusters(Index.valueOf(3)).size() == 1;
		assert m_clustering.getClusters(Index.valueOf(4)).size() == 1;
		assert m_clustering.getClusters(Index.valueOf(5)).size() == 1;
		assert m_clustering.getClusters(Index.valueOf(6)).size() == 1;
		assert m_clustering.getClusters(Index.valueOf(7)).size() == 1;
		assert m_clustering.getClusters(Index.valueOf(8)).size() == 1;
	}
	
	public void testGetVertices() {
		Set<Index> b;
		String sExp="";
		for(int c : m_clustering.getClusterIds()) {
			b = m_clustering.getVertices(c);
			switch(c){
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				sExp = "[0, 1, 2, 3, 4]";
				break;
			case 5:
			case 6:
			case 7:
			case 8:
				sExp = "[5, 6, 7, 8]";
				break;
			}
			assertEquals(sExp, b.toString());
		}
	}
	
	/**
	 * This is to test if the VertexInfo of the graph vertices have been set properly
	 */
	public void testVertexInfo() {
		GraphInterface<Index,BasicVertexInfo> g = m_clustering.getGraph();
		BasicVertexInfo vi0 = g.getVertex(Index.valueOf(0));
		BasicVertexInfo vi1 = g.getVertex(Index.valueOf(1));
		BasicVertexInfo vi2 = g.getVertex(Index.valueOf(2));
		BasicVertexInfo vi3 = g.getVertex(Index.valueOf(3));
		BasicVertexInfo vi4 = g.getVertex(Index.valueOf(4));
		BasicVertexInfo vi5 = g.getVertex(Index.valueOf(5));
		BasicVertexInfo vi6 = g.getVertex(Index.valueOf(6));
		BasicVertexInfo vi7 = g.getVertex(Index.valueOf(7));
		BasicVertexInfo vi8 = g.getVertex(Index.valueOf(8));
		
		if (VertexFactory.isVertexInfo(vi0)&&
			VertexFactory.isVertexInfo(vi1)&&	
			VertexFactory.isVertexInfo(vi2)&&
			VertexFactory.isVertexInfo(vi3)&&
			VertexFactory.isVertexInfo(vi4)&&
			VertexFactory.isVertexInfo(vi5)&&
			VertexFactory.isVertexInfo(vi6)&&
			VertexFactory.isVertexInfo(vi7)
			){
				assert ((VertexInfo)vi8).isBorder()==true;
				assert ((VertexInfo)vi1).isBorder()==true;
				assert ((VertexInfo)vi0).isBorder()==false;
				assert ((VertexInfo)vi2).isBorder()==false;
				assert ((VertexInfo)vi3).isBorder()==false;
				assert ((VertexInfo)vi4).isBorder()==false;
				assert ((VertexInfo)vi5).isBorder()==false;
				assert ((VertexInfo)vi6).isBorder()==false;
				assert ((VertexInfo)vi7).isBorder()==false;
				
				int c0 = ((VertexInfo)vi0).getClusters().get(0);
				int c1 = ((VertexInfo)vi1).getClusters().get(0);
				int c2 = ((VertexInfo)vi2).getClusters().get(0);
				int c3 = ((VertexInfo)vi3).getClusters().get(0);
				int c4 = ((VertexInfo)vi4).getClusters().get(0);
				int c5 = ((VertexInfo)vi5).getClusters().get(0);
				int c6 = ((VertexInfo)vi6).getClusters().get(0);
				int c7 = ((VertexInfo)vi7).getClusters().get(0);
				int c8 = ((VertexInfo)vi8).getClusters().get(0);
				
				assert c0==c1 && c0==c2 &&c0==c3&&c0==c4;
				assert c5==c8&&c5==c7&&c5==c6;
			}
		else 
			assertEquals("VertexInfo", "Vertx" ,"the Vertex type must be VertexInfo"); 
		
		
	}
}

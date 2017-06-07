package tests.structuralEquivalence;

import javolution.util.Index;
import junit.framework.TestCase;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;
import algorithms.structuralEquivalence.StructuralEquivalenceExtractor;

import common.FastListNG;

public class StructuralEquivalenceExtractorTest extends TestCase {
	protected GraphInterface<Index,BasicVertexInfo> graph_1, graph_2, graph_3, graph_4, graph_5, g_consistency_check; /*Dana: added*/
	private int[][] expectedEquivalenceGroups1 = {{2, 3, 4}, {5, 6, 7}, {0}, {1}};
	private int[][] expectedEquivalenceGroups2 = {{2, 3, 4}, {1, 0}};
	private int[][] expectedEquivalenceGroups3 = {{4, 5, 6, 7}, {0}, {1}, {2}, {3}, {10}, {11}, {8}, {9}};
	private int[][] expectedEquivalenceGroups4 = {{2, 3}, {4}, {5}, {0, 1}};
	private int[][] expectedEquivalenceGroups5 = {{1}, {0}, {4, 3}, {6}, {2}, {5}};
	
	public void setUp(){
		graph_1 = new GraphAsHashMap<Index,BasicVertexInfo>();
		
		for (int i=0; i<8; i++)
			graph_1.addVertex(Index.valueOf(i));
		graph_1.addEdge(Index.valueOf(0), Index.valueOf(1));
		graph_1.addEdge(Index.valueOf(0), Index.valueOf(2));
		graph_1.addEdge(Index.valueOf(0), Index.valueOf(3));
		graph_1.addEdge(Index.valueOf(0), Index.valueOf(4));
		graph_1.addEdge(Index.valueOf(1), Index.valueOf(5));
		graph_1.addEdge(Index.valueOf(1), Index.valueOf(6));
		graph_1.addEdge(Index.valueOf(1), Index.valueOf(7));
		
		graph_2 = new GraphAsHashMap<Index,BasicVertexInfo>();
		
		for (int i=0; i<5; i++)
			graph_2.addVertex(Index.valueOf(i));
		graph_2.addEdge(Index.valueOf(0), Index.valueOf(2));
		graph_2.addEdge(Index.valueOf(0), Index.valueOf(3));
		graph_2.addEdge(Index.valueOf(0), Index.valueOf(4));
		graph_2.addEdge(Index.valueOf(1), Index.valueOf(2));
		graph_2.addEdge(Index.valueOf(1), Index.valueOf(3));
		graph_2.addEdge(Index.valueOf(1), Index.valueOf(4));
		
		graph_3 = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int i=0; i<12; i++)
			graph_3.addVertex(Index.valueOf(i));
		graph_3.addEdge(Index.valueOf(0), Index.valueOf(2));
		graph_3.addEdge(Index.valueOf(1), Index.valueOf(3));
		
		graph_3.addEdge(Index.valueOf(2), Index.valueOf(4));
		graph_3.addEdge(Index.valueOf(2), Index.valueOf(5));
		graph_3.addEdge(Index.valueOf(2), Index.valueOf(6));
		graph_3.addEdge(Index.valueOf(2), Index.valueOf(7));
		
		graph_3.addEdge(Index.valueOf(3), Index.valueOf(4));
		graph_3.addEdge(Index.valueOf(3), Index.valueOf(5));
		graph_3.addEdge(Index.valueOf(3), Index.valueOf(6));
		graph_3.addEdge(Index.valueOf(3), Index.valueOf(7));
		
		graph_3.addEdge(Index.valueOf(8), Index.valueOf(4));
		graph_3.addEdge(Index.valueOf(8), Index.valueOf(5));
		graph_3.addEdge(Index.valueOf(8), Index.valueOf(6));
		graph_3.addEdge(Index.valueOf(8), Index.valueOf(7));
		
		graph_3.addEdge(Index.valueOf(9), Index.valueOf(4));
		graph_3.addEdge(Index.valueOf(9), Index.valueOf(5));
		graph_3.addEdge(Index.valueOf(9), Index.valueOf(6));
		graph_3.addEdge(Index.valueOf(9), Index.valueOf(7));
		
		graph_3.addEdge(Index.valueOf(10), Index.valueOf(8));
		graph_3.addEdge(Index.valueOf(11), Index.valueOf(9));
		
		graph_4 = new GraphAsHashMap<Index,BasicVertexInfo>();
		
		for (int i=0; i<6; i++)
			graph_4.addVertex(Index.valueOf(i));
		graph_4.addEdge(Index.valueOf(0), Index.valueOf(2));
		graph_4.addEdge(Index.valueOf(0), Index.valueOf(3));
		graph_4.addEdge(Index.valueOf(0), Index.valueOf(4));
		graph_4.addEdge(Index.valueOf(1), Index.valueOf(2));
		graph_4.addEdge(Index.valueOf(1), Index.valueOf(3));
		graph_4.addEdge(Index.valueOf(1), Index.valueOf(4));
		graph_4.addEdge(Index.valueOf(4), Index.valueOf(5));
		
		graph_5 = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int i=0; i<7; i++)
			graph_5.addVertex(Index.valueOf(i));
		graph_5.addEdge(Index.valueOf(0), Index.valueOf(1));
		graph_5.addEdge(Index.valueOf(0), Index.valueOf(2));
		graph_5.addEdge(Index.valueOf(0), Index.valueOf(3));
		graph_5.addEdge(Index.valueOf(0), Index.valueOf(4));
		graph_5.addEdge(Index.valueOf(1), Index.valueOf(2));
		graph_5.addEdge(Index.valueOf(2), Index.valueOf(3));
		graph_5.addEdge(Index.valueOf(2), Index.valueOf(4));
		graph_5.addEdge(Index.valueOf(2), Index.valueOf(6));
		graph_5.addEdge(Index.valueOf(3), Index.valueOf(5));
		graph_5.addEdge(Index.valueOf(4), Index.valueOf(5));
		graph_5.addEdge(Index.valueOf(5), Index.valueOf(6));
		
		g_consistency_check = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int i=0; i<10; i++)
			g_consistency_check.addVertex(Index.valueOf(i), new VertexInfo());
		
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(8), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(1), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(1), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(1), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(2), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(2), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(3), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(3), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(3), Index.valueOf(8), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(4), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(5), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(5), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(5), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(6), Index.valueOf(8), new EdgeInfo<Index,BasicVertexInfo>());
		
		//Dana: i added graph g_test_p
//		g_test_p = new GraphAsHashMap<Index>();
//		for (int i=0; i<6; i++)
//			g_test_p.addVertex(Index.valueOf(i), new VertexInfo());
//		g_test_p.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index>());
//		g_test_p.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index>());
//		g_test_p.addEdge(Index.valueOf(0), Index.valueOf(4), new EdgeInfo<Index>());
//		g_test_p.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index>());
//		g_test_p.addEdge(Index.valueOf(1), Index.valueOf(3), new EdgeInfo<Index>());
//		g_test_p.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index>());
//		g_test_p.addEdge(Index.valueOf(2), Index.valueOf(5), new EdgeInfo<Index>());
//		g_test_p.addEdge(Index.valueOf(3), Index.valueOf(4), new EdgeInfo<Index>());
//		g_test_p.addEdge(Index.valueOf(4), Index.valueOf(5), new EdgeInfo<Index>());
	}
	
	public void testStructuralEquivalence(){
		testPartitions(graph_1, expectedEquivalenceGroups1);
		testPartitions(graph_2, expectedEquivalenceGroups2);
		testPartitions(graph_3, expectedEquivalenceGroups3);
		testPartitions(graph_4, expectedEquivalenceGroups4);
		testPartitions(graph_5, expectedEquivalenceGroups5);
	}

	private void testPartitions(GraphInterface<Index,BasicVertexInfo> graph, int[][] expectedEquivalenceGroups) {
		StructuralEquivalenceExtractor<Index,BasicVertexInfo> se = new StructuralEquivalenceExtractor<Index,BasicVertexInfo>(graph);
		FastListNG<FastListNG<Index>> partitions = se.getPartitions();
		
		for (int i=0; i<expectedEquivalenceGroups.length; i++){
			int[] eGroup = expectedEquivalenceGroups[i];
			FastListNG<Index> eGroupList = new FastListNG<Index>();
			for (int j=0; j<eGroup.length; j++){
				eGroupList.add(Index.valueOf(eGroup[j]));
			}
			assertTrue(partitions.contains(eGroupList));
		}
		System.out.println(partitions);
	}
}
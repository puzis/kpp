package tests.clustering;


import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.Index;
import junit.framework.TestCase;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import algorithms.clustering.LabelPropagation;

public class LabelPropagationTest extends TestCase {

	private static FastMap<Integer,FastSet<Index>> runAlg(GraphInterface<Index,BasicVertexInfo> g){
		LabelPropagation alg = new LabelPropagation(g);
		alg.run();
		FastMap<Integer,FastSet<Index>> actualResult = alg.getClusters();
		return actualResult;
	}
	
	/**
	 * each array in the expectedResult param contains the label as the first velua and 
	 * avertices as next values.
	 * @param expectedResult
	 * @return
	 */
	private static FastMap<Integer,FastSet<Index>> packResult(int[][] expectedResult){
		FastMap<Integer,FastSet<Index>> packedResult;
		packedResult=new FastMap<Integer,FastSet<Index>>();
		for (int[] cluster: expectedResult){
			assert(cluster.length>=2);
			Integer label = cluster[0];
			packedResult.put(label, new FastSet<Index>());
			for (int i=1;i<cluster.length;i++){
				packedResult.get(label).add(Index.valueOf(cluster[i]));
			}
		}
		return packedResult;
	}
	
	public static void testSingleVertex(){
		GraphInterface<Index,BasicVertexInfo> g =  new GraphAsHashMap<Index,BasicVertexInfo>();
		g.addVertex(Index.valueOf(0));
		
		FastMap<Integer,FastSet<Index>> actualResult, expectedResult;
		actualResult = runAlg(g);		
		expectedResult = packResult(new int[][]{{0,0}});
		
		assertEquals(expectedResult, actualResult);
	}

	public static void testMultipleDisconnectedVertexes(){
		GraphInterface<Index,BasicVertexInfo> g =  new GraphAsHashMap<Index,BasicVertexInfo>();
		g.addVertex(Index.valueOf(0));
		g.addVertex(Index.valueOf(1));
		g.addVertex(Index.valueOf(2));
		
		FastMap<Integer,FastSet<Index>> actualResult, expectedResult;
		actualResult = runAlg(g);		
		expectedResult = packResult(new int[][]{{0,0},{1,1},{2,2}});
		
		assertEquals(expectedResult, actualResult);
	}
	
	public static void testStar(){
		GraphInterface<Index,BasicVertexInfo> g =  new GraphAsHashMap<Index,BasicVertexInfo>();
		g.addVertex(Index.valueOf(0));
		g.addVertex(Index.valueOf(1));
		g.addVertex(Index.valueOf(2));
		g.addVertex(Index.valueOf(3));
		g.addEdge(Index.valueOf(0), Index.valueOf(1));
		g.addEdge(Index.valueOf(0), Index.valueOf(2));
		g.addEdge(Index.valueOf(0), Index.valueOf(3));
		
		for (int i=0;i<10;i++){
			FastMap<Integer,FastSet<Index>> actualResult;
			actualResult = runAlg(g);		
			
			assertEquals(1, actualResult.size());
		}
	}

	
	public static void testQlique(){
		GraphInterface<Index,BasicVertexInfo> g =  new GraphAsHashMap<Index,BasicVertexInfo>();
		g.addVertex(Index.valueOf(0));
		g.addVertex(Index.valueOf(1));
		g.addVertex(Index.valueOf(2));
		g.addVertex(Index.valueOf(3));
		g.addEdge(Index.valueOf(0), Index.valueOf(1));
		g.addEdge(Index.valueOf(1), Index.valueOf(2));
		g.addEdge(Index.valueOf(2), Index.valueOf(3));
		g.addEdge(Index.valueOf(3), Index.valueOf(0));
		g.addEdge(Index.valueOf(0), Index.valueOf(2));
		g.addEdge(Index.valueOf(1), Index.valueOf(3));
		
		for (int i=0;i<10;i++){
			FastMap<Integer,FastSet<Index>> actualResult;
			actualResult = runAlg(g);		
			
			assertEquals(1, actualResult.size());
		}
	}
	
	public static void testTwoQliques(){
		GraphInterface<Index,BasicVertexInfo> g =  new GraphAsHashMap<Index,BasicVertexInfo>();
		g.addVertex(Index.valueOf(0));
		g.addVertex(Index.valueOf(1));
		g.addVertex(Index.valueOf(2));
		g.addVertex(Index.valueOf(3));
		g.addEdge(Index.valueOf(0), Index.valueOf(1));
		g.addEdge(Index.valueOf(0), Index.valueOf(2));
		g.addEdge(Index.valueOf(0), Index.valueOf(3));
		g.addEdge(Index.valueOf(1), Index.valueOf(2));
		g.addEdge(Index.valueOf(1), Index.valueOf(3));
		g.addEdge(Index.valueOf(2), Index.valueOf(3));
		
		g.addVertex(Index.valueOf(4));
		g.addVertex(Index.valueOf(5));
		g.addVertex(Index.valueOf(6));
		g.addVertex(Index.valueOf(7));
		g.addEdge(Index.valueOf(4), Index.valueOf(5));
		g.addEdge(Index.valueOf(4), Index.valueOf(6));
		g.addEdge(Index.valueOf(4), Index.valueOf(7));
		g.addEdge(Index.valueOf(5), Index.valueOf(6));
		g.addEdge(Index.valueOf(5), Index.valueOf(7));
		g.addEdge(Index.valueOf(6), Index.valueOf(7));
		
		g.addEdge(Index.valueOf(0), Index.valueOf(4));
		
		
		int clusters=0;
		for (int i=0;i<1000;i++){
			FastMap<Integer,FastSet<Index>> actualResult;
			actualResult = runAlg(g);
			clusters+=actualResult.size()-1;
		}
		System.out.println(clusters);
		assertTrue(950<clusters);

	}
}

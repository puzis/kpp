package tests.topology;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.Network;
import server.common.ServerConstants;
import topology.GraphFactory;
import topology.GraphFactory.GraphDataStructure;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.HyperGraphInterface;
import topology.SerializableGraphRepresentation;
import topology.graphParsers.GraphParserFactory;

/***
 * 
 * @author dana Gradstein 
 * this test goal is to check the GraphDataStructure, 
 * the SWITCH statements at GraphParsingContainer at 
 * "executeContent" method to getGraph that include the file content
 * and "execute" method to getGraph who does't include the file content
 * testDirection will check graph directions 
 */
public class GraphDataStructure1Test extends TestCase {
		
	
	Network greedyNet = new Network("greedy"); 
	
//	public void testGraphOptimized_whithoutContent(){
//		//testing importNetwork with optimizedGraphAsArray, without file context 
//		GraphInterface<Index>  g = GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "greedyClustering.net", "", new DummyProgress(), 1, GraphDataStructure.OPTIMIZED_GRAPH_AS_ARRAY);
//		OptimizedGraphAsArray<Index> optimizedGraphAsArray = new OptimizedGraphAsArray<Index>(g);
//				
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(0), Index.valueOf(1)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(0), Index.valueOf(2)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(0), Index.valueOf(3)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(0), Index.valueOf(4)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(0)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(2)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(3)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(4)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(8)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(2), Index.valueOf(0)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(2), Index.valueOf(1)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(2), Index.valueOf(3)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(2), Index.valueOf(4)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(3), Index.valueOf(0)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(3), Index.valueOf(1)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(3), Index.valueOf(2)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(3), Index.valueOf(4)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(4), Index.valueOf(0)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(4), Index.valueOf(1)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(4), Index.valueOf(2)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(4), Index.valueOf(3)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(5), Index.valueOf(6)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(5), Index.valueOf(7)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(5), Index.valueOf(8)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(6), Index.valueOf(5)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(6), Index.valueOf(7)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(6), Index.valueOf(8)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(7), Index.valueOf(5)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(7), Index.valueOf(6)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(7), Index.valueOf(8)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(8), Index.valueOf(5)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(8), Index.valueOf(6)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(8), Index.valueOf(7)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(8), Index.valueOf(1)));
//		
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(0))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(1))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(2))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(3))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(4))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(5))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(6))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(7))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(8))) ;
//		
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(0)));
//		assertEquals(5, optimizedGraphAsArray.getDegree(Index.valueOf(1)));
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(2)));
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(3)));
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(4)));
//		assertEquals(3, optimizedGraphAsArray.getDegree(Index.valueOf(5)));
//		assertEquals(3, optimizedGraphAsArray.getDegree(Index.valueOf(6)));
//		assertEquals(3, optimizedGraphAsArray.getDegree(Index.valueOf(7)));
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(8)));
//	}

//	public void testGraphOptimized_withContent(){
//		//test OptimizedGraphAsArray with file content (checking the executeContent method from GraphParsingContainer)
//		String networkFilename = "data/greedyClustering.net";
//		BufferedInputStream bis = null;
//		FileInputStream fis = null;
//
//		try{
//		File file = new File(networkFilename);
//		byte[] buffer = new byte[(int) file.length()];
//		fis = new FileInputStream(file);
//	    bis = new BufferedInputStream(fis);
//	    bis.read(buffer);
//
//		GraphInterface<Index> g = GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "greedyClustering.net" , new String(buffer), new DummyProgress(), 1, GraphDataStructure.OPTIMIZED_GRAPH_AS_ARRAY);
//		OptimizedGraphAsArray<Index> optimizedGraphAsArray = new OptimizedGraphAsArray<Index>(g);
//			
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(0), Index.valueOf(1)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(0), Index.valueOf(2)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(0), Index.valueOf(3)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(0), Index.valueOf(4)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(0)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(2)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(3)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(4)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(1), Index.valueOf(8)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(2), Index.valueOf(0)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(2), Index.valueOf(1)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(2), Index.valueOf(3)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(2), Index.valueOf(4)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(3), Index.valueOf(0)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(3), Index.valueOf(1)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(3), Index.valueOf(2)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(3), Index.valueOf(4)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(4), Index.valueOf(0)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(4), Index.valueOf(1)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(4), Index.valueOf(2)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(4), Index.valueOf(3)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(5), Index.valueOf(6)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(5), Index.valueOf(7)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(5), Index.valueOf(8)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(6), Index.valueOf(5)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(6), Index.valueOf(7)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(6), Index.valueOf(8)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(7), Index.valueOf(5)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(7), Index.valueOf(6)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(7), Index.valueOf(8)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(8), Index.valueOf(5)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(8), Index.valueOf(6)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(8), Index.valueOf(7)));
//		assertTrue(optimizedGraphAsArray.isEdge(Index.valueOf(8), Index.valueOf(1)));
//		
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(0))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(1))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(2))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(3))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(4))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(5))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(6))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(7))) ;
//		assertTrue(optimizedGraphAsArray.isVertex(Index.valueOf(8))) ;
//		
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(0)));
//		assertEquals(5, optimizedGraphAsArray.getDegree(Index.valueOf(1)));
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(2)));
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(3)));
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(4)));
//		assertEquals(3, optimizedGraphAsArray.getDegree(Index.valueOf(5)));
//		assertEquals(3, optimizedGraphAsArray.getDegree(Index.valueOf(6)));
//		assertEquals(3, optimizedGraphAsArray.getDegree(Index.valueOf(7)));
//		assertEquals(4, optimizedGraphAsArray.getDegree(Index.valueOf(8)));
//		if (bis != null)
//			bis.close();
//		if (fis != null)
//			fis.close();
//		}
//		catch(IOException ex)
//		{
//			ex.printStackTrace();
//		} 		
//	}
	
	
	public void testGraphAsHashMap_whithoutContent(){
		//test GraphAsHashMap without file content (checking the execute method from GraphParsingContainer)
		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "greedyClustering.net", "", new DummyProgress(), 1, graph); 
		GraphInterface<Index,BasicVertexInfo> graphAsHashMap = GraphFactory.copyAsSimple(graph);
		
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(1)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(2)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(3)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(4)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(0)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(2)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(3)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(4)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(8)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(0)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(1)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(3)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(4)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(0)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(1)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(2)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(4)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(0)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(1)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(2)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(3)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(6)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(8)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(5)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(7)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(8)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(5)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(6)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(8)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(5)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(6)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(7)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(1)));
		
		
		
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(0))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(1))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(2))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(3))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(4))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(5))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(6))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(7))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(8))) ;
		
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(0)));
		assertEquals(5, graphAsHashMap.getDegree(Index.valueOf(1)));
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(2)));
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(3)));
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(4)));
		assertEquals(3, graphAsHashMap.getDegree(Index.valueOf(5)));
		assertEquals(3, graphAsHashMap.getDegree(Index.valueOf(6)));
		assertEquals(3, graphAsHashMap.getDegree(Index.valueOf(7)));
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(8)));
	}
	
	public void testGraphAsHashMap_withContent(){
		//test GraphAsHashMap with file content (checking the executeContent method from GraphParsingContainer)
		String networkFilename = "data/greedyClustering.net";
		BufferedInputStream bis = null;
		FileInputStream fis = null;

		try{
		File file = new File(networkFilename);
		byte[] buffer = new byte[(int) file.length()];
		fis = new FileInputStream(file);
	    bis = new BufferedInputStream(fis);
	    bis.read(buffer);

		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "greedyClustering.net" , new String(buffer), new DummyProgress(), 1, graph);
		GraphInterface<Index,BasicVertexInfo> graphAsHashMap = GraphFactory.copyAsSimple(graph);
		
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(1)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(2)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(3)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(4)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(0)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(2)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(3)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(4)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(8)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(0)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(1)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(3)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(4)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(0)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(1)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(2)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(4)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(0)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(1)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(2)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(3)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(6)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(8)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(5)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(7)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(8)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(5)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(6)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(8)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(5)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(6)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(7)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(1)));
		
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(0))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(1))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(2))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(3))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(4))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(5))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(6))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(7))) ;
		assertTrue(graphAsHashMap.isVertex(Index.valueOf(8))) ;
		
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(0)));
		assertEquals(5, graphAsHashMap.getDegree(Index.valueOf(1)));
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(2)));
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(3)));
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(4)));
		assertEquals(3, graphAsHashMap.getDegree(Index.valueOf(5)));
		assertEquals(3, graphAsHashMap.getDegree(Index.valueOf(6)));
		assertEquals(3, graphAsHashMap.getDegree(Index.valueOf(7)));
		assertEquals(4, graphAsHashMap.getDegree(Index.valueOf(8)));
		if (bis != null)
			bis.close();
		if (fis != null)
			fis.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		} 
	}
		
		
		
	
	public void testDiGraphAsHashMap_withoutContent(){
		//test GraphAsHashMap without file content (checking the execute method from GraphParsingContainer)
		
		
		
		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "greedyClustering.net", "", new DummyProgress(), 1, graph); 
		GraphInterface<Index,BasicVertexInfo> diGraphAsHashMap = GraphFactory.copyAsSimple(graph);	
		
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(1)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(2)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(3)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(4)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(0)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(2)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(3)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(4)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(8)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(0)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(1)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(3)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(4)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(0)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(1)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(2)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(4)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(0)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(1)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(2)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(3)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(6)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(8)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(5)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(7)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(8)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(5)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(6)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(8)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(5)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(6)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(7)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(1)));
		
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(0))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(1))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(2))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(3))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(4))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(5))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(6))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(7))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(8))) ;
		
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(0)));
		assertEquals(5, diGraphAsHashMap.getDegree(Index.valueOf(1)));
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(2)));
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(3)));
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(4)));
		assertEquals(3, diGraphAsHashMap.getDegree(Index.valueOf(5)));
		assertEquals(3, diGraphAsHashMap.getDegree(Index.valueOf(6)));
		assertEquals(3, diGraphAsHashMap.getDegree(Index.valueOf(7)));
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(8)));
				
	}
	
	public void testDiGraphAsHashMap_withContent(){
		//test DiGraphAsHashMap with file content (checking the executeContent method from GraphParsingContainer)
		String networkFilename = "data/greedyClustering.net";
		BufferedInputStream bis = null;
		FileInputStream fis = null;

		try{
		File file = new File(networkFilename);
		byte[] buffer = new byte[(int) file.length()];
		fis = new FileInputStream(file);
	    bis = new BufferedInputStream(fis);
		bis.read(buffer);

		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "greedyClustering.net" , new String(buffer), new DummyProgress(), 1, graph); 					
		GraphInterface<Index,BasicVertexInfo> diGraphAsHashMap = GraphFactory.copyAsSimple(graph);
		
		
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(1)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(2)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(3)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(0), Index.valueOf(4)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(0)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(2)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(3)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(4)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(1), Index.valueOf(8)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(0)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(1)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(3)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(2), Index.valueOf(4)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(0)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(1)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(2)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(3), Index.valueOf(4)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(0)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(1)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(2)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(4), Index.valueOf(3)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(6)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(8)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(5)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(7)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(6), Index.valueOf(8)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(5)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(6)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(8)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(5)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(6)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(7)));
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(8), Index.valueOf(1)));
		
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(0))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(1))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(2))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(3))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(4))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(5))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(6))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(7))) ;
		assertTrue(diGraphAsHashMap.isVertex(Index.valueOf(8))) ;
		
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(0)));
		assertEquals(5, diGraphAsHashMap.getDegree(Index.valueOf(1)));
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(2)));
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(3)));
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(4)));
		assertEquals(3, diGraphAsHashMap.getDegree(Index.valueOf(5)));
		assertEquals(3, diGraphAsHashMap.getDegree(Index.valueOf(6)));
		assertEquals(3, diGraphAsHashMap.getDegree(Index.valueOf(7)));
		assertEquals(4, diGraphAsHashMap.getDegree(Index.valueOf(8)));
		if (bis != null)
			bis.close();
		if (fis != null)
			fis.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		} 
				
	}
	
	
/*
TODO: apply test for hyper graphs to support abitrary DEFAULT spec
	public void testDefaultl_withoutContent(){
		HyperGraphInterface<Index> defaultGraph = GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "greedyClustering.net", "", new DummyProgress(), 1, GraphInterface.DEFAULT);
		
		assertTrue(defaultGraph.isEdge(Index.valueOf(0), Index.valueOf(1)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(0), Index.valueOf(2)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(0), Index.valueOf(3)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(0), Index.valueOf(4)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(0)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(2)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(3)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(4)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(8)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(2), Index.valueOf(0)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(2), Index.valueOf(1)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(2), Index.valueOf(3)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(2), Index.valueOf(4)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(3), Index.valueOf(0)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(3), Index.valueOf(1)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(3), Index.valueOf(2)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(3), Index.valueOf(4)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(4), Index.valueOf(0)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(4), Index.valueOf(1)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(4), Index.valueOf(2)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(4), Index.valueOf(3)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(5), Index.valueOf(6)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(5), Index.valueOf(8)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(6), Index.valueOf(5)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(6), Index.valueOf(7)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(6), Index.valueOf(8)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(7), Index.valueOf(5)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(7), Index.valueOf(6)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(7), Index.valueOf(8)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(8), Index.valueOf(5)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(8), Index.valueOf(6)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(8), Index.valueOf(7)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(8), Index.valueOf(1)));
		
		assertTrue(defaultGraph.isVertex(Index.valueOf(0))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(1))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(2))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(3))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(4))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(5))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(6))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(7))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(8))) ;
		
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(0)));
		assertEquals(5, defaultGraph.getDegree(Index.valueOf(1)));
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(2)));
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(3)));
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(4)));
		assertEquals(3, defaultGraph.getDegree(Index.valueOf(5)));
		assertEquals(3, defaultGraph.getDegree(Index.valueOf(6)));
		assertEquals(3, defaultGraph.getDegree(Index.valueOf(7)));
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(8)));
	}
*/
 
/*
TODO: apply test for hyper graphs to support abitrary DEFAULT spec
	public void testDefaultl_withContent(){
		String networkFilename = "data/greedyClustering.net";
		BufferedInputStream bis = null;
		FileInputStream fis = null;

		try{
		File file = new File(networkFilename);
		byte[] buffer = new byte[(int) file.length()];
		fis = new FileInputStream(file);
	    bis = new BufferedInputStream(fis);
		bis.read(buffer);

		GraphInterface<Index> defaultGraph = GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "greedyClustering.net" , new String(buffer), new DummyProgress(), 1, GraphInterface.DEFAULT); 					
		
		assertTrue(defaultGraph.isEdge(Index.valueOf(0), Index.valueOf(1)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(0), Index.valueOf(2)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(0), Index.valueOf(3)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(0), Index.valueOf(4)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(0)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(2)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(3)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(4)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(1), Index.valueOf(8)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(2), Index.valueOf(0)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(2), Index.valueOf(1)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(2), Index.valueOf(3)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(2), Index.valueOf(4)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(3), Index.valueOf(0)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(3), Index.valueOf(1)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(3), Index.valueOf(2)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(3), Index.valueOf(4)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(4), Index.valueOf(0)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(4), Index.valueOf(1)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(4), Index.valueOf(2)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(4), Index.valueOf(3)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(5), Index.valueOf(6)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(5), Index.valueOf(8)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(6), Index.valueOf(5)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(6), Index.valueOf(7)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(6), Index.valueOf(8)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(7), Index.valueOf(5)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(7), Index.valueOf(6)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(7), Index.valueOf(8)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(8), Index.valueOf(5)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(8), Index.valueOf(6)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(8), Index.valueOf(7)));
		assertTrue(defaultGraph.isEdge(Index.valueOf(8), Index.valueOf(1)));
		
		assertTrue(defaultGraph.isVertex(Index.valueOf(0))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(1))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(2))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(3))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(4))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(5))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(6))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(7))) ;
		assertTrue(defaultGraph.isVertex(Index.valueOf(8))) ;
		
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(0)));
		assertEquals(5, defaultGraph.getDegree(Index.valueOf(1)));
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(2)));
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(3)));
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(4)));
		assertEquals(3, defaultGraph.getDegree(Index.valueOf(5)));
		assertEquals(3, defaultGraph.getDegree(Index.valueOf(6)));
		assertEquals(3, defaultGraph.getDegree(Index.valueOf(7)));
		assertEquals(4, defaultGraph.getDegree(Index.valueOf(8)));
		if (bis != null)
			bis.close();
		if (fis != null)
			fis.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
*/
	
	
	
	public void testDirections(){
		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "graphDataStructureTest.net", "", new DummyProgress(), 1, graph); 
		GraphInterface<Index,BasicVertexInfo> graphAsHashMap = GraphFactory.copyAsSimple(graph); 
				
		SerializableGraphRepresentation digraph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "graphDataStructureTest.net", "", new DummyProgress(), 1, digraph); 
		GraphInterface<Index,BasicVertexInfo> diGraphAsHashMap = GraphFactory.copyAsSimple(digraph);
		
		
		//if the graph is undirected, one edge that has been added should be to both directions
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(graphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(5)));
		
		//if the graph is directed, one edge that has been added shouldn't appeare at the oposite direction
		assertTrue(diGraphAsHashMap.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertFalse(diGraphAsHashMap.isEdge(Index.valueOf(7), Index.valueOf(5)));
	}
	
	
	
	

}

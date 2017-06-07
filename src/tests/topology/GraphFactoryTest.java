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
import topology.DiGraphAsHashMap;
import topology.GraphAsHashMap;
import topology.GraphFactory;
import topology.GraphFactory.GraphDataStructure;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.HyperGraphInterface;
import topology.SerializableGraphRepresentation;
import topology.UndirectedHyperGraphAsHashMap;
import topology.graphParsers.GraphParserFactory;

/***
 * 
 * @author danahend
 * this test will check the method at class "GraphFactory": 
 * createGraphDataStructure that return the graph's type and return the correct AbstractGraph instance
 * 
 * also, it will check every usage at GraphFactory: [class]_[method]
 * testGraphParserFactory_execute 
 * testGraphParserFactory_executeContent
 * testGraphParserFactory_getPajekGraph
 * testSerializableGraphRepresentation_getGraph
 * 
 */


public class GraphFactoryTest extends TestCase{
	
	public void testGraphFactoryTest_createGraphDataStructure() {
		HyperGraphInterface<Index,BasicVertexInfo> graphDiGraphAsHashMap = GraphFactory.createGraph(GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP);
		assertEquals("DI_GRAPH_AS_HASH_MAP", graphDiGraphAsHashMap.getType().toString());
		
		HyperGraphInterface<Index,BasicVertexInfo> graphgraphGraphAsHashMap = GraphFactory.createGraph(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP);
		assertEquals("GRAPH_AS_HASH_MAP", graphgraphGraphAsHashMap.getType().toString());
		
		
		HyperGraphInterface<Index,BasicVertexInfo> graphHyper = GraphFactory.createGraph(GraphFactory.GraphDataStructure.HYPER_GRAPH_AS_HASH_MAP);
		assertEquals("HYPER_GRAPH_AS_HASH_MAP", graphHyper.getType().toString());
		
		HyperGraphInterface<Index,BasicVertexInfo> graphDefault = GraphFactory.createGraph(GraphFactory.DEFAULT_GRAPH_TYPE);
		assertEquals("GRAPH_AS_HASH_MAP", graphDefault.getType().toString());
	}
	
//	public void testGraphFactoryTest_createGraphDataStructureByType() {
//		DiGraphAsHashMap<Index> graphDiGraphAsHashMap = GraphFactory.<DiGraphAsHashMap<Index>>createGraphDataStructure(DiGraphAsHashMap.class);
//		assertEquals("DI_GRAPH_AS_HASH_MAP", graphDiGraphAsHashMap.getType().toString());
//		
//		GraphAsHashMap<Index> graphgraphGraphAsHashMap = GraphFactory.<GraphAsHashMap<Index>>createGraphDataStructure(GraphAsHashMap.class);
//		assertEquals("GRAPH_AS_HASH_MAP", graphgraphGraphAsHashMap.getType().toString());
//		
//		OptimizedGraphAsArray<Index> graphOptimizedGraphAsArray = GraphFactory.<OptimizedGraphAsArray<Index>>createGraphDataStructure(OptimizedGraphAsArray.class);
//		assertEquals("OPTIMIZED_GRAPH_AS_ARRAY", graphOptimizedGraphAsArray.getType().toString());
//		
//		UndirectedHyperGraphAsHashMap<Index> graphHyper = GraphFactory.<UndirectedHyperGraphAsHashMap<Index>>createGraphDataStructure(UndirectedHyperGraphAsHashMap.class);
//		assertEquals("HYPER_GRAPH_AS_HASH_MAP", graphHyper.getType().toString());
//		
//	}
	
	public void testGraphParserFactory_execute (){
		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "graphDataStructureTest.net", "", new DummyProgress(), 1, graph);
		GraphInterface<Index,BasicVertexInfo> graphWithoutContent = GraphFactory.copyAsSimple(graph);
		
		
		assertTrue(graphWithoutContent.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(graphWithoutContent.isEdge(Index.valueOf(7), Index.valueOf(8)));
		assertTrue(graphWithoutContent.isEdge(Index.valueOf(8), Index.valueOf(7)));
		
		assertTrue(graphWithoutContent.isVertex(Index.valueOf(0))) ;
		assertTrue(graphWithoutContent.isVertex(Index.valueOf(1))) ;
		assertTrue(graphWithoutContent.isVertex(Index.valueOf(2))) ;
		assertTrue(graphWithoutContent.isVertex(Index.valueOf(3))) ;
		assertTrue(graphWithoutContent.isVertex(Index.valueOf(4))) ;
		assertTrue(graphWithoutContent.isVertex(Index.valueOf(5))) ;
		assertTrue(graphWithoutContent.isVertex(Index.valueOf(6))) ;
		assertTrue(graphWithoutContent.isVertex(Index.valueOf(7))) ;
		assertTrue(graphWithoutContent.isVertex(Index.valueOf(8))) ;
		
		assertEquals(0, graphWithoutContent.getDegree(Index.valueOf(0)));
		assertEquals(0, graphWithoutContent.getDegree(Index.valueOf(1)));
		assertEquals(0, graphWithoutContent.getDegree(Index.valueOf(2)));
		assertEquals(0, graphWithoutContent.getDegree(Index.valueOf(3)));
		assertEquals(0, graphWithoutContent.getDegree(Index.valueOf(4)));
		assertEquals(1, graphWithoutContent.getDegree(Index.valueOf(5)));
		assertEquals(0, graphWithoutContent.getDegree(Index.valueOf(6)));
		assertEquals(2, graphWithoutContent.getDegree(Index.valueOf(7)));
		assertEquals(1, graphWithoutContent.getDegree(Index.valueOf(8)));
	}
	
	public void testGraphParserFactory_executeContent (){
		String networkFilename = "data/graphDataStructureTest.net";
		BufferedInputStream bis = null;
		FileInputStream fis = null;

		try{
		File file = new File(networkFilename);
		byte[] buffer = new byte[(int) file.length()];
		fis = new FileInputStream(file);
	    bis = new BufferedInputStream(fis);
	    bis.read(buffer);

		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP); 
		GraphParserFactory.getGraph(ServerConstants.DATA_DIR, "graphDataStructureTest.net" , new String(buffer), new DummyProgress(), 1, graph); 
		GraphInterface<Index,BasicVertexInfo> graphWithContent = GraphFactory.copyAsSimple(graph);
		
		
		assertTrue(graphWithContent.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(graphWithContent.isEdge(Index.valueOf(7), Index.valueOf(8)));
		assertTrue(graphWithContent.isEdge(Index.valueOf(8), Index.valueOf(7)));
		
		assertTrue(graphWithContent.isVertex(Index.valueOf(0))) ;
		assertTrue(graphWithContent.isVertex(Index.valueOf(1))) ;
		assertTrue(graphWithContent.isVertex(Index.valueOf(2))) ;
		assertTrue(graphWithContent.isVertex(Index.valueOf(3))) ;
		assertTrue(graphWithContent.isVertex(Index.valueOf(4))) ;
		assertTrue(graphWithContent.isVertex(Index.valueOf(5))) ;
		assertTrue(graphWithContent.isVertex(Index.valueOf(6))) ;
		assertTrue(graphWithContent.isVertex(Index.valueOf(7))) ;
		assertTrue(graphWithContent.isVertex(Index.valueOf(8))) ;
		
		assertEquals(0, graphWithContent.getDegree(Index.valueOf(0)));
		assertEquals(0, graphWithContent.getDegree(Index.valueOf(1)));
		assertEquals(0, graphWithContent.getDegree(Index.valueOf(2)));
		assertEquals(0, graphWithContent.getDegree(Index.valueOf(3)));
		assertEquals(0, graphWithContent.getDegree(Index.valueOf(4)));
		assertEquals(1, graphWithContent.getDegree(Index.valueOf(5)));
		assertEquals(0, graphWithContent.getDegree(Index.valueOf(6)));
		assertEquals(2, graphWithContent.getDegree(Index.valueOf(7)));
		assertEquals(1, graphWithContent.getDegree(Index.valueOf(8)));
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
	
	
	public void testSerializableGraphRepresentation_getGraph(){
		Network net = new Network("graphDataStructureTest");
		net.importNetwork(new DummyProgress(), "graphDataStructureTest.net", "", "net", GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP);
		GraphInterface<Index,BasicVertexInfo> graph = net.getGraphSimple();
		
		assertTrue(graph.isEdge(Index.valueOf(5), Index.valueOf(7)));
		assertTrue(graph.isEdge(Index.valueOf(7), Index.valueOf(8)));
		assertTrue(graph.isEdge(Index.valueOf(8), Index.valueOf(7)));
		
		assertTrue(graph.isVertex(Index.valueOf(0))) ;
		assertTrue(graph.isVertex(Index.valueOf(1))) ;
		assertTrue(graph.isVertex(Index.valueOf(2))) ;
		assertTrue(graph.isVertex(Index.valueOf(3))) ;
		assertTrue(graph.isVertex(Index.valueOf(4))) ;
		assertTrue(graph.isVertex(Index.valueOf(5))) ;
		assertTrue(graph.isVertex(Index.valueOf(6))) ;
		assertTrue(graph.isVertex(Index.valueOf(7))) ;
		assertTrue(graph.isVertex(Index.valueOf(8))) ;
		
		assertEquals(0, graph.getDegree(Index.valueOf(0)));
		assertEquals(0, graph.getDegree(Index.valueOf(1)));
		assertEquals(0, graph.getDegree(Index.valueOf(2)));
		assertEquals(0, graph.getDegree(Index.valueOf(3)));
		assertEquals(0, graph.getDegree(Index.valueOf(4)));
		assertEquals(1, graph.getDegree(Index.valueOf(5)));
		assertEquals(0, graph.getDegree(Index.valueOf(6)));
		assertEquals(2, graph.getDegree(Index.valueOf(7)));
		assertEquals(1, graph.getDegree(Index.valueOf(8)));
	}
	
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

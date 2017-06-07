package tests.topology;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.ServerConstants;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphFactory;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.GraphPrinter;
import topology.SerializableGraphRepresentation;
import topology.WeightsLoader;
import topology.graphParsers.NetFileParser;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

public class GraphLoaderTest extends TestCase
{
	public GraphLoaderTest(String arg0)
	{
		super(arg0);
	}
	
	public void testVertices()
	{
		GraphInterface<Index,BasicVertexInfo> graph = new GraphAsHashMap<Index,BasicVertexInfo>();
		graph.addVertex(Index.valueOf(0));
		graph.addVertex(Index.valueOf(1));
		graph.addVertex(Index.valueOf(2));
		graph.addVertex(Index.valueOf(3));
		graph.addVertex(Index.valueOf(4));
		assertEquals(graph.getNumberOfVertices(), 5);
		graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		graph.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		graph.addEdge(Index.valueOf(3), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		graph.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		assertEquals(graph.getNumberOfEdges(), 4);
	}
	
	public void testWeights()
	{
		double [][] expected = {{1, 2, 3, 4, 5}, {2, 3.1, 4.2, 5.3, 6.4}, {5, 4, 3, 2, 1}, {6.4, 5.3, 4.2, 3.1, 2}, {2, 3, 4, 5, 6}};
		File outFile = new File(ServerConstants.DATA_DIR + "weights/test.wc");
		ObjectOutputStream out = null;
		try{
			out = new ObjectOutputStream(new FileOutputStream(outFile));

	        out.writeObject(expected);
		}
		catch(IOException ex)
		{
			System.err.println("An IOException has occured while trying to save the expected weights to file " 
					+ outFile.getName() + ":\n" + ex);
		}
		finally{
			try{
				out.flush();
				if (out != null) out.close();
			}
			catch(IOException ex)
			{
				System.err.println("An IOException has occured while trying to close the output stream after writting the file: " 
						+ outFile.getName() + ":\n" + ex);
			}
		}
		double [][] weights = WeightsLoader.loadWeightsFromFile(ServerConstants.DATA_DIR + "weights/test.wc", 5);
		/** 1 2 3 4 5
			2 3.1 4.2 5.3 6.4
			5 4 3 2 1
			6.4 5.3 4.2 3.1 2
			2 3 4 5 6
		**/
		
		for (int i = 0; i < weights.length; i++)
			for (int j = 0; j < weights.length; j++)
				assertEquals(expected[i][j], weights[i][j]);
	}
	
	public void testLoadingUndirected()
	{
		NetFileParser loader = new NetFileParser();
		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
		loader.analyzeFile("data/smallnet.net", new DummyProgress(), 1, graph);
		GraphInterface<Index,BasicVertexInfo> sgraph = GraphFactory.copyAsSimple(graph);
		GraphPrinter printer = new GraphPrinter(sgraph);
		try{
		System.out.println(printer.getAnalyzedFile(new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, sgraph, true, new DummyProgress(), 1)));
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		assertTrue(sgraph.isEdge(Index.valueOf(1), Index.valueOf(2)));
		assertTrue(sgraph.isEdge(Index.valueOf(2), Index.valueOf(1)));
	}
	public void testLoadingDirected()
	{
		NetFileParser loader = new NetFileParser();
		SerializableGraphRepresentation graph = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.DI_GRAPH_AS_HASH_MAP); 
		loader.analyzeFile("data/smallnet.net", new DummyProgress(), 1, graph);
		
		GraphInterface<Index,BasicVertexInfo> sgraph = GraphFactory.copyAsSimple(graph);
		GraphPrinter printer = new GraphPrinter(sgraph);
		try{
		System.out.println(printer.getAnalyzedFile(new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, sgraph, true, new DummyProgress(), 1)));
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		assertTrue(sgraph.isEdge(Index.valueOf(1), Index.valueOf(2)));
		assertFalse(sgraph.isEdge(Index.valueOf(2), Index.valueOf(1)));
	}
}

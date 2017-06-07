package topology;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Random;

import javolution.util.Index;
import server.common.LoggingManager;
import server.common.ServerConstants;

public class RandomNetworkGenerator 
{
	private int m_numberOfVertices = 0;
	private double m_probability = 0;
	private AbstractUndirectedGraph<Index,BasicVertexInfo> m_graph = null;
	private double[][] m_edgesExistance = new double[1][1];
	
	public RandomNetworkGenerator(double p)
	{
		m_probability = p;
	}
	
	public AbstractUndirectedGraph<Index,BasicVertexInfo> generate(int numOfVertices)
	{
		m_numberOfVertices = numOfVertices;
			
		m_graph = new GraphAsHashMap<Index,BasicVertexInfo>();
		
		double [][] edgesExistance = new double[numOfVertices][numOfVertices];
		for (int i = 0; i < numOfVertices; i++)
			Arrays.fill(edgesExistance[i], 0, numOfVertices, -1);
		
		for (int i = 0; i < m_edgesExistance[0].length; i++)
			System.arraycopy(m_edgesExistance[i], 0, edgesExistance[i], 0, m_edgesExistance[i].length);
		
		m_edgesExistance = new double[numOfVertices][numOfVertices];
		for (int i = 0; i < numOfVertices; i++)
			System.arraycopy(edgesExistance[i], 0, m_edgesExistance[i], 0, edgesExistance[i].length);
		
		for (int i = 0; i < numOfVertices; i++)
			m_graph.addVertex(Index.valueOf(i), new VertexInfo());
		
		fillEdgesExsistance();
		
		return m_graph;
	}
	
	private void fillEdgesExsistance()
	{
		double p = 1 / m_probability;
		Random r = new Random();
		
		for (int i = 0; i < m_numberOfVertices; i++)
			for (int j = 0; j < m_numberOfVertices; j++)
			{
				if (i != j && m_edgesExistance[i][j] == -1)
				{
					if (r.nextInt((int)p) == 1)
					{
						m_edgesExistance[i][j] = 1;
						if (!m_graph.isEdge(Index.valueOf(j), Index.valueOf(i)))
							m_graph.addEdge(Index.valueOf(i), Index.valueOf(j));
					}
					else 
						m_edgesExistance[i][j] = 0;
				}
				else if (m_edgesExistance[i][j] == 1)
				{
					if (!m_graph.isEdge(Index.valueOf(j), Index.valueOf(i)))
						m_graph.addEdge(Index.valueOf(i), Index.valueOf(j));
				}
			}
	}
	
	private void save(String directory, int vertexNum, GraphInterface<Index,BasicVertexInfo> graph)
	{
		File dir = new File(directory);
		if (!dir.exists())
			dir.mkdirs();
		
		File outFile = new File(directory + "/random" + vertexNum + ".grf");
		ObjectOutputStream out = null;
		try{
			out = new ObjectOutputStream(new FileOutputStream(outFile));
	        out.writeObject(graph);
		}
		catch(Exception ex)
		{
			LoggingManager.getInstance().writeSystem("An IOException has occured while trying to save to file " + outFile.getName(), "RandomNetworkGenerator", "save", ex);
		}
		finally{
			try{
				if (out != null)
				{
					out.flush();
					out.close();
				}
			}
			catch(IOException ex)
			{
				LoggingManager.getInstance().writeSystem("An IOException has occured while trying to close the output stream after writting the file: " + outFile.getName(), "RandomNetworkGenerator", "save", ex);
			}
		}
	}
	
	/**
	 * args[0] - The probability for an edge to exist.
	 * args[1] - The directory name for outputting the graphs.
	 * args[2] - Number of vertices in the initial graph.
	 * args[3] - Interval between the number of vertices.
	 * args[4] - Number of graphs to create.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		RandomNetworkGenerator randomGenerator = new RandomNetworkGenerator(Double.parseDouble(args[0]));
		int numOfVertices = Integer.parseInt(args[2]);
		int intervalSize = Integer.parseInt(args[3]);
		int graphsCounter = Integer.parseInt(args[4]);
		GraphInterface<Index,BasicVertexInfo> graph = null;
		
		for (int i = 0; i < graphsCounter; i++)
		{
			graph = randomGenerator.generate(numOfVertices);
			randomGenerator.save(ServerConstants.DATA_DIR + args[1], numOfVertices, graph);
			numOfVertices += intervalSize;
		}
	}
	
//	public static void main(String[] args) throws Exception
//	{
//		RandomNetworkGenerator randomGenerator = new RandomNetworkGenerator(0.25, ServerConstants.DATA_DIR + "randomNetworks");
//		Graph graph = randomGenerator.generate(100);
//		DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.BFS_ALGORITHM, graph, new PhaseProgress(), 1);
//		dw.saveToDisk(ServerConstants.DATA_DIR + "r1.dw", new PhaseProgress(), 1);
//		
//		Graph graph2 = randomGenerator.generate(200);
//		DataWorkshop dw2 = new DataWorkshop(ShortestPathAlgorithmInterface.BFS_ALGORITHM, graph, new PhaseProgress(), 1);
//		dw.saveToDisk(ServerConstants.DATA_DIR + "r2.dw", new PhaseProgress(), 1);
//	}
}

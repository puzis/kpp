package algorithms.shortestPath;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphFactory.GraphDataStructure;
import topology.GraphInterface;
import topology.HyperGraphInterface;
import topology.MultiWeightedHyperGraph;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class ShortestPathFactory
{
	/** The default type for shortest path algorithm is BFS. */
	private static ShortestPathAlg m_defaultAlgType = ShortestPathAlg.BFS;
	
	public static boolean isCompatible(ShortestPathAlg spAlg, GraphFactory.GraphDataStructure gds){
		switch(gds){
		case DI_GRAPH_AS_HASH_MAP:
			switch(spAlg){
			case BFS:return true;
			case DIJKSTRA:return true;
			case HYPERBFS:return true;
			case HYPERDIJKSTRA:return true;
			default:return false;
			}
		case GRAPH_AS_HASH_MAP:
			switch(spAlg){
			case BFS:return true;
			case DIJKSTRA:return true;
			case HYPERBFS:return true;
			case HYPERDIJKSTRA:return true;
			default:return false;
			}
		case HYPER_GRAPH_AS_HASH_MAP:
			switch(spAlg){
			case BFS:return false;
			case DIJKSTRA:return false;
			case HYPERBFS:return true;
			case HYPERDIJKSTRA:return true;
			default:return false;
			}
		case WEIGHTED_HYPER_GRAPH:
			switch(spAlg){
			case BFS:return false;
			case DIJKSTRA:return false;
			case HYPERBFS:return false;
			case HYPERDIJKSTRA:return true;
			default:return false;
			}
		default:
			return false;
		}
	}
	
	
	public static ShortestPathAlgorithmInterface getShortestPathAlgorithm(ShortestPathAlg algType, MultiWeightedHyperGraph<Index,BasicVertexInfo> graph)
	{
		switch(algType){
		case HYPERBFS:
			return new HyperBFSAlgorithm(graph, true);
		case HYPERDIJKSTRA:
			return new HyperDijkstraAlgorithm(graph);
		default:
			throw new IllegalArgumentException("Algorithm type not supported for MultiWeightedHyperGraph<Index>.");
		}
	}

	public static ShortestPathAlgorithmInterface getShortestPathAlgorithm(ShortestPathAlg algType, HyperGraphInterface<Index,BasicVertexInfo> graph)
	{
		if (GraphFactory.isHyper(graph.getType())){
			switch(algType){
			case HYPERBFS:
				return new HyperBFSAlgorithm(graph, true);
			case BFS:
				return new HyperBFSAlgorithm(graph, false);
			default:
				throw new IllegalArgumentException("Algorithm type not supported for HyperGraphInterface<Index>.");
			}
		}
		else
			return getShortestPathAlgorithm(algType, (GraphInterface<Index,BasicVertexInfo>)graph);
	}

	public static ShortestPathAlgorithmInterface getShortestPathAlgorithm(ShortestPathAlg algType, GraphInterface<Index,BasicVertexInfo> graph)
	{
		switch(algType){
		case BFS:
			return new BFSAlgorithm(graph);
		case HYPERBFS:
			return new HyperBFSAlgorithm(graph, true);
		case DIJKSTRA:
			return new DijkstraAlgorithm(graph);
		default:
			throw new IllegalArgumentException("Algorithm type not supported for GraphInterface<Index>.");
		}
	}

	public static ShortestPathAlgorithmInterface getShortestPathAlgorithm(ShortestPathAlg algType, GraphInterface<Index,BasicVertexInfo> graph, boolean maintainArray)
	{
		switch(algType){
		case BFS:
			return new BFSAlgorithm(graph, maintainArray);
		case DIJKSTRA:
			return new DijkstraAlgorithm(graph);
		default:
			throw new IllegalArgumentException("Algorithm type not supported.");
		}
	}
	
	public static ShortestPathAlg getDefaultAlgorithmType()
	{
		return m_defaultAlgType;
	}
}

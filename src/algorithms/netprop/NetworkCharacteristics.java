package algorithms.netprop;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.CentralityAlgorithmInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;
import algorithms.shortestPath.ShortestPathFactory;

public class NetworkCharacteristics {
	
	static double density (GraphInterface<Index,BasicVertexInfo> G){
		return ((double) G.getNumberOfEdges()) / G.getNumberOfVertices();
	}

	
	static double graphBetweenness(GraphInterface<Index,BasicVertexInfo> G){
		int n = G.getNumberOfVertices();
		
		CentralityAlgorithmInterface bc = new BrandesBC(G);
		double maxBC = 0;
		for (int i=0;i<n;i++){
			double currentBC = bc.getCentrality(i);
			if (currentBC>maxBC)
				maxBC=currentBC;
		}
		double maxPossibleBC = n*(n-1); 
		return maxBC / maxPossibleBC;
	}
	
	static double characteristicPathLength(GraphInterface<Index,BasicVertexInfo> G){
		return characteristicPathLength(ShortestPathAlg.BFS,G);
	}


	private static double characteristicPathLength(ShortestPathAlg spalg,
			GraphInterface<Index,BasicVertexInfo> g) {
		
		ShortestPathAlgorithmInterface sp = ShortestPathFactory.getShortestPathAlgorithm(spalg, g);
		double totalDist = 0;
		int n = g.getNumberOfVertices();
		for (int i=0;i<n;i++){
			sp.run(i);
			double[] dist = sp.getDistanceArray();
			for (int j=0;j<n;j++)
				totalDist+=dist[j];			
		}
		return totalDist / n / (n-1);
	}
	
	
}

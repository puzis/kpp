package algorithms.centralityAlgorithms.dist;

import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

public class DistArrayMatrix implements DistanceMatrixInterface {

	private double[][] _dists;
	
	@Override
	public double getDistance(int u, int v) {
		return _dists[u][v];
	}

	@Override
	public void setDistance(int u, int v, double dist) {
		_dists[u][v]=dist;
	}

	
	public static double[][] getShortestPathDistances(GraphInterface<Index,BasicVertexInfo> graph, ShortestPathAlgorithmInterface shortestPathAlg,
														AbstractExecution progress, double percentage){
		
		double[][] distance = new double[graph.getNumberOfVertices()][graph.getNumberOfVertices()];

		for (int s = 0; s < graph.getNumberOfVertices(); s++) {
			shortestPathAlg.run(s);

			double p = progress.getProgress();
			p += (1 / (double) graph.getNumberOfVertices()) * percentage;
			progress.setProgress(p);

			System.arraycopy(shortestPathAlg.getDistanceArray(), 0, distance[s], 0, distance[s].length);
		}
		for (int i = 0; i < distance.length; i++) {
			for (int j = 0; j < distance[i].length; j++) {
				if (Double.isNaN(distance[i][j])) {
					distance[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		return distance;
	}
}

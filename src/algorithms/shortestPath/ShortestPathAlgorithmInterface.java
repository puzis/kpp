package algorithms.shortestPath;


public interface ShortestPathAlgorithmInterface 
{
	public static enum ShortestPathAlg {BFS, DIJKSTRA, HYPERBFS, HYPERDIJKSTRA, BFSHYPER_NONOPTIMIZED};
	public static final ShortestPathAlg DEFAULT = ShortestPathAlg.BFS;
	
	public void run(int s);
	
	public void addListener(GraphTraversalListener l);
	
	public void removeListener(GraphTraversalListener l);
	
	public double[] getDistanceArray();

	public double getDistance(int intValue);
	
	
	public long getNumberOfDiscovered();
	public long getNumberOfRediscovered();
	public long getNumberOfExpanded();
	public void resetCounters();
	

}
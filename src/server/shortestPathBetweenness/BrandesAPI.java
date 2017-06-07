package server.shortestPathBetweenness;

/**
 * Created by IntelliJ IDEA.
 * User: giese
 * Date: 26.07.11
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public interface BrandesAPI {
    int create(int netID, String communicationWeightsStr);
    
    int create(int netID, String communicationWeightsStr, String shortestPathAlg);

    int create(int netID);

    int create(int netID, int tmID);

    int create(int netID, int tmID, Object[] sources);

    int create(int netID, int tmID, Object[] sources, String shortestPathAlg);

	int create(int netID, int tmID, Object[] sources, String shortestPathAlg, boolean delayExecution);

	int createAsynch(int netID, String communicationWeightsStr);

    int createAsynch(int netID, int tmID);

    int createAsynch(int netID);

    int destroy(int algID);

    double getBetweenness(int algID, int vertex);

    Object[] getBetweenness(int algID, int[] vertices);

    Object[] getBetweenness(int algID);

	int setSources(int algID, Object[] sources);

	int run(int algID);
	
	
	
	public int getNumberOfVertices(int algID);
	public int getNumberOfEdges(int algID);
	public double getNumberOfDiscovered(int algID);
	public double getNumberOfRediscovered(int algID);
	public double getNumberOfExpanded(int algID);
	public int resetCounters(int algID);
	
	
	
	
}


public class GBCLib {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    server.network.NetworkController nc = new server.network.NetworkController();
	    int netID = nc.importNetwork("nettestfile.net", "");
	    System.out.println("NetID: "+netID);
	    System.out.println("Number of Vertices: "+nc.getNumberOfVertices(netID));
	    System.out.println("Number of Edges: "+nc.getNumberOfEdges(netID));
	      
	    server.shortestPathBetweenness.GBCController gbcc = new server.shortestPathBetweenness.GBCController(); 
	    int algID = gbcc.create(netID, "", false, false);    
	    System.out.println("AlgID: "+algID);
	      
	    double gbc = gbcc.getGBC(algID, new Object[]{3}, new Object[]{}); 
	    System.out.println("GBC is: " +gbc);
	}
}



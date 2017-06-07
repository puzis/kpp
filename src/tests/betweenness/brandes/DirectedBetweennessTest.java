package tests.betweenness.brandes;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.FastList;
import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.ServerConstants;
import topology.BasicVertexInfo;
import topology.DiGraphAsHashMap;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.SerializableGraphRepresentation;
import topology.VertexInfo;
import topology.graphParsers.NetFileParser;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.TrafficMatrixBC;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

import common.MatricesUtils;

public class DirectedBetweennessTest extends TestCase
{
    GraphInterface<Index,BasicVertexInfo> g_line, g_clique, g_7_1, g_7_2, defaultnet, g_star;
    Object[] g_graphs;

    int [][] g_groups = {{3}, {2}, {2, 3}, {2, 4}, {0, 2, 3}};
    
    double [] g_line_results = {15, 14, 17, 19, 18};
    double [] g_clique_results = {12.0, 12.0, 22.0, 22.0, 30.0};
    double [] g_7_1_results = {15.0, 14, 17, 19, 18};
    double [] g_7_2_results = {16.6667, 11.6667, 20.3333, 20, 28};
    
    double [][] g_results = {g_line_results, g_clique_results , g_7_1_results, g_7_2_results};
    
    double [] g_line_betweenness = {6.00, 11.00, 14.00, 15.00, 14.00, 11.00, 6.00};
    double [] g_clique_betweenness = {12.00, 12.00, 12.00, 12.00, 12.00, 12.00, 12.00};
    double [] g_7_1_betweenness = {6.00, 6.00, 14.00, 15.00, 14.00, 6.00, 6.00};
    double [] g_7_2_betweenness = {16.6667, 11.6667, 11.6667, 16.6667, 11.6667, 11.6667};
    double [] g_star_betweenness = {6.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00};
    
    double [][] g_line_commWeights = {{0, 0, 0, 0, 0, 0, 1},
    								  {0, 0, 0, 0, 0, 0, 0},
    								  {0, 0, 0, 0, 0, 0, 0},
    								  {0, 0, 0, 0, 0, 0, 0},
    								  {0, 0, 0, 0, 0, 0, 0},
    								  {0, 0, 0, 0, 0, 0, 0},
    								  {0, 0, 0, 0, 0, 0, 0}};
    
    double [] g_line_bcResults = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
    
    double [][] g_7_2_commWeights = {{0, 0, 0, 0, 0, 0},
    								 {0, 0, 0, 0, 1, 0},
    								 {0, 0, 0, 0, 0, 1},
    								 {0, 0, 0, 0, 0, 0},
    								 {0, 0, 0, 0, 0, 0},
    								 {0, 0, 1, 0, 0, 0}};

    double [] g_7_2_bcResults = {2.0000, 1.6667, 2.3333, 2.0000, 1.6667, 2.3333};
    
    public DirectedBetweennessTest(String arg0){	super(arg0);	}

    public void setUp(){
    	g_star = new DiGraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 7; i++)
			g_star.addVertex(Index.valueOf(i));
		for(int i = 1; i < 7; i++)
			g_star.addEdge(Index.valueOf(0), Index.valueOf(i), new EdgeInfo<Index,BasicVertexInfo>());
		
    	g_line = new DiGraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_line.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
        {
            g_line.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }

        g_clique = new DiGraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_clique.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 7; v++)
            for (int u = 0; u < 7; u++)
                if (u != v)
                {
                	if (!g_clique.isEdge(Index.valueOf(v), Index.valueOf(u)))
                		g_clique.addEdge(Index.valueOf(v), Index.valueOf(u), new EdgeInfo<Index,BasicVertexInfo>());
                }

        g_7_1 = new DiGraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_7_1.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
        {
            g_7_1.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_1.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>()); 
        g_7_1.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());

        g_7_2 = new DiGraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 6; v++)
            g_7_2.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 5; v++)
        {
            g_7_2.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
            g_7_2.addEdge(Index.valueOf(v+1), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_2.addEdge(Index.valueOf(0), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>()); 
        g_7_2.addEdge(Index.valueOf(5), Index.valueOf(0), new EdgeInfo<Index,BasicVertexInfo>()); 
        g_7_2.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        g_7_2.addEdge(Index.valueOf(3), Index.valueOf(0), new EdgeInfo<Index,BasicVertexInfo>());
        
		SerializableGraphRepresentation snet = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
        new NetFileParser().analyzeFile(ServerConstants.DATA_DIR + "defaultnet.net", new DummyProgress(), 1, snet);
        defaultnet = GraphFactory.copyAsSimple(snet);
        g_graphs = new Object[]{g_line, g_clique, g_7_1, g_7_2};
        
    }

    public void testDataWorkshopBetweenness() throws Exception
    {
        assertTrue(testDWBetweennessPerGraph(g_line, g_line_betweenness, null));
        assertTrue(testDWBetweennessPerGraph(g_clique, g_clique_betweenness, null));
        assertTrue(testDWBetweennessPerGraph(g_7_1, g_7_1_betweenness, null));
        assertTrue(testDWBetweennessPerGraph(g_7_2, g_7_2_betweenness, null));        
    }
    
    private boolean testDWBetweennessPerGraph(GraphInterface<Index,BasicVertexInfo> graph, double [] betweenness, AbsTrafficMatrix commWeights) throws Exception
    {
    	if (commWeights == null)
    		commWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); //MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
            
    	NumberFormat formatter = new DecimalFormat("0.000");
    	
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, commWeights, true, new DummyProgress(), 1);
        for (int i = 0; i < graph.getNumberOfVertices(); i++)
        {
            assertEquals(formatter.format(dw.getBetweenness(i)),formatter.format(betweenness[i]));                
        }
        
        TrafficMatrixBC brandes = new TrafficMatrixBC(ShortestPathAlgorithmInterface.DEFAULT, graph, commWeights, new DummyProgress(), 1);
		brandes.run();
		for (int i = 0; i < graph.getNumberOfVertices(); i++)
        {
            if (!formatter.format(brandes.getCentrality(i)).equals(formatter.format(betweenness[i])))
                return false;
        }
        return true;
    }
    
    public void testMatricesUpdates() throws Exception
    {
    	assertTrue(testMatricesUpdates(g_line));
    	assertTrue(testMatricesUpdates(g_clique));
    	assertTrue(testMatricesUpdates(g_7_1));
    	assertTrue(testMatricesUpdates(g_7_2));
    	assertTrue(testMatricesUpdates(defaultnet));
    }
    
    private boolean testMatricesUpdates(GraphInterface<Index,BasicVertexInfo> graph) throws Exception
    {
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, true, new DummyProgress(), 1);
    	Object[] group = new Object[graph.getNumberOfVertices()];
    	for (int i = 0; i < graph.getNumberOfVertices(); i++)
    		group[i] = Index.valueOf(i);
    	
    	CandidatesBasedAlgorithm cba = new CandidatesBasedAlgorithm(dw, group);
    	double [][] sigma = null;
    	double [][] pathBetweenness = null;
    	
    	for (int v = 0; v < graph.getNumberOfVertices(); v++)
    	{
    		cba.addMember(v);
    		sigma = cba.getDataWorkshop().getSigma();
    		pathBetweenness = cba.getDataWorkshop().getPathBetweeness();
    		
    		for (int w = 0; w < graph.getNumberOfVertices(); w++)
    		{
    			if (sigma[v][w] != 0) return false;
    			if (pathBetweenness[v][w] != 0) return false;
    			if (sigma[w][v] != 0) return false;
    			if (pathBetweenness[w][v] != 0) return false;
    		}
    	}
    	
    	sigma = cba.getDataWorkshop().getSigma();
		pathBetweenness = cba.getDataWorkshop().getPathBetweeness();
    	for (int i = 0; i < graph.getNumberOfVertices(); i++)
    		for (int j = 0; j < graph.getNumberOfVertices(); j++)
    		{
    			if (sigma[i][j] != 0) return false;
    			if (pathBetweenness[i][j] != 0) return false;
    		}
    	
    	/** Add vertices in random order (by implementing random permutation). */ 
    	for (int tries = 0; tries < 10; tries++)
    	{
	    	cba = new CandidatesBasedAlgorithm(dw, group);
	    	int [] permutation = MatricesUtils.permute(graph.getNumberOfVertices());
	    	
	    	for (int v = 0; v < graph.getNumberOfVertices(); v++)
	    		cba.addMember(permutation[v]);
	    	sigma = cba.getDataWorkshop().getSigma();
	    	pathBetweenness = cba.getDataWorkshop().getPathBetweeness();
	    	
	    	for (int i = 0; i < graph.getNumberOfVertices(); i++)
	    		for (int j = 0; j < graph.getNumberOfVertices(); j++)
	    		{
	    			if (sigma[i][j] != 0) return false;
	    			if (pathBetweenness[i][j] != 0) return false;
	    		}
    	}
    	return true;
    }

    public void testGBC() throws Exception
    {
    	for (int j=0;j<g_results.length;j++){
        	for (int i =0;i<g_groups.length;i++){
        		Index [] iGroup = new Index[g_groups[i].length];        		
            	for(int k=0;k<iGroup.length;k++)iGroup[k]=Index.valueOf(g_groups[i][k]);
            	testGBC((GraphInterface<Index,BasicVertexInfo>)(g_graphs[j]), iGroup, g_results[j][i]);        	    		
        	}
    	}
    }
    
    @SuppressWarnings("unchecked")
	public void testMixedGBC() throws Exception
    {
    	/** g_line GBC */
    	Object[] group;    	
    	group = new Object[]{g_line.getEdge(Index.valueOf(2), Index.valueOf(3))};    	
        testGBC(g_line, group, 12.0);
        
        group = new Object[]{Index.valueOf(0),g_line.getEdge(Index.valueOf(2), Index.valueOf(3))};
        testGBC(g_line, group, 14.0);
    }
    
    public void testMixedGroups() throws Exception
    {
    	for (int j=0;j<g_results.length;j++){
        	for (int i =0;i<g_groups.length;i++){
        		FastList<Object> iGroup = new FastList<Object>();        		
            	for(int k=0;k<g_groups[i].length;k++){
        			Index v = Index.valueOf(g_groups[i][k]);
            		if(k%2==0){
            			//edges
            			
            			for(AbstractSimpleEdge<Index,BasicVertexInfo> e: ((GraphInterface<Index,BasicVertexInfo>)(g_graphs[j])).getOutgoingEdges(v))
            				iGroup.add(e);
            			
            			for(AbstractSimpleEdge<Index,BasicVertexInfo> e: ((GraphInterface<Index,BasicVertexInfo>)(g_graphs[j])).getIncomingEdges(v))
            				iGroup.add(e);
            		}else{
            			//vertex
            			iGroup.add(v);
            		}            		
            	}
            	testGBC((GraphInterface<Index,BasicVertexInfo>)(g_graphs[j]), iGroup.toArray(), g_results[j][i]);        	    		
        	}
    	}
    }
    
    
    private void testGBC(GraphInterface<Index,BasicVertexInfo> graph, Object[] group, double expectedResult) throws Exception
    {
    	Object[] g = group;
    	NumberFormat formatter = new DecimalFormat("0.0000");
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, true, new DummyProgress(), 1);
    	assertEquals(formatter.format(expectedResult), formatter.format(CandidatesBasedAlgorithm.calculateGB(dw, g, new DummyProgress(), 1)));
    }

    public void testDataWorkshopBetweennessWithWeights() throws Exception
    {
    	assertTrue(testDWBetweennessPerGraph(g_line, g_line_bcResults, new DenseTrafficMatrix(g_line_commWeights)));
        assertTrue(testDWBetweennessPerGraph(g_7_2, g_7_2_bcResults, new DenseTrafficMatrix(g_7_2_commWeights)));        
    }
    
    public void testEGBCvsGBC() throws Exception
    {
    	testEGBCvsGBC(g_line, g_line_results);
    	testEGBCvsGBC(g_clique, g_clique_results);
    	testEGBCvsGBC(g_7_1, g_7_1_results);
    	testEGBCvsGBC(g_7_2, g_7_2_results);
    }
    /** Test the edge version of GBC */
    public void testEGBCvsGBC(GraphInterface<Index,BasicVertexInfo> graph, double [] results) throws Exception
    {
    	NumberFormat formatter = new DecimalFormat("0.0000");
    	
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, true, new DummyProgress(), 1);
    	Object[] group = new Object[graph.getNumberOfVertices()];
    	for (int i = 0; i < graph.getNumberOfVertices(); i++)
    		group[i] = Index.valueOf(i);
    	CandidatesBasedAlgorithm cba = null;
    	for (int i = 0; i < g_groups.length; i++)
    	{
    		FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> links = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
    		
    		cba = new CandidatesBasedAlgorithm(dw, group);
    		int [] iGroup = g_groups[i];
    		for (int j = 0; j < iGroup.length; j++)
    		{
//    			cba.addMember(iGroup[j]);
    			for (AbstractSimpleEdge<Index,BasicVertexInfo> e: graph.getOutgoingEdges(Index.valueOf(iGroup[j])))
                {
    				AbstractSimpleEdge<Index,BasicVertexInfo> vertices1 = e;
                    cba.addMember(vertices1);
                    links.add(vertices1);
                }
    			for (AbstractSimpleEdge<Index,BasicVertexInfo> e: graph.getIncomingEdges(Index.valueOf(iGroup[j])))
                {
    				AbstractSimpleEdge<Index,BasicVertexInfo> vertices1 = e;
                    cba.addMember(vertices1);
                    links.add(vertices1);
                }
    		}
    		double actual = cba.getGroupBetweenness();
    		Object [] edges = new Object[links.size()];
    		for (int k=0; k<links.size();k++)
    			edges[k]=links.get(k);
    		double actual2 = CandidatesBasedAlgorithm.calculateGB(dw, edges, new DummyProgress(), 1);
    		
    		assertEquals(formatter.format(results[i]), formatter.format(actual));
    		assertEquals(formatter.format(results[i]), formatter.format(actual2));
    	}
    }
}
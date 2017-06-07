package tests.betweenness.brandes;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.FastMap;
import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import topology.BANetworkGenerator;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.TopologyConstants;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class LatencyTest extends TestCase 
{
	private GraphInterface<Index,BasicVertexInfo> m_graph, g_circle = null;

	
    double [] g_circle_bc   = {14.0, 13.0, 13.0, 14.0, 13.0, 13.0};
	
    double [][] g_circle_TM = 
    		{{0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 1, 0},
			 {0, 0, 0, 0, 0, 1},
			 {0, 0, 0, 0, 0, 0},
			 {0, 0, 0, 0, 0, 0},
			 {0, 0, 1, 0, 0, 0}};

    double [] g_circle_tmbc = {1.5,2,2.5,1.5,2,2.5};

	
	public LatencyTest(String arg0)
	{
		super(arg0);
	}
	
	public void setUp()
	{
        m_graph = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int i = 0; i < 4; i++)
        	m_graph.addVertex(Index.valueOf(i), new VertexInfo());
        
        FastMap<String, String> info_1 = new FastMap<String, String>();
        info_1.put(TopologyConstants.LATENCY, Double.toString(1.5));
        
        FastMap<String, String> info_2 = new FastMap<String, String>();
        info_2.put(TopologyConstants.LATENCY, Double.toString(1));
        
        m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>(1.5, 1.0));
        m_graph.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>(1.5, 1.0));
//        m_graph.addEdge(0, 2, new EdgeInfo(1.0, info_2));
//        EdgeInfo.resetEdgeInfo();
        m_graph.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        m_graph.addEdge(Index.valueOf(3), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
        
        /////////////////////////////////////////////////////////////////
        
        info_1.clear();
        info_1.put(TopologyConstants.LATENCY, Double.toString(1));
        info_2.clear();
        info_2.put(TopologyConstants.LATENCY, Double.toString(2));
        g_circle = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int i=0;i<6;i++)
            g_circle.addVertex(Index.valueOf(i));
        for (int i=0;i<5;i++)
            g_circle.addEdge(Index.valueOf(i),Index.valueOf(i+1),new EdgeInfo<Index,BasicVertexInfo>());
        g_circle.addEdge(Index.valueOf(0),Index.valueOf(5),new EdgeInfo<Index,BasicVertexInfo>());
        g_circle.addEdge(Index.valueOf(0),Index.valueOf(3),new EdgeInfo<Index,BasicVertexInfo>(2, 1.0));
        
	}
	
	public void testShortestPaths() throws Exception
	{
		NumberFormat formatter = new DecimalFormat("0.000");
    	
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlg.DIJKSTRA, m_graph, true, new DummyProgress(), 1);
    	double[][] d = dw.getDistanceMatrix();
    	double[][] s = dw.getSigma();
    	assertEquals(formatter.format(1.5), formatter.format(d[0][1]));
    	assertEquals(formatter.format(1.0), formatter.format(d[0][3]));
    	assertEquals(formatter.format(2.0), formatter.format(d[0][2]));
    	assertEquals(formatter.format(1.5), formatter.format(d[1][2]));
    	assertEquals(formatter.format(2.5), formatter.format(d[1][3]));
    	assertEquals(formatter.format(2), formatter.format(s[1][3]));
	}
	
	
	public void testBetweenness(){
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlg.DIJKSTRA, g_circle, true, new DummyProgress(), 1);
    	dw.computePairBetweenness();
    	double[][] p = dw.getPathBetweeness();
    	for (int i=0;i<g_circle_bc.length;i++)
    		assertEquals(g_circle_bc[i], p[i][i]);
		
	}
	
	public void testBetweennessTM(){
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlg.DIJKSTRA, g_circle, new DenseTrafficMatrix(g_circle_TM), true, new DummyProgress(), 1);
    	dw.computePairBetweenness();
    	double[][] p = dw.getPathBetweeness();
    	for (int i=0;i<g_circle_tmbc.length;i++)
    		assertEquals(g_circle_tmbc[i], p[i][i]);
		
	}
	
	public void testGBCTM(){
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlg.DIJKSTRA, g_circle, new DenseTrafficMatrix(g_circle_TM), true, new DummyProgress(), 1);
    	dw.computePairBetweenness();
    	Index[] candidates = new Index[g_circle.getNumberOfVertices()];
    	for (int i=0;i<candidates.length;i++)
    		candidates[i] = Index.valueOf(i);    	
    	Double gb = CandidatesBasedAlgorithm.calculateGB(dw,candidates, new DummyProgress(), 1);
   		assertEquals(3.0, gb);		
	}
	
	public void testConsistencyOnRandomBA(){
		for (int r=0;r<10;r++){
			BANetworkGenerator gen = new BANetworkGenerator(2);
			GraphInterface<Index,BasicVertexInfo> g = gen.generate(100);
			
			
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : g.getEdges()){
				g.getEdgeWeight(e).setLabel(TopologyConstants.LATENCY, String.valueOf(Math.random()*100));
			}
			
	    	DataWorkshop dw = new DataWorkshop(ShortestPathAlg.DIJKSTRA, g, true, true, new DummyProgress(), 1);
	
	//		try {
	//			dw.saveToDisk("LatencyTest_FaultyNetwork_1", new DummyProgress(), 1);
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	    	
	    	Index[] candidates = new Index[g.getNumberOfVertices()];
	    	for (int i=0;i<candidates.length;i++)
	    		candidates[i] = Index.valueOf(i);
			CandidatesBasedAlgorithm cca = new CandidatesBasedAlgorithm(dw, candidates);
			common.ArrayUtils.shuffle(candidates);
			double gbprev = 0;
			for (int i=0;i<candidates.length;i++){
				cca.addMember(Index.valueOf(i));
				double gb = cca.getGroupBetweenness();
				assertTrue(gb>=gbprev);
				gbprev = gb;
				
				double [][] sigma = cca.getSigmaClone();
				double [][] pb = cca.getPathBetweenessClone();
				for (Index x: candidates)
					for (Index y: candidates){
						assertTrue(sigma[x.intValue()][y.intValue()]>=-0.000000000001);
						assertTrue(pb[x.intValue()][y.intValue()]>=-0.0000001000001);
					}
			}
		}
	}
	
}

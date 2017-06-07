package tests.betweenness.bcc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.ServerConstants;
import topology.AbstractGraph;
import topology.AbstractUndirectedGraph;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphFactory;
import topology.GraphInterface;
import topology.GraphUtils;
import topology.SerializableGraphRepresentation;
import topology.VertexFactory;
import topology.VertexInfo;
import topology.graphParsers.NetFileParser;
import algorithms.bcc.BCCAlgorithm;
import algorithms.bcc.BiConnectedComponent;
import algorithms.centralityAlgorithms.betweenness.bcc.BCCalculatorInterface;
import algorithms.centralityAlgorithms.betweenness.bcc.BetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.bcc.EvenFasterBetweenness;
import algorithms.centralityAlgorithms.betweenness.bcc.TMBetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.structuralEquivalence.StructuralEquivalenceUnifier;

public class BetweennessTest extends TestCase
{
    AbstractUndirectedGraph<Index,BasicVertexInfo> g_line, g_clique, g_7_1, g_7_2, g_tennisBat, g_star, g_diamond, g_quartetDiamond, g_spider,
    						g_tripleDiamond, g_triangleWithHands, g_shortTennisBat, g_mitsubishi, g_letterT, g_flower, g_triangleWithLegs;
    GraphInterface<Index,BasicVertexInfo> defaultnet, joe;
    int [][] g_groups = {{3}, {2}, {2, 3}, {2, 4}, {0, 2, 3}};
    double [] g_line_results = {30, 28, 34, 38, 36};
    double [] g_clique_results = {12.0, 12.0, 22.0, 22.0, 30.0};
    double [] g_7_1_results = {30.0, 28, 34, 38, 36};
    double [] g_7_2_results = {16.6667, 11.6667, 20.3333, 20, 28};
    
    double [] g_star_betweenness = {42.00, 12.00, 12.00, 12.00, 12.00, 12.00, 12.00};
    double [] g_line_betweenness = {12.00, 22.00, 28.00, 30.00, 28.00, 22.00, 12.00};
    double [] g_clique_betweenness = {12.00, 12.00, 12.00, 12.00, 12.00, 12.00, 12.00};
    double [] g_7_1_betweenness = {12.00, 12.00, 28.00, 30.00, 28.00, 12.00, 12.00};
    double [] g_7_2_betweenness = {16.6667, 11.6667, 11.6667, 16.6667, 11.6667, 11.6667};
    double [] g_diamond_betweenness = {16.00, 13.00, 16.00, 32.00, 16.00, 13.00, 16.00};
    double [] g_tripleDiamond_betweenness = {25.0, 19.0, 25.0, 56.0, 34.0, 56.0, 34.0, 25.0, 25.0, 19.0};
    double [] g_triangleWithHands_betweenness = {8.00, 18.00, 8.0, 8.0, 8.0};
    double [] g_shortTennisBat_betweenness = {10.00, 15.00, 10.00, 9.00, 8.00};
    double [] g_mitsubishi_betweenness = {75.0, 25.0, 19.0, 25.0, 25.0, 19.0, 25.0, 25.0, 19.0, 25.0};
    double [] g_quartet_betweenness = {34.0, 25.0, 34.0, 83.0, 95.0, 83.0, 40.0, 34.0, 34.0, 25.0, 34.0, 25.0, 34.0};
    double [] g_letterT_betweenness = {8.0, 18.0, 8.0, 14.0, 8.0};
    double [] g_flower_betweenness = {18.0, 14.0, 8.0, 8.0, 8.0};
    double [] g_spider_betweenness = {14.0, 8.0, 18.0, 8.0, 8.0};
    double [] g_triangleWithLegs_betweenness = {8.000, 14.000, 14.000, 8.000, 8.000};
    
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
    
	double [][][] g_line_bcc_CW = 
		{  
			//0--1
			{	{00.0,06.0},				
			    {06.0,00.0}},
			//1--2
			{	{00.0,10.0},
			    {10.0,00.0}},
			//2--3
			{	{00.0,12.0},
			    {12.0,00.0}},
			//3--4
			{	{00.0,12.0},
			    {12.0,00.0}},
			//4--5
			{	{00.0,10.0},
			    {10.0,00.0}},
			//5--6
			{	{00.0,06.0},
			    {06.0,00.0}}
		};
    
    
    
    public BetweennessTest(String arg0){	super(arg0);	}

    public void setUp(){
    	g_star = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 7; i++)
			g_star.addVertex(Index.valueOf(i));
		for(int i = 1; i < 7; i++)
			g_star.addEdge(Index.valueOf(0), Index.valueOf(i), new EdgeInfo<Index,BasicVertexInfo>());
		
    	g_tennisBat = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 6; i++)
			g_tennisBat.addVertex(Index.valueOf(i));
		for(int i = 0; i < 5; i++)
			g_tennisBat.addEdge(Index.valueOf(i), Index.valueOf(i+1), new EdgeInfo<Index,BasicVertexInfo>());
		g_tennisBat.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		g_tennisBat.addEdge(Index.valueOf(2), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
		
        g_line = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_line.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++){
            g_line.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }

        g_clique = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_clique.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 7; v++)
            for (int u = 0; u < 7; u++)
                if (u != v){
                	if (!g_clique.isEdge(Index.valueOf(u), Index.valueOf(v)))
                		g_clique.addEdge(Index.valueOf(u), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
                }

        g_7_1 = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_7_1.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++){
            g_7_1.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_1.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>()); 
        g_7_1.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());

        g_7_2 = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 6; v++)
            g_7_2.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 5; v++){
            g_7_2.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_2.addEdge(Index.valueOf(0), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>()); 
        g_7_2.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_diamond = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
        	g_diamond.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++){
        	g_diamond.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_diamond.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        g_diamond.addEdge(Index.valueOf(6), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_mitsubishi = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 10; v++)
        	g_mitsubishi.addVertex(Index.valueOf(v), new VertexInfo());
        
        g_mitsubishi.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
        g_mitsubishi.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
        g_mitsubishi.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        g_mitsubishi.addEdge(Index.valueOf(3), Index.valueOf(0), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_mitsubishi.addEdge(Index.valueOf(0), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
        g_mitsubishi.addEdge(Index.valueOf(4), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
        g_mitsubishi.addEdge(Index.valueOf(5), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
        g_mitsubishi.addEdge(Index.valueOf(6), Index.valueOf(0), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_mitsubishi.addEdge(Index.valueOf(0), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
        g_mitsubishi.addEdge(Index.valueOf(7), Index.valueOf(8), new EdgeInfo<Index,BasicVertexInfo>());
        g_mitsubishi.addEdge(Index.valueOf(8), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
        g_mitsubishi.addEdge(Index.valueOf(0), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_tripleDiamond = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 10; v++)
        	g_tripleDiamond.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++){
        	g_tripleDiamond.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_tripleDiamond.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        g_tripleDiamond.addEdge(Index.valueOf(6), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        g_tripleDiamond.addEdge(Index.valueOf(5), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
        g_tripleDiamond.addEdge(Index.valueOf(5), Index.valueOf(8), new EdgeInfo<Index,BasicVertexInfo>());
        g_tripleDiamond.addEdge(Index.valueOf(8), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
        g_tripleDiamond.addEdge(Index.valueOf(7), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_quartetDiamond = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 13; v++)
        	g_quartetDiamond.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++){
        	g_quartetDiamond.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_quartetDiamond.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        g_quartetDiamond.addEdge(Index.valueOf(6), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        g_quartetDiamond.addEdge(Index.valueOf(5), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
        g_quartetDiamond.addEdge(Index.valueOf(5), Index.valueOf(8), new EdgeInfo<Index,BasicVertexInfo>());
        g_quartetDiamond.addEdge(Index.valueOf(8), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
        g_quartetDiamond.addEdge(Index.valueOf(7), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_quartetDiamond.addEdge(Index.valueOf(4), Index.valueOf(10), new EdgeInfo<Index,BasicVertexInfo>());
        g_quartetDiamond.addEdge(Index.valueOf(10), Index.valueOf(11), new EdgeInfo<Index,BasicVertexInfo>());
        g_quartetDiamond.addEdge(Index.valueOf(11), Index.valueOf(12), new EdgeInfo<Index,BasicVertexInfo>());
        g_quartetDiamond.addEdge(Index.valueOf(4), Index.valueOf(12), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_triangleWithHands = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 5; v++)
        	g_triangleWithHands.addVertex(Index.valueOf(v), new VertexInfo());
        g_triangleWithHands.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
        g_triangleWithHands.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
        g_triangleWithHands.addEdge(Index.valueOf(1), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
        g_triangleWithHands.addEdge(Index.valueOf(1), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
        g_triangleWithHands.addEdge(Index.valueOf(3), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_shortTennisBat = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 5; i++)
			g_shortTennisBat.addVertex(Index.valueOf(i), new VertexInfo());
		g_shortTennisBat.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		g_shortTennisBat.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_shortTennisBat.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_shortTennisBat.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		g_shortTennisBat.addEdge(Index.valueOf(1), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_letterT = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int i=0; i<5; i++)
			g_letterT.addVertex(Index.valueOf(i), new VertexInfo());
		g_letterT.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		g_letterT.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		g_letterT.addEdge(Index.valueOf(1), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_letterT.addEdge(Index.valueOf(3), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_flower = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int i=0; i<5; i++)
			g_flower.addVertex(Index.valueOf(i), new VertexInfo());
		
		g_flower.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		g_flower.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		g_flower.addEdge(Index.valueOf(1), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_flower.addEdge(Index.valueOf(0), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_spider = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int i=0; i<5; i++)
			g_spider.addVertex(Index.valueOf(i), new VertexInfo());
		
		g_spider.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		g_spider.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		g_spider.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_spider.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_triangleWithLegs = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int i=0; i<5; i++)
			g_triangleWithLegs.addVertex(Index.valueOf(i), new VertexInfo());
		
		g_triangleWithLegs.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		g_triangleWithLegs.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		g_triangleWithLegs.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_triangleWithLegs.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		g_triangleWithLegs.addEdge(Index.valueOf(1), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		
		SerializableGraphRepresentation snet = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
		new NetFileParser().analyzeFile(ServerConstants.DATA_DIR + "defaultnet.net", new DummyProgress(), 1, snet);
		defaultnet = GraphFactory.copyAsSimple(snet);
		
		
		SerializableGraphRepresentation sjoe = new SerializableGraphRepresentation(GraphFactory.GraphDataStructure.GRAPH_AS_HASH_MAP); 
        new NetFileParser().analyzeFile(ServerConstants.DATA_DIR + "joe.net", new DummyProgress(), 1, sjoe);
        joe = GraphFactory.copyAsSimple(sjoe);
    }

	public void testEagerBCCLineCW() throws Exception
    {
    	NumberFormat formatter = new DecimalFormat("0.000");
    	BCCAlgorithm bccAlg = new BCCAlgorithm(g_line);
    	BetweennessCalculator bc = new BetweennessCalculator(bccAlg);
    	testCW(formatter, bc);
    }
    
    public void testUltraEagerBCCLineTrafficCW() throws Exception
    {
    	NumberFormat formatter = new DecimalFormat("0.000");
    	BCCAlgorithm bccAlg = new BCCAlgorithm(g_line);
    	TMBetweennessCalculator bc = new TMBetweennessCalculator(bccAlg, new DefaultTrafficMatrix(g_line.getNumberOfVertices()));
    	testCW(formatter, bc);
    }
    
    private void testCW(NumberFormat formatter, BCCalculatorInterface bc) throws Exception {    	
    	
		bc.runBCCAlgorithm();
		AbsTrafficMatrix actualCW;    	    		
		Iterator<BiConnectedComponent> itr =bc.getSubGraphs();
		for ( BiConnectedComponent component=itr.next() ;itr.hasNext(); component=itr.next()){
			GraphInterface<Index,BasicVertexInfo> subGraph = component.getComponent();
			
			//assumption: g_line_bcc_CW is ordered by the smallest vertex in component
			Iterator<Index> vitr = subGraph.getVertices().iterator();
			int k = vitr.next().intValue();
			while (vitr.hasNext()){
				int l = vitr.next().intValue();
				if (l<k)k=l;
			}
			
			actualCW = bc.createTrafficMatrix(subGraph);
			for (int i = 0; i < 2; i++)
				for (int j = 0; j < 2; j++)
					assertEquals(formatter.format(g_line_bcc_CW[k][i][j]), formatter.format(actualCW.getWeight(i, j)));
		}
	}

    public void testTrafficBCCCW() throws Exception{
    	testTrafficBCCCW(g_line);
    }
    private void testTrafficBCCCW(AbstractUndirectedGraph<Index,BasicVertexInfo> graph) throws Exception{
    	NumberFormat formatter = new DecimalFormat("0.000");
    	BCCAlgorithm bccAlg = new BCCAlgorithm(graph);
    	
    	BetweennessCalculator eagerBC = new BetweennessCalculator(bccAlg);
    	TMBetweennessCalculator trafficBC = new TMBetweennessCalculator(bccAlg, new DefaultTrafficMatrix(graph.getNumberOfVertices()));
    	
    	trafficBC.runBCCAlgorithm();	eagerBC.runBCCAlgorithm();
		AbsTrafficMatrix trafficCW, eagerCW;    	    		
		GraphInterface<Index,BasicVertexInfo> subGraph;
		for (int k=0;k<graph.getNumberOfVertices()-1;k++){
			subGraph = GraphUtils.reduceVertices(graph, Index.valueOf(k) ,Index.valueOf(k+1));    	    		
			trafficCW = trafficBC.createTrafficMatrix(subGraph);
			eagerCW = eagerBC.createTrafficMatrix(subGraph);
			for (int i = 0; i < 2; i++)
				for (int j = 0; j < 2; j++){
					assertEquals(formatter.format(eagerCW.getWeight(i, j)), formatter.format(trafficCW.getWeight(i, j)));
				}
		}
    }
    
    public void testComb() throws Exception{
    	AbsTrafficMatrix commWeights = new DefaultTrafficMatrix(g_triangleWithLegs.getNumberOfVertices()); //MatricesUtils.getDefaultWeights(g_triangleWithLegs.getNumberOfVertices());
        NumberFormat formatter = new DecimalFormat("0.000");
        
        DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, g_triangleWithLegs, commWeights, true, new DummyProgress(), 1);
        for (int i=0; i<g_triangleWithLegs.getNumberOfVertices(); i++){
        	System.out.println(formatter.format(dw.getBetweenness(i)));
        }
    }
    
    public void testFasterBetweenness() throws Exception
    {
    	testFasterBetweenness(g_line, g_line_betweenness, null);
    	testFasterBetweenness(g_star, g_star_betweenness, null);
        testFasterBetweenness(g_clique, g_clique_betweenness, null);
        testFasterBetweenness(g_7_1, g_7_1_betweenness, null);
        testFasterBetweenness(g_7_2, g_7_2_betweenness, null);        
        testFasterBetweenness(g_diamond, g_diamond_betweenness, null);
        testFasterBetweenness(g_tripleDiamond, g_tripleDiamond_betweenness, null);
        testFasterBetweenness(g_triangleWithHands, g_triangleWithHands_betweenness, null);
        testFasterBetweenness(g_shortTennisBat, g_shortTennisBat_betweenness, null);
        testFasterBetweenness(g_mitsubishi, g_mitsubishi_betweenness, null);
        testFasterBetweenness(g_quartetDiamond, g_quartet_betweenness, null);
        testFasterBetweenness(g_letterT, g_letterT_betweenness, null);
        testFasterBetweenness(g_flower, g_flower_betweenness, null);
        testFasterBetweenness(g_spider, g_spider_betweenness, null);
        testFasterBetweenness(g_triangleWithLegs, g_triangleWithLegs_betweenness, null);
    }
    
    private void testFasterBetweenness(AbstractUndirectedGraph<Index,BasicVertexInfo> graph, double [] betweenness, AbsTrafficMatrix commWeights) throws Exception
    {
    	if (commWeights == null)
    		commWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); //MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
            
    	NumberFormat formatter = new DecimalFormat("0.000");
    	
    	BetweennessCalculator bcCalc = new BetweennessCalculator(new BCCAlgorithm(graph));
    	EvenFasterBetweenness eagerbc = new EvenFasterBetweenness(graph, bcCalc);	
    	eagerbc.run();
    	
    	// SE+BCC
    	StructuralEquivalenceUnifier sed = new StructuralEquivalenceUnifier(graph);
		sed.run();
		AbsTrafficMatrix unifiedCW = sed.getUnifiedCW();
		AbstractGraph<Index,BasicVertexInfo> unifiedGraph = (AbstractGraph<Index,BasicVertexInfo>)sed.getUnifiedGraph();
		BetweennessCalculator unifiedBcCalc = new BetweennessCalculator(new BCCAlgorithm(unifiedGraph), unifiedCW);
		EvenFasterBetweenness unified_eagerbc = new EvenFasterBetweenness(graph, unifiedBcCalc);	
    	unified_eagerbc.run();
    	
    	TMBetweennessCalculator tmbcCalc = new TMBetweennessCalculator(new BCCAlgorithm(graph), commWeights);
    	EvenFasterBetweenness tmbc = new EvenFasterBetweenness(graph, tmbcCalc);	
    	tmbc.run();
    	
    	DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, commWeights, true, new DummyProgress(), 1);
    	for (int i = 0; i < graph.getNumberOfVertices(); i++){
            assertEquals(formatter.format(betweenness[i]), formatter.format(dw.getBetweenness(i)));
        }
        for (int i = 0; i < graph.getNumberOfVertices(); i++){
            assertEquals(formatter.format(betweenness[i]), formatter.format(eagerbc.getCentrality(i)));
        }
        for (int i = 0; i < graph.getNumberOfVertices(); i++){
            assertEquals(formatter.format(betweenness[i]), formatter.format(tmbc.getCentrality(i)));
        }
        // SE+BCC
        // TODO: The indexes in the original and unified graphs do not correspond (of course), therefore the 
        // current test won't pass. We need a mapping between the original and the unified vertices (equivalence classes).
        for (int i = 0; i < graph.getNumberOfVertices(); i++){
        	int unifiedVertex = sed.getContainingUnifiedVertex(i).intValue();
        	int multiplicity=1; 
        	if (VertexFactory.isVertexInfo(sed.getUnifiedGraph().getVertex(Index.valueOf(unifiedVertex))))
        		multiplicity = ((VertexInfo)sed.getUnifiedGraph().getVertex(Index.valueOf(unifiedVertex))).getMultiplicity();
        	double centrality = unified_eagerbc.getCentrality(unifiedVertex);
            assertEquals(formatter.format(betweenness[i]), formatter.format(centrality/(double)multiplicity));
        }
    }
}
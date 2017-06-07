package tests.betweenness;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import topology.AbstractGraph;
import topology.AbstractUndirectedGraph;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.bcc.BCCAlgorithm;
import algorithms.centralityAlgorithms.betweenness.bcc.BetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.bcc.EvenFasterBetweenness;
import algorithms.centralityAlgorithms.betweenness.bcc.TMBetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.structuralEquivalence.StructuralEquivalenceUnifier;

public class SE_BCC_Test  extends TestCase{

	AbstractUndirectedGraph<Index,BasicVertexInfo> g_line, g_clique, g_7_1, g_7_2, g_tennisBat, g_star, g_diamond, g_quartetDiamond, g_spider,
	g_tripleDiamond, g_triangleWithHands, g_shortTennisBat, g_mitsubishi, g_letterT, g_flower, g_triangleWithLegs,
	g_consistency_check;

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
	
	public SE_BCC_Test(String arg0){	super(arg0);	}

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
		
		g_consistency_check = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int i=0; i<10; i++)
			g_consistency_check.addVertex(Index.valueOf(i), new VertexInfo());
		
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(8), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(0), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(1), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(1), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(1), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(2), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(2), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(3), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(3), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(3), Index.valueOf(8), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(4), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
		
		g_consistency_check.addEdge(Index.valueOf(5), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(5), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(5), Index.valueOf(9), new EdgeInfo<Index,BasicVertexInfo>());
		g_consistency_check.addEdge(Index.valueOf(6), Index.valueOf(8), new EdgeInfo<Index,BasicVertexInfo>());
    }

    public void testOnNetworks() throws Exception
	{
    	AbstractUndirectedGraph<Index,BasicVertexInfo> graph = g_consistency_check;
		NumberFormat formatter = new DecimalFormat("0.000");
    	
	   	// SE+BCC
    	StructuralEquivalenceUnifier sed = new StructuralEquivalenceUnifier(graph);
		sed.run();
		AbsTrafficMatrix unifiedCW = sed.getUnifiedCW();
		AbstractGraph<Index,BasicVertexInfo> unifiedGraph = (AbstractGraph<Index,BasicVertexInfo>)sed.getUnifiedGraph();
		
		BetweennessCalculator unifiedBccCalc = new BetweennessCalculator(new BCCAlgorithm(unifiedGraph), unifiedCW);
    	EvenFasterBetweenness unified_eagerbcv = new EvenFasterBetweenness(graph, unifiedBccCalc); unified_eagerbcv.run();

    	BrandesBC brandes = new BrandesBC(ShortestPathAlgorithmInterface.DEFAULT, graph, new DummyProgress(), 1);
		brandes.run();
		
		DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, true, new DummyProgress(), 1);
		
        for (int i = 0; i < graph.getNumberOfVertices(); i++){
        	assertEquals(formatter.format(dw.getBetweenness(i)),         formatter.format(brandes.getCentrality(i)));
        	
        	int unifiedVertex = sed.getContainingUnifiedVertex(i).intValue();
        	int multiplicity=1;
        	if (VertexFactory.isVertexInfo(sed.getUnifiedGraph().getVertex(Index.valueOf(unifiedVertex))))
        		multiplicity = ((VertexInfo)sed.getUnifiedGraph().getVertex(Index.valueOf(unifiedVertex))).getMultiplicity();
        	double centrality = unified_eagerbcv.getCentrality(unifiedVertex);
        	if (!formatter.format(centrality/(double)multiplicity).equals(formatter.format(brandes.getCentrality(i)))){
        		System.out.println(i);
        		System.out.print("SE+BCC:");
        		System.out.println(centrality/(double)multiplicity);
        		System.out.print("Brandes: ");
        		System.out.println(brandes.getCentrality(i));
//        		System.out.print(graph);
        	}
        	assertEquals(formatter.format(centrality/(double)multiplicity), formatter.format(brandes.getCentrality(i)));

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
    	testFasterBetweenness(g_mitsubishi, g_mitsubishi_betweenness, null);
        testFasterBetweenness(g_quartetDiamond, g_quartet_betweenness, null);
    	testFasterBetweenness(g_triangleWithLegs, g_triangleWithLegs_betweenness, null);
    	
        testFasterBetweenness(g_triangleWithHands, g_triangleWithHands_betweenness, null);
        testFasterBetweenness(g_shortTennisBat, g_shortTennisBat_betweenness, null);
        testFasterBetweenness(g_letterT, g_letterT_betweenness, null);
        testFasterBetweenness(g_flower, g_flower_betweenness, null);
        testFasterBetweenness(g_spider, g_spider_betweenness, null);
        
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
    	
    	TMBetweennessCalculator tmBcCalc = new TMBetweennessCalculator(new BCCAlgorithm(graph), commWeights);
       	EvenFasterBetweenness tmbc = new EvenFasterBetweenness(graph, tmBcCalc);	
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
package tests.bcc;

import javolution.util.Index;
import junit.framework.TestCase;
import topology.AbstractUndirectedGraph;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import algorithms.bcc.BCCAlgorithm;
import algorithms.centralityAlgorithms.betweenness.bcc.BetweennessCalculator;

public class EBiConnComponentsTreeTest extends TestCase
{
	private AbstractUndirectedGraph<Index,BasicVertexInfo> g_tennisBat = null, g_middleV = null, g_line = null, g_circle = null, g_crossedCircle = null, g_star = null;
	private int[][] star_components = {{0,1}, {0,2}, {0,3}, {0,4}, {0,5}, {0,6}};
	private int[][] line_components = {{0,1}, {1,2}, {2,3}, {3,4}, {4,5}, {5,6}};	
	private int[][] middleV_components = {{0,1,2}, {2,3}, {3,4}, {4,5,6}};
	private int[][] tennis_bat_components = {{0,1}, {1,2}, {2,3,4,5}};
	
	public EBiConnComponentsTreeTest(String arg0){	super(arg0);	}
	
	public void setUp()
	{
		g_tennisBat = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 6; i++)
			g_tennisBat.addVertex(Index.valueOf(i));
		for(int i = 0; i < 5; i++)
			g_tennisBat.addEdge(Index.valueOf(i), Index.valueOf(i+1));
		g_tennisBat.addEdge(Index.valueOf(2), Index.valueOf(4));
		g_tennisBat.addEdge(Index.valueOf(2), Index.valueOf(5));
		
		g_middleV = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 7; i++)
			g_middleV.addVertex(Index.valueOf(i));
		for(int i = 0; i < 6; i++)
			g_middleV.addEdge(Index.valueOf(i), Index.valueOf(i+1));
		
		g_middleV.addEdge(Index.valueOf(0), Index.valueOf(2));
		g_middleV.addEdge(Index.valueOf(4), Index.valueOf(6));
		
		g_line = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 7; i++)
			g_line.addVertex(Index.valueOf(i));
		for(int i = 0; i < 6; i++)
			g_line.addEdge(Index.valueOf(i), Index.valueOf(i+1));
				
		g_circle = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 7; i++)
			g_circle.addVertex(Index.valueOf(i));
		for(int i = 0; i < 6; i++)
			g_circle.addEdge(Index.valueOf(i), Index.valueOf(i+1));
		g_circle.addEdge(Index.valueOf(0), Index.valueOf(6));
		
		g_crossedCircle = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 7; i++)
			g_crossedCircle.addVertex(Index.valueOf(i));
		for(int i = 0; i < 6; i++)
			g_crossedCircle.addEdge(Index.valueOf(i), Index.valueOf(i+1));
		g_crossedCircle.addEdge(Index.valueOf(0), Index.valueOf(6));
		g_crossedCircle.addEdge(Index.valueOf(0), Index.valueOf(3));
		
		g_star = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 7; i++)
			g_star.addVertex(Index.valueOf(i));
		for(int i = 1; i < 7; i++)
			g_star.addEdge(Index.valueOf(0), Index.valueOf(i));
	}
	
	public void testConnectedComponentsStar()
	{
		BCCAlgorithm eBcc = new BCCAlgorithm(g_star);
		BetweennessCalculator bcCalc = new BetweennessCalculator(eBcc);
        bcCalc.runBCCAlgorithm();
		GraphInterface<Object,Object> componentsTree = bcCalc.getBiConnectedComponentsTree();
		
		testStarComponents(componentsTree);
	}

	private void testStarComponents(GraphInterface<Object,Object> componentsTree) {
		assertTrue(componentsTree.isVertex(Index.valueOf(0)));
		for (int i=0; i<star_components.length; i++){
			GraphInterface<Index,BasicVertexInfo> cc = new GraphAsHashMap<Index,BasicVertexInfo>();
			int[] component = star_components[i];
			for (int j=0; j<component.length; j++){
				cc.addVertex(Index.valueOf(component[j]));
			}
			for (int v = 0; v < component.length; v++)
	            for (int u = 0; u < component.length; u++)
	                if (component[u] != component[v]){
	                	if (!cc.isEdge(Index.valueOf(component[u]), Index.valueOf(component[v])))
	                		cc.addEdge(Index.valueOf(component[u]), Index.valueOf(component[v]));
	                }
			assertTrue(componentsTree.isVertex(cc));
			assertTrue(componentsTree.isEdge(Index.valueOf(0), cc));
		}
	}

	public void testConnectedComponentsLine()
	{
		BCCAlgorithm eBcc = new BCCAlgorithm(g_line);
		BetweennessCalculator bcCalc = new BetweennessCalculator(eBcc);
        bcCalc.runBCCAlgorithm();
		GraphInterface<Object,Object> componentsTree = bcCalc.getBiConnectedComponentsTree();
		
		testLineComponents(componentsTree);
	}

	private void testLineComponents(GraphInterface<Object,Object> componentsTree) {
		for (int i=1; i<6; i++)
			assertTrue(componentsTree.isVertex(Index.valueOf(i)));

		assertTrue(!componentsTree.isVertex(Index.valueOf(0)));
		assertTrue(!componentsTree.isVertex(Index.valueOf(6)));
		
		for (int i=0; i<line_components.length; i++){
			GraphInterface<Index,BasicVertexInfo> cc = new GraphAsHashMap<Index,BasicVertexInfo>();
			int[] component = line_components[i];
			for (int j=0; j<component.length; j++){
				cc.addVertex(Index.valueOf(component[j]));
			}
			for (int v = 0; v < component.length; v++)
	            for (int u = 0; u < component.length; u++)
	                if (component[u] != component[v]){
	                	if (!cc.isEdge(Index.valueOf(component[u]), Index.valueOf(component[v])))
	                		cc.addEdge(Index.valueOf(component[u]), Index.valueOf(component[v]));
	                }	        
			assertTrue(componentsTree.isVertex(cc));
			
			switch (i){
			case 0:
						assertEquals(0, componentsTree.getDegree(Index.valueOf(i)));
			case 5:		assertEquals(1, componentsTree.getDegree(cc));
						break;
			case 1: case 2: case 3: case 4:	
						assertEquals(2, componentsTree.getDegree(cc));
						assertEquals(2, componentsTree.getDegree(Index.valueOf(i)));
						break;
			}
		}
	}

	public void testConnectedComponentsMiddleV()
	{
		BCCAlgorithm eBcc = new BCCAlgorithm(g_middleV);
		BetweennessCalculator bcCalc = new BetweennessCalculator(eBcc);
        bcCalc.runBCCAlgorithm();
		GraphInterface<Object,Object> componentsTree = bcCalc.getBiConnectedComponentsTree();
		
		testMiddleVComponents(componentsTree);
	}

	private void testMiddleVComponents(GraphInterface<Object,Object> componentsTree) {
		assertEquals(7, componentsTree.getNumberOfVertices());
		assertEquals(6, componentsTree.getNumberOfEdges());
		
		for (int i=2; i<5; i++)
			assertTrue(componentsTree.isVertex(Index.valueOf(i)));

		assertTrue(!componentsTree.isVertex(Index.valueOf(0)));
		assertTrue(!componentsTree.isVertex(Index.valueOf(1)));
		assertTrue(!componentsTree.isVertex(Index.valueOf(5)));
		assertTrue(!componentsTree.isVertex(Index.valueOf(6)));
		
		for (int i=0; i<middleV_components.length; i++){
			GraphInterface<Index,BasicVertexInfo> cc = new GraphAsHashMap<Index,BasicVertexInfo>();
			int[] component = middleV_components[i];
			for (int j=0; j<component.length; j++){
				cc.addVertex(Index.valueOf(component[j]));
			}
			for (int v = 0; v < component.length; v++)
	            for (int u = 0; u < component.length; u++)
	                if (component[u] != component[v]){
	                	if (!cc.isEdge(Index.valueOf(component[u]), Index.valueOf(component[v])))
	                		cc.addEdge(Index.valueOf(component[u]), Index.valueOf(component[v]));
	                }
			assertTrue(componentsTree.isVertex(cc));
			
			switch (i){
			case 0: case 3:
						assertEquals(1, componentsTree.getDegree(cc));
						break;
			case 1: case 2: 	
						assertEquals(2, componentsTree.getDegree(cc));
						break;
			}
		}
	}
	
	public void testConnectedComponentsTennisBat()
	{
		BCCAlgorithm eBcc = new BCCAlgorithm(g_tennisBat);
		BetweennessCalculator bcCalc = new BetweennessCalculator(eBcc);
        bcCalc.runBCCAlgorithm();
		GraphInterface<Object,Object> componentsTree = bcCalc.getBiConnectedComponentsTree();
		
		testTennisBatComponents(componentsTree);
	}

	private void testTennisBatComponents(GraphInterface<Object,Object> componentsTree) {
		assertEquals(5, componentsTree.getNumberOfVertices());
		assertEquals(4, componentsTree.getNumberOfEdges());
		
		assertTrue(componentsTree.isVertex(Index.valueOf(1)));
		assertTrue(componentsTree.isVertex(Index.valueOf(2)));
		
		for (int i=0; i<tennis_bat_components.length; i++){
			GraphInterface<Index,BasicVertexInfo> cc = new GraphAsHashMap<Index,BasicVertexInfo>();
			int[] component = tennis_bat_components[i];
			for (int j=0; j<component.length; j++){
				cc.addVertex(Index.valueOf(component[j]));
			}
			for (int v = 0; v < component.length; v++)
	            for (int u = 0; u < component.length; u++)
	                if (component[u] != component[v] && (
	                		!(component[v]==3 && component[u]==5) &&
	                		!(component[v]==5 && component[u]==3)))
	                {
	                	if (!cc.isEdge(Index.valueOf(component[u]), Index.valueOf(component[v])))
	                		cc.addEdge(Index.valueOf(component[u]), Index.valueOf(component[v]));
	                }
			
			assertTrue(componentsTree.isVertex(cc));
			
			switch (i){
			case 0: case 2:
						assertEquals(1, componentsTree.getDegree(cc));
						break;
			case 1: 	
						assertEquals(2, componentsTree.getDegree(cc));
						break;
			}
		}
	}
	
	public void testConnectedComponentsTreeCrossedCircle()
	{
		BCCAlgorithm eBcc = new BCCAlgorithm(g_crossedCircle);
		BetweennessCalculator bcCalc = new BetweennessCalculator(eBcc);
        bcCalc.runBCCAlgorithm();
		GraphInterface<Object,Object> componentsTree = bcCalc.getBiConnectedComponentsTree();
		
		testCrossedCircleComponents(componentsTree);
	}
	
	private void testCrossedCircleComponents(GraphInterface<Object,Object> componentsTree){
		GraphInterface<Index,BasicVertexInfo> cc = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int i = 0; i < 7; i++)
			cc.addVertex(Index.valueOf(i));
		
		cc.addEdge(Index.valueOf(0), Index.valueOf(1));
		cc.addEdge(Index.valueOf(0), Index.valueOf(6));
		cc.addEdge(Index.valueOf(5), Index.valueOf(6));
		cc.addEdge(Index.valueOf(4), Index.valueOf(5));
		cc.addEdge(Index.valueOf(3), Index.valueOf(4));
		cc.addEdge(Index.valueOf(2), Index.valueOf(3));
		cc.addEdge(Index.valueOf(0), Index.valueOf(3));
		cc.addEdge(Index.valueOf(1), Index.valueOf(2));
		assertTrue(componentsTree.isVertex(cc));
		
		assertEquals(1, componentsTree.getNumberOfVertices());
		assertEquals(0, componentsTree.getNumberOfEdges());
	}
}
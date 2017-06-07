package tests.rbc;

import javolution.util.FastList;
import javolution.util.Index;
import algorithms.centralityAlgorithms.rbc.GRBCAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

public class GRBCTestNG extends VRBCTestNG {

	public void setUp(){
		
		super.setUp();
		
		g_line.setDeltaGroups(new int[][][]{{{0},{0},{0}}, {{0},{2,6},{6}}, {{0},{6,2},{6}}, {{0},{-1},{6}}});
		g_line.setDeltaValues(new double[]{1, 1, 1, 0});
		
		g_line.setSourceDependencyGroups(new int[][][]{ {{0},{0,1,1}},{{0},{1,2,5}} });
		g_line.setSourceDependencyValues(new double[]{6, 6});
		
		g_line.setTargetDependencyGroups(new int[][][]{ {{0,0,1},{1}},{{1,2},{5}} });
		g_line.setTargetDependencyValues(new double[]{6, 3});
		
		g_line.setBetweennessGroups(new int[][]{{0}, {1}, {2}, {3}, {0,0,1,1}, {1,5}, {0,0,0}});
		g_line.setBetweennessValues(new double[]{12, 22, 28, 30, 22, 36, 12});
	}
	
	public void testDelta() throws Exception{
		AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	GRBCAlgorithm grbcAlg = new GRBCAlgorithm(g_line, cw);
    	for (int i=0; i<g_line.getDeltaValues().length; i++){
    		int[][] group = g_line.getDeltaGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		Index v2 = Index.valueOf(group[2][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[1].length; j++){
    			int u = group[1][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = grbcAlg.getDelta(v1, seq, v2);
    		assertEquals(formatter.format(g_line.getDeltaValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	grbcAlg = new GRBCAlgorithm(g_middleV, cw);
    	for (int i=0; i<g_middleV.getDeltaValues().length; i++){
    		int[][] group = g_middleV.getDeltaGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		Index v2 = Index.valueOf(group[0][2]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][1]));
    		double actual = grbcAlg.getDelta(v1, seq, v2);
    		assertEquals(formatter.format(g_middleV.getDeltaValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	grbcAlg = new GRBCAlgorithm(g_crossedCircle, cw);
    	for (int i=0; i<g_crossedCircle.getDeltaValues().length; i++){
    		int[][] group = g_crossedCircle.getDeltaGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		Index v2 = Index.valueOf(group[0][2]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][1]));
    		double actual = grbcAlg.getDelta(v1, seq, v2);
    		assertEquals(formatter.format(g_crossedCircle.getDeltaValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testSourceDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	GRBCAlgorithm grbcAlg = new GRBCAlgorithm(g_line, cw);
    	for (int i=0; i<g_line.getSourceDependencyValues().length; i++){
    		int[][] group = g_line.getSourceDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[1].length; j++){
    			int u = group[1][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = grbcAlg.getSourceDependency(v1, seq);
    		assertEquals(formatter.format(g_line.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	grbcAlg = new GRBCAlgorithm(g_middleV, cw);
    	for (int i=0; i<g_middleV.getSourceDependencyValues().length; i++){
    		int[][] group = g_middleV.getSourceDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][1]));
    		double actual = grbcAlg.getSourceDependency(v1, seq);
    		assertEquals(formatter.format(g_middleV.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	grbcAlg = new GRBCAlgorithm(g_crossedCircle, cw);
    	for (int i=0; i<g_crossedCircle.getSourceDependencyValues().length; i++){
    		int[][] group = g_crossedCircle.getSourceDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][1]));
    		double actual = grbcAlg.getSourceDependency(v1, seq);
    		assertEquals(formatter.format(g_crossedCircle.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testTargetDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	GRBCAlgorithm grbcAlg = new GRBCAlgorithm(g_line, cw);
    	for (int i=0; i<g_line.getTargetDependencyValues().length; i++){
    		int[][] group = g_line.getTargetDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[1][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[0].length; j++){
    			int u = group[0][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = grbcAlg.getTargetDependency(seq, v1);
    		assertEquals(formatter.format(g_line.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	grbcAlg = new GRBCAlgorithm(g_middleV, cw);
    	for (int i=0; i<g_middleV.getTargetDependencyValues().length; i++){
    		int[][] group = g_middleV.getTargetDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][1]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][0]));
    		double actual = grbcAlg.getTargetDependency(seq, v1);
    		assertEquals(formatter.format(g_middleV.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	grbcAlg = new GRBCAlgorithm(g_crossedCircle, cw);
    	for (int i=0; i<g_crossedCircle.getTargetDependencyValues().length; i++){
    		int[][] group = g_crossedCircle.getTargetDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][1]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][0]));
    		double actual = grbcAlg.getTargetDependency(seq, v1);
    		assertEquals(formatter.format(g_crossedCircle.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testBetweenness() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	GRBCAlgorithm grbcAlg = new GRBCAlgorithm(g_line, cw);
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int[] group = g_line.getBetweennessGroups()[i];
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group.length; j++){
    			int u = group[j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = grbcAlg.getBetweeness(seq);
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	grbcAlg = new GRBCAlgorithm(g_middleV, cw);
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		int v = g_middleV.getBetweennessGroups()[i][0];
    		FastList<Index> seq = new FastList<Index>();
    		seq.add(Index.valueOf(v));
    		double actual = grbcAlg.getBetweeness(seq);
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	grbcAlg = new GRBCAlgorithm(g_crossedCircle, cw);
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		int v = g_crossedCircle.getBetweennessGroups()[i][0];
    		FastList<Index> seq = new FastList<Index>();
    		seq.add(Index.valueOf(v));
    		double actual = grbcAlg.getBetweeness(seq);
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    	}
    }
/**
 * def fillGroupTestGraphs(l):
    testGraph=TestGraph()
    testGraph.name = "line"
    testGraph.G = topology.graph.GraphAsMatrix()
    for v in range(7):
        testGraph.G.addVertex(v)
        for v in range(6):
            testGraph.G.addEdge(v,v+1)
    testGraph.delta={(0,0,0):1,(0,(2,6),6):1,(0,(6,2),6):1,(0,(),6):0}
    testGraph.sourceDependency={(0,frozenset((0,1,1)),Bullet):6,(0,frozenset((1,2,5)),Bullet):6}
    testGraph.targetDependency={(Bullet,frozenset((0,0,1)),1):6,(Bullet,frozenset((1,2)),5):3}
    testGraph.betweenness={(Bullet,frozenset((0,0,1,1)),Bullet):22,(Bullet,frozenset((1,5)),Bullet):36,(Bullet,frozenset((0,0,0)),Bullet):12}
    l.append(testGraph)
    pass
    
 * class GRBTest(VRBTest):
    
    def setUp(self):
        self._testGraphs=[]
        fillTestGraphs(self._testGraphs)
        fillGroupTestGraphs(self._testGraphs)
        self._testedClass =GRBC

 */
}

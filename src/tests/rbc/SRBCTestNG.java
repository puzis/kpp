package tests.rbc;

import javolution.util.FastList;
import javolution.util.Index;
import algorithms.centralityAlgorithms.rbc.SRBCAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

import common.ShadowedHistoriedCacheArr;

public class SRBCTestNG extends VRBCTestNG {

	public void setUp(){
		super.setUp();
		
		g_line.setDeltaGroups(new int[][][]{{{0},{-1},{1}}, {{0},{-1},{0}}, {{0},{0,0,0},{0}}, {{0},{2,6},{6}}, {{0},{6,2},{6}}});
		g_line.setDeltaValues(new double[]{1, 1, 1, 1, 0});
		g_line.setSourceDependencyGroups(new int[][][]{ {{0},{0,1,1}},{{0},{1,2,5}} });
		g_line.setSourceDependencyValues(new double[]{6, 2});
		g_line.setTargetDependencyGroups(new int[][][]{ {{0,0,1},{1}},{{1,2},{5}} });
		g_line.setTargetDependencyValues(new double[]{1, 2});
		g_line.setBetweennessGroups(new int[][]{{0,0,1,1}, {1,5}, {0,0,0}});
		g_line.setBetweennessValues(new double[]{6, 4, 12});
		
		g_crossedCircle.setDeltaGroups(new int[][][]{ {{1},{0,5},{6}}, {{1},{0,4},{6}},  {{1},{2,3},{4}}, {{1},{0,4},{4}}, {{1},{0,5},{5}}, {{1},{0,5},{4}} });
		g_crossedCircle.setDeltaValues(new double[] {0.50, 0,  0.25, 0.5, 0.5, 0.5});
		g_crossedCircle.setSourceDependencyGroups(new int[][][]{ {{1},{0,5}},{{1},{0,4}}, {{1},{2,3}} });
		g_crossedCircle.setSourceDependencyValues(new double[] {1.50, 0.5, 1.25});
		g_crossedCircle.setTargetDependencyGroups(new int[][][]{ {{0,5},{6}}, {{0,4},{6}}, {{2,3},{4}} });
		g_crossedCircle.setTargetDependencyValues(new double []{1.50, 0, 0.75});
	}
	
	public void testDelta() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	SRBCAlgorithm srbcAlg = new SRBCAlgorithm(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
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
    		double actual = srbcAlg.getDelta(v1, seq, v2);
    		assertEquals(formatter.format(g_line.getDeltaValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	srbcAlg = new SRBCAlgorithm(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getDeltaValues().length; i++){
    		int[][] group = g_middleV.getDeltaGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		Index v2 = Index.valueOf(group[0][2]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][1]));
    		double actual = srbcAlg.getDelta(v1, seq, v2);
    		assertEquals(formatter.format(g_middleV.getDeltaValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	srbcAlg = new SRBCAlgorithm(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getDeltaValues().length; i++){
    		int[][] group = g_crossedCircle.getDeltaGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		Index v2 = Index.valueOf(group[2][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[1].length; j++){
    			int u = group[1][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = srbcAlg.getDelta(v1, seq, v2);
    		assertEquals(formatter.format(g_crossedCircle.getDeltaValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testSourceDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	SRBCAlgorithm srbcAlg = new SRBCAlgorithm(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getSourceDependencyValues().length; i++){
    		int[][] group = g_line.getSourceDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[1].length; j++){
    			int u = group[1][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = srbcAlg.getSourceDependency(v1, seq);
    		assertEquals(formatter.format(g_line.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	srbcAlg = new SRBCAlgorithm(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getSourceDependencyValues().length; i++){
    		int[][] group = g_middleV.getSourceDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][1]));
    		double actual = srbcAlg.getSourceDependency(v1, seq);
    		assertEquals(formatter.format(g_middleV.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	srbcAlg = new SRBCAlgorithm(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getSourceDependencyValues().length; i++){
    		int[][] group = g_crossedCircle.getSourceDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[1].length; j++){
    			int u = group[1][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = srbcAlg.getSourceDependency(v1, seq);
    		assertEquals(formatter.format(g_crossedCircle.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testTargetDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	SRBCAlgorithm srbcAlg = new SRBCAlgorithm(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getTargetDependencyValues().length; i++){
    		int[][] group = g_line.getTargetDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[1][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[0].length; j++){
    			int u = group[0][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = srbcAlg.getTargetDependency(seq, v1);
    		assertEquals(formatter.format(g_line.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	srbcAlg = new SRBCAlgorithm(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getTargetDependencyValues().length; i++){
    		int[][] group = g_middleV.getTargetDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][1]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][0]));
    		double actual = srbcAlg.getTargetDependency(seq, v1);
    		assertEquals(formatter.format(g_middleV.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	srbcAlg = new SRBCAlgorithm(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getTargetDependencyValues().length; i++){
    		int[][] group = g_crossedCircle.getTargetDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[1][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[0].length; j++){
    			int u = group[0][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = srbcAlg.getTargetDependency(seq, v1);
    		assertEquals(formatter.format(g_crossedCircle.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testBetweenness() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	SRBCAlgorithm srbcAlg = new SRBCAlgorithm(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int[] group = g_line.getBetweennessGroups()[i];
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group.length; j++){
    			int u = group[j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = srbcAlg.getBetweeness(seq);
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	srbcAlg = new SRBCAlgorithm(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		int v = g_middleV.getBetweennessGroups()[i][0];
    		FastList<Index> seq = new FastList<Index>();
    		seq.add(Index.valueOf(v));
    		double actual = srbcAlg.getBetweeness(seq);
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	srbcAlg = new SRBCAlgorithm(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		int[] group = g_crossedCircle.getBetweennessGroups()[i];
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group.length; j++){
    			int u = group[j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = srbcAlg.getBetweeness(seq);
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    	}
    }
/**
 * def fillSequenceTestGraphs(l):
    testGraph=TestGraph()
    testGraph.name = "line"
    testGraph.G = topology.graph.GraphAsMatrix()
    for v in range(7):
        testGraph.G.addVertex(v)
        for v in range(6):
            testGraph.G.addEdge(v,v+1)
    testGraph.delta={(0,(),1):1,(0,(),0):1,(0,(0,0,0),0):1,(0,(2,6),6):1,(0,(6,2),6):0}
    testGraph.sourceDependency={(0,(0,1,1),Bullet):6,(0,(1,2,5),Bullet):2}
    testGraph.targetDependency={(Bullet,(0,0,1),1):1,(Bullet,(1,2),5):2}
    testGraph.betweenness={(Bullet,(0,0,1,1),Bullet):6,(Bullet,(1,5),Bullet):4,(Bullet,(0,0,0),Bullet):12}
    l.append(testGraph)

    
    testGraph=TestGraph()
    testGraph.name = "tennis bat"
    testGraph.G = topology.graph.GraphAsMatrix()
    for v in range(7):
        testGraph.G.addVertex(v)
    for v in range(6):
        testGraph.G.addEdge(v,v+1)
    testGraph.G.addEdge(0,5)
    testGraph.G.addEdge(2,5)
    testGraph.delta=           {(1,(0,5),6)     :0.50,(1,(0,4),6)     :0,  (1,(2,3),4)     :0.25, (1,(0,4),4):0.5, (1,(0,5),5):0.5, (1,(0,5),4):0.5}
    testGraph.sourceDependency={(1,(0,5),Bullet):1.50,(1,(0,4),Bullet):0.5,(1,(2,3),Bullet):1.25}
    testGraph.targetDependency={(Bullet,(0,5),6):1.50,(Bullet,(0,4),6):0,  (Bullet,(2,3),4):0.75}
    testGraph.betweenness={}
    l.append(testGraph)    
    
    pass

 * class SRBTest(VRBTest):
    
    def setUp(self):
        self._testGraphs=[]
        fillTestGraphs(self._testGraphs)
        fillSequenceTestGraphs(self._testGraphs)
        self._testedClass =SRBC
        pass

 */
}

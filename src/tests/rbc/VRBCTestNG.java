package tests.rbc;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.Index;
import junit.framework.TestCase;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.rbc.VRBCAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

public class VRBCTestNG extends TestCase {
	protected TestGraph g_line, g_middleV, g_crossedCircle;
	protected NumberFormat formatter = new DecimalFormat("0.000");
	
    public void setUp(){
        g_line = new TestGraph();
        for (int v = 0; v < 7; v++)
            g_line.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
            g_line.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        
        g_line.setDeltaGroups(new int[][][]{{{0,0,0}}, {{0,0,6}}, {{0,6,6}}, {{0,2,6}}, {{0,6,2}}});
        g_line.setDeltaValues(new double[]{1,1,1,1,0});
        g_line.setSourceDependencyGroups(new int[][][]{{{0,1}}, {{0,2}}});
        g_line.setSourceDependencyValues(new double[]{6,5});
        g_line.setTargetDependencyGroups(new int[][][]{{{4,6}}, {{0,1}}});
        g_line.setTargetDependencyValues(new double[]{5,1});
        g_line.setBetweennessGroups(new int[][]{{0}, {1}, {2}, {3}});
        g_line.setBetweennessValues(new double[]{12, 22, 28, 30});
        
        g_middleV = new TestGraph();
        for (int v = 0; v < 7; v++)
	    	g_middleV.addVertex(Index.valueOf(v), new VertexInfo());
	    for (int v = 0; v < 6; v++){
	    	g_middleV.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
	    }
	    g_middleV.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>()); 
	    g_middleV.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
	
	    g_middleV.setDeltaGroups(new int[][][]{{{0,0,0}}, {{0,0,6}}, {{0,6,6}}, {{0,2,6}}, {{0,6,2}}});
	    g_middleV.setDeltaValues(new double[]{1,1,1,1,0});
	    g_middleV.setSourceDependencyGroups(new int[][][]{{{0,1}}, {{0,2}}});
	    g_middleV.setSourceDependencyValues(new double[]{1,5});
	    g_middleV.setTargetDependencyGroups(new int[][][]{{{4,6}}, {{0,1}}});
	    g_middleV.setTargetDependencyValues(new double[]{5,1});
	    g_middleV.setBetweennessGroups(new int[][]{{0}, {1}, {2}, {3}});
	    g_middleV.setBetweennessValues(new double[]{12, 12, 28, 30});
	    
    	g_crossedCircle = new TestGraph();
		for(int i = 0; i < 7; i++)
			g_crossedCircle.addVertex(Index.valueOf(i));
		for(int i = 0; i < 6; i++)
			g_crossedCircle.addEdge(Index.valueOf(i), Index.valueOf(i+1), new EdgeInfo<Index,BasicVertexInfo>());
		g_crossedCircle.addEdge(Index.valueOf(0), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
		g_crossedCircle.addEdge(Index.valueOf(2), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());

		g_crossedCircle.setDeltaGroups(new int[][][]{{{0,0,0}}, {{0,0,6}}, {{0,6,6}}, {{1,4,6}}, {{1,2,6}}, {{1,5,6}}, {{1,2,4}}, {{1,5,4}}});
		g_crossedCircle.setDeltaValues(new double[]{1, 1, 1, 0, 0.5, 1, 0.5, 0.75});
		g_crossedCircle.setSourceDependencyGroups(new int[][][]{{{1,4}}, {{1,3}}, {{1,2}}, {{5,6}}, {{5,2}}, {{2,5}}, {{6,5}}});
		g_crossedCircle.setSourceDependencyValues(new double[]{1, 1.25, 3.50, 1, 2, 3, 6});
		g_crossedCircle.setTargetDependencyGroups(new int[][][]{{{4,1}}, {{3,1}}, {{2,1}}, {{6,5}}, {{2,5}}, {{5,2}}, {{5,6}}});
		g_crossedCircle.setTargetDependencyValues(new double[]{1, 1.50, 3.75, 1, 2, 3, 6});
		g_crossedCircle.setBetweennessGroups(new int[][]{{6}, {5}, {2}});
		g_crossedCircle.setBetweennessValues(new double[]{12, 28.5, 20.5});
    }
    
    public void testDelta() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	VRBCAlgorithm vrbcAlg = new VRBCAlgorithm(g_line, cw);
    	for (int i=0; i<g_line.getDeltaValues().length; i++){
    		int[] group = g_line.getDeltaGroups()[i][0];
    		double actual = vrbcAlg.getDelta(Index.valueOf(group[0]), Index.valueOf(group[1]), Index.valueOf(group[2]));
    		assertEquals(formatter.format(g_line.getDeltaValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new VRBCAlgorithm(g_middleV, cw);
    	for (int i=0; i<g_middleV.getDeltaValues().length; i++){
    		int[] group = g_middleV.getDeltaGroups()[i][0];
    		double actual = vrbcAlg.getDelta(Index.valueOf(group[0]), Index.valueOf(group[1]), Index.valueOf(group[2]));
    		assertEquals(formatter.format(g_middleV.getDeltaValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new VRBCAlgorithm(g_crossedCircle, cw);
    	for (int i=0; i<g_crossedCircle.getDeltaValues().length; i++){
    		int[] group = g_crossedCircle.getDeltaGroups()[i][0];
    		double actual = vrbcAlg.getDelta(Index.valueOf(group[0]), Index.valueOf(group[1]), Index.valueOf(group[2]));
    		assertEquals(formatter.format(g_crossedCircle.getDeltaValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testSourceDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	VRBCAlgorithm vrbcAlg = new VRBCAlgorithm(g_line, cw);
    	for (int i=0; i<g_line.getSourceDependencyValues().length; i++){
    		int[] group = g_line.getSourceDependencyGroups()[i][0];
    		double actual = vrbcAlg.getSourceDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_line.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new VRBCAlgorithm(g_middleV, cw);
    	for (int i=0; i<g_middleV.getSourceDependencyValues().length; i++){
    		int[] group = g_middleV.getSourceDependencyGroups()[i][0];
    		double actual = vrbcAlg.getSourceDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_middleV.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new VRBCAlgorithm(g_crossedCircle, cw);
    	for (int i=0; i<g_crossedCircle.getSourceDependencyValues().length; i++){
    		int[] group = g_crossedCircle.getSourceDependencyGroups()[i][0];
    		double actual = vrbcAlg.getSourceDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_crossedCircle.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testTargetDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	VRBCAlgorithm vrbcAlg = new VRBCAlgorithm(g_line, cw);
    	for (int i=0; i<g_line.getTargetDependencyValues().length; i++){
    		int[] group = g_line.getTargetDependencyGroups()[i][0];
    		double actual = vrbcAlg.getTargetDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_line.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new VRBCAlgorithm(g_middleV, cw);
    	for (int i=0; i<g_middleV.getTargetDependencyValues().length; i++){
    		int[] group = g_middleV.getTargetDependencyGroups()[i][0];
    		double actual = vrbcAlg.getTargetDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_middleV.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new VRBCAlgorithm(g_crossedCircle, cw);
    	for (int i=0; i<g_crossedCircle.getTargetDependencyValues().length; i++){
    		int[] group = g_crossedCircle.getTargetDependencyGroups()[i][0];
    		double actual = vrbcAlg.getTargetDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_crossedCircle.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testBetweenness() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	VRBCAlgorithm vrbcAlg = new VRBCAlgorithm(g_line, cw);
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int v = g_line.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new VRBCAlgorithm(g_middleV, cw);
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		int v = g_middleV.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new VRBCAlgorithm(g_crossedCircle, cw);
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		int v = g_crossedCircle.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    	}
    }
/**
 * 
 *  
    
class VRBTest(unittest.TestCase):
    
    def setUp(self):
        self._testGraphs=[]
        fillTestGraphs(self._testGraphs)
        self._testedClass = VRBC

    def _test_function(self,testee,testbase):
        for testinput in testbase:
            self.assertAlmostEqual(testee(*filter(lambda x:x!=Bullet,testinput)),testbase[testinput])

    def test_instanciation(self):
        G = createRandomBAGraph(10,2)
        rbcAlg = self._testedClass(G)
        G = createRandomGraph(10,1)
        rbcAlg = self._testedClass(G)
        pass
        
    def test_delta(self):
        for g in self._testGraphs:
            assert isinstance(g,TestGraph)
            rbcAlg = self._testedClass(g.G)
            self._test_function(rbcAlg.getDelta,g.delta)
        pass

    def test_sourceDependency(self):
        for g in self._testGraphs:
            assert isinstance(g,TestGraph)
            rbcAlg = self._testedClass(g.G)
            self._test_function(rbcAlg.getSourceDependency,g.sourceDependency)
        pass

    def test_targetDependency(self):
        for g in self._testGraphs:
            assert isinstance(g,TestGraph)
            rbcAlg = self._testedClass(g.G)
            self._test_function(rbcAlg.getTargetDependency,g.targetDependency)
        pass
    
    def test_betweenness(self):
        for g in self._testGraphs:
            assert isinstance(g,TestGraph)
            rbcAlg = self._testedClass(g.G)
            self._test_function(rbcAlg.getBetweenness,g.betweenness)
        pass

 */
}

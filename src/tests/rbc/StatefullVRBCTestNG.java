package tests.rbc;

import javolution.util.Index;
import algorithms.centralityAlgorithms.rbc.StatefullVRBCAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

import common.ShadowedHistoriedCacheArr;

public class StatefullVRBCTestNG extends VRBCTestNG {

	public void setUp(){
		super.setUp();
	}
	
	public void testDelta() throws Exception{
		AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	StatefullVRBCAlgorithm vrbcAlg = new StatefullVRBCAlgorithm(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getDeltaValues().length; i++){
    		int[] group = g_line.getDeltaGroups()[i][0];
    		double actual = vrbcAlg.getDelta(Index.valueOf(group[0]), Index.valueOf(group[1]), Index.valueOf(group[2]));
    		assertEquals(formatter.format(g_line.getDeltaValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new StatefullVRBCAlgorithm(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getDeltaValues().length; i++){
    		int[] group = g_middleV.getDeltaGroups()[i][0];
    		double actual = vrbcAlg.getDelta(Index.valueOf(group[0]), Index.valueOf(group[1]), Index.valueOf(group[2]));
    		assertEquals(formatter.format(g_middleV.getDeltaValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new StatefullVRBCAlgorithm(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getDeltaValues().length; i++){
    		int[] group = g_crossedCircle.getDeltaGroups()[i][0];
    		double actual = vrbcAlg.getDelta(Index.valueOf(group[0]), Index.valueOf(group[1]), Index.valueOf(group[2]));
    		assertEquals(formatter.format(g_crossedCircle.getDeltaValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testSourceDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	StatefullVRBCAlgorithm vrbcAlg = new StatefullVRBCAlgorithm(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getSourceDependencyValues().length; i++){
    		int[] group = g_line.getSourceDependencyGroups()[i][0];
    		double actual = vrbcAlg.getSourceDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_line.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new StatefullVRBCAlgorithm(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getSourceDependencyValues().length; i++){
    		int[] group = g_middleV.getSourceDependencyGroups()[i][0];
    		double actual = vrbcAlg.getSourceDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_middleV.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new StatefullVRBCAlgorithm(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getSourceDependencyValues().length; i++){
    		int[] group = g_crossedCircle.getSourceDependencyGroups()[i][0];
    		double actual = vrbcAlg.getSourceDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_crossedCircle.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testTargetDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	StatefullVRBCAlgorithm vrbcAlg = new StatefullVRBCAlgorithm(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getTargetDependencyValues().length; i++){
    		int[] group = g_line.getTargetDependencyGroups()[i][0];
    		double actual = vrbcAlg.getTargetDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_line.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new StatefullVRBCAlgorithm(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getTargetDependencyValues().length; i++){
    		int[] group = g_middleV.getTargetDependencyGroups()[i][0];
    		double actual = vrbcAlg.getTargetDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_middleV.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new StatefullVRBCAlgorithm(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getTargetDependencyValues().length; i++){
    		int[] group = g_crossedCircle.getTargetDependencyGroups()[i][0];
    		double actual = vrbcAlg.getTargetDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_crossedCircle.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testBetweenness() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	StatefullVRBCAlgorithm vrbcAlg = new StatefullVRBCAlgorithm(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int v = g_line.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new StatefullVRBCAlgorithm(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		int v = g_middleV.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new StatefullVRBCAlgorithm(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		int v = g_crossedCircle.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    	}
    }
}
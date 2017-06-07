package tests.rbc;

import javolution.util.Index;
import algorithms.centralityAlgorithms.rbc.ContributionVRBC;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

import common.ShadowedHistoriedCacheArr;

public class ContributionVRBCTestNG extends VRBCTestNG {

	public void setUp(){
		super.setUp();
	}
	
	public void testDelta() throws Exception{
		AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	ContributionVRBC vrbcAlg = new ContributionVRBC(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getDeltaValues().length; i++){
    		int[] group = g_line.getDeltaGroups()[i][0];
    		double actual = vrbcAlg.getDelta(Index.valueOf(group[0]), Index.valueOf(group[1]), Index.valueOf(group[2]));
    		assertEquals(formatter.format(g_line.getDeltaValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getDeltaValues().length; i++){
    		int[] group = g_middleV.getDeltaGroups()[i][0];
    		double actual = vrbcAlg.getDelta(Index.valueOf(group[0]), Index.valueOf(group[1]), Index.valueOf(group[2]));
    		assertEquals(formatter.format(g_middleV.getDeltaValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getDeltaValues().length; i++){
    		int[] group = g_crossedCircle.getDeltaGroups()[i][0];
    		double actual = vrbcAlg.getDelta(Index.valueOf(group[0]), Index.valueOf(group[1]), Index.valueOf(group[2]));
    		assertEquals(formatter.format(g_crossedCircle.getDeltaValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testSourceDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	ContributionVRBC vrbcAlg = new ContributionVRBC(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getSourceDependencyValues().length; i++){
    		int[] group = g_line.getSourceDependencyGroups()[i][0];
    		double actual = vrbcAlg.getSourceDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_line.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getSourceDependencyValues().length; i++){
    		int[] group = g_middleV.getSourceDependencyGroups()[i][0];
    		double actual = vrbcAlg.getSourceDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_middleV.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getSourceDependencyValues().length; i++){
    		int[] group = g_crossedCircle.getSourceDependencyGroups()[i][0];
    		double actual = vrbcAlg.getSourceDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_crossedCircle.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testTargetDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	ContributionVRBC vrbcAlg = new ContributionVRBC(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getTargetDependencyValues().length; i++){
    		int[] group = g_line.getTargetDependencyGroups()[i][0];
    		double actual = vrbcAlg.getTargetDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_line.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getTargetDependencyValues().length; i++){
    		int[] group = g_middleV.getTargetDependencyGroups()[i][0];
    		double actual = vrbcAlg.getTargetDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_middleV.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getTargetDependencyValues().length; i++){
    		int[] group = g_crossedCircle.getTargetDependencyGroups()[i][0];
    		double actual = vrbcAlg.getTargetDependency(Index.valueOf(group[0]), Index.valueOf(group[1]));
    		assertEquals(formatter.format(g_crossedCircle.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testBetweenness() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	ContributionVRBC vrbcAlg = new ContributionVRBC(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int v = g_line.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		int v = g_middleV.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		int v = g_crossedCircle.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testHistoryAndShadowing() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	ContributionVRBC vrbcAlg = new ContributionVRBC(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int v = g_line.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	vrbcAlg.saveState();
    	vrbcAlg.getMCache().StartShadowing();
    	//writing values to write cache while reading from read cache, changes on current write should not apply to reads
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int v = g_line.getBetweennessGroups()[i][0];
    		vrbcAlg.getMCache().put(vrbcAlg.getMDONTCARE(), v, vrbcAlg.getMDONTCARE(), 0.0);
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	vrbcAlg.getMCache().StopShadowing();
    	//changes now applied as caches swapped. should read new written values from cache.
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int v = g_line.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(0.0), formatter.format(actual));
    	}
    	
    	vrbcAlg.getMCache().StartShadowing();
    	//cleaning the cache , but 0.0 are still being read from cache.
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int v = g_line.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		vrbcAlg.getMCache().put(vrbcAlg.getMDONTCARE(), v, vrbcAlg.getMDONTCARE(), ShadowedHistoriedCacheArr.EMPTY);
    		assertEquals(formatter.format(0.0), formatter.format(actual));
    	}
    	vrbcAlg.getMCache().StopShadowing();
    	// now cache misses should occur, and recalculation of betweeness. writing the new values to cache. 0.0 is still being read
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int v = g_line.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}

    	vrbcAlg.restoreState();
    	//state of original cache restored, should be the old values.
       	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int v = g_line.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
   
       	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		int v = g_middleV.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	vrbcAlg = new ContributionVRBC(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		int v = g_crossedCircle.getBetweennessGroups()[i][0];
    		double actual = vrbcAlg.getBetweeness(Index.valueOf(v));
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    	}
    }   
}
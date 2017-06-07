package tests.rbc;

import common.ShadowedHistoriedCacheArr;

import javolution.util.FastList;
import javolution.util.Index;
import algorithms.centralityAlgorithms.rbc.FasterGRBC;
import algorithms.centralityAlgorithms.rbc.sets.DynamicRBCSet;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

public class DynamicRBCSetTestNG extends GRBCTestNG {
/**
 * class FasterGRBTest(GRBTest):    
    def setUp(self):
        self._testGraphs=[]
        fillTestGraphs(self._testGraphs)
        fillGroupTestGraphs(self._testGraphs)
        self._testedClass =FasterGRBC
        pass
 */
	public void setUp(){
		super.setUp();
	}
	
	public void testGroupCentrality(){
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	DynamicRBCSet set = null;
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int[] group = g_line.getBetweennessGroups()[i];
    		set = new DynamicRBCSet(new FasterGRBC(g_line, cw, null, ShadowedHistoriedCacheArr.CACHEID));
    		for (int j=0; j<group.length; j++){
    			int u = group[j];
    			if (u!=-1)
    				set.add(Index.valueOf(u));
    		}
    		double actual = set.getGroupCentrality();
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		set = new DynamicRBCSet(new FasterGRBC(g_middleV, cw, null, ShadowedHistoriedCacheArr.CACHEID));
        	int v = g_middleV.getBetweennessGroups()[i][0];
    		
    		set.add(Index.valueOf(v));
    		double actual = set.getGroupCentrality();
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		set = new DynamicRBCSet(new FasterGRBC(g_crossedCircle, cw, null, ShadowedHistoriedCacheArr.CACHEID));
        	int v = g_crossedCircle.getBetweennessGroups()[i][0];
    		
    		set.add(Index.valueOf(v));
    		double actual = set.getGroupCentrality();
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    	}
    }
	
	public void testVertexContribution(){
		AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	DynamicRBCSet set = null;
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int[] group = g_line.getBetweennessGroups()[i];
    		set = new DynamicRBCSet(new FasterGRBC(g_line, cw, null, ShadowedHistoriedCacheArr.CACHEID));
    		if (group.length == 1){
    			set = new DynamicRBCSet(new FasterGRBC(g_line, cw, null, ShadowedHistoriedCacheArr.CACHEID));
    			double actual = set.getContribution(Index.valueOf(group[0]));
        		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	 	}
    		else{
    			for (int j=0; j<group.length; j++){
        			int u = group[j];
        			set.add(Index.valueOf(u));
        			double actual = set.getContribution(Index.valueOf(u));
        			assertEquals(formatter.format(0.0), formatter.format(actual));
        		}
    		}
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		set = new DynamicRBCSet(new FasterGRBC(g_middleV, cw, null, ShadowedHistoriedCacheArr.CACHEID));
        	int v = g_middleV.getBetweennessGroups()[i][0];
    		double actual = set.getContribution(Index.valueOf(v));
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    		set.add(Index.valueOf(v));
    		actual = set.getContribution(Index.valueOf(v));
    		assertEquals(formatter.format(0.0), formatter.format(actual));
    		
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		set = new DynamicRBCSet(new FasterGRBC(g_crossedCircle, cw, null, ShadowedHistoriedCacheArr.CACHEID));
        	int v = g_crossedCircle.getBetweennessGroups()[i][0];
        	double actual = set.getContribution(Index.valueOf(v));
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    		set.add(Index.valueOf(v));
    		actual = set.getContribution(Index.valueOf(v));
    		assertEquals(formatter.format(0.0), formatter.format(actual));
    	}
	}
	
	public void testGroupContribution(){
		AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	DynamicRBCSet set = null;
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int[] group = g_line.getBetweennessGroups()[i];
    		set = new DynamicRBCSet(new FasterGRBC(g_line, cw, null, ShadowedHistoriedCacheArr.CACHEID));
    		FastList<Index> newgroup = new FastList<Index>();
    		for (int j=0; j<group.length; j++){
    			int u = group[j];
    			if (u!=-1)
    				newgroup.add(Index.valueOf(u));
    		}
    		double actual = set.getContribution(newgroup);
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		set = new DynamicRBCSet(new FasterGRBC(g_middleV, cw, null, ShadowedHistoriedCacheArr.CACHEID));
        	int v = g_middleV.getBetweennessGroups()[i][0];
        	FastList<Index> newgroup = new FastList<Index>();
        	newgroup.add(Index.valueOf(v));
    		double actual = set.getContribution(newgroup);
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		set = new DynamicRBCSet(new FasterGRBC(g_crossedCircle, cw, null, ShadowedHistoriedCacheArr.CACHEID));
        	int v = g_crossedCircle.getBetweennessGroups()[i][0];
        	FastList<Index> newgroup = new FastList<Index>();
    		newgroup.add(Index.valueOf(v));
    		double actual = set.getContribution(newgroup);
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    	}
	}
}
package tests.rbc;

import javolution.util.FastList;
import javolution.util.Index;
import algorithms.centralityAlgorithms.rbc.FasterSRBC;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;

import common.ShadowedHistoriedCacheArr;

public class FasterSRBTestNG extends SRBCTestNG {
	
	public void setUp(){
		super.setUp();
	}
	
	public void testDelta() throws Exception{
		AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	FasterSRBC fsrbcAlg = new FasterSRBC(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
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
    		double actual = fsrbcAlg.getDelta(v1, seq, v2);
    		assertEquals(formatter.format(g_line.getDeltaValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	fsrbcAlg = new FasterSRBC(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getDeltaValues().length; i++){
    		int[][] group = g_middleV.getDeltaGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		Index v2 = Index.valueOf(group[0][2]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][1]));
    		double actual = fsrbcAlg.getDelta(v1, seq, v2);
    		assertEquals(formatter.format(g_middleV.getDeltaValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	fsrbcAlg = new FasterSRBC(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
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
    		double actual = fsrbcAlg.getDelta(v1, seq, v2);
    		assertEquals(formatter.format(g_crossedCircle.getDeltaValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testSourceDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	FasterSRBC fsrbcAlg = new FasterSRBC(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getSourceDependencyValues().length; i++){
    		int[][] group = g_line.getSourceDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[1].length; j++){
    			int u = group[1][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = fsrbcAlg.getSourceDependency(v1, seq);
    		assertEquals(formatter.format(g_line.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	fsrbcAlg = new FasterSRBC(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getSourceDependencyValues().length; i++){
    		int[][] group = g_middleV.getSourceDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][1]));
    		double actual = fsrbcAlg.getSourceDependency(v1, seq);
    		assertEquals(formatter.format(g_middleV.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	fsrbcAlg = new FasterSRBC(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getSourceDependencyValues().length; i++){
    		int[][] group = g_crossedCircle.getSourceDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[1].length; j++){
    			int u = group[1][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = fsrbcAlg.getSourceDependency(v1, seq);
    		assertEquals(formatter.format(g_crossedCircle.getSourceDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testTargetDependency() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	FasterSRBC fsrbcAlg = new FasterSRBC(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getTargetDependencyValues().length; i++){
    		int[][] group = g_line.getTargetDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[1][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[0].length; j++){
    			int u = group[0][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = fsrbcAlg.getTargetDependency(seq, v1);
    		assertEquals(formatter.format(g_line.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	fsrbcAlg = new FasterSRBC(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getTargetDependencyValues().length; i++){
    		int[][] group = g_middleV.getTargetDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[0][1]);
    		FastList<Index> seq = new FastList<Index>();
			seq.add(Index.valueOf(group[0][0]));
    		double actual = fsrbcAlg.getTargetDependency(seq, v1);
    		assertEquals(formatter.format(g_middleV.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	fsrbcAlg = new FasterSRBC(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getTargetDependencyValues().length; i++){
    		int[][] group = g_crossedCircle.getTargetDependencyGroups()[i];
    		Index v1 = Index.valueOf(group[1][0]);
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group[0].length; j++){
    			int u = group[0][j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = fsrbcAlg.getTargetDependency(seq, v1);
    		assertEquals(formatter.format(g_crossedCircle.getTargetDependencyValues()[i]), formatter.format(actual));
    	}
    }
    
    public void testBetweenness() throws Exception{
    	AbsTrafficMatrix cw = new DefaultTrafficMatrix(g_line.getNumberOfVertices());
    	FasterSRBC fsrbcAlg = new FasterSRBC(g_line, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_line.getBetweennessValues().length; i++){
    		int[] group = g_line.getBetweennessGroups()[i];
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group.length; j++){
    			int u = group[j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = fsrbcAlg.getBetweeness(seq);
    		assertEquals(formatter.format(g_line.getBetweennessValues()[i]), formatter.format(actual));
    	}
   
    	cw = new DefaultTrafficMatrix(g_middleV.getNumberOfVertices());
    	fsrbcAlg = new FasterSRBC(g_middleV, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_middleV.getBetweennessValues().length; i++){
    		int v = g_middleV.getBetweennessGroups()[i][0];
    		FastList<Index> seq = new FastList<Index>();
    		seq.add(Index.valueOf(v));
    		double actual = fsrbcAlg.getBetweeness(seq);
    		assertEquals(formatter.format(g_middleV.getBetweennessValues()[i]), formatter.format(actual));
    	}
    	
    	cw = new DefaultTrafficMatrix(g_crossedCircle.getNumberOfVertices());
    	fsrbcAlg = new FasterSRBC(g_crossedCircle, cw, ShadowedHistoriedCacheArr.CACHEID);
    	for (int i=0; i<g_crossedCircle.getBetweennessValues().length; i++){
    		int[] group = g_crossedCircle.getBetweennessGroups()[i];
    		FastList<Index> seq = new FastList<Index>();
    		for (int j=0; j<group.length; j++){
    			int u = group[j];
    			if (u!=-1)
    				seq.add(Index.valueOf(u));
    		}
    		double actual = fsrbcAlg.getBetweeness(seq);
    		assertEquals(formatter.format(g_crossedCircle.getBetweennessValues()[i]), formatter.format(actual));
    	}
    }
}
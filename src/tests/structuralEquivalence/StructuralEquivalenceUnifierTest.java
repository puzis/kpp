package tests.structuralEquivalence;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.structuralEquivalence.StructuralEquivalenceUnifier;

public class StructuralEquivalenceUnifierTest extends StructuralEquivalenceExtractorTest {
	
	protected NumberFormat formatter = new DecimalFormat("0.0000");
	protected double[][] m_cw1 = {
			{12.0, 9.0, 3.0, 3.0}, 
			{9.0, 12.0, 3.0, 3.0}, 
			{3.0, 3.0, 6.0, 1.0}, 
			{3.0, 3.0, 1.0, 6.0}};
	protected double[][] m_cw2 = {
			{14.0, 6.0}, 
			{6.0, 10.0}};
	protected double[][] m_cw3 = {
			{0.0, 1.0, 1.0, 1.0, 1.0, 4.0, 1.0, 1.0, 1.0}, 
			{1.0, 0.0, 1.0, 1.0, 1.0, 4.0, 1.0, 1.0, 1.0}, 
			{1.0, 1.0, 3.0, 1.0, 1.0, 4.0, 1.0, 1.0, 1.0}, 
			{1.0, 1.0, 1.0, 3.0, 1.0, 4.0, 1.0, 1.0, 1.0}, 
			{1.0, 1.0, 1.0, 1.0, 0.0, 4.0, 1.0, 1.0, 1.0}, 
			{4.0, 4.0, 4.0, 4.0, 4.0, 24.0, 4.0, 4.0, 4.0}, 
			{1.0, 1.0, 1.0, 1.0, 1.0, 4.0, 0.0, 1.0, 1.0}, 
			{1.0, 1.0, 1.0, 1.0, 1.0, 4.0, 1.0, 3.0, 1.0}, 
			{1.0, 1.0, 1.0, 1.0, 1.0, 4.0, 1.0, 1.0, 3.0}};
	protected double[][] m_cw4 = {
			{5.333333373069763, 4.0, 2.0, 2.0}, 
			{4.0,               6.0, 2.0, 2.0}, 
			{2.0,               2.0, 0.0, 1.0}, 
			{2.0,               2.0, 1.0, 0.6666666865348816}};
	protected double[][] m_cw5 = {
			{0.0, 1.0, 2.0, 1.0, 1.0, 1.0},
			{1.0, 0.666666686535, 2.0, 1.0, 1.0, 1.0},
			{2.0, 2.0, 4.0, 2.0, 2.0, 2.0},
			{1.0, 1.0, 2.0, 0.0, 1.0, 1.0},
			{1.0, 1.0, 2.0, 1.0, 0.666666686535, 1.0},
			{1.0, 1.0, 2.0, 1.0, 1.0, 0.666666686535}};
	
	public void testUnifiedCW(){
		testUnifiedCW(m_cw1, graph_1);
		testUnifiedCW(m_cw2, graph_2);
		testUnifiedCW(m_cw3, graph_3);
		testUnifiedCW(m_cw4, graph_4);
		testUnifiedCW(m_cw5, graph_5);
	}

	private void testUnifiedCW(double[][] expected, GraphInterface<Index,BasicVertexInfo> graph) {
		StructuralEquivalenceUnifier sed = new StructuralEquivalenceUnifier(graph);
		sed.run();
		
		GraphInterface<Index,BasicVertexInfo> unifiedGraph = sed.getUnifiedGraph();
		System.out.println(unifiedGraph);
		
		AbsTrafficMatrix cw;
		cw = sed.getUnifiedCW();
		testUnifiedCW(expected, cw);
	}
	
	private void testUnifiedCW(double[][] expected, AbsTrafficMatrix actual){
		for (int i=0; i<expected.length; i++){
			for (int j=0; j<expected[0].length; j++){
				assertEquals(formatter.format(expected[i][j]), formatter.format(actual.getWeight(i, j)));
			}
		}
	}
}

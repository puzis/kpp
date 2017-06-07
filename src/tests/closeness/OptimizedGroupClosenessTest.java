package tests.closeness;

import java.text.DecimalFormat;

import java.text.NumberFormat;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.DiGraphAsHashMap;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.OptimizedGreedyGroupCloseness;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;


public class OptimizedGroupClosenessTest extends TestCase {
	private GraphInterface<Index,BasicVertexInfo> m_graph;
	private GraphInterface<Index,BasicVertexInfo> m_disconnectedGraph;
    private NumberFormat formatter;
	private Index[] group;
	
    @Override
	public void setUp()
	{
             m_graph = new DiGraphAsHashMap<Index,BasicVertexInfo>();
             formatter = new DecimalFormat("0.0000");

             for (int i = 0; i < 8; i++)
                 m_graph.addVertex(Index.valueOf(i), new VertexInfo());
             m_graph.addEdge(Index.valueOf(0), Index.valueOf(1), new EdgeInfo<Index,BasicVertexInfo>());
             m_graph.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
             m_graph.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
             m_graph.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
             m_graph.addEdge(Index.valueOf(3), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
             m_graph.addEdge(Index.valueOf(5), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
             m_graph.addEdge(Index.valueOf(6), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
             m_graph.addEdge(Index.valueOf(4), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
             
             m_disconnectedGraph = new GraphAsHashMap<Index,BasicVertexInfo>();

             for (int i = 0; i < 8; i++)
            	 m_disconnectedGraph.addVertex(Index.valueOf(i), new VertexInfo());
             m_disconnectedGraph.addEdge(Index.valueOf(1), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>());
             m_disconnectedGraph.addEdge(Index.valueOf(2), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
             m_disconnectedGraph.addEdge(Index.valueOf(2), Index.valueOf(4), new EdgeInfo<Index,BasicVertexInfo>());
             m_disconnectedGraph.addEdge(Index.valueOf(3), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
             m_disconnectedGraph.addEdge(Index.valueOf(5), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
             m_disconnectedGraph.addEdge(Index.valueOf(6), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
             m_disconnectedGraph.addEdge(Index.valueOf(4), Index.valueOf(7), new EdgeInfo<Index,BasicVertexInfo>());
             
	}
    
    private void compareWithOldAlgorithm(GraphInterface<Index,BasicVertexInfo> graph, Index[] group, FormulaType formulaType) {
    	double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(formulaType, dists, null);
    	
    	IClosenessAlgorithm calg = new ClosenessAlgorithm(graph, formula, new DummyProgress(), 1);
    	double oldC = calg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
    	
    	//OptimizedGreedyGroupCloseness optimized = new OptimizedGreedyGroupCloseness(calg.getDistanceMatrix(), calg.getFormula());
    	OptimizedGreedyGroupCloseness optimized = new OptimizedGreedyGroupCloseness(graph, formula, new DummyProgress(), 1);
    	
    	for (Index n : group) {
			optimized.add(n);
		}
    	
    	double newC = optimized.getGroupCloseness();
    	System.out.println(oldC+" "+newC);
    	assertEquals(formatter.format(oldC), formatter.format(newC));
    }
    
    public void testCorrectCalculation() throws Exception {
    	
    	group = new Index[2];
        group[0] = Index.valueOf(2);
        group[1] = Index.valueOf(4);
        //compareWithOldAlgorithm(m_graph,group,new ClosenessFormula());
        compareWithOldAlgorithm(m_graph,group,FormulaType.FORMULA_TYPE_RECIPROCAL);
        //compareWithOldAlgorithm(m_graph,group,new ClosenessFormulaExponential(0.2));
        
        group = new Index[1];
        group[0] = Index.valueOf(2);
        //compareWithOldAlgorithm(m_graph,group,new ClosenessFormula());
        compareWithOldAlgorithm(m_graph,group,FormulaType.FORMULA_TYPE_RECIPROCAL);
        //compareWithOldAlgorithm(m_graph,group,new ClosenessFormulaExponential(0.2));
        
        group = new Index[1];
        group[0] = Index.valueOf(4);
        //compareWithOldAlgorithm(m_graph,group,new ClosenessFormula());
        compareWithOldAlgorithm(m_graph,group,FormulaType.FORMULA_TYPE_RECIPROCAL);
        //compareWithOldAlgorithm(m_graph,group,new ClosenessFormulaExponential(0.2));
        
        group = new Index[0];
        //compareWithOldAlgorithm(m_graph,group,new ClosenessFormula());
        compareWithOldAlgorithm(m_graph,group,FormulaType.FORMULA_TYPE_RECIPROCAL);
        //compareWithOldAlgorithm(m_graph,group,new ClosenessFormulaExponential(0.2));
        
    }
    
    public void testUnconnectedGraph() throws Exception {
        
        group = new Index[1];
        group[0] = Index.valueOf(2);
        m_graph.removeVertex(Index.valueOf(1));
        compareWithOldAlgorithm(m_disconnectedGraph,group,FormulaType.FORMULA_TYPE_RECIPROCAL);
        //compareWithOldAlgorithm(m_disconnectedGraph,group,new ClosenessFormulaExponential(0.2));
        
        // expected to fail since normal formula cant handle non connected graphs
        //compareWithOldAlgorithm(m_disconnectedGraph,group,new ClosenessFormula());
    }
        
        public void testClosenessNoVertices() throws Exception{
        	double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
        	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
        	
        	OptimizedGreedyGroupCloseness optimized = new OptimizedGreedyGroupCloseness(m_graph, formula, new DummyProgress(), 1);
        	double closeness = optimized.getGroupCloseness();
            assertEquals(0.0, closeness);
        }
}

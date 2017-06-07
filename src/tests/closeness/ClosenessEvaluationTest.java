package tests.closeness;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.DiGraphAsHashMap;
import topology.EdgeInfo;
import topology.GraphInterface;
import topology.VertexInfo;

public class ClosenessEvaluationTest extends TestCase 
{
	private GraphInterface<Index,BasicVertexInfo> m_graph;
    private NumberFormat formatter;
	private Object[] group;
    @Override
	public void setUp()
	{
             m_graph = new DiGraphAsHashMap<Index,BasicVertexInfo>();
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
             formatter = new DecimalFormat("0.0000");
             group = new Object[2];
             
	}
    
	
	private IClosenessAlgorithm createClosenessAlg(FormulaType formulaType, Object param) {
		double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(formulaType, dists, param);
		IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
		return cAlg;
	}
	
	public void testClosenessReciprocal() throws Exception
	{
		
			FormulaType formula = FormulaType.FORMULA_TYPE_RECIPROCAL;
            	
            group[0] = Index.valueOf(2);
            group[1] = Index.valueOf(4);

            IClosenessAlgorithm cAlg = createClosenessAlg(formula,null);
            double closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            //System.out.println(closeness);
            assertEquals(formatter.format(1+0.5+1+1.0/3+1.0/4+0.5), formatter.format(closeness));
            
            group = new Object[2];
            group[0] = Index.valueOf(2);
            group[1] = Index.valueOf(0);
            cAlg = createClosenessAlg(formula,null);
            closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            //System.out.println(closeness);
            assertEquals(formatter.format(1+1+3*0.5+2*1.0/3+1.0/4), formatter.format(closeness));
            
            m_graph.addEdge(Index.valueOf(2), Index.valueOf(0), new EdgeInfo<Index,BasicVertexInfo>());
            group = new Object[1];
            group[0] = Index.valueOf(2);
            cAlg = createClosenessAlg(formula,null);
            closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            //System.out.println(closeness);
            assertEquals(formatter.format(1+3*0.5+3*1.0/3+1.0/4), formatter.format(closeness));
            
            group = new Object[2];
            group[0] = Index.valueOf(2);
            group[1] = Index.valueOf(4);
            cAlg = createClosenessAlg(formula,null);
            closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            //System.out.println(closeness);
            assertEquals(formatter.format(2+ 3*0.5+2*1.0/3+1.0/4), formatter.format(closeness));
	}
	
	public void testClosenessExponential() throws Exception
	{
		
			//ClosenessFormula formula = new ClosenessFormulaExponential(0.3);
            	
            group[0] = Index.valueOf(2);
            group[1] = Index.valueOf(4);
            FormulaType formula = FormulaType.FORMULA_TYPE_EXPONENTIAL;
            Object param = new Double(0.3);
            
            IClosenessAlgorithm cAlg = createClosenessAlg(formula,param);
            double closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            //System.out.println(closeness);
            assertEquals(formatter.format(4.233), formatter.format(closeness));
            
            group = new Object[2];
            group[0] = Index.valueOf(2);
            group[1] = Index.valueOf(0);
            cAlg = createClosenessAlg(formula,param);
            closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            //System.out.println(closeness);
            assertEquals(formatter.format(5.423), formatter.format(closeness));
            
            m_graph.addEdge(Index.valueOf(2), Index.valueOf(0), new EdgeInfo<Index,BasicVertexInfo>());
            group = new Object[1];
            group[0] = Index.valueOf(2);
            cAlg = createClosenessAlg(formula,param);
            closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            //System.out.println(closeness);
            assertEquals(formatter.format(4.913), formatter.format(closeness));
            
            group = new Object[2];
            group[0] = Index.valueOf(2);
            group[1] = Index.valueOf(4);
            cAlg = createClosenessAlg(formula,param);
            closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            //System.out.println(closeness);
            assertEquals(formatter.format(5.423), formatter.format(closeness));
	}
        
        public void testClosenessNoEdges() throws Exception
	{
        	m_graph = new DiGraphAsHashMap<Index,BasicVertexInfo>();
            for (int i = 0; i < 8; i++)
                m_graph.addVertex(Index.valueOf(i), new VertexInfo());
            group = new Object[2];
            group[0] = Index.valueOf(0);
            group[1] = Index.valueOf(1);
            IClosenessAlgorithm cAlg = createClosenessAlg(FormulaType.FORMULA_TYPE_RECIPROCAL,null);
            double closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            System.out.println(closeness);
            assertEquals(formatter.format(2), formatter.format(closeness));
	}
        
        public void testClosenessNoVertices() throws Exception{
        	m_graph = new DiGraphAsHashMap<Index,BasicVertexInfo>();
        	double[][] dists = DistArrayMatrix.getShortestPathDistances(m_graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, m_graph) , new DummyProgress(), 1.0);
        	IClosenessFormula formula = FormulaFactory.createFormula(FormulaType.FORMULA_TYPE_RECIPROCAL, dists, null);
        	
        	IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_graph, formula, new DummyProgress(), 1);
            double closeness = cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 1);
            System.out.println(closeness);
            assertEquals(formatter.format(0), formatter.format(closeness));
        }
}
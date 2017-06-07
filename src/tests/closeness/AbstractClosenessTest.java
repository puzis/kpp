package tests.closeness;

import java.text.DecimalFormat;

import java.text.NumberFormat;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.DiGraphAsHashMap;
import topology.EdgeInfo;
import topology.GraphInterface;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.GroupClosenessAlgorithmMSBFS;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.OptimizedGreedyGroupCloseness;
import algorithms.centralityAlgorithms.closeness.formula.FormulaFactory;
import algorithms.centralityAlgorithms.closeness.formula.FormulaType;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import algorithms.centralityAlgorithms.dist.DistArrayMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

public abstract class AbstractClosenessTest extends TestCase{
	
	protected GraphInterface<Index,BasicVertexInfo> _graph;
    protected NumberFormat _formatter;
    protected Index[] _vertices;
    protected int _nodeCount = 8;
    @Override
	public void setUp()
	{
    		_vertices = new Index[_nodeCount];
    		for (int i=0;i<_nodeCount;i++) {
    			_vertices[i] = Index.valueOf(i);
    		}
             _graph = new DiGraphAsHashMap<Index,BasicVertexInfo>();
             for (int i = 0; i < 8; i++)
                 _graph.addVertex(Index.valueOf(i), new VertexInfo());
             _graph.addEdge(_vertices[0], _vertices[1], new EdgeInfo<Index,BasicVertexInfo>());
             _graph.addEdge(_vertices[1], _vertices[2], new EdgeInfo<Index,BasicVertexInfo>());
             _graph.addEdge(_vertices[2], _vertices[3], new EdgeInfo<Index,BasicVertexInfo>());
             _graph.addEdge(_vertices[2], _vertices[4], new EdgeInfo<Index,BasicVertexInfo>());
             _graph.addEdge(_vertices[3], _vertices[5], new EdgeInfo<Index,BasicVertexInfo>());
             _graph.addEdge(_vertices[5], _vertices[6], new EdgeInfo<Index,BasicVertexInfo>());
             _graph.addEdge(_vertices[6], _vertices[7], new EdgeInfo<Index,BasicVertexInfo>());
             _graph.addEdge(_vertices[4], _vertices[7], new EdgeInfo<Index,BasicVertexInfo>());
             _formatter = new DecimalFormat("0.0000");             
	}
    
    public GroupClosenessAlgorithmMSBFS createMSBFS(GraphInterface<Index,BasicVertexInfo> graph, FormulaType formulaType, double[] importanceVec) {
    	double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(formulaType, dists, importanceVec);
    	return new GroupClosenessAlgorithmMSBFS(graph, formula);
    }
    
    public IClosenessAlgorithm createNG(GraphInterface<Index,BasicVertexInfo> graph, FormulaType formulaType, double[] importanceVec) {
    	double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(formulaType, dists, importanceVec);
    	return new ClosenessAlgorithm(graph, formula, new DummyProgress(), 1);
    }
    
    public OptimizedGreedyGroupCloseness createOptimized(GraphInterface<Index,BasicVertexInfo> graph, FormulaType formulaType, double[] importanceVec) {
    	double[][] dists = DistArrayMatrix.getShortestPathDistances(graph, ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlgorithmInterface.DEFAULT, graph) , new DummyProgress(), 1.0);
    	IClosenessFormula formula = FormulaFactory.createFormula(formulaType, dists, importanceVec);
    	return new OptimizedGreedyGroupCloseness(graph, formula, new DummyProgress(), 1);
    }
}

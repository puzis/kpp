package algorithms.centralityAlgorithms.nfc.preprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import common.IndexFastList;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.WeightedUlrikNG;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

/**
 * @author Polina Zilberman
 *
 * version 1.0
 *
 * A set of data structures and algorithms required for betweenness
 * calculations. given: G=(V,E) - undirected unweighted graph n = |V|, m = |E|
 *
 * O(1) retrieved data: B(x) - individual vertex betweenness (with full
 * preprocessing only) d{x,y} - distance between vertices x and y sigma{x,y} -
 * number of shortest pathes between x and y delta(x,w,y) -
 * sigma{x,w}*sigma{w,y}/sigma{x,y} delta(x,w,.) - sum of delta(x,w,y) for all y
 * in V PB{x,y} - sum of delta(v,x,y,u) for all v,u in V
 *
 * {} - curly braces indicate unknown/unimportant order of arguments
 *
 * other data: AverageSigma AverageDistance PathDispersion
 */
public class DataWorkshopNFC extends algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop{

    private double m_confidence_threshold;

    public DataWorkshopNFC(double confidence_threshold) {
    	m_confidence_threshold = confidence_threshold;
    }

    public DataWorkshopNFC(int numberOfVertices, double confidence_threshold) {
        super(numberOfVertices);
        m_confidence_threshold = confidence_threshold;
    }

    /**
     * (GroupBasedAlgorithm, Graph) -> GroupBasedAlgorithm Performs
     * precomputation O(n^3 + nm) Why +nm ? may be it works on multigraphs too
     * :) impl: consider just-in-time calculation of PB values)
     *
     * @param graph
     * @param communicationWeights
     */
    public DataWorkshopNFC(ShortestPathAlg spAlg, GraphInterface<Index, BasicVertexInfo> graph, AbsTrafficMatrix communicationWeights, boolean createRoutingTable, double confidence_threshold, AbstractExecution progress, double percentage) {
        init(spAlg, graph, communicationWeights, createRoutingTable, false, confidence_threshold, progress, percentage);
    }

    public DataWorkshopNFC(ShortestPathAlg spAlg, GraphInterface<Index, BasicVertexInfo> graph, boolean createRoutingTable, double confidence_threshold, AbstractExecution progress, double percentage) {
        AbsTrafficMatrix communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); //MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
        init(spAlg, graph, communicationWeights, createRoutingTable, false, confidence_threshold, progress, percentage);
    }

    public DataWorkshopNFC(ShortestPathAlg spAlg, GraphInterface<Index, BasicVertexInfo> graph, boolean createRoutingTable, boolean fullPrecomp, double confidence_threshold, AbstractExecution progress, double percentage) {
        AbsTrafficMatrix communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); //MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
        init(spAlg, graph, communicationWeights, createRoutingTable, fullPrecomp, confidence_threshold, progress, percentage);
    }

    protected void init(ShortestPathAlg spAlg, GraphInterface<Index, BasicVertexInfo> graph, AbsTrafficMatrix communicationWeights, boolean createRoutingTable, boolean fullPrecomputation, double confidence_threshold, AbstractExecution progress, double percentage) {
        if (graph == null) {
            throw new RuntimeException("Graph is null");
        }
        m_confidence_threshold = confidence_threshold;
       
        super.init(spAlg, graph, communicationWeights, createRoutingTable, fullPrecomputation, progress, percentage);
    }

    @Override
    public double computePairBetweeness(int v1, int v2) {
        double result = 0, delta;
       	for (int s=0; s < m_numberOfVertices; s++){
       		for (int t=0; t < m_numberOfVertices; t++){
        			
       			// NFC(v1,v2) = \sum_{s,t} Min(delta_{s,t}(v1), delta_{s,t}(v2))
       			double delta_st_v1 = getDelta(s, v1, t);
       			double delta_st_v2 = getDelta(s, v2, t);
        			
       			delta = Math.min(delta_st_v1, delta_st_v2);
       			result += delta;
       		}
       	}
        m_pathBetweeness[v1][v2] = result;
        return result;
    }

    @Override
    public double getDelta(int u, int w, int v) {
        if (getDeltaBC(u,w,v)>=m_confidence_threshold){
        	return 1;
        } else {
            return 0;
        }
    }

    
    public double getDeltaBC(int u, int w, int v) {
        if (Math.abs(m_distanceMatrix[u][v] - (m_distanceMatrix[u][w] + m_distanceMatrix[w][v])) < DISTANCE_PRECISION) {
            if (m_sigma[u][w] * m_sigma[w][v] * m_sigma[u][v] == 0) {
                return (double) 0;
            }
            return m_sigma[u][w] * m_sigma[w][v] / (double) m_sigma[u][v];
        } else {
            return 0;
        }
    }


    
    public double getConfidenceThreshold(){
    	return m_confidence_threshold;
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.gbc;

import algorithms.centralityAlgorithms.betweenness.brandes.WeightedUlrikNG;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import common.ArrayUtils;
import java.util.Arrays;
import javolution.util.Index;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.GraphInterface;

/**
 *
 * @author bennyl
 */
public final class GBCPreprocessor {

    private static double DISTANCE_PRECISION = 1E-8;
    private PreprocessedData original, workingCopy;
    private int[] graphToGroupVertexMap;

    public GBCPreprocessor(GraphInterface<Index, ? extends BasicVertexInfo> graph, AbsTrafficMatrix trafficMatrix, VertexGroup group) {
        final int n = graph.getNumberOfVertices();
        
        //preprocess graph
        original = new PreprocessedData();
        initializeOriginalData(graph, trafficMatrix);

        //compact the original by keeping only the group relative information
        graphToGroupVertexMap = new int[n];
        Arrays.fill(graphToGroupVertexMap, -1);
        for (int i=0; i<group.size(); i++){
            graphToGroupVertexMap[group.getVertex(i)] = i;
        }
        original.compact(group);
        
        //create working copy
        workingCopy = original.createCopy();
    }

    /**
     * revert all changes made to this object to the original preprocessing
     * results.
     */
    public void revertAllChanges() {
        original.copyTo(workingCopy);
    }

    private double calculateDelta(int u, int w, int v) {
        if (Math.abs(original.distanceMatrix[u][v] - (original.distanceMatrix[u][w] + original.distanceMatrix[w][v])) < DISTANCE_PRECISION) {
            if (original.numShortPaths[u][w] * original.numShortPaths[w][v] * original.numShortPaths[u][v] == 0) {
                return (double) 0;
            }
            return original.numShortPaths[u][w] * original.numShortPaths[w][v] / (double) original.numShortPaths[u][v];
        } else {
            return 0;
        }
    }
    
    /**
     * return the number of shortest paths from s to t that pass through v
     * @param s
     * @param t
     * @param v
     * @return 
     */
    public int getNumberOfShortestPaths(int s, int v, int t){
        s = graphToGroupVertexMap[s];
        t = graphToGroupVertexMap[t];
        v = graphToGroupVertexMap[v];
        
        return (int) (workingCopy.numShortPaths[s][t] * getDeltaOfRelativeNodes(s, v, t));
    }
    
    /**
     * return the ratio of paths that pass from s to t through v
     * @param s
     * @param v
     * @param t
     * @return 
     */
    public double getDelta(int s, int v, int t) {
        return getDeltaOfRelativeNodes(graphToGroupVertexMap[s], graphToGroupVertexMap[v], graphToGroupVertexMap[t]);
    }
    
    private double getDeltaOfRelativeNodes(int s, int v, int t) {
        if (Math.abs(workingCopy.distanceMatrix[s][t] - (workingCopy.distanceMatrix[s][v] + workingCopy.distanceMatrix[v][t])) < DISTANCE_PRECISION) {
            if (workingCopy.numShortPaths[s][v] * workingCopy.numShortPaths[v][t] * workingCopy.numShortPaths[s][t] == 0) {
                return (double) 0;
            }
            return workingCopy.numShortPaths[s][v] * workingCopy.numShortPaths[v][t] / (double) workingCopy.numShortPaths[s][t];
        } else {
            return 0;
        }
    }

    public double getDistance(int v1, int v2) {
        return workingCopy.distanceMatrix[graphToGroupVertexMap[v1]][graphToGroupVertexMap[v2]];
    }

    public double getNumberOfShortestPaths(int v1, int v2) {
        return workingCopy.numShortPaths[graphToGroupVertexMap[v1]][graphToGroupVertexMap[v2]];
    }

    public double getPairBetweeness(int v1, int v2) {
        return workingCopy.pairPathBetweeness[graphToGroupVertexMap[v1]][graphToGroupVertexMap[v2]];
    }

    /**
     * change the preprocessed data, at anytime you can revert all the changes
     * by calling {@link revertAllChanges}
     *
     * @param v1
     * @param v2
     * @param value
     */
    public void setDistance(int v1, int v2, double value) {
        workingCopy.distanceMatrix[graphToGroupVertexMap[v1]][graphToGroupVertexMap[v2]] = value;
    }

    /**
     * change the preprocessed data, at anytime you can revert all the changes
     * by calling {@link revertAllChanges}
     *
     * @param v1
     * @param v2
     * @param value
     */
    public void setNumberOfShortestPaths(int v1, int v2, double value) {
        workingCopy.numShortPaths[graphToGroupVertexMap[v1]][graphToGroupVertexMap[v2]] = value;
    }

    /**
     * change the preprocessed data, at anytime you can revert all the changes
     * by calling {@link revertAllChanges}
     *
     * @param v1
     * @param v2
     * @param value
     */
    public void setPairBetweeness(int v1, int v2, double value) {
        workingCopy.pairPathBetweeness[graphToGroupVertexMap[v1]][graphToGroupVertexMap[v2]] = value;
    }

    private void initializeOriginalData(GraphInterface<Index, ? extends BasicVertexInfo> graph, AbsTrafficMatrix trafficMatrix) {
        //calculate distance, sigma and deltadot
        WeightedUlrikNG gb = new WeightedUlrikNG(ShortestPathAlgorithmInterface.DEFAULT, (GraphInterface) graph, trafficMatrix, true, new DummyProgress(), 0);
        gb.run();
        original.distanceMatrix = gb.getDistance();
        original.numShortPaths = gb.getSigma();
        final int n = graph.getNumberOfVertices();
        final double[][] deltaDot = gb.getDeltaDot();
        original.pairPathBetweeness = new double[n][n];
        double delta;
        //calculate pair betweeness
        for (int v1 = 0; v1 < n; v1++) {
            for (int v2 = 0; v2 < n; v2++) {
                original.pairPathBetweeness[v1][v2] = 0;
                for (int u = 0; u < n; u++) {
                    delta = calculateDelta(u, v1, v2);
                    original.pairPathBetweeness[v1][v2] += deltaDot[u][v2] * delta;
                }
            }
        }
    }

    private static final class PreprocessedData {

        double[][] distanceMatrix;
        double[][] numShortPaths;
        double[][] pairPathBetweeness;

        public void copyTo(PreprocessedData to) {
            ArrayUtils.copy(distanceMatrix, to.distanceMatrix);
            ArrayUtils.copy(numShortPaths, to.numShortPaths);
            ArrayUtils.copy(pairPathBetweeness, to.pairPathBetweeness);
        }

        /**
         * this method assumes that none of the matrixes are empty..
         *
         * @return
         */
        public PreprocessedData createCopy() {
            PreprocessedData ret = new PreprocessedData();

            ret.distanceMatrix = new double[distanceMatrix.length][distanceMatrix[0].length];
            ret.numShortPaths = new double[numShortPaths.length][numShortPaths[0].length];
            ret.pairPathBetweeness = new double[pairPathBetweeness.length][pairPathBetweeness[0].length];
            copyTo(ret);
            return ret;
        }

        /**
         * compact this preprocessed fields based on the given vertex group, the
         * computed matrixes will transformed to be relative to the group, in
         * order to use the data after the compaction you should store a map
         * from the absolute indexes to the relative indexes
         *
         * @param group
         */
        public void compact(VertexGroup group) {
            distanceMatrix = map(group, distanceMatrix);
            numShortPaths = map(group, numShortPaths);
            pairPathBetweeness = map(group, pairPathBetweeness);

        }

        private double[][] map(VertexGroup group, double[][] absoluteMatrix) {
            double[][] temp = new double[group.size()][group.size()];
            for (int i = 0; i < group.size(); i++) {
                for (int j = 0; j < group.size(); j++) {
                    temp[i][j] = absoluteMatrix[group.getVertex(i)][group.getVertex(j)];
                }
            }
            
            return temp;
        }
    }
}

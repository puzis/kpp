/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.gbc;

import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;

/**
 *
 * @author bennyl
 */
public class DeploymentGBC {

    private GBCPreprocessor preprocessor;
    private GraphInterface<Index, ? extends BasicVertexInfo> graph;
    private AbsTrafficMatrix trafficMatrix;
    private VertexGroup group;

    /**
     * 
     * @param graph
     * @param trafficMatrix
     * @param group - guaranteed not to be changed by this class. (you can pass the object without copying it..)
     * @param eagerPreprocessing 
     */
    public DeploymentGBC(GraphInterface<Index, ? extends BasicVertexInfo> graph, AbsTrafficMatrix trafficMatrix, VertexGroup group, boolean eagerPreprocessing) {
        this.graph = graph;
        this.trafficMatrix = trafficMatrix;
        this.group = group;

        if (eagerPreprocessing) {
            getPreprocessor();
        }
    }

    private GBCPreprocessor getPreprocessor() {
        if (preprocessor == null) {
            preprocessor = new GBCPreprocessor(graph, trafficMatrix, group);
        }

        return preprocessor;
    }
    
    public double calculate(VertexGroup group) {
        group.orderBySampleRates();
        
        GBCPreprocessor p = getPreprocessor();
        p.revertAllChanges();
        
        double ret = 0;
        
        for (int i = 0; i < group.size(); i++) {
            int v = group.getVertex(i);
            double sr = group.getSampleRate(i);
            ret += p.getPairBetweeness(v, v) * sr;

            for (int xi = 0; xi < group.size(); xi++) {
                for (int yi = 0; yi < group.size(); yi++) {
                    int x = group.getVertex(xi), y = group.getVertex(yi);
                    p.setNumberOfShortestPaths(x, y, p.getNumberOfShortestPaths(x, y) - p.getNumberOfShortestPaths(x, v, y));
                    updatePairBetweeness(p, x, y, v);
                }
            }
        }
        return ret;
    }

    private void updatePairBetweeness(GBCPreprocessor p, int x, int y, int v) {
        p.setPairBetweeness(x, y, p.getPairBetweeness(x, y) - p.getPairBetweeness(x, y) * p.getDelta(x, v, y));
        if (y != v) {
            p.setPairBetweeness(x, y, p.getPairBetweeness(x, y) - p.getPairBetweeness(x, v) * p.getDelta(x, y, v));
        }

        if (x != v) {
            p.setPairBetweeness(x, y, p.getPairBetweeness(x, y) - p.getPairBetweeness(v, y) * p.getDelta(v, x, y));
        }
    }
    

    private static final class IntTuple {

        int[] array;

        public IntTuple(int[] array) {
            this.array = Arrays.copyOf(array, array.length);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 73 * hash + Arrays.hashCode(this.array);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IntTuple other = (IntTuple) obj;
            if (!Arrays.equals(this.array, other.array)) {
                return false;
            }
            return true;
        }
    }
}

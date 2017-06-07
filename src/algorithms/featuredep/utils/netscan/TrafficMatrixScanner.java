/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.featuredep.utils.netscan.TrafficEstimationElement.EstimatedValueElement;
import topology.AbstractUndirectedGraph;

/**
 *
 * @author bennyl
 */
public class TrafficMatrixScanner implements TrafficEstimationElement.TrafficEstimationVisitor {

    AbsTrafficMatrix matrix;
    boolean needNormalization;

    public TrafficMatrixScanner(AbstractUndirectedGraph topology, boolean normalize) {
        matrix = new DenseTrafficMatrix(topology.getNumberOfVertices()+1);
        this.needNormalization = !normalize;
    }

    @Override
    public void visit(EstimatedValueElement e) {
        matrix.setWeight(e.getFromIndex(), e.getToIndex(), e.getEstimation());
    }

    public AbsTrafficMatrix getResultMatrix() {
        if (needNormalization) {
            double sum = 0;
            for (int i = 0; i < matrix.getDimensions(); i++) {
                for (int j = 0; j < matrix.getDimensions(); j++) {
                    sum += matrix.getWeight(i, j);
                }
            }

            for (int i = 0; i < matrix.getDimensions(); i++) {
                for (int j = 0; j < matrix.getDimensions(); j++) {
                    matrix.setWeight(i, j, matrix.getWeight(i, j) / sum);
                }
            }
            
            needNormalization = false;
        }
        
        return matrix;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep;

import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.featuredep.input.DeployableVertexInfoIfc;
import algorithms.featuredep.input.DeployableTopologyGraph;

/**
 * Taken from the suggestion document:
 *
 * Encoding protocol support and endpoint importance to TM
 *
 * The following is a definition of a modified set of Traffic matrixes T_p^'
 * ,(p∈P) that takes the importance and supported protocols of the end points
 * into consideration
 *
 * {@code
 * ∀u,v∈V,∀p∈P, T_p^'
 * [u,v]= {
 *  T[u,v]*Imp_out (v)*Imp_in | u can communicate with v using protocol p
 *  0 | otherwise
 * }
 * }
 *
 * @author bennyl
 */
public class ReducedTrafficMatrix extends AbsTrafficMatrix {

    AbsTrafficMatrix baseMatrix;
    DeployableTopologyGraph topology;
    int protocol;

    public ReducedTrafficMatrix(AbsTrafficMatrix baseMatrix, DeployableTopologyGraph topology, int protocol) {
        this.baseMatrix = baseMatrix;
        this.topology = topology;
        this.protocol = protocol;
    }

    @Override
    public double getWeight(int i, int j) {
        DeployableVertexInfoIfc ni = topology.getNode(i);
        DeployableVertexInfoIfc nj = topology.getNode(j);
        if (ni.isClientOf(protocol) && nj.isServerOf(protocol)) {
            return baseMatrix.getWeight(i, j) * (double) ni.getImportanceOut() * (double) nj.getImportanceIn();
        }

        return 0;
    }

    @Override
    public void setWeight(int i, int j, double w) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setAllWeights(double w) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void mul(double a) {
        throw new UnsupportedOperationException("Not supported.");
    }
}

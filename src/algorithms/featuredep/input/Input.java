/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input;

import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

/**
 *
 * @author bennyl
 */
public class Input {

    private DeployableTopologyGraph tgraph;
    private int supportedProtocols;
    private FeatureSet fset;
    private AbsTrafficMatrix tmatrix;
    private AttackDamageAssesmentFunction damageAssesment;

    public DeployableTopologyGraph getTopology() {
        return tgraph;
    }

    public int getNumberOfSupportedProtocols() {
        return supportedProtocols;
    }

    public void setSupportedProtocols(int supportedProtocols) {
        this.supportedProtocols = supportedProtocols;
    }

    public AttackDamageAssesmentFunction getDamageAssesment() {
        return damageAssesment;
    }

    public FeatureSet getFeatureSet() {
        return fset;
    }

    public AbsTrafficMatrix getTrafficMatrix() {
        return tmatrix;
    }

    public void setTopology(DeployableTopologyGraph tgraph) {
        this.tgraph = tgraph;
    }

    public void setFeatureSet(FeatureSet fset) {
        this.fset = fset;
    }

    public void setTrafficMatrix(AbsTrafficMatrix tmatrix) {
        this.tmatrix = tmatrix;
    }

    public void setDamageAssesment(AttackDamageAssesmentFunction damageAssesment) {
        this.damageAssesment = damageAssesment;
    }
}

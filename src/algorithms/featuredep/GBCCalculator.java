/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep;

import algorithms.featuredep.input.DeployableTopologyGraph;
import algorithms.featuredep.gbc.DeploymentGBC;
import algorithms.featuredep.gbc.VertexGroup;

/**
 *
 * @author bennyl
 */
public class GBCCalculator {

    DeploymentGBC[] gbcp;
    private final FDProblem problem;
    private VertexGroup deployables;
//    private double normalizingFactor;

    public GBCCalculator(FDProblem p) {
        final DeployableTopologyGraph topology = p.getOriginalInput().getTopology();
        final VertexGroup deployableNodes = topology.getDeployableNodes();

        gbcp = new DeploymentGBC[p.getOriginalInput().getNumberOfSupportedProtocols()];

        for (int i = 0; i < gbcp.length; i++) {
            System.out.println("Creating GBC Handler for protocol " + i);
            gbcp[i] = new DeploymentGBC(topology.getBaseTopology(), p.getTrafficMatrixForProtocol(i), deployableNodes, true);
        }

        this.problem = p;
        this.deployables = deployableNodes.clone();
//        this.normalizingFactor = Math.pow(p.getOriginalInput().getTopology().getNumberOfNodes(), 2);
    }

    /**
     * taken from the suggestion:
     *
     * {@code
     * ∀p∈P and an assignment A.
     * RBC_p (A) Is defined to be the value RBC(D,{ρ_d=Util(A[d],p) |d∈D})
     * calculated using G,R and T_p^' .
     *
     * }
     *
     * @param assignment
     * @param protocol
     * @return
     */
    public double calculate(boolean[][] assignment, int protocol) {
        double utility;
        for (int d = 0; d < deployables.size(); d++) {
            utility = problem.getOriginalInput().getFeatureSet().calcUtility(assignment[d], protocol);
            deployables.setSampleRate(d, utility);
        }

        double res = gbcp[protocol].calculate(deployables) ;/// normalizingFactor;
        return res;
    }
}
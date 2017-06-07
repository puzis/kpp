/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep;

import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.featuredep.gbc.VertexGroup.VertexAndSampleRate;
import algorithms.featuredep.input.Input;
import common.ArrayUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class FDProblem {

    private AbsTrafficMatrix[] reducedMatrixes;
    private Input in;
    private GBCCalculator gbcc;
    private Semaphore costCalculationLock = new Semaphore(1);
    public double maxPrice;

    /**
     * see suggestion document for details about reducing the input into this
     * optimization problem.
     *
     * @param input
     */
    public void reduce(Input input) {
        //store the input for later usage.
        in = input;

        //1. reduce traffic matrixes
        System.out.println("Creating traffic matrixes");
        reducedMatrixes = new AbsTrafficMatrix[input.getNumberOfSupportedProtocols()];
        for (int i = 0; i < reducedMatrixes.length; i++) {
            reducedMatrixes[i] = new ReducedTrafficMatrix(input.getTrafficMatrix(), input.getTopology(), i);
        }
        System.out.println("done");
        
        maxPrice = new Assignment(this).setAllAssignmentTo(true).calcDeploymentPrice(this);

        //2. Calculate and cache (or lazy - whatever suites) the gbc_p models
        System.out.println("generating GBC calculator");
        gbcc = new GBCCalculator(this);
        System.out.println("done");
    }

    /**
     * get the maximal price that a customer can pay.
     * @return 
     */
    public double getMaxPrice() {
        return maxPrice;
    }

    /**
     * definition of assignment (taken from the suggestion):
     *
     * {@code
     * An Assignment A∈D×{0,1}^|F| represents a single deployment of features
     * over the network.
     *
     * For later usage, the following symbols are allocated to special
     * assignments:
     *
     * A^1 is the "all true" assignment, means: ∀d∈D, A^1[d]=1^|F| A^0 is the
     * "all false" assignment, means: ∀d∈D, A^0 [d]=0^|F|
     *
     * }
     *
     * @return new matrix that can represent an assignment to this problem
     */
    public boolean[][] allocateAllFalseAssignment() {
        return new boolean[in.getTopology().getDeployableNodes().size()][in.getFeatureSet().getNumberOfFeatures()];
    }

    
    public double calcAssignmentQuality(boolean[][] rawAssignment) {
        try {
            costCalculationLock.acquire();
            double ret = 0;
            for (int p = 0; p < in.getNumberOfSupportedProtocols(); p++) {
                ret += calcDeploymentUtilityForProtocol(rawAssignment, p) * in.getDamageAssesment().assest(p);
            }
            
            return ret - calcDeploymentPrice(rawAssignment);
        } catch (InterruptedException ex) {
            Logger.getLogger(FDProblem.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
            return -1;
        } finally {
            costCalculationLock.release();
        }
    }

    public double calcDeploymentPrice(boolean[][] assignment) {
        return in.getFeatureSet().calcCost(assignment);
    }

    /**
     * calculates and return the utility of the given assignment for the given
     * protocol
     *
     * @param assignment
     * @param p
     * @return
     */
    public double calcDeploymentUtilityForProtocol(boolean[][] assignment, int p) {
        return gbcc.calculate(assignment, p);
    }


    /**
     * will allocate new assignment and write true on each of its cells.
     *
     * @see allocateAssignment
     * @return
     */
    public boolean[][] allocateAllTrueAssignment() {
        boolean[][] maxProtectionAssignment = allocateAllFalseAssignment();
        for (int i = 0; i < maxProtectionAssignment.length; i++) {
            Arrays.fill(maxProtectionAssignment[i], true);
        }

        return maxProtectionAssignment;
    }

    /**
     * @return the original input to the problem.
     */
    public Input getOriginalInput() {
        return in;
    }

    /**
     * @param protocol
     * @return the reduced traffic matrix that take into consideration the given
     * protocol.
     */
    public AbsTrafficMatrix getTrafficMatrixForProtocol(int protocol) {
        return reducedMatrixes[protocol];
    }

    /**
     * HELPER METHODS
     */
    public int getNumberOfDeployableNodes() {
        return getOriginalInput().getTopology().getDeployableNodes().size();
    }

    public int getNumberOfFeatures() {
        return getOriginalInput().getFeatureSet().getNumberOfFeatures();
    }

    public int getNumberOfProtocols() {
        return in.getNumberOfSupportedProtocols();
    }
    
    public Integer[] getDeploymentPointsOrderedByGBC(){
        Integer[] dp = new Integer[in.getTopology().getDeployableNodes().size()];
        for (int i=0; i<dp.length; i++) dp[i]=i;
        
        final double[] dpq = new double[in.getTopology().getDeployableNodes().size()];
        Assignment a = new Assignment(this);
        boolean[] truea = new boolean[a.getNumberOfFeatures()];
        Arrays.fill(truea, true);
        
        for (int i=0; i<a.getNumberOfDeploymentPoints(); i++){
            a.setAllAssignmentTo(false);
            a.assign(i, truea);
            dpq[i] = a.calcQuility(this);
        }
        
        Arrays.sort(dp, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return Double.compare(dpq[o1], dpq[o2]);
            }
        });
        
        return dp;
    }
}

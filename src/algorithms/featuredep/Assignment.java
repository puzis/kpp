/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep;

import algorithms.featuredep.utils.BooleanBruteForceRunner;
import common.ArrayUtils;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public class Assignment {
    boolean[][] assignment;

    public Assignment(int numberOfDeploymentNodes, int numberOfFeatures) {
        assignment = new boolean[numberOfDeploymentNodes][numberOfFeatures];
    }

    public Assignment(FDProblem problem) {
        this(problem.getNumberOfDeployableNodes(), problem.getNumberOfFeatures());
    }
    
    public Assignment setAllAssignmentTo(boolean value){
        for (int i=0; i<assignment.length; i++){
            Arrays.fill(assignment[i], value);
        }
        return this;
    }
    
    public void assign(int deployableNode, int feature, boolean value){
        assignment[deployableNode][feature] = value;
    }
    
    public double calcQuility(FDProblem problem){        
        return problem.calcAssignmentQuality(assignment);
    }
    
    public double calcDeploymentPrice(FDProblem problem){
        return problem.calcDeploymentPrice(assignment);
    }
        
    /**
     * find the feature that setting it to {@code switchTo} will cost the least
     * if no such value found (all of the features are already on the switchTo state) 
     * then this function will return -1
     * @param deploymentPoint
     * @return 
     */
    public int findMaximalQualityFeatureToSwitch(int deploymentPoint, FDProblem problem, boolean switchTo){
        double bestQuality = -1;
        int bestQualityIndex = -1;
        final boolean[] a = assignment[deploymentPoint];
        for (int i=0; i<a.length; i++){
            if (a[i] != switchTo){
                a[i] = switchTo;
                double quality = calcQuility(problem);
                a[i] = !switchTo;
                
                if (bestQualityIndex == -1 || bestQuality < quality){
                    bestQualityIndex = i;
                    bestQuality = quality;
                }
            }
        }
        
        return bestQualityIndex;
    }
    
    public void assignBestLocalDeployment(final int deploymentPoint, final FDProblem p){
        final boolean[] best = new boolean[getNumberOfFeatures()];
        final double[] bestQuality = {Double.MIN_VALUE};
        
        new BooleanBruteForceRunner(getNumberOfFeatures()) {

            @Override
            protected void handle(boolean[] value) {
                assign(deploymentPoint, value);
                double q = calcQuility(p);
                if (q > bestQuality[0]){
                    System.arraycopy(value, 0, best, 0, value.length);
                    bestQuality[0] = q;
                }
            }
        }.run();
        
        assign(deploymentPoint, best);
    }
    
    
    /**
     * 
     * @param problem
     * @param switchTo
     * @param budget  - can be -1 if budget is not relevant
     * @return (can return -1 if no such value found) 
     */
    public long findMaximalQualityContinuesIndexToSwitch(FDProblem problem, boolean switchTo, double budget){
        double bestQuality = -1;
        long bestQualityIndex = -1;
        double quality;
        for (long i = 0; i<getContinuesSize(); i++){
            if (getContinuesValue(i) != switchTo){
                assignContinues(i, switchTo);
                quality = calcQuility(problem);
                if ((budget == -1 || calcDeploymentPrice(problem) <= budget)
                        && (bestQualityIndex == -1 || quality > bestQuality)){
                    bestQuality = quality;
                    bestQualityIndex = i;
                }
                assignContinues(i, !switchTo);
            }
        }
        
        return bestQualityIndex;
    }
    
    public void assign(int deployableNode, boolean[] assignment){
        System.arraycopy(assignment, 0, this.assignment[deployableNode], 0, assignment.length);
    }
    
    public void clearPartialAssignment(int deployableNode){
        Arrays.fill(assignment[deployableNode], false);
    }
    
    public Assignment deepCopy(){
        Assignment ass = new Assignment(getNumberOfDeploymentPoints(), getNumberOfFeatures());
        for (int i=0; i<assignment.length; i++){
            ass.assign(i, assignment[i]);
        }
        
        return ass;
    }

    public String toString(FDProblem problem) {
        StringBuilder sb = new StringBuilder();
        sb.append("Assignment:\n");
        for (int i=0; i<assignment.length; i++){
            sb.append("Deployment Node ").append(i).append(": ").append(Arrays.toString(assignment[i])).append("\n");
        }
        sb.append("Quality: ").append(calcQuility(problem)).append("\nPrice: ").append(calcDeploymentPrice(problem));
        
        return sb.toString();
    }

    public boolean getValue(int dnode, int feature) {
        return assignment[dnode][feature];
    }
    
    public boolean getContinuesValue(long dnodeAndFeature){
        return getValue(extractContinuesNode(dnodeAndFeature), extractContinuesFeature(dnodeAndFeature));
    }

    public boolean[][] getRawAssignment() {
        return assignment;
    }

    public void randomize(Random rng) {
        for (int i=0; i<assignment.length; i++){
            for (int j=0; j<assignment[i].length; j++){
                assignment[i][j] = rng.nextBoolean();
            }
        }
    }

    public int getNumberOfDeploymentPoints() {
        return assignment.length;
    }
    
    public int getNumberOfFeatures(){
        return assignment[0].length;
    }
    
    public int getContinuesSize(){
        return getNumberOfDeploymentPoints() * getNumberOfFeatures();
    }
    
    public void assignContinues(long deploymentAndFeature, boolean value){
        assign(extractContinuesNode(deploymentAndFeature), extractContinuesFeature(deploymentAndFeature), value);
    }

    private int extractContinuesNode(long deploymentAndFeature) {
        return (int) (deploymentAndFeature / getNumberOfFeatures());
    }

    private int extractContinuesFeature(long deploymentAndFeature) {
        return (int) (deploymentAndFeature % getNumberOfFeatures());
    }
}

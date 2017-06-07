package tests.rbc;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;

public class TestGraph extends GraphAsHashMap<Index,BasicVertexInfo>{

	private static final long serialVersionUID = 1L;
	
	private int [][][] deltaGroups;
	private double [] deltaValues;
	
	private int [][][] sourceDependencyGroups;
	private double [] sourceDependencyValues;

	private int [][][] targetDependencyGroups;
	private double [] targetDependencyValues;

	private int [][] betweennessGroups;
	private double [] betweennessValues;
	
	public TestGraph(){
		super();
	}
	
	public void setDeltaGroups(int[][][] groups){
		deltaGroups = groups;
	}

	public double[] getDeltaValues() {
		return deltaValues;
	}

	public void setDeltaValues(double[] deltaValues) {
		this.deltaValues = deltaValues;
	}

	public int[][][] getSourceDependencyGroups() {
		return sourceDependencyGroups;
	}

	public void setSourceDependencyGroups(int[][][] sourceDependencyGroups) {
		this.sourceDependencyGroups = sourceDependencyGroups;
	}

	public double[] getSourceDependencyValues() {
		return sourceDependencyValues;
	}

	public void setSourceDependencyValues(double[] sourceDependencyValues) {
		this.sourceDependencyValues = sourceDependencyValues;
	}

	public int[][][] getTargetDependencyGroups() {
		return targetDependencyGroups;
	}

	public void setTargetDependencyGroups(int[][][] targetDependencyGroups) {
		this.targetDependencyGroups = targetDependencyGroups;
	}

	public double[] getTargetDependencyValues() {
		return targetDependencyValues;
	}

	public void setTargetDependencyValues(double[] targetDependencyValues) {
		this.targetDependencyValues = targetDependencyValues;
	}

	public int[][] getBetweennessGroups() {
		return betweennessGroups;
	}

	public void setBetweennessGroups(int[][] betweennessGroups) {
		this.betweennessGroups = betweennessGroups;
	}

	public double[] getBetweennessValues() {
		return betweennessValues;
	}

	public void setBetweennessValues(double[] betweennessValues) {
		this.betweennessValues = betweennessValues;
	}

	public int[][][] getDeltaGroups() {
		return deltaGroups;
	}
}

package algorithms.centralityAlgorithms.closeness;

import java.io.Serializable;

import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;

import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

public interface IClosenessAlgorithm extends Serializable{

	public double getCloseness(Index v);

	public double getCloseness(int v);

	public double getCloseness(AbstractSimpleEdge<Index,BasicVertexInfo> e);

	public double[] getCloseness();

	public IClosenessFormula getFormula();

	public double getGroupCloseness(Object[] group);

	public double calculateMixedGroupCloseness(Object[] group, AbstractExecution progress, double percentage);

	public double[][] getDistanceMatrix();
	
	public double getContribution (Index v, double groupCloseness, double[][] cache);
	public double addToGroup (Index v, double groupCloseness, double[][] cache);
}

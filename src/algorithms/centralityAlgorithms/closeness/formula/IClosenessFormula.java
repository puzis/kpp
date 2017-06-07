package algorithms.centralityAlgorithms.closeness.formula;

import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import javolution.util.Index;

public interface IClosenessFormula {

	public double compute(int s, int t, double dist);
	public double compute(int s, int v, int t);
	public double compute(Index s, Index v, Index t);
	public double compute(Index s, AbstractSimpleEdge<Index, BasicVertexInfo> e, Index t);
	public double getBest(double c0, double c1);
	public double getWorstCloseness();
	public double normalize(double c);
	public double[][] getDistances();
}

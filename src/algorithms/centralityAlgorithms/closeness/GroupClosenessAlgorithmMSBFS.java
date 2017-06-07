package algorithms.centralityAlgorithms.closeness;

import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;
import javolution.util.Index;
import server.common.DummyProgress;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;

/**
 * An algorithm for computing group closeness using a sort of multiple source
 * BFS (hence MSBFS). Computes the closeness of a group of k vertices in a graph
 * with n vertices and m edges in O(m+k)
 * 
 * @author Yuri Bakulin
 * 
 */
public class GroupClosenessAlgorithmMSBFS extends AbsClosenessAlgorithm implements IClosenessAlgorithm {

	private static final long serialVersionUID = 1L;

	public GroupClosenessAlgorithmMSBFS(GraphInterface<Index,BasicVertexInfo> graph, IClosenessFormula formula) {
		super(graph);
		this._formula = formula;
	}
	
	@SuppressWarnings("unchecked")
	public double calculateGroupCloseness(Object[] group) {
		if (group.length == 0)
		{
			return 0;
		}
		double closeness = 0;
		int n = this._graph.getNumberOfVertices();
		int[] lastFound = new int[n];
		int lastFoundCount = 0;
		int[] newNodes = new int[n];
		int[] closestMember = new int[n];
		int newNodesCount = 0;
		boolean[] notVisited = new boolean[n];
		java.util.Arrays.fill(notVisited, true);
		for (lastFoundCount = 0; lastFoundCount < group.length; lastFoundCount++) {
			if (group[lastFoundCount] instanceof AbstractSimpleEdge<?,?>)
			{
				AbstractSimpleEdge<Index,BasicVertexInfo> e = (AbstractSimpleEdge<Index,BasicVertexInfo>)group[lastFoundCount];
				int v = e.getV0().intValue();
				int u = e.getV1().intValue();
				
				lastFound[lastFoundCount] = v;
				closeness += this.calcCloseness(v, v, 0);
				notVisited[v] = false;
				closestMember[v] = -1;
				
				lastFound[lastFoundCount] = u;
				closeness += this.calcCloseness(u, u, 0);
				notVisited[u] = false;
				closestMember[u] = -1;
				
			}
			else{
				int v = ((Index) group[lastFoundCount]).intValue();
				lastFound[lastFoundCount] = v;
				closeness += this.calcCloseness(v, v, 0);
				notVisited[v] = false;
				closestMember[v] = -1;
			}
			
		}

		double dist = 0;
		while (lastFoundCount > 0) {
			dist++;
			newNodesCount = 0;
			for (int i = 0; i < lastFoundCount; i++) {
				int v = lastFound[i];
				for (AbstractSimpleEdge<Index,BasicVertexInfo> edge : this._graph.getOutgoingEdges(Index.valueOf(v))) {
					// dirty fix. EdgeInterface has no direction consistency.
					// I would expect that every EdgeInterface edge in
					// GraphInterface.getOutgoingEdges(v) would uphold the
					// invariant:
					// edge.getV0() == v
					// but it doesn't work that way.
					int neighbor = (edge.getV1().intValue() == v ? edge.getV0()
							.intValue() : edge.getV1().intValue());
					
					if (notVisited[neighbor]) {
						closestMember[neighbor] = closestMember[v] < 0 ? v : closestMember[v];
						notVisited[neighbor] = false;
						newNodes[newNodesCount] = neighbor;
						newNodesCount++;
						// closeness += this._formula.compute(dist);
						closeness += this.calcCloseness(closestMember[neighbor], neighbor, dist);
					}
				}
			}
			int[] tmp = lastFound;
			lastFound = newNodes;
			newNodes = tmp;
			lastFoundCount = newNodesCount;
		}
		return closeness;
	}
	
	public double[][] getDistanceMatrix(){
		return null;
	}

	@Override
	public double getGroupCloseness(Object[] group) {
		return this.calculateGroupCloseness(group);
	}
	
	private double calcCloseness(int s, int t, double d)
	{
		return this._formula.compute(s, t, d);
	}

	@Override
	public double getCloseness(Index v) {
		return this.calculateGroupCloseness(new Object[] {v});
	}

	@Override
	public double getCloseness(int v) {
		return this.calculateGroupCloseness(new Object[] {Index.valueOf(v)});
	}

	@Override
	public double getCloseness(AbstractSimpleEdge<Index,BasicVertexInfo> e) {
		return this.calculateGroupCloseness(new Object[] {e});
	}

	@Override
	public double[] getCloseness() {
		double[] closeness = new double[this._graph.getNumberOfVertices()];
		for (int i = 0; i < closeness.length; i++) {
			closeness[i] = this.getCloseness(i);
		}
		return closeness;
	}

	@Override
	public IClosenessFormula getFormula() {
		return this._formula;
	}

	@Override
	public double calculateMixedGroupCloseness(Object[] group, AbstractExecution progress, double percentage) {
		// theres a problem with the implementation of mixed group closeness,
		// the computation of edge closeness is inconsistent with the one in ClosenessAlgorithm 
		return calculateMixedGroupCloseness(group, new DummyProgress(), 0);
	}
}

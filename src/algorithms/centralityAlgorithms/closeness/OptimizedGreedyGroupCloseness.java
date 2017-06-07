package algorithms.centralityAlgorithms.closeness;

import javolution.util.FastList;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.closeness.formula.IClosenessFormula;

/* last changes:

 * changed all references to distances in the algorithm to closeness value.
 */

/**
 * Optimizes the calculation of group centrality by maintaining a record of
 * which group member is closest to any non-group member node in the graph.
 * 
 * @author yuri
 * 
 */
public class OptimizedGreedyGroupCloseness extends ClosenessAlgorithm {

	private static final long serialVersionUID = 1L;
	//protected double[] m_shortestDistance;
	protected int[] _closestMember;
	protected FastList<Index> m_members;
	protected double m_closeness = 0;

	public OptimizedGreedyGroupCloseness(GraphInterface<Index,BasicVertexInfo> graph, IClosenessFormula formula, 
											AbstractExecution progress, double percentage) {
		super(graph, formula, progress, percentage);
		this._closestMember = new int[this._dists.length];
		for (int i = 0; i < this._closestMember.length; i++) {
			this._closestMember[i] = -1;
		}
		this.m_members = new FastList<Index>();
	}
	
	public void add(Index v) {
		this.m_members.add(v);
		for (int i = 0; i < this._closestMember.length; i++) {
			int curNeighbor = this._closestMember[i];
			double c = curNeighbor < 0 ? 0 : this.calcCloseness(curNeighbor, i);
			if (curNeighbor < 0 || c < this.calcCloseness(v.intValue(), i)) {
				this._closestMember[i] = v.intValue();
				// subtracting the old contribution (unless the distance is
				// infinite) and adding the new
				this.m_closeness += this.calcCloseness(v.intValue(), i)
						- (curNeighbor < 0 ? 0 : this.calcCloseness(curNeighbor, i));
			}
		}
	}

	/**
	 * Removes a vertex from the group. This method is slower than add() since
	 * when removing a vertex it is necessary to iterate all current group
	 * members to find the new shortest distance to each graph node which was
	 * previously accessible from the removed vertex.
	 * 
	 * @param v
	 */
	public void remove(Index v) {
		this.m_members.remove(v);
		for (int i = 0; i < this._closestMember.length; i++) {
			if (this._closestMember[i] == v.intValue()) {

				int newNeighbour = -1;
				
				for (Index index : this.m_members) {
					if (newNeighbour < 0 || this.calcCloseness(index.intValue(),i) > this.calcCloseness(newNeighbour,i)) {
						newNeighbour = index.intValue();
					}
				}
				this._closestMember[i] = newNeighbour;
				this.m_closeness += this.calcCloseness(newNeighbour, i)
						- this.calcCloseness(v.intValue(),i);
			}
		}
	}

	protected double calcCloseness(int src, int dest) {
		double c = this._formula.compute(-1, src, dest);
		return this._formula.normalize(c);
	}
	
	public double getContribution(Index v) {
		double contribution = 0;
		for (int i = 0; i < this._closestMember.length; i++) {
			int curNeighbour = this._closestMember[i];
			double curCloseness = curNeighbour < 0 ? 0 :this.calcCloseness(curNeighbour, i);
			double newCloseness = this.calcCloseness(v.intValue(),i);
			if (newCloseness > curCloseness)
			contribution += newCloseness - curCloseness;
		}
		return contribution;
	}

	public double getContribution(Object[] group) {
		double contribution = 0;
		for (int i = 0; i < this._closestMember.length; i++) {
			int curNeighbour = this._closestMember[i];
			double curCloseness = (curNeighbour < 0 ? 0 : this.calcCloseness(curNeighbour, i));
			double bestCloseness = curCloseness;
			for (int j = 0; j < group.length; j++) {
				int newNeighbour = ((Index) group[j]).intValue();
				double newCloseness = this.calcCloseness(newNeighbour, i);
				if (newCloseness > bestCloseness)
				{
					bestCloseness = newCloseness; 
				}
			}
			if (bestCloseness > curCloseness) {
				contribution += bestCloseness - curCloseness;
			}
		}
		return contribution;
	}

	public double getGroupCloseness() {
		return this.m_closeness;
	}

	@Override
	public double getGroupCloseness(Object[] group) {
		return this.getContribution(group);
	}

	@Override
	public double getCloseness(Index v) {
		return this.getContribution(v);
	}

	@Override
	public double getCloseness(int v) {
		return this.getContribution(Index.valueOf(v));
	}

	public int getSize() {
		return this.m_members.size();
	}

	public FastList<Index> getMembers() {
		return this.m_members;
	}
}
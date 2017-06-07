package algorithms.shortestPath;

import topology.AbstractHyperEdge;
import topology.BasicVertexInfo;
import javolution.util.FastList;
import javolution.util.Index;


public abstract class AbstractShortestPathAlgorithm implements
		ShortestPathAlgorithmInterface, GraphTraversalListener {

	protected FastList<GraphTraversalListener> m_listeners = new FastList<GraphTraversalListener>();
	
	protected long m_count_discovered = 0;
	protected long m_count_rediscovered = 0;
	protected long m_count_expanded = 0;

	public void resetCounters(){
		m_count_discovered = 0;
		m_count_rediscovered = 0;
		m_count_expanded = 0;
	}
	public long getNumberOfDiscovered(){
		return m_count_discovered;
	}
	public long getNumberOfRediscovered(){
		return m_count_rediscovered;
	}
	public long getNumberOfExpanded(){
		return m_count_expanded;
	}
	
	
	public void addListener(GraphTraversalListener l) {
		m_listeners.add(l);
	}

	public void removeListener(GraphTraversalListener l) {
		m_listeners.remove(l);
	}

	public void beforeExpand(Index v) {
		for (GraphTraversalListener l: m_listeners){
			l.beforeExpand(v);
		}
	}

	public void vertexDiscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge) {
		m_count_discovered++;
		for (GraphTraversalListener l: m_listeners){
			l.vertexDiscovered(v, w, hedge);
		}
	}

	public void vertexRediscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge, double dist) {
		m_count_rediscovered++;
		for (GraphTraversalListener l: m_listeners){
			l.vertexRediscovered(v, w, hedge, dist);
		}
	}

	public void afterExpand(Index v) {		
		m_count_expanded++;
		for (GraphTraversalListener l: m_listeners){
			l.afterExpand(v);
		}
	}

	public boolean isExpandable(Index v) {
		boolean res = true;
		for (GraphTraversalListener l: m_listeners){
			res = res && l.isExpandable(v);
		}
		return res;
	}

}

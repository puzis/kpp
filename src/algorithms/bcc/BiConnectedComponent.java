package algorithms.bcc;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.GraphUtils;

public class BiConnectedComponent {
	private FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> m_edges;
	private GraphInterface<Index,BasicVertexInfo> m_component = null;
	GraphInterface<Index,BasicVertexInfo> m_graph;
	
	/**
	 * NOTE: was not tested with directed graphs !
	 * @param graph
	 */
	public BiConnectedComponent(GraphInterface<Index,BasicVertexInfo> graph){
		this(graph, new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>());
	}

	/**
	 * NOTE: was not tested with directed graphs !
	 * @param graph
	 * @param edges
	 */
	public BiConnectedComponent(GraphInterface<Index,BasicVertexInfo> graph, FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> edges){
		m_edges = edges;
		m_graph = graph;
		if (edges.size()>0)
			m_component = GraphUtils.reduceEdges(m_graph, m_edges.iterator());
	}
	
	public void addEdge(AbstractSimpleEdge<Index,BasicVertexInfo> e){
		m_edges.add(e);
	}
	
	public GraphInterface<Index,BasicVertexInfo> getComponent(){
		if (m_component==null){
			m_component = GraphUtils.reduceEdges(m_graph, m_edges.iterator());
		}
		return m_component;
	}

	@Override
	public String toString(){
		if (m_component == null) return "null";
		return m_component.toString();
	}

	@Override
	public int hashCode() {
		if (m_component != null)
			return m_component.hashCode();
		return m_edges.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BiConnectedComponent other = (BiConnectedComponent) obj;
		if (m_component == null) {
			if (other.m_component != null)
				return false;
		} else if (!m_component.equals(other.m_component))
			return false;
		if (m_edges == null) {
			if (other.m_edges != null)
				return false;
		} else if (!m_edges.equals(other.m_edges))
			return false;
		return true;
	}
}

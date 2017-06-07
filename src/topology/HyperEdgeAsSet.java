package topology;

import java.util.Collection;

import javolution.util.FastMap;
import javolution.util.FastSet;

public class HyperEdgeAsSet<VertexType,VertexInfoStructure> extends AbstractHyperEdge<VertexType,VertexInfoStructure> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected FastSet<VertexType> m_vertices;

	
	public HyperEdgeAsSet(Iterable<VertexType> vertices) {
		super();
		m_vertices = new FastSet<VertexType>();
		for (VertexType v: vertices)
			m_vertices.add(v);
	}
	public HyperEdgeAsSet(Iterable<VertexType> vertices, EdgeInfo<VertexType,VertexInfoStructure> info) {
		super(info);
		m_vertices = new FastSet<VertexType>();
		for (VertexType v: vertices)
			m_vertices.add(v);
	}

	@Override
	public Collection<VertexType> getVertices() {
		return m_vertices.unmodifiable();
	}

	@Override
	public Collection<VertexType> getSources() {
		return m_vertices.unmodifiable();
	}

	@Override
	public Collection<VertexType> getTargets() {
		return m_vertices.unmodifiable();
	}

	@Override
	public Collection<VertexType> getNeighbors(VertexType v) {
		FastSet<VertexType> n = new FastSet<VertexType>();
		n.addAll(this.m_vertices);
		n.remove(v);
		return n;
	}


	
	protected void remove(VertexType v) {
		m_vertices.remove(v);
	}		
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for(VertexType v : getVertices()) {
			sb.append(v).append(' ');
		}
		sb.append('}');
		return sb.toString();
	}
	@Override
	public int getNumberOfVertices() {
		return m_vertices.size();
	}

}

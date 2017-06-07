package topology;

public class HashableUndirectedEdge<VertexType,VertexInfoStructure> extends
		UndirectedSimpleEdge<VertexType,VertexInfoStructure> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HashableUndirectedEdge(VertexType v0, VertexType v1) {
		super(v0, v1);
	}
	public HashableUndirectedEdge(VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> info) {
		super(v0, v1, info);
	}

	
	@Override
	public int hashCode() {			
		final int prime = 31;
		int result = 1;
		result += ((m_v0 == null) ? 0 : m_v0.hashCode());
		result += ((m_v1 == null) ? 0 : m_v1.hashCode());
		result *= prime;
		return result;
	}
	
}

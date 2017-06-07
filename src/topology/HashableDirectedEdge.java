package topology;

import javolution.util.FastMap;

public class HashableDirectedEdge<VertexType,VertexInfoStructure> extends DirectedSimpleEdge<VertexType,VertexInfoStructure> {

		private static final long serialVersionUID = 1L;
		
		public HashableDirectedEdge (VertexType v0, VertexType v1){
			super(v0,v1);
		}
		public HashableDirectedEdge (VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> info){
			super(v0,v1,info);
		}
		public HashableDirectedEdge (VertexType v0, VertexType v1, double latency, double multiplicity, FastMap<String, String> info){		    
			super(v0,v1,latency,multiplicity,info);
		}
	
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((m_v0 == null) ? 0 : m_v0.hashCode());
			result = prime * result + ((m_v1 == null) ? 0 : m_v1.hashCode());
			return result;
		}

}

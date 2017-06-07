package topology;

import java.util.Collection;
import java.util.HashSet;

import javolution.util.FastMap;



public class DirectedSimpleEdge<VertexType,VertexInfoStructure> extends AbstractSimpleEdge<VertexType,VertexInfoStructure> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DirectedSimpleEdge(VertexType v0, VertexType v1){
		super(v0,v1);
	}
	public DirectedSimpleEdge(VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> info){
		super(v0,v1,info);
	}
    public DirectedSimpleEdge(VertexType v0, VertexType v1, double latency, double multiplicity, FastMap<String, String> info){
    	super(v0,v1,latency,multiplicity,info);    	
    }
	
	
	@Override
	public boolean equals(Object obj) {
		//edges with null vertices will always be unequal
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (m_v0 == null) 
			return false;
		if (m_v1 == null) 
			return false;
		if (obj instanceof  DirectedSimpleEdge<?,?>) {
			final AbstractSimpleEdge<VertexType,VertexInfoStructure> other = (AbstractSimpleEdge<VertexType,VertexInfoStructure>) obj;
			if (other.m_v0 == null) 
				return false;
			if (other.m_v1 == null) 
				return false;			
			if (m_v0.equals(other.m_v0) && m_v1.equals(other.m_v1))
					return true;
		}
		return false;
	}

	@Override
	public Collection<VertexType> getSources() {
		HashSet<VertexType> rslt = new HashSet<VertexType>();
		rslt.add(m_v0);
		return rslt;
	}

	@Override
	public Collection<VertexType> getTargets() {
		HashSet<VertexType> rslt = new HashSet<VertexType>();
		rslt.add(m_v1);
		return rslt;
	}


	@Override
	public void flipInPlace() {
		VertexType tmp = this.m_v0;
		this.m_v0=this.m_v1;
		this.m_v1=tmp;		
	}

}

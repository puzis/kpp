/**
 * 
 */
package topology;

import java.util.Collection;

import javolution.util.FastMap;

/**
 * @author Rami Puzis
 *
 */
public class UndirectedSimpleEdge<VertexType,VertexInfoStructure> extends AbstractSimpleEdge<VertexType,VertexInfoStructure> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public UndirectedSimpleEdge(VertexType v0,VertexType v1){
		super(v0,v1);
	}
	public UndirectedSimpleEdge(VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> info){
		super(v0,v1,info);
	}
    public UndirectedSimpleEdge(VertexType v0, VertexType v1, double latency, double multiplicity){
    	super(v0,v1,latency,multiplicity);
    }
	
    public UndirectedSimpleEdge(VertexType v0, VertexType v1, double latency, double multiplicity, FastMap<String, String> info){
    	super(v0,v1,latency,multiplicity,info);    	
    }
	
	@Override
	public Collection<VertexType> getSources() {			
		return this.getVertices();
	}
	@Override
	public Collection<VertexType> getTargets() {
		return this.getVertices();
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
		if (obj instanceof  UndirectedSimpleEdge<?,?>) {
			final UndirectedSimpleEdge<?,?> other = (UndirectedSimpleEdge<?,?>) obj;
			if (other.m_v0 == null) 
				return false;
			if (other.m_v1 == null) 
				return false;			
			if (m_v0.equals(other.m_v0) && m_v1.equals(other.m_v1))
					return true;
			if (m_v0.equals(other.m_v1) && m_v1.equals(other.m_v0))
					return true;
		}
		return false;
	}
	
	
	/**
	 * Methods flip and flipInPlace of undirected edges should not modify the order of vertices.
	 * flip will return a clone of this edge. 
	 * flipInPlace will do nothing. (eventualy :) )
	 * now flipInPlace works as in directed edge, otherwise tests do not pass....
	 * TODO: remove code from flipInPlace - field order should not matter.  
	 */
	@Override
	public void flipInPlace() {
		VertexType tmp = this.m_v0;
		this.m_v0=this.m_v1;
		this.m_v1=tmp;		
	}

}

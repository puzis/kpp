/**
 * 
 */
package topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javolution.util.FastMap;

/**
 * @author Rami Puzis
 *
 */
public abstract class AbstractSimpleEdge<VertexType,VertexInfoStructure> extends AbstractHyperEdge<VertexType,VertexInfoStructure> 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected VertexType m_v0;
	protected VertexType m_v1;

	
	protected AbstractSimpleEdge(VertexType v0, VertexType v1){
		super();
		m_v0=v0;
		m_v1=v1;
	}
	protected AbstractSimpleEdge(VertexType v0, VertexType v1, EdgeInfo<VertexType,VertexInfoStructure> info){
		super(info);
		m_v0=v0;
		m_v1=v1;
	}
	public AbstractSimpleEdge(VertexType v0, VertexType v1, double latency,double multiplicity) {
		super(latency,multiplicity);
    	m_v0=v0;
    	m_v1=v1;		
	}
    public AbstractSimpleEdge(VertexType v0, VertexType v1, double latency, double multiplicity, FastMap<String, String> info){
    	super(latency,multiplicity,info);
    	m_v0=v0;
    	m_v1=v1;
    }
	
	public VertexType getV0() {	return m_v0;	}

	public VertexType getV1() {	return m_v1;	}

	public String toString()
	{
		return "[" + m_v0 + ", " + m_v1 + "]:"+this.getLatency()+":"+this.getMultiplicity();
	}

	public VertexType getNeighbor(VertexType v) {
		//TODO: extract or pull up method: consolidate with other Edge implementations. 
		VertexType v0 = this.getV0();
		VertexType v1 = this.getV1();
		
		if (v0.equals(v))	return v1;
		else if (v1.equals(v))	return v0;
		return null;
	}

	@Override
	public Collection<VertexType> getVertices() {
		ArrayList<VertexType> rslt = new ArrayList<VertexType>(2);
		rslt.add(m_v0);
		rslt.add(m_v1);
		return rslt;
	}

	@Override
	public Collection<VertexType> getNeighbors(VertexType v) {
		HashSet<VertexType> rslt = new HashSet<VertexType>();
		rslt.add(this.getNeighbor(v));
		return rslt;
	}
	
	public AbstractSimpleEdge<VertexType,VertexInfoStructure> clone() {
		AbstractSimpleEdge<VertexType,VertexInfoStructure> copy = (AbstractSimpleEdge<VertexType,VertexInfoStructure>)super.clone();
		return copy;
	}
	
	public AbstractSimpleEdge<VertexType,VertexInfoStructure> flip() {
		AbstractSimpleEdge<VertexType,VertexInfoStructure> copy = this.clone();
		copy.flipInPlace();
		return copy;
	}
	@Override
	public int getNumberOfVertices() {
		return 2;
	}
	
	public abstract void flipInPlace();

}

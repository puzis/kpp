package topology;

import javolution.util.FastMap;


public abstract class AbstractHyperEdge<VertexType,VertexInfoStructure> extends EdgeInfo<VertexType,VertexInfoStructure>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
    public AbstractHyperEdge(){
    	super();
    }
    public AbstractHyperEdge(double latency, double multiplicity, FastMap<String, String> info){
    	super(latency,multiplicity,info);
    }
    public AbstractHyperEdge(double multiplicity){
    	super(multiplicity);    	
    }
    public AbstractHyperEdge(EdgeInfo<VertexType,VertexInfoStructure> info){
    	super(info);    	
    }
    public AbstractHyperEdge(double multiplicity, FastMap<String, String> info){
    	super(multiplicity,info);
    }
	public AbstractHyperEdge(double latency, double multiplicity) {
		super(latency,multiplicity);
	}
	
	
	
	public abstract Iterable<VertexType> getVertices();
	public abstract Iterable<VertexType> getSources();
	public abstract Iterable<VertexType> getTargets();
	public abstract Iterable<VertexType> getNeighbors(VertexType v);
	public abstract int getNumberOfVertices();
}

package topology;

import java.io.Serializable;
import java.util.Map.Entry;

import javolution.util.FastList;
import javolution.util.FastMap;


/**
 * 
 * @param <VertexType>
 */
public class EdgeInfo<VertexType,VertexInfoStructure> implements Serializable, Cloneable
{
	
	
	private static final long serialVersionUID = 1L;
	
	/** Optionally it would be capacity */
	protected double m_latency;
	protected double m_multiplicity;	

    /**
     * @invariant always info.get(TopologyConstants.LATENCY)==m_latency
     */
	private FastMap<String, String> m_info = null;
    
    
    public EdgeInfo(){	
    	this(1,1);	
    }
        
  
    public EdgeInfo(double latency, double multiplicity, FastMap<String, String> info){
    	m_latency = latency;
    	m_multiplicity = multiplicity;
    	if(info!=null){
    		initInfo();
    		m_info.putAll(info);
    	}
    }

    public EdgeInfo(double multiplicity){
    	this(multiplicity,new FastMap<String,String>());    	
    }
    
    public EdgeInfo(EdgeInfo<VertexType,VertexInfoStructure> info){
    	this(1,info.m_info);    	
    }
    
    public EdgeInfo(double multiplicity, FastMap<String, String> info){
    	m_multiplicity = multiplicity;
    	if(info!=null)
    	{
        	m_info = new FastMap<String,String>();
    		m_info.putAll(info);
    		
    		if (m_info.containsKey(TopologyConstants.LATENCY.trim().toLowerCase()))
    			m_latency=Double.valueOf(m_info.get(TopologyConstants.LATENCY.trim().toLowerCase()));
    		else
    			m_latency = 1;
    	}
		else
			m_latency = 1;
    }


	public EdgeInfo(double latency, double multiplicity) {
    	m_multiplicity = multiplicity;
    	m_latency = latency;
    	//m_info = new FastMap<String,String>();
    	//m_info.put(TopologyConstants.LATENCY.trim().toLowerCase(), String.valueOf(m_latency));
    	m_info = null;
	}


	public String getLabel(String labelName){
		if (m_info!=null){
	    	String labelVal = m_info.get(labelName.toLowerCase().trim());
	    	if (labelVal != null)
	    		return labelVal;
		}
    	return null;
    }

    public void setLabel(String labelName, String labelValue){
    	if(m_info==null) initInfo();
    	
    	m_info.put(labelName.trim().toLowerCase(), labelValue);
    	if (labelName.equals(TopologyConstants.LATENCY.trim().toLowerCase()))
    		m_latency = Double.valueOf(labelValue);
    } 

    private void initInfo() {
    	m_info = new FastMap<String,String>();
    	m_info.put(TopologyConstants.LATENCY.trim().toLowerCase(), String.valueOf(m_latency));		
	}


	public void setLatency(double weight){
    	m_latency = weight;
    	if(m_info!=null)
    		m_info.put(TopologyConstants.LATENCY.trim().toLowerCase(), String.valueOf(m_latency));
    }
    
    public double getLatency(){	
    	return m_latency;	
    }
  
    //@Rami;
    public String toString(){
    	StringBuilder rslt = new StringBuilder();
    	rslt.append(" ").append(m_latency).append(" ")
    	.append(m_multiplicity).append(" ");
		
    	if(m_info!=null)
			for (Entry<String, String> info : m_info.entrySet())
				rslt.append(info.getKey()).append(" ").append(info.getValue()).append(" ");
    	
    	
		return  rslt.toString();
    }
    
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((m_info == null) ? 0 : m_info.hashCode());
//		result = prime * result
//				+ ((m_weight == null) ? 0 : m_weight.hashCode());
//		return result;
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final EdgeInfo<VertexType,VertexInfoStructure> other = (EdgeInfo<VertexType,VertexInfoStructure>) obj;
		if (m_info == null) {
			if (other.m_info != null)
				return false;
		} else if (!m_info.equals(other.m_info))
			return false;
		if (m_latency == Double.NaN) {
			if (other.m_latency != Double.NaN)
				return false;
		} else if (!(m_latency == other.m_latency))
			return false;
		return true;
	}
	
	
	@Override
	public EdgeInfo<VertexType,VertexInfoStructure> clone(){
		try {
			EdgeInfo<VertexType,VertexInfoStructure> copy = (EdgeInfo<VertexType,VertexInfoStructure>)super.clone();
			//field by field copy performed with Object.clone()
			
			FastMap<String,String> info = copy.m_info;
			if (info!=null){
				copy.m_info = new FastMap<String,String>();
				copy.m_info.putAll(info);
			}
			return copy;
		} catch (CloneNotSupportedException e) {
			assert false;
		}
		assert false;
		return null;
	}

	public double getMultiplicity() {
		return m_multiplicity;
	}

	public void setMultiplicity(double multiplicity) {
		this.m_multiplicity = multiplicity;
	}
}
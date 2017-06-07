package topology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;

import javolution.util.FastComparator;
import javolution.util.FastMap;

import common.Pair;

public class VertexInfo extends BasicVertexInfo implements Serializable, Comparable<VertexInfo>, Cloneable
{
	private static final long serialVersionUID = 1L;
	
    protected int m_deployment = -1;
    protected double m_betweeness = -1;
    protected int m_degree = -1;
    protected boolean m_border = false;
    private FastMap<String, Pair<String, String>> m_optionalInfo = new FastMap<String, Pair<String, String>>();
    
    //TODO Should be added to a subclass of VertexInfo
    protected ArrayList<Integer> m_clusters = new ArrayList<Integer>();
    
	public ArrayList<Integer> getClusters() {
		return m_clusters;
	}

	public void addCluster(Integer cluster) {
		if(!m_clusters.contains(cluster))
			m_clusters.add(cluster);
	}
    
	public void removeFromCluster(Integer cluster) {
		if(m_clusters.contains(cluster)) {
			m_clusters.remove(cluster);
		}
	}
    public boolean isBorder() {
		return m_border;
	}

	public void setBorder(boolean border) {
		this.m_border = border;
		m_optionalInfo.put(TopologyConstants.BORDER.trim().toLowerCase(), new Pair<String, String>(TopologyConstants.BORDER.trim().toLowerCase(),String.valueOf(border)));
	}

	
    
    public VertexInfo(){}
    
    public VertexInfo(int vertexNum, String label){
        m_vertexNum = vertexNum;
        m_label = label;
    }
    
    //@Rami
    public VertexInfo(int vertexNum, String label, int multiplicity){
    	this(vertexNum,label);
    	m_multiplicity = multiplicity;
    	m_optionalInfo.put(TopologyConstants.VERTEX_MULTIPLICITY.trim().toLowerCase(), new Pair<String,String>(
				TopologyConstants.VERTEX_MULTIPLICITY.trim(),String.valueOf(m_multiplicity)));
    }

    //@Rami: modified
    public VertexInfo(int vertexNum, String label, double x, double y, double z, FastMap<String , Pair<String, String>> info){
    	this(vertexNum,label);
    	m_optionalInfo.setKeyComparator(new StringComparator());
    	
        m_x = x;
        m_y = y;
        m_z = z;

        m_optionalInfo.putAll(info);
        
		if (m_optionalInfo.containsKey(TopologyConstants.DEGREE.trim().toLowerCase()))
			m_degree=Integer.parseInt(m_optionalInfo.get(TopologyConstants.DEGREE.trim().toLowerCase()).getValue2());
		if (m_optionalInfo.containsKey(TopologyConstants.BETWEENNESS.trim().toLowerCase()))
			m_betweeness=Double.parseDouble(m_optionalInfo.get(TopologyConstants.BETWEENNESS.trim().toLowerCase()).getValue2());
		if (m_optionalInfo.containsKey(TopologyConstants.DEPLOYMENT))
			m_deployment=Integer.parseInt(m_optionalInfo.get(TopologyConstants.DEPLOYMENT.trim().toLowerCase()).getValue2());
		if (m_optionalInfo.containsKey(TopologyConstants.VERTEX_MULTIPLICITY.trim().toLowerCase()))
			m_multiplicity=Integer.parseInt(m_optionalInfo.get(TopologyConstants.VERTEX_MULTIPLICITY.trim().toLowerCase()).getValue2());
		if (m_optionalInfo.containsKey(TopologyConstants.LATENCY.trim().toLowerCase()))
			m_latency=Integer.parseInt(m_optionalInfo.get(TopologyConstants.LATENCY.trim().toLowerCase()).getValue2());
		if (m_optionalInfo.containsKey(TopologyConstants.BORDER.trim().toLowerCase()))
			m_border=Boolean.parseBoolean(m_optionalInfo.get(TopologyConstants.BORDER.trim().toLowerCase()).getValue2());
    }
    


    public VertexInfo(VertexInfo other) {
    	this(other.m_vertexNum,other.m_label,other.m_x,other.m_y, other.m_z,other.m_optionalInfo);
	}

	public String getLabel(){
        return m_label;
    }

    public String getLabel(String labelName){
    	Pair<String, String> labelVal = m_optionalInfo.get(labelName.trim().toLowerCase());
    	if (labelVal != null)
    		return labelVal.getValue2();
    	return null;
    }
   
    //@Rami: modified
    public void setLabel(String labelName, String labelValue){
		if (labelName.trim().toLowerCase().equals(TopologyConstants.DEGREE.trim().toLowerCase()))
			m_degree=Integer.parseInt(labelValue);
		else if (labelName.trim().toLowerCase().equals(TopologyConstants.BETWEENNESS.trim().toLowerCase()))
			m_betweeness=Double.parseDouble(labelValue);
		else if (labelName.trim().toLowerCase().equals(TopologyConstants.DEPLOYMENT.trim().toLowerCase()))
			m_deployment=Integer.parseInt(labelValue);
		else if (labelName.trim().toLowerCase().equals(TopologyConstants.VERTEX_MULTIPLICITY.trim().toLowerCase()))
			m_multiplicity=Integer.parseInt(labelValue);			

		String name = labelName.trim().toLowerCase();
		if (m_optionalInfo.containsKey(name))
			m_optionalInfo.remove(name);
		m_optionalInfo.put(name, new Pair<String, String>(labelName.trim(), labelValue));
    }

    public String getNetwork(){
    	if (m_optionalInfo.containsKey(TopologyConstants.NSP.toLowerCase())){
    		Pair<String, String> networkType = m_optionalInfo.get(TopologyConstants.NSP.toLowerCase());
    		return ((networkType == null) ? "" : networkType.getValue2());
    	} else {
    		return "";
    	}
    }

	public void setDeployment(int m_deployment) {
		this.m_deployment = m_deployment;
		m_optionalInfo.put(TopologyConstants.DEPLOYMENT.trim().toLowerCase(), new Pair<String,String>(
																				TopologyConstants.DEPLOYMENT.trim(),String.valueOf(m_deployment)));
	}
	
	/**
	 * @param m_betweeness the m_betweeness to set
	 */
	public void setBetweeness(double m_betweeness) {
		this.m_betweeness = m_betweeness;
		m_optionalInfo.put(TopologyConstants.BETWEENNESS.trim().toLowerCase(),new Pair<String,String>(
																				TopologyConstants.BETWEENNESS.trim(),String.valueOf(m_betweeness)));
	}
	
	public int getMultiplicity() {
		return m_multiplicity;
	}

	//@Rami: modified
	public String toString(){		
		StringBuilder defaultInfo = new StringBuilder(" " + Integer.toString(m_vertexNum) + " '" + m_label + "' " + m_x + " " + m_y + " " + m_z + " ");
		
		for (Entry<String, Pair<String, String>> info : m_optionalInfo.entrySet()){
			defaultInfo.append(" ").append(info.getValue().getValue1()).append(" ").append(info.getValue().getValue2()).append(" ");
		}
		return  defaultInfo.toString(); 
	}
	
	public StringComparator getStringComparator(){
		return new StringComparator();
	}
	
	public class StringComparator extends FastComparator<String>{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean areEqual(String arg0, String arg1){
			return arg0.equalsIgnoreCase(arg1);
		}

		@Override
		public int compare(String arg0, String arg1){
			return arg0.compareToIgnoreCase(arg1);
		}

		@Override
		public int hashCodeOf(String arg0){
			return arg0.hashCode();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		long temp;
//		temp = Double.doubleToLongBits(m_betweeness);
//		result = prime * result + (int) (temp ^ (temp >>> 32));
//		result = prime * result + m_degree;
//		result = prime * result + m_deployment;
//		result = prime * result + m_multiplicity;
//		result = prime * result + ((m_optionalInfo == null) ? 0 : m_optionalInfo.hashCode());
//		result = prime * result + ((m_label == null) ? 0 : m_label.hashCode());
//		result = prime * result + m_vertexNum;
//		temp = Double.doubleToLongBits(m_x);
//		result = prime * result + (int) (temp ^ (temp >>> 32));
//		temp = Double.doubleToLongBits(m_y);
//		result = prime * result + (int) (temp ^ (temp >>> 32));
//		temp = Double.doubleToLongBits(m_z);
//		result = prime * result + (int) (temp ^ (temp >>> 32));
//		return result;
//	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final VertexInfo other = (VertexInfo) obj;
		if (Double.doubleToLongBits(m_betweeness) != Double
				.doubleToLongBits(other.m_betweeness))
			return false;
		if (m_degree != other.m_degree)
			return false;
		if (m_deployment != other.m_deployment)
			return false;
		if (m_multiplicity != other.m_multiplicity)
			return false;
		if (m_optionalInfo == null) {
			if (other.m_optionalInfo != null)
				return false;
		} else if (!m_optionalInfo.equals(other.m_optionalInfo))
			return false;
		if (m_label == null) {
			if (other.m_label != null)
				return false;
		} else if (!m_label.equals(other.m_label))
			return false;
		if (m_vertexNum != other.m_vertexNum)
			return false;
		if (Double.doubleToLongBits(m_x) != Double.doubleToLongBits(other.m_x))
			return false;
		if (Double.doubleToLongBits(m_y) != Double.doubleToLongBits(other.m_y))
			return false;
		if (Double.doubleToLongBits(m_z) != Double.doubleToLongBits(other.m_z))
			return false;
		return true;
	}

	@Override
	public int compareTo(VertexInfo o) {
		return this.m_vertexNum - o.m_vertexNum;
	}
	
	@Override
	public VertexInfo clone() {
		return new VertexInfo(this);
	}

	public double getLatency() {
		return m_latency;
	}	
}
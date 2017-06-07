package algorithms.clustering;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import topology.GraphInterface;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;
/**
 * 
 * @author Manish Purohit & Anshul Sawant
 *
 * @param <Integer>
 */
public abstract class Clustering<VertexType extends Number,VertexInfoStructure>
{

	protected GraphInterface<VertexType,VertexInfoStructure> m_graph;
	protected Map<Integer, Set<VertexType>> m_clusterToVertices = new HashMap<Integer, Set<VertexType>>();
	protected Map<Integer, Set<VertexType>> m_clusterToBorders = new HashMap<Integer, Set<VertexType>>();

	protected boolean m_isClustered = false;
	
	public Clustering(GraphInterface<VertexType,VertexInfoStructure> g){
		m_graph = g;
	}

	protected void applyLabel(VertexType v, int clusterId){
		BasicVertexInfo vi = m_graph.getVertex(v);
		if (VertexFactory.isVertexInfo(vi)){
			((VertexInfo)vi).addCluster(clusterId);	
			if(((VertexInfo)vi).getClusters().size()>1)
			{
				((VertexInfo)vi).setBorder(true);
				addToMap(m_clusterToBorders, clusterId, v);
			
			}
			addToMap(m_clusterToVertices, clusterId, v);
			}
		else 
			throw new IllegalArgumentException("the Vertex must be VertexInfo");
	}
	
	protected void computeBorders() {
		for(VertexType v : m_graph.getVertices()) {
			BasicVertexInfo vi = m_graph.getVertex(v);
			if (VertexFactory.isVertexInfo(vi)){
				if(((VertexInfo)vi).getClusters().size() > 1) {
					((VertexInfo)vi).setBorder(true);
					for(int clusterId : ((VertexInfo)vi).getClusters())
						addToMap(m_clusterToBorders, clusterId, v);
				}
			}
			else 
				throw new IllegalArgumentException("the Vertex must be VertexInfo");
		}
	}
	protected void addToMap(Map<Integer, Set<VertexType>> m, int key, VertexType val){
		if(m.containsKey(key))
			m.get(key).add(val);
		else{
			HashSet<VertexType> vForClusters = new HashSet<VertexType>();
			vForClusters.add(val);
			m.put(key, vForClusters);
		}
	}
	
	public List<Integer> getClusters(VertexType v)
	{
		if (VertexFactory.isVertexInfo(m_graph.getVertex(v)))
			return ((VertexInfo)m_graph.getVertex(v)).getClusters();
		else 
			throw new IllegalArgumentException("the Vertex must be VertexInfo");
	}

	public Set<VertexType> getVertices(int clusterId){
		return m_clusterToVertices.get(clusterId);
	}
	
	public Set<Integer> getClusterIds()
	{
		return m_clusterToVertices.keySet();
	}
	
	public int getNoOfClusters(){
		return m_clusterToVertices.size();
	}
	
	public Set<VertexType> getBorderVertices(int clusterId){
		if(m_clusterToBorders.containsKey(clusterId))
			return m_clusterToBorders.get(clusterId);
		else
			return new HashSet<VertexType>();
	}
	
	public void generateClusters() throws Exception{
		if(m_isClustered)
			return;
		doClustering();
		m_isClustered = true;
	}
	
	abstract void doClustering() throws Exception;

	public GraphInterface<VertexType,VertexInfoStructure> getGraph()
	{
		return m_graph;
	}
	
	public boolean isBorder(VertexType v){
		if (VertexFactory.isVertexInfo(m_graph.getVertex(v)))
			return ((VertexInfo)m_graph.getVertex(v)).isBorder();
		else 
			return false; 
	}

	public Set<VertexType> getBorderVertices(){
		HashSet<VertexType> borders = new HashSet<VertexType>();
		for(Integer i: m_clusterToBorders.keySet()){
			borders.addAll(m_clusterToBorders.get(i));
		}
		return borders;
	}
	
	protected void addBorder(VertexType v, int c){
		if (VertexFactory.isVertexInfo(m_graph.getVertex(v))){
			addToMap(m_clusterToBorders, c, v);
			((VertexInfo)m_graph.getVertex(v)).setBorder(true);
		}
		else
			throw new IllegalArgumentException("the Vertex must be VertexInfo");
		
	}
	
	protected void addBorder(VertexType v, Set<Integer> cs){
		if (VertexFactory.isVertexInfo(m_graph.getVertex(v))){
			for(int c: cs){
				addToMap(m_clusterToBorders, c, v);
			}
			((VertexInfo)m_graph.getVertex(v)).setBorder(true);
		}
		else 
			throw new IllegalArgumentException("the Vertex must be VertexInfo");

	}
	
	protected void removeBorder(VertexType v){
		BasicVertexInfo vi = m_graph.getVertex(v);
		if (VertexFactory.isVertexInfo(vi)){
		for(int c: ((VertexInfo)vi).getClusters()){
			m_clusterToBorders.get(c).remove(v.intValue());
		}
		((VertexInfo)vi).setBorder(false);
		}
		else 
			throw new IllegalArgumentException("the Vertex must be VertexInfo");

	}
	
	public String toString() {
		if(!m_isClustered) {
			return "Not clustered: "+m_graph.toString();
		}
		StringBuilder sb = new StringBuilder();
		for(int c : m_clusterToVertices.keySet()) {
			sb.append(" {");
			for(VertexType v : m_clusterToVertices.get(c)) {
				sb.append(v.intValue()+" ");
			}
			sb.append("} ");
		}
		return sb.toString();
	}
}

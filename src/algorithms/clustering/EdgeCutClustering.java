package algorithms.clustering;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javolution.util.FastSet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import topology.AbstractSimpleEdge;
import topology.GraphInterface;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;

/**
 * This is a marker class to indicate that the deriving clustering is based on edge cuts.
 * We assume that implementing class implements doClustering() after which all the
 * vertices are assigned correct clusters. This class then post-processes the data
 * to generate border vertices.
 * 
 * Following data-structures must be maintained by the classes extending this class
 * 1. m_clusterToBorders
 * 2. m_clusterToVertices
 * 3. In class VertexInfo cluster to which the vertex info belongs should be added 
 * 4. In class VertexInfo setBorder must be called 
 * @author root
 *
 * @param <Integer>
 */
public class EdgeCutClustering<VertexType extends Number,VertexInfoStructure> extends Clustering<VertexType,VertexInfoStructure>{
	
	public EdgeCutClustering(GraphInterface<VertexType,VertexInfoStructure> g) {
		super(g);
	}

	//TODO Not sure if works properly. Not Tested. Ignore for now
	/*public EdgeCutClustering(GraphInterface<VertexType> g, ClusteringParser parser){
		super(g);
		Map<Integer, Set<Integer>> vertexToCluster = parser.getVertexToClustersMap();
		for(Integer v: vertexToCluster.keySet()){
			for(Integer c: vertexToCluster.get(v)){
				applyLabel(v, c);
			}
		}
	}*/

	protected void applyLabel(VertexType v, int clusterId){
		BasicVertexInfo vi = m_graph.getVertex(v);
		if (VertexFactory.isVertexInfo(vi))
			((VertexInfo)vi).addCluster(clusterId);
		addToMap(m_clusterToVertices, clusterId, v);
	}
	
	/**
	 * 
	 * @param v
	 * @return Array of set of indices where first element is set of internal neighbors.
	 * 2nd is set of border neighbors, 3rd is set of external neighbors.
	 */
	protected Set<VertexType>[] getNeighbors(VertexType v) {
		if(!m_isClustered || !VertexFactory.isVertexInfo(m_graph.getVertex(v)))
			throw new IllegalStateException();
		Set<VertexType> bN = new FastSet<VertexType>(), iN = new FastSet<VertexType>(), eN= new FastSet<VertexType>();
		
		int fromC =((VertexInfo) m_graph.getVertex(v)).getClusters().get(0);
		
		for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e: m_graph.getOutgoingEdges(v)){
			VertexType w = e.getNeighbor(v);
			BasicVertexInfo wInfo = m_graph.getVertex(w);
			
			if(((VertexInfo)wInfo).getClusters().contains(fromC)) {
				if(((VertexInfo)wInfo).isBorder())
					bN.add(w);
				else
					iN.add(w);
			}
			else {
				eN.add(w);
			}
		}
		Set[] ret = {iN, bN, eN};
		return ret;
	}
	
	protected void computeBorderVertices(){
		Collection<? extends AbstractSimpleEdge<VertexType,VertexInfoStructure>>  edges = m_graph.getEdges();
		for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e: edges){
			VertexType v0 = e.getV0();
			VertexType v1 = e.getV1();
			BasicVertexInfo vi0 = m_graph.getVertex(v0);
			BasicVertexInfo vi1 = m_graph.getVertex(v1);
			if (VertexFactory.isVertexInfo(vi0)||VertexFactory.isVertexInfo(vi1)){
				int c0 = ((VertexInfo)vi0).getClusters().get(0);
				int c1 = ((VertexInfo)vi1).getClusters().get(0);
				if(c0 != c1){
					addToMap(m_clusterToBorders, c0, v0);
					addToMap(m_clusterToBorders, c1, v1);
					((VertexInfo)vi0).setBorder(true);
					((VertexInfo)vi1).setBorder(true);
				}
			}
		}
	}
	
	public void generateClusters(){
		if(m_isClustered)
			return;
		doClustering();
		computeBorderVertices();
		m_isClustered = true;
	}
	
	public List<Integer> getClusters(VertexType v)
	{
		if (VertexFactory.isVertexInfo(m_graph.getVertex(v)))
			return ((VertexInfo)m_graph.getVertex(v)).getClusters();
		else 
			throw new IllegalArgumentException("the Vertex must be VertexInfo");
	}

	@Override
	protected void doClustering(){
		throw new NotImplementedException();
	}
	
	protected Set<Integer> neighboursOfCluster(int c){
		Set<Integer> n = new FastSet<Integer>();
		n.add(c);
		for(VertexType v: getBorderVertices(c)){
			for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e: m_graph.getOutgoingEdges(v)){
				n.add(getClusters(e.getNeighbor(v)).get(0));				
			}
		}
		n.remove(c);
		return n;
	}

}

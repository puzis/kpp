package algorithms.structuralEquivalence;

import java.util.HashMap;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.UnifiedTrafficMatrix;

import common.FastListNG;

public class StructuralEquivalenceUnifier {
	
	private GraphInterface<Index,BasicVertexInfo> m_originalGraph;
	private FastListNG<FastListNG<Index>> m_partitions;
	private GraphInterface<Index,BasicVertexInfo> m_unifiedGraph;
//	private AbsTrafficMatrix m_unifiedCW;
	Map<FastList<Index>, Index> m_verticesMap = new HashMap<FastList<Index>, Index>();
	
	// A mapping between the original vertices and the indexes of the partitions they belong to.
	private Map<Index, Index> m_vertex_partition_map = new HashMap<Index, Index>();
	
	public StructuralEquivalenceUnifier(GraphInterface<Index,BasicVertexInfo> graph){
		m_originalGraph = graph;
	}
	
	public void run(){
		buildUnifiedGraph();
	}
	
	// Map between the original vertices and the indexes of the partitions they belong to (which is the index of the unified vertex in the unified graph).
	private void mapVertexToUnifiedVertex(FastList<Index> unifiedVertices, Index unifiedVertexIdx){
		for(FastList.Node<Index> uvNode=unifiedVertices.head(), end=unifiedVertices.tail(); (uvNode = uvNode.getNext())!=end;){
			Index v = uvNode.getValue();
			m_vertex_partition_map.put(v, unifiedVertexIdx);
		}
	}
	
	/**
	 * Return the index of the unified vertex in the unified graph that contains the given 
	 * original vertex.
	 * 
	 * @param originalVertex
	 */
	public Index getContainingUnifiedVertex(Index originalVertex){
		return m_vertex_partition_map.get(originalVertex);
	}
	
	public Index getContainingUnifiedVertex(int originalVertex){
		return getContainingUnifiedVertex(Index.valueOf(originalVertex));
	}
	
	private void buildUnifiedGraph(){
		StructuralEquivalenceExtractor<Index,BasicVertexInfo> seExtractor = new StructuralEquivalenceExtractor<Index,BasicVertexInfo>(m_originalGraph);
		m_partitions = seExtractor.getPartitions();
				
		int index=0;
		for(FastListNG.Node<FastListNG<Index>> pNode=m_partitions.head(), end=m_partitions.tail(); (pNode = pNode.getNext())!=end;){
			FastList<Index> unifiedVertices = pNode.getValue();
			Index idx = Index.valueOf(index);
			m_verticesMap.put(unifiedVertices, idx);
			// Map between the original vertices and the index of the unified vertex in the unified graph.
			mapVertexToUnifiedVertex(unifiedVertices, idx);
			index++;
		}
		
		m_unifiedGraph = new GraphAsHashMap<Index,BasicVertexInfo>();
		
		for(FastListNG.Node<FastListNG<Index>> pNode=m_partitions.head(), end=m_partitions.tail(); (pNode = pNode.getNext())!=end;){
			FastList<Index> unifiedVertices = pNode.getValue();
			Index uvIdx = m_verticesMap.get(unifiedVertices);
			VertexInfo vInfo = new UnifiedVertexInfo<Index>(uvIdx.intValue(), uvIdx.toString(), unifiedVertices);//@Rami: modified
			if (!m_unifiedGraph.isVertex(uvIdx))
				m_unifiedGraph.addVertex(uvIdx, vInfo);
			
			Index v = unifiedVertices.getFirst();
			for(FastListNG.Node<FastListNG<Index>> pNeighborNode=m_partitions.head(), endNeighbor=m_partitions.tail(); (pNeighborNode = pNeighborNode.getNext())!=endNeighbor;){
				FastListNG<Index> neighbor = pNeighborNode.getValue();
				if (!neighbor.equals(unifiedVertices)){
					if (m_originalGraph.isEdge(v, neighbor.getFirst())){
						Index neighborIdx = m_verticesMap.get(neighbor);
						vInfo = new UnifiedVertexInfo<Index>(neighborIdx.intValue(), neighborIdx.toString(), neighbor); //@Rami: modified
						if (!m_unifiedGraph.isVertex(neighborIdx))
							m_unifiedGraph.addVertex(neighborIdx, vInfo);
						if(!m_unifiedGraph.isEdge(uvIdx, neighborIdx))
							m_unifiedGraph.addEdge(uvIdx, neighborIdx);
					}
				}
			}
		}
	}
		
	public GraphInterface<Index,BasicVertexInfo> getUnifiedGraph(){
		return m_unifiedGraph;
	}

	public AbsTrafficMatrix getUnifiedCW() {
		return new UnifiedTrafficMatrix(m_unifiedGraph.getNumberOfVertices(), m_originalGraph, m_unifiedGraph);
//		return m_unifiedCW;
	}
	
	public FastListNG<FastListNG<Index>> getEquivalenceClasses(){
		return m_partitions;
	}
}
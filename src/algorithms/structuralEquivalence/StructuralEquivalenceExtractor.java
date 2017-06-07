package algorithms.structuralEquivalence;

import java.util.HashMap;
import java.util.Map;

import topology.AbstractSimpleEdge;
import topology.GraphInterface;

import common.FastListNG;

public class StructuralEquivalenceExtractor<VertexType,VertexInfoStructure> {
	private GraphInterface<VertexType,VertexInfoStructure> m_graph;
	private FastListNG<FastListNG<VertexType>> partitions;
	
	/** Computation of maximal strong structural equivalence of a graph (MSE)
	 *  @param graph
	 */
	public StructuralEquivalenceExtractor(GraphInterface<VertexType,VertexInfoStructure> graph){
		m_graph = graph;
		run();
	}
	
	private void run(){
		partitions = new FastListNG<FastListNG<VertexType>>();
		FastListNG<VertexType> vClass = new FastListNG<VertexType>();
		Map<VertexType, FastListNG<VertexType>> vertexClassMap = new HashMap<VertexType, FastListNG<VertexType>>();
		
		for (VertexType v : m_graph.getVertices()){
		    vClass.add(v);
		    vertexClassMap.put(v, vClass);
		}
	    partitions.add(vClass);

		for (VertexType v : m_graph.getVertices()){
			/** Scan the outgoing edges of v. */
			for(AbstractSimpleEdge<VertexType,VertexInfoStructure> vu: m_graph.getOutgoingEdges(v)){
				/** Foreach outgoing edge (v,u) determine the class C of u. 
				 *  A vertex must permit access to its class in constant time. */
				VertexType u = vu.getNeighbor(v);
				
				FastListNG<VertexType> cClass = vertexClassMap.get(u);
				
				FastListNG<VertexType> cTagClass = new FastListNG<VertexType>();
				/** Move all v's outgoing vertices from C to C'. */
				cClass.remove(u); cTagClass.add(u);
				vertexClassMap.put(u, cTagClass);
				
				for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e:m_graph.getOutgoingEdges(v)){
					u = e.getNeighbor(v);
					if (cClass.contains(u)){
						cClass.remove(u); cTagClass.add(u);
						vertexClassMap.put(u, cTagClass);
					}
				}
				partitions.add(cTagClass);
				if (cClass.size()==0)	/** Eliminate C if C is now empty. */
					partitions.remove(cClass);
			}
			/** Foreach class C to which an incoming neighbor of v belongs to. */
			for(AbstractSimpleEdge<VertexType,VertexInfoStructure> vu: m_graph.getIncomingEdges(v)){
				/** Foreach incoming edge (v,u) determine the class C of u. */
				VertexType u = vu.getNeighbor(v);
				FastListNG<VertexType> cClass = vertexClassMap.get(u);
				
				FastListNG<VertexType> cTagClass = new FastListNG<VertexType>();
				/** Move all v's incoming vertices from C to C'. */
				cClass.remove(u); cTagClass.add(u);
				vertexClassMap.put(u, cTagClass);
				
				for(AbstractSimpleEdge<VertexType,VertexInfoStructure> e: m_graph.getOutgoingEdges(v)){
					u = e.getNeighbor(v);
					if (cClass.contains(u)){
						cClass.remove(u); cTagClass.add(u);
						vertexClassMap.put(u, cTagClass);
					}
				}
				partitions.add(cTagClass);
				if (cClass.size()==0)	/** Eliminate C if C is now empty. */
					partitions.remove(cClass);
			}
		}
	}
	
	public FastListNG<FastListNG<VertexType>> getPartitions(){
		return partitions;
	}
}
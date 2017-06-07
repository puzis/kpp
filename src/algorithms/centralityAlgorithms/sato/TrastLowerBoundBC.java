package algorithms.centralityAlgorithms.sato;

import java.util.HashSet;
import java.util.Set;

import javolution.util.Index;
import server.common.DummyProgress;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.clustering.EdgeCutClustering;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class TrastLowerBoundBC extends BrandesBC {
	EdgeCutClustering<Index,BasicVertexInfo> m_clustering;
	boolean isFlipped;

	public TrastLowerBoundBC (GraphInterface<Index,BasicVertexInfo> sato, EdgeCutClustering<Index,BasicVertexInfo> clustering) {
		m_clustering = clustering;
		isFlipped = false;
		Index sources[] = new Index[sato.getNumberOfVertices()];
		int i = 0;
		for (int c : clustering.getClusterIds()) {
			Set<Index> vertices = clustering.getVertices(c);
			Set<Index> borders = clustering.getBorderVertices(c);
			for(Index v : vertices) {
				if(!clustering.isBorder(v)) {
					sources[i++] =v; 
				}
			}
			for(Index v : borders) {
				sources[i++] = v;
			}
		}
		super.init(ShortestPathAlg.DIJKSTRA, sato, sources, new DummyProgress(), 0, null);
	}

	@Override
	public void beforeSingleSourceShortestPaths(int source) {
		Index s = Index.valueOf(source);
		boolean border = false;
		if (VertexFactory.isVertexInfo(m_graph.getVertex(Index.valueOf(source))))
			border = ((VertexInfo)m_graph.getVertex(Index.valueOf(source))).isBorder();
		int cluster = m_clustering.getClusters(s).get(0);
		if(!isFlipped && !border) {
			isFlipped = flipStubs(cluster);
		}
		if(isFlipped && border ) {
			flipStubs(cluster);
			isFlipped = false;
		}
	}

	protected boolean flipStubs(int cluster) {
		boolean hasEdges = false;
		Iterable<? extends AbstractSimpleEdge<Index,BasicVertexInfo>> sourceEdges;
		Set<AbstractSimpleEdge<Index,BasicVertexInfo>> toRemove;
		for(Index v : m_clustering.getVertices(cluster)) {
			//if the vertex is not VertexInfo the default border is false.    
			//if the first condition is TRUE the second condition will not be checked 
			if(!VertexFactory.isVertexInfo(m_graph.getVertex(v))||!((VertexInfo)m_graph.getVertex(v)).isBorder()) {
				if(!isFlipped) {
					sourceEdges = m_graph.getIncomingEdges(v);
				}
				else {
					sourceEdges = m_graph.getOutgoingEdges(v);
				}

				toRemove = new HashSet<AbstractSimpleEdge<Index,BasicVertexInfo>>();
				for (AbstractSimpleEdge<Index,BasicVertexInfo> e : sourceEdges) {
					m_graph.addEdge(e.flip(), m_graph.getEdgeWeight(e));
					toRemove.add(e);
					hasEdges = true;
				}
				for(AbstractSimpleEdge<Index,BasicVertexInfo> e : toRemove) {
					m_graph.removeEdge(e);
				}
			}
		}
		return hasEdges;
	}
}

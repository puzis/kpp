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
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class TrastUpperBoundBC extends BrandesBC {

	@Override
	public void beforeSingleSourceShortestPaths(int source) {
		flip(source, true);
	}
	
	protected void flip(int source, boolean incoming) {
		Index s = Index.valueOf(source);
		if(!VertexFactory.isVertexInfo(m_graph.getVertex(s))||!((VertexInfo)m_graph.getVertex(s)).isBorder()) {
			Iterable<? extends AbstractSimpleEdge<Index,BasicVertexInfo>> sourceEdges;
			if(incoming) {
				sourceEdges = m_graph.getIncomingEdges(s);
			}
			else {
				sourceEdges = m_graph.getOutgoingEdges(s);
			}
			Set<AbstractSimpleEdge<Index,BasicVertexInfo>> toRemove = new HashSet<AbstractSimpleEdge<Index,BasicVertexInfo>>();
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : sourceEdges) {
				m_graph.addEdge(e.flip(), m_graph.getEdgeWeight(e));
				toRemove.add(e);
				//m_graph.removeEdge(e);
			}
			for(AbstractSimpleEdge<Index,BasicVertexInfo> e : toRemove) {
				m_graph.removeEdge(e);
			}
		}
	}
	
	@Override
	public void afterAccumulation(int source) {
        for (int i=0;i<m_BC.length && !m_progress.isDone();i++){
        	m_BC[i] += m_delta[i];
        }        
        flip(source, false);
	}
	
	public TrastUpperBoundBC(GraphInterface<Index,BasicVertexInfo> sato) {
		super.init(ShortestPathAlg.DIJKSTRA, sato, null, new DummyProgress(), 0, null);
	}

}

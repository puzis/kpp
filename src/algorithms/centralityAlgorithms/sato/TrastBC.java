package algorithms.centralityAlgorithms.sato;

import javolution.util.Index;
import server.common.DummyProgress;
import topology.AbstractHyperEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.clustering.Clustering;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;


public class TrastBC extends BrandesBC {

	protected int m_sourceCluster = -1;
	protected boolean m_isSourceBorder;
	protected boolean[] m_verticesInSourceCluster;
	protected Clustering<Index,BasicVertexInfo> m_clustering;	
	
	@Override
	public void beforeSingleSourceShortestPaths(int s){
		if(m_sourceCluster >= 0){
			for(Index v: m_clustering.getVertices(m_sourceCluster)){
				m_verticesInSourceCluster[v.intValue()] = false;
			}
		}
		
		m_sourceCluster = m_clustering.getClusters(Index.valueOf(s)).get(0);
		for(Index v: m_clustering.getVertices(m_sourceCluster)){
			m_verticesInSourceCluster[v.intValue()] = true;
		}
		m_isSourceBorder = m_clustering.isBorder(Index.valueOf(s));
	}
		

	public TrastBC(Clustering<Index,BasicVertexInfo> c, ShortestPathAlg alg)  throws Exception{
		super();
		GraphInterface<Index,BasicVertexInfo> satoGraph = (new SatoGraphBuilder(c, alg)).buildSATOGraph();
		m_clustering = c;
		m_verticesInSourceCluster = new boolean[satoGraph.getNumberOfVertices()];
		super.init( ShortestPathAlg.DIJKSTRA, satoGraph, null, new DummyProgress(), 0, null);		
	}

	
	@Override
	public boolean isExpandable(Index v){
		if(m_clustering.isBorder(v) || v.intValue() == m_currentSource)
			return true;
		else
			return false;
	}
	
	@Override
	public void vertexDiscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge){
		int wi = w.intValue();
		if(m_verticesInSourceCluster[wi] && !m_clustering.isBorder(w) && !m_isSourceBorder )
			return;
		m_sigma[wi]=0;
		m_P.get(wi).clear();
    	updateSigma(v,w);
    	updatePredecessors(v,w);
	}

	@Override
	public void vertexRediscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge, double dist){
		int wi = w.intValue();
		if( m_verticesInSourceCluster[wi] && !m_clustering.isBorder(w) && !m_isSourceBorder)
			return;
        if (m_d[wi] == dist){
        	updateSigma(v,w);
        	updatePredecessors(v,w);
        }		
	}
	
	public GraphInterface<Index,BasicVertexInfo> getGraph(){
		return m_graph;
	}
}

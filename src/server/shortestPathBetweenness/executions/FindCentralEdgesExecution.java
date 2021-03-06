package server.shortestPathBetweenness.executions;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.betweenness.brandes.AbsGreedyEdgeBetweeness;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;

public class FindCentralEdgesExecution extends AbstractExecution
{
	private FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> m_candidates = null;
	private int[] m_givenVertices = null;
	private AbstractSimpleEdge<Index,BasicVertexInfo>[] m_givenEdges = null;
	private DataWorkshop m_dataWorkshop = null;
	private Algorithm m_alg; 
	private Bound m_boundType;
	private double m_bound = 0;
	private Object [] m_centralEdges = null;
	
	public FindCentralEdgesExecution(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, int[] givenVertices, AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, DataWorkshop dataWorkshop, 
			Algorithm alg, Bound boundType, double bound){
		m_candidates = candidates;
		m_givenVertices = givenVertices;
		m_givenEdges = givenEdges;
		m_dataWorkshop = dataWorkshop;
		m_alg = alg;
		m_boundType = boundType;
		m_bound = bound;
	}

	@Override
	public Object[] getResult() {
		return m_centralEdges;
	}

	@Override
	public void run() 
	{
		try{
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
			AbstractSimpleEdge<Index,BasicVertexInfo>[] ce = AbsGreedyEdgeBetweeness.findEdges(m_candidates, m_givenVertices, m_givenEdges, m_dataWorkshop, m_alg, m_boundType, m_bound, this, 1);
			m_centralEdges = new Object [ce.length*2];
			
			int i = 0;
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : ce)
			{
				m_centralEdges[i++] = new Integer(((Index)e.getV0()).intValue());
				m_centralEdges[i++] = new Integer(((Index)e.getV1()).intValue());
			}
			reportSuccess(AbstractExecution.PHASE_SUCCESS, AbstractExecution.PHASE_COMPLETE);
			
		}
		catch(Exception ex)
		{
			LoggingManager.getInstance().writeSystem(ex.getMessage(), "FindCentralEdgesExecution", "run", ex);
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
		}
	}
}

package server.degree.executions;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import server.common.ServerConstants.Algorithm;
import server.common.ServerConstants.Bound;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import algorithms.centralityAlgorithms.degree.AbsGreedyDegree;
import algorithms.centralityAlgorithms.degree.DegreeAlgorithm;

public class FindCentralEdgesExecution extends AbstractExecution
{
	private FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> m_candidates = null;
	private int[] m_givenVertices = null;
	private AbstractSimpleEdge<Index,BasicVertexInfo>[] m_givenEdges = null;
	private DegreeAlgorithm m_degreeAlgorithm = null;
	private Algorithm m_alg; 
	private Bound m_boundType;
	private double m_bound = 0;
	private Object [] m_centralEdges = null;
	
	public FindCentralEdgesExecution(DegreeAlgorithm degreeAlgorithm, Algorithm alg, FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> candidates, int[] givenVertices, 
			AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, Bound boundType, int bound){
		m_candidates = candidates;
		m_givenVertices = givenVertices;
		m_givenEdges = givenEdges;
		m_degreeAlgorithm = degreeAlgorithm;
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
			AbstractSimpleEdge<Index,BasicVertexInfo>[] ce = AbsGreedyDegree.findEdges(m_degreeAlgorithm, m_alg, m_candidates, m_givenVertices, m_givenEdges, m_boundType, (int)m_bound, this, 1);
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
			LoggingManager.getInstance().writeSystem(ex.getMessage(), "FindCentralVerticesExecution", "run", ex);
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
		}
	}
}

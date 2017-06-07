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

public class FindCentralVerticesExecution extends AbstractExecution
{
	private FastList<Index> m_candidates = null;
	private int[] m_givenVertices = null;
	private AbstractSimpleEdge<Index,BasicVertexInfo>[] m_givenEdges = null;
	private DegreeAlgorithm m_degreeAlgorithm = null;
	private Algorithm m_alg; 
	private Bound m_boundType;
	private double m_bound = 0;
	private Object [] m_centralVertices = null;
	
	public FindCentralVerticesExecution(DegreeAlgorithm degreeAlgorithm, Algorithm alg, FastList<Index> candidates, int[] givenVertices, 
			AbstractSimpleEdge<Index,BasicVertexInfo>[] givenEdges, Bound boundType, double bound){
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
		return m_centralVertices;
	}

	@Override
	public void run() 
	{
		try{
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
			Index [] cv = AbsGreedyDegree.findVertices(m_degreeAlgorithm, m_alg, m_candidates, m_givenVertices, m_givenEdges, m_boundType, (int)m_bound, this, 1);
			m_centralVertices = new Object [cv.length];
			
			int i = 0;
			for (Index v : cv)
			{
				m_centralVertices[i++] = new Integer(((Index)v).intValue());
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
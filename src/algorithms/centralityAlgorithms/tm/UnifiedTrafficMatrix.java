package algorithms.centralityAlgorithms.tm;

import java.util.HashMap;
import java.util.Map;

import javolution.util.Index;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.structuralEquivalence.UnifiedVertexInfo;

public class UnifiedTrafficMatrix extends AbsTrafficMatrix{
	
	private static final long serialVersionUID = 1L;

	private Map<Index, Double> m_tm = new HashMap<Index, Double>();
	
	// The original (non unified) traffic matrix.
	private GraphInterface<Index,BasicVertexInfo> m_originalGraph;
	
	private GraphInterface<Index,BasicVertexInfo> m_unifiedGraph; 
	
	public UnifiedTrafficMatrix(int dimensionSize, GraphInterface<Index,BasicVertexInfo> originalGraph, GraphInterface<Index,BasicVertexInfo> unifiedGraph){
		m_matrixDimensions = dimensionSize;
		m_originalGraph = originalGraph;
		m_unifiedGraph = unifiedGraph;
	}
	
	/**
	 * cells[i,j] (i<>j) can be computed on every access and 
	 * cells[i,i] can be computed on-demand and stored in an array.
	 * 
	 * @see algorithms.centralityAlgorithms.tm.AbsTrafficMatrix#getWeight(int, int)
	 */
	@Override
	public double getWeight(int i, int j) {
		Index iIdx = Index.valueOf(i);
		Index jIdx = Index.valueOf(j);
		if (i==j){
			if (m_tm.containsKey(iIdx)) 
				return m_tm.get(iIdx);
			
			//consider communication within equivalence class:					
			//1) consider end points
			int iMultiplicity=1;
			if (VertexFactory.isVertexInfo( m_unifiedGraph.getVertex(iIdx)))
				iMultiplicity = ((VertexInfo)m_unifiedGraph.getVertex(iIdx)).getMultiplicity();

			double tm = 2*(iMultiplicity)*(iMultiplicity-1);
			
			//consider communication within equivalence class:					
			//2) consider intermediate vertices - neighbors
			for(AbstractSimpleEdge<Index,BasicVertexInfo> e: m_unifiedGraph.getOutgoingEdges(iIdx)){
				Index neighbor = e.getNeighbor(iIdx);
				int nMultiplicity=1;
				if (VertexFactory.isVertexInfo(m_unifiedGraph.getVertex(neighbor)))
					nMultiplicity = ((VertexInfo)m_unifiedGraph.getVertex(neighbor)).getMultiplicity();
			
				Index memberOfn = ((UnifiedVertexInfo<Index>)m_unifiedGraph.getVertex(neighbor)).getVertices().getFirst();
				int numberOfNeighbors = m_originalGraph.getDegree(memberOfn);
				
				tm += ((float)(nMultiplicity*(nMultiplicity-1))/numberOfNeighbors)*iMultiplicity;
			}

			m_tm.put(iIdx, new Double(tm));
			return tm;
		}
		else{
			//consider communication between equivalence classes:
			int iMultiplicity=1;
			int jMultiplicity=1;
			if(VertexFactory.isVertexInfo(m_unifiedGraph.getVertex(iIdx))&&
					VertexFactory.isVertexInfo(m_unifiedGraph.getVertex(jIdx))){
				iMultiplicity = ((VertexInfo)m_unifiedGraph.getVertex(iIdx)).getMultiplicity();
				jMultiplicity = ((VertexInfo)m_unifiedGraph.getVertex(jIdx)).getMultiplicity();
			}
			double tm = iMultiplicity * jMultiplicity;
			return tm;
		}
	}

	@Override
	public void setAllWeights(double w) {
		throw new NotImplementedException();
	}

	@Override
	public void setWeight(int i, int j, double w) {
		throw new NotImplementedException();
	}

	@Override
	public void mul(double a) {
		throw new NotImplementedException();
	}
	
}


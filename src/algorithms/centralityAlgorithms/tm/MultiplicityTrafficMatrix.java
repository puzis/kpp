package algorithms.centralityAlgorithms.tm;

import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.HyperGraphInterface;

public class MultiplicityTrafficMatrix extends AbsTrafficMatrix {
	HyperGraphInterface<Index, ? extends BasicVertexInfo> m_hgraph;
	public MultiplicityTrafficMatrix(HyperGraphInterface<Index,? extends BasicVertexInfo> hgraph) {
		m_hgraph = hgraph;
	}
	@Override
	public double getWeight(int i, int j) {
		if(i==j) {
			int k=m_hgraph.getVertex(Index.valueOf(i)).getMultiplicity();
			return 2*k*(k-1);
		}
		if(!m_hgraph.isVertex(Index.valueOf(i)) || !m_hgraph.isVertex(Index.valueOf(i)))
			return 0;
		return m_hgraph.getVertex(Index.valueOf(i)).getMultiplicity()*m_hgraph.getVertex(Index.valueOf(j)).getMultiplicity();
	}

	@Override
	public void setWeight(int i, int j, double w) {
		throw new UnsupportedOperationException("Method setWeight is not supported in MultiplicityTrafficMatrix.");		
	}

	@Override
	public void setAllWeights(double w) {
		throw new UnsupportedOperationException("Method setWeight is not supported in MultiplicityTrafficMatrix.");		
	}

	@Override
	public void mul(double a) {
		throw new UnsupportedOperationException("Method setWeight is not supported in MultiplicityTrafficMatrix.");		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Index v: m_hgraph.getVertices()) {
			sb.append(v.intValue()+" : ");
			for(Index w : m_hgraph.getVertices()) {
				sb.append(getWeight(v.intValue(), w.intValue()));
				sb.append(' ');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}

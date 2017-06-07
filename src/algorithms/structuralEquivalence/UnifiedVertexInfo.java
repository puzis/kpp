package algorithms.structuralEquivalence;

import javolution.util.FastList;
import topology.VertexInfo;

public class UnifiedVertexInfo<VertexType> extends VertexInfo {

	private static final long serialVersionUID = 1L;
	private FastList<VertexType> m_vertices;
	
	//@Rami: modified
    public UnifiedVertexInfo(int vertexNum, String label, FastList<VertexType> vertices){
        super(vertexNum, label, vertices.size());
        m_vertices = vertices;
    }
    
    
    @Override
    public String toString(){
    	return super.toString();//+ " UnifiedVertices '" + m_vertices.toString() +"'";
    }

	public FastList<VertexType> getVertices() {
		return m_vertices;
	}
}

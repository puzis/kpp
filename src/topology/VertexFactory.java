package topology;


import javolution.util.FastMap;

import common.Pair;

public class VertexFactory {

	public static BasicVertexInfo createVertexStructure(GraphFactory.VertexInfoType vertexInfoType,int vertexNum, String label,double x,double y,double z,FastMap<String , Pair<String, String>>  info)
	{
		BasicVertexInfo vertex; 
		switch(vertexInfoType){
		case VERTEX_INFO:
			vertex = new VertexInfo(vertexNum, label, x, y, z, info);
			break;
		default:
			throw new IllegalArgumentException("Vertex structure is not recognized");
		}
		return vertex;		
	}
	
	// TODO: when Vertex will be usable, it should run 
//	public static Vertex createVertexStructure(String vertexStructure,int vertexNum, String label,double x,double y,double z){
//		if  (vertexStructure.equals("Vertex"))
//			return new Vertex(vertexNum, label, x, y, z);
//		else 
//			throw new IllegalArgumentException("vertex structure is not recognized");
//
//	}
	
	public static BasicVertexInfo createVertexStructure(GraphFactory.VertexInfoType vertexInfoType,int vertexNum, String label){
		BasicVertexInfo vertex; 
		switch(vertexInfoType){
		case VERTEX_INFO:
			vertex = new VertexInfo(vertexNum, label);
			break;
		default:
			throw new IllegalArgumentException("vertex structure is not recognized");
		}
		return vertex; 
	}
	
	
	public static boolean isVertexInfo (BasicVertexInfo vertex){
		if (vertex instanceof VertexInfo)
			return true;
		else 
			return false; 
	}
}

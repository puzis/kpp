package tests.closeness;

import javolution.util.Index;

import topology.BasicVertexInfo;
import topology.GraphAsHashMap;

public class TriangleGraph extends GraphAsHashMap<Index,BasicVertexInfo>{
	
	public TriangleGraph()
	{
		int i = 0;
		this.addVertex(Index.valueOf(i++));
		this.addVertex(Index.valueOf(i++));
		this.addVertex(Index.valueOf(i++));
		i = 0;
		this.addEdge(Index.valueOf(i++),Index.valueOf(i));
		this.addEdge(Index.valueOf(i++),Index.valueOf(i));
		this.addEdge(Index.valueOf(i++),Index.valueOf(i%3));
	}

}

package tests.closeness;

import javolution.util.Index;

import topology.BasicVertexInfo;
import topology.GraphAsHashMap;

public class LineGraph extends GraphAsHashMap<Index,BasicVertexInfo> {

	public LineGraph(int length)
	{
		super();
		for (int i = 0; i < length; i++) {
			this.addVertex(Index.valueOf(i));
		}
		for (int i = 0; i < length-1; i++) {
			this.addEdge(Index.valueOf(i), Index.valueOf(i+1));
		}
	}
}

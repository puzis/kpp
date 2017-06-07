package algorithms.centralityAlgorithms.betweenness.bcc;

import topology.BasicVertexInfo;
import topology.VertexInfo;

public class BiConnectedVertexInfo extends VertexInfo
{
	private static final long serialVersionUID = 1L;
	private int unmarkedNeighborsCounter = 0;
	
	public int getUnmarkedNeighborsCounter(){	return unmarkedNeighborsCounter;	}
	public void increaseUnmarkedNeighborsCounter()
	{	
		unmarkedNeighborsCounter++;	
	}
	public void decreaseUnmarkedNeighborsCounter(){	unmarkedNeighborsCounter--;	}
	
	@Override
	public String toString(){
		return super.toString() + " " + "unmarkedNeighbors " + unmarkedNeighborsCounter;
	}
}

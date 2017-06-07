package topology;

import javolution.util.FastList;
import javolution.util.Index;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;

/**
 * This class is responsible for creating the *.txt file.
 * 
 * @author Polina Zilberman
 *
 */
public class GraphPrinter 
{
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	private int m_dt_Vertices_cnt = 0;
	private BasicVertexInfo vInfo;
	
	public GraphPrinter(GraphInterface<Index,BasicVertexInfo> graph)
	{
		this.m_graph = graph;
	}
	
	public String getVerticesStr()
	{
		m_dt_Vertices_cnt = 0;
		StringBuilder res = new StringBuilder();
		res.append("*Vertices ").append(m_graph.getNumberOfVertices()).append("\n");
		for (int i = 0; i < m_graph.getNumberOfVertices(); i++)// && !((Thread.currentThread() instanceof ExecutionInterface) && ((ExecutionInterface)Thread.currentThread()).isDone()); i++)
		{
			vInfo = m_graph.getVertex(Index.valueOf(i));
			if (vInfo!=null){
				res.append(vInfo);
				if (VertexFactory.isVertexInfo(vInfo)){
					if (((VertexInfo)vInfo).getNetwork().equalsIgnoreCase("'DT'"))
						m_dt_Vertices_cnt++;
				} 
					
			}
			res.append("\n");
		}
		return res.toString();
	}
	
	public String getEdgesStr(DataWorkshop dw)
	{
		StringBuilder res = new StringBuilder();
		if (!m_graph.isDirected())
			res.append("*Edges\n");
		else
			res.append("*Arcs\n");
		for(AbstractSimpleEdge<Index,BasicVertexInfo> e : m_graph.getEdges()) {
        	res.append(" ").append(e.getV0().intValue() + 1).append(" ").append(e.getV1().intValue() + 1).append(" ").append(m_graph.getEdgeWeight(e))
        		.append(" Betweenness ").append(dw.getPairBetweenness(e.getV0().intValue(), e.getV1().intValue())/dw.getCommunicationWeight()).append("\n");
        }
		return res.toString();
	}

	public String getEdgesStr()
	{
		StringBuilder res = new StringBuilder();
		if (!m_graph.isDirected())
			res.append("*Edges\n");
		else
			res.append("*Arcs\n");
		for(AbstractSimpleEdge<Index,BasicVertexInfo> e : m_graph.getEdges()) {
        	res.append(" ").append(e.getV0().intValue()).append(" ").append(e.getV1().intValue())
        	.append(" ").append(m_graph.getEdgeWeight(e)).append("\n");
        }
		return res.toString();
	}
	
	public String getRoutingTableStr(FastList<Index> [][] routingTable)
	{
		StringBuilder res = new StringBuilder();
		res.append("*Routing\n[");
		for (int i = 0; i < m_graph.getNumberOfVertices(); i++)// && !((Thread.currentThread() instanceof ExecutionInterface) && ((ExecutionInterface)Thread.currentThread()).isDone()); i++)
		{
			res.append("[");
			for (int j = 0; j < m_graph.getNumberOfVertices(); j++)
			{
				res.append(routingTable[i][j]);
			}
			res.append("]");
		}
		res.append("]\n");
		return res.toString();
	}
	
	public String getDistanceMatrix(double[][] distances){
		StringBuilder res = new StringBuilder();
		res.append("*Distances\n[");
		for (int i = 0; i < m_graph.getNumberOfVertices(); i++)
		{
			res.append("[");
			for (int j = 0; j < m_graph.getNumberOfVertices(); j++)
			{
				res.append(distances[i][j]);
				if (j < m_graph.getNumberOfVertices()-1){
					res.append(",");
				}
			}
			res.append("]");
		}
		res.append("]\n");
		return res.toString();
	}
	
	public String getVertexBetweenessStr(DataWorkshop dw)
	{
		StringBuilder res = new StringBuilder();
		res.append("*Betweenness\n");
		for (int i = 0; i < m_graph.getNumberOfVertices(); i++)// && !((Thread.currentThread() instanceof ExecutionInterface) && ((ExecutionInterface)Thread.currentThread()).isDone()); i++)
		{
			res.append(i + 1).append("\t").append(dw.getBetweenness(i)/dw.getCommunicationWeight()).append("\n");
		}
		return res.toString();
	}
	
	public String getDT_Vertices()
	{
		return "*DT_Vertices\t" + m_dt_Vertices_cnt + "\n";
	}
	
	public String getAnalyzedFile(DataWorkshop dw)
	{
		StringBuilder res = new StringBuilder();
		res.append(getVerticesStr()).append(getEdgesStr(dw)).append(getRoutingTableStr(dw.getRoutingTable()))
			.append(getDistanceMatrix(dw.getDistanceMatrix()))
			.append(getVertexBetweenessStr(dw)).append(getDT_Vertices()).append("*END\n");
		return res.toString();
	}

}
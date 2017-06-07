package algorithms.centralityAlgorithms.randomWalkBetweeness;

import java.util.ArrayList;


import algorithms.centralityAlgorithms.BasicSet;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;

import common.Pair;

public class GroupRandomWalkBetweeness 
{
	private RandomWalkBetweeness m_rwb = null;
	private double m_groupBetweenness = 0;		/** Group RW Betweenness Centrality of currentGroup. */
	FastList<Index> m_members = new FastList<Index>();
	private double [][] m_degrees = null;
	private double [][] m_adjacencyMatrix = null;
	private ArrayList<FastList<Index>> m_G = null;
	
	public GroupRandomWalkBetweeness(GraphInterface<Index,BasicVertexInfo> graph)
	{
		ArrayList<FastList<Index>> G = retrieveNeighbors(graph);
		init(G);
	}
	public GroupRandomWalkBetweeness(ArrayList<FastList<Index>> G){ init(G); }
	
	private void init(ArrayList<FastList<Index>> G)
	{
		m_G = G;
		m_members.add(Index.valueOf(m_G.size() - 1));
		m_adjacencyMatrix = new double [m_G.size()][m_G.size()];
		m_degrees = new double[m_G.size()][m_G.size()];
		for (int i = 0; i < G.size(); i++)
		{
			FastList<Index> neighbors = G.get(i);
			for (FastList.Node<Index> nNode = neighbors.head(), end = neighbors.tail(); (nNode = nNode.getNext()) != end;)
			{
				int neighbor = nNode.getValue().intValue();
				m_adjacencyMatrix[i][neighbor] = 1;
			}
			m_degrees[i][i] = neighbors.size();
		}
		m_rwb = new RandomWalkBetweeness(m_degrees, m_adjacencyMatrix, m_G, m_members);
	}
	
	//double [] degrees, double [][] adjacencyMatrix, ArrayList<FastList<Index>> G
	public static double calculateGB(FastList<Object> group, GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage) throws Exception
	{
		ArrayList<FastList<Index>> G = retrieveNeighbors(graph);
		
		GroupRandomWalkBetweeness grwb = new GroupRandomWalkBetweeness(G);
    	double p = progress.getProgress();
    	
		for (Object member : group)
		{
			grwb.addMember(member);
			
			p += (1 / (double) group.size()) * percentage;	
			progress.setProgress(p);
		}

		double result = grwb.getGroupBetweenness();
		return result;
	}

	public static double calculateGB(FastList<Object> group, GroupRandomWalkBetweeness grwb, AbstractExecution progress, double percentage) throws Exception
	{
		double p = progress.getProgress();
    	
		for (Object member : group)
		{
			grwb.addMember(member);
			
			p += (1 / (double) group.size()) * percentage;	
			progress.setProgress(p);
		}

		double result = grwb.getGroupBetweenness();
		return result;
	}

	public void addMember(Object member) throws Exception
	{
		if (member instanceof Index)
		{
			addMember(((Index) member).intValue());
		}
		else if (member instanceof AbstractSimpleEdge)
		{
			addMember((AbstractSimpleEdge) member);
		}
		else
		{
			LoggingManager.getInstance().writeSystem("The given member is of invalid type: " + member.toString(), "CandidatesBasedAlgorithm", "addMember", null);
			throw new Exception ("The given member is of invalid type: " + member.toString());
		}
	}
	
	public void addMember(int v)
	{
		if (m_members.contains(Index.valueOf(v)))
			return;
		
		m_groupBetweenness += m_rwb.getVertexBetweeness(v);
		
		m_members.add(Index.valueOf(v));
		m_rwb = new RandomWalkBetweeness(m_degrees, m_adjacencyMatrix, m_G, m_members);
	}

	public void addMember(Pair<Index, Index> e)
	{
	}
	
	public double getGroupBetweenness() 
	{ 
		return m_groupBetweenness; 
	}
	
	private static ArrayList<FastList<Index>> retrieveNeighbors(GraphInterface<Index,BasicVertexInfo> graph)
	{
		ArrayList<FastList<Index>> G = new ArrayList<FastList<Index>>(graph.getNumberOfVertices());
		for(int i = 0; i < graph.getNumberOfVertices(); i++)
		{
			FastList<Index> neighbors = new FastList<Index>();
			Index iIndex = Index.valueOf(i);
			
			for(AbstractSimpleEdge<Index,BasicVertexInfo> e: graph.getOutgoingEdges(iIndex))
			{
				Index neighbour = e.getNeighbor( iIndex);
				neighbors.add(neighbour);
			}
			G.add(i, neighbors);
		}
		return G;
	}
	
	public double getVertexBetweeness(int v)
	{
		return m_rwb.getVertexBetweeness(v);
	}
	
	public static double calculateSumGroup(GraphInterface<Index,BasicVertexInfo> graph, Object[] group, AbstractExecution progress, double percentage) 
	{
		ArrayList<FastList<Index>> G = retrieveNeighbors(graph);
		GroupRandomWalkBetweeness grwb = new GroupRandomWalkBetweeness(G);
		
		double p = progress.getProgress();
    	
		double result = 0;
		for (Object member : group)
		{
			if (member instanceof Index)
			{
				result += grwb.getVertexBetweeness(((Index)member).intValue());
			}
			else
			{
				LoggingManager.getInstance().writeSystem("The given member is of invalid type: " + member.toString(), "CandidatesBasedAlgorithm", "calculateSumGroup", null);
				throw new IllegalArgumentException ("The given member is of invalid type: " + member.toString());
			}
    			
    		p += (1 / (double) group.length) * percentage;	
    		progress.setProgress(p);
    	}

   		return result;
	}
	
	public static double calculateSumGroup(GroupRandomWalkBetweeness grwb, Object[] group, AbstractExecution progress, double percentage) throws Exception
	{
		double p = progress.getProgress();
    	
		double result = 0;
		for (Object member : group)
		{
			if (member instanceof Index)
			{
				result += grwb.getVertexBetweeness(((Index)member).intValue());
			}
			else
			{
				LoggingManager.getInstance().writeSystem("The given member is of invalid type: " + member.toString(), "CandidatesBasedAlgorithm", "calculateSumGroup", null);
				throw new Exception ("The given member is of invalid type: " + member.toString());
			}
    			
    		p += (1 / (double) group.length) * percentage;	
    		progress.setProgress(p);
    	}

   		return result;
	}
}
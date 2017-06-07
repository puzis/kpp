package algorithms.centralityAlgorithms.degree;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastTable;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;

/**
 * Calculates the group degree of a given group of vertices.
 * Definition of group degree: 
 * Number of non - group nodes that are connected to group members plus number of group nodes.
 * 
 * @author Polina Zilberman
 *
 */
public class GroupDegreeAlgorithm 
{
//	private FastMap<Index, Index> m_degrees = null;
//	private FastList<Index> m_candidates = new FastList<Index>();
	private FastMap<Index, FastList<Index>> m_neighbors = null;
	private int[] m_vertices = null;
	private int m_groupDegree = 0;
	
	public GroupDegreeAlgorithm(GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage) 
	{
		m_vertices = new int[graph.getNumberOfVertices()];	int i = 0;
		for (Index v : graph.getVertices()){
			m_vertices[i++] = v.intValue();
		}
		DegreeAlgorithm singleVertexDegree = new DegreeAlgorithm(m_vertices, graph, progress, percentage * 0.89);
//		m_degrees = singleVertexDegree.getDegrees();
		m_neighbors = singleVertexDegree.getNeighbors();

		progress.setProgress(0.99);
	}
	
	public GroupDegreeAlgorithm(DegreeAlgorithm degreeAlgorithm, AbstractExecution progress, double percentage) throws Exception
	{
		m_neighbors = degreeAlgorithm.getNeighbors();
		m_vertices = degreeAlgorithm.getVertices();
		
		progress.setProgress(0.99);
	}
	
	public static double calculateSumGroup(Object[] group, GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage) 
	{
		GroupDegreeAlgorithm alg = new GroupDegreeAlgorithm(graph, progress, percentage);
		double result = 0;
		
		for (Object member : group)
		{
			double p = progress.getProgress();
			
			if (member instanceof Index)
			{
				result += alg.getDegree(((Index)member).intValue());
			}
			else
			{
				result += 2;
			}
        	p += (1 / (double) group.length) * percentage;	
			progress.setProgress(p);
		}
		return result;
	}
	
	/**
	 * Goes through all group members and adds the number of their neighbors (those who weren't taken into account earlier).
	 * 
	 * @param group
	 * @param graph
	 * @return number of vertices that are neighbors of the group, but are not members of it.
	 */
	public static int calculateGroupDegree(FastList<Index> group, GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage)
	{
		int result = 0;
		FastTable<Index> takenIntoAccount = new FastTable<Index>();
		for (FastList.Node<Index> vNode = group.head(), end = group.tail(); (vNode = vNode.getNext()) != end;)
		{
			double p = progress.getProgress();
			
			Index vIndex = vNode.getValue();

        	for (AbstractSimpleEdge<Index,BasicVertexInfo> e: graph.getOutgoingEdges(vIndex))
        	{
        		Index neighbor = e.getNeighbor(vIndex);
        		if (!takenIntoAccount.contains(neighbor) && !group.contains(neighbor))
        		{
        			takenIntoAccount.add(neighbor);
        			result++;
        		}
        	}
        	p += (1 / (double) group.size()) * percentage;	
			progress.setProgress(p);
		}
		return result + group.size();
	}
	
	public static int calculateLinksDegree(FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> group, GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage)
	{
		FastTable<Index> takenIntoAccount = new FastTable<Index>();
		for (FastList.Node<AbstractSimpleEdge<Index,BasicVertexInfo>> vNode = group.head(), end = group.tail(); (vNode = vNode.getNext()) != end;)
		{
			double p = progress.getProgress();
			
			Index v1Index = vNode.getValue().getV0();
			Index v2Index = vNode.getValue().getV1();

			if (!takenIntoAccount.contains(v1Index))
				takenIntoAccount.add(v1Index);
			if (!takenIntoAccount.contains(v2Index))
				takenIntoAccount.add(v2Index);
			
        	p += (1 / (double) group.size()) * percentage;	
			progress.setProgress(p);
		}
		return takenIntoAccount.size();
	}
	
	public int getDegree(int v)
	{
		return m_neighbors.get(Index.valueOf(v)).size();
	}
	
	public void add(int v)
	{
		Index vIndex = Index.valueOf(v);
		
		m_groupDegree += m_neighbors.get(Index.valueOf(v)).size();
		
		/** Update degrees (remove the contribution of the vertex v) */
		/** Remove v and its neighbors from the neighbors of all vertices in the graph. */
		FastList<Index> vNeighbors = new FastList<Index>(m_neighbors.get(vIndex));	/** v's neighbors */
		
		for (int m : m_vertices)
		{
			Index mIndex = Index.valueOf(m);

			/** Remove v */
			/** FastList.remove returns true if this collection contained the specified value; false otherwise.  */
			m_neighbors.get(mIndex).remove(vIndex);
			
			/** Remove v's neighbors */
			for (FastList.Node<Index> vNode = vNeighbors.head(), end = vNeighbors.tail(); (vNode = vNode.getNext()) != end;)
			{
					Index nIndex = vNode.getValue();	/** neighbor */
					
					m_neighbors.get(mIndex).remove(nIndex);
			}
		}
	}
	
	public void add(AbstractSimpleEdge<Index,BasicVertexInfo> e)
	{
		Index v1 = e.getV0();
		Index v2 = e.getV1();

		for (int m : m_vertices)
		{
			Index mIndex = Index.valueOf(m);
			m_neighbors.get(mIndex).remove(v1);
			m_neighbors.get(mIndex).remove(v2);
		}
	}

	public static int calculateMixedGroupDegree(Object[] group, GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage)
	{
		int result = 0;	int groupVerticesCounter = 0;
		FastList<Index> takenIntoAccount = new FastList<Index>();
		FastList<Object> g = new FastList<Object>(group.length);
		
		for (Object member : group)
		{
			g.add(member);
//			if (member instanceof Index && !takenIntoAccount.contains(member))
//				takenIntoAccount.add((Index)member);
//			else{
//				Edge link = (Edge) member;
//				Index v1Index = link.getValue1();
//				Index v2Index = link.getValue2();
//				if (!takenIntoAccount.contains(v1Index)) takenIntoAccount.add(v1Index);
//				if (!takenIntoAccount.contains(v2Index)) takenIntoAccount.add(v2Index);
//			}
		}
		for (Object member : group)
		{
			double p = progress.getProgress();
			
			if (member instanceof Index)
			{
				Index vIndex = (Index) member;
				Iterable<? extends AbstractSimpleEdge<Index,BasicVertexInfo>> edges = graph.getOutgoingEdges(vIndex);
	        	for (AbstractSimpleEdge<Index,BasicVertexInfo> e: edges)
	        	{
	        		Index neighbor = (Index)e.getNeighbor(vIndex);
	        		if (!takenIntoAccount.contains(neighbor) && !g.contains(neighbor))
	        		{
	        			takenIntoAccount.add(neighbor);
	        			result++;
	        		}
	        	}
				groupVerticesCounter ++;
			}
			else
			{
				assert (member instanceof AbstractSimpleEdge<?,?>);
				AbstractSimpleEdge<Index,BasicVertexInfo> edge = (AbstractSimpleEdge<Index,BasicVertexInfo>) member;
				Index v1Index = edge.getV0();
				Index v2Index = edge.getV1();

				if (!takenIntoAccount.contains(v1Index) && !g.contains(v1Index))
				{
					takenIntoAccount.add(v1Index);
					groupVerticesCounter++;
				}
				if (!takenIntoAccount.contains(v2Index) && !g.contains(v2Index))
				{
					takenIntoAccount.add(v2Index);
					groupVerticesCounter++;
				}

			}
        	p += (1 / (double) group.length) * percentage;	
			progress.setProgress(p);
		}
		return result + groupVerticesCounter;
	}
	
	public int getGroupDegree(){	return m_groupDegree;	}
	
	/** Returns the vertex with maximum neighbors. */
	public int getMaxNeighbors(FastList<Index> candidates)
	{
		int maxV = 0; int maxNeighbors = -1;
		
		for(FastList.Node<Index> vNode = candidates.head(), end = candidates.tail(); (vNode = vNode.getNext()) != end;)
		{
			FastList<Index> vNeighbors = new FastList<Index>(m_neighbors.get(vNode.getValue()));	/** v's neighbors */
			if (vNeighbors.size() > maxNeighbors)
			{
				maxNeighbors = vNeighbors.size();
				maxV = vNode.getValue().intValue();
			}
		}
		return maxV;
	}
}
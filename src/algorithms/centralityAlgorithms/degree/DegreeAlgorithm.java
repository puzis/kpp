package algorithms.centralityAlgorithms.degree;

import java.io.Serializable;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;

import common.Pair;

/**
 * Calculates and holds the degrees of given vertices.
 * 
 * @author Polina Zilberman
 */
public class DegreeAlgorithm implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int[] m_vertices = null;
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	
	private FastMap<Index, Index> m_degrees = null;				/** KEY - vertex, VALUE - degree */
	private FastMap<Index, FastList<Index>> m_neighbors = null;	/** KEY - vertex, VALUE - neighbors */
	private FastList<Pair<Index, Index>> m_degreeList = null;
	/**
	 * Calculates the degree of each vertex in the vertices group.
	 * 
	 * @param vertices
	 * @param graph
	 */
	public DegreeAlgorithm(int[] vertices, GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage) 
	{
		m_vertices = vertices;
		m_degrees = new FastMap<Index, Index>(m_vertices.length);
		m_degreeList = new FastList<Pair<Index, Index>>();
		m_neighbors = new FastMap<Index, FastList<Index>>(m_vertices.length);
		m_graph = graph;
		
//		for (FastList.Node<Index> vNode = m_vertices.head(), end = m_vertices.tail(); (vNode = vNode.getNext()) != end;)
//		{
		for (int v : m_vertices)
		{
//			Index vIndex = vNode.getValue();
			Index vIndex = Index.valueOf(v);
			m_neighbors.put(vIndex, new FastList<Index>());
				
			int degree = 1;
		   	/** Add myself to my neighborhood. */
		   	m_neighbors.get(vIndex).add(vIndex);
		    	
		   	for (AbstractSimpleEdge<Index,BasicVertexInfo> e: m_graph.getOutgoingEdges(vIndex))
		   	{
		   		Index neighbor = e.getNeighbor(vIndex);
	    		m_neighbors.get(vIndex).add(neighbor);
	    		degree++;
	    	}
		   	m_degrees.put(vIndex, Index.valueOf(degree));
		   	m_degreeList.add(new Pair<Index, Index>(vIndex, Index.valueOf(degree)));
		    	
		   	double p = progress.getProgress();
		   	p += (1 / (double) m_vertices.length) * percentage;	
			progress.setProgress(p);
		}
	}
	
	public Pair<FastList<Index>, Index> computeDegree(Index vNode, AbstractExecution progress, double percentage)
	{
		m_neighbors.put(vNode, new FastList<Index>());
		
		int degree = 1;
    	/** Add myself to my neighborhood. */
    	m_neighbors.get(vNode).add(vNode);
    	
    	for (AbstractSimpleEdge<Index,BasicVertexInfo> e: m_graph.getOutgoingEdges(vNode))
    	{
    		Index neighbor = e.getNeighbor(vNode);
    		m_neighbors.get(vNode).add(neighbor);
    		
    		degree++;
    	}
    	m_degrees.put(vNode, Index.valueOf(degree));
    	m_degreeList.add(new Pair<Index, Index>(vNode, Index.valueOf(degree)));
    	
    	double p = progress.getProgress();
    	p += (1 / (double) m_vertices.length) * percentage;	
		progress.setProgress(p);
		
		return new Pair<FastList<Index>, Index>(m_neighbors.get(vNode), Index.valueOf(degree));
	}
	
	public int getDegree(int v){	return m_degrees.get(Index.valueOf(v)).intValue();	}
	
	public FastMap<Index, Index> getDegrees(){	return m_degrees;	}
	
	public FastList<Pair<Index, Index>> getDegreeList(){	return m_degreeList;	}
	
	public FastMap<Index, FastList<Index>> getNeighbors(){	return m_neighbors;	}
	
	public int[] getVertices(){	return m_vertices;	}
}
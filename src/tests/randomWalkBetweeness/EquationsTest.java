package tests.randomWalkBetweeness;

import java.util.ArrayList;

import javolution.util.FastList;
import javolution.util.Index;
import junit.framework.TestCase;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import Jama.Matrix;
import algorithms.centralityAlgorithms.randomWalkBetweeness.RandomWalkBetweeness;

public class EquationsTest extends TestCase
{
	private GraphInterface<Index,BasicVertexInfo> m_graph = null;
	private Matrix DMat = null;
	private Matrix AMat = null;
	private Matrix MMat = null;
	private Matrix sVectorV = null;
	private Matrix sVectorT = null;
	private Matrix IMat = null;

	private int m_v = 0;
	private int m_t = 3;
	
	public void setUp() 
	{
		m_graph = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int v = 0; v < 11; v++)
			m_graph.addVertex(Index.valueOf(v));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(1));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(2));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(3));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(4));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(5));
		m_graph.addEdge(Index.valueOf(0), Index.valueOf(6));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(2));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(7));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(8));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(9));
		m_graph.addEdge(Index.valueOf(1), Index.valueOf(10));
		
		m_graph.addEdge(Index.valueOf(3), Index.valueOf(4));
		m_graph.addEdge(Index.valueOf(3), Index.valueOf(5));
		m_graph.addEdge(Index.valueOf(3), Index.valueOf(6));
		m_graph.addEdge(Index.valueOf(4), Index.valueOf(5));
		m_graph.addEdge(Index.valueOf(4), Index.valueOf(6));
		m_graph.addEdge(Index.valueOf(5), Index.valueOf(6));
		m_graph.addEdge(Index.valueOf(7), Index.valueOf(8));
		m_graph.addEdge(Index.valueOf(7), Index.valueOf(9));
		m_graph.addEdge(Index.valueOf(7), Index.valueOf(10));
		m_graph.addEdge(Index.valueOf(8), Index.valueOf(9));
		m_graph.addEdge(Index.valueOf(8), Index.valueOf(10));
		m_graph.addEdge(Index.valueOf(9), Index.valueOf(10));
		
		ArrayList<FastList<Index>> G = new ArrayList<FastList<Index>>();
		for(int i = 0; i < m_graph.getNumberOfVertices(); i++)
		{
			FastList<Index> neighbors = new FastList<Index>();
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e: m_graph.getOutgoingEdges(Index.valueOf(i)))
			{
				Index n = e.getNeighbor(Index.valueOf(i));
				neighbors.add(n);
			}
			G.add(i, neighbors);
		}
		
		double [][] adjacencyMatrix = new double [G.size()][G.size()];
		double [][]degrees = new double[G.size()][G.size()];
		for (int i = 0; i < G.size(); i++)
		{
			FastList<Index> neighbors = G.get(i);
			for (FastList.Node<Index> nNode = neighbors.head(), end = neighbors.tail(); (nNode = nNode.getNext()) != end;)
			{
				int neighbor = nNode.getValue().intValue();
				adjacencyMatrix[i][neighbor] = 1;
			}
			degrees[i][i] = neighbors.size();
			
		}
		DMat = new Matrix(degrees);
		AMat = new Matrix(adjacencyMatrix);
		MMat = AMat.times(DMat.inverse());
		
		/** Source vector s; let the source vertex be 1, and the target vertex t = 3. */
		double [][] sVArray = new double [G.size()][1];
		for (int i = 0; i < G.size(); i++)
		{
			if (i == 1) sVArray[i][0] = 1;
			else if (i == m_v) sVArray[i][0] = -1;
			else sVArray[i][0] = 0;
		}
		sVectorV = new Matrix(sVArray);
		
		/** Source vector s; let the source vertex be 1, and the target vertex t = 3. */
		double [][] sTArray = new double [G.size()][1];
		for (int i = 0; i < G.size(); i++)
		{
			if (i == 1) sTArray[i][0] = 1;
			else if (i == m_t) sTArray[i][0] = -1;
			else sTArray[i][0] = 0;
		}
		sVectorT = new Matrix(sTArray);
		
		double [][] iArray = new double [G.size() - 1][G.size() - 1];
		for (int i = 0; i < G.size() - 1; i++)
		{
			for (int j = 0; j < G.size() - 1; j++)
			{
				if (i == j)
					iArray[i][j] = 1;
				else
					iArray[i][j] = 0;
			}
		}
			
		IMat = new Matrix(iArray);
	}
	
	public void testEquation()
	{
		Matrix removedDMatV = null;
		Matrix removedDMatT = null;
		Matrix removedAMat = null;
		Matrix removedMMat = null;
		
		/** Calculate left side of the equation. */
		/** Dv */
		FastList<Index> verticesToRemoveV = new FastList<Index>();
		verticesToRemoveV.add(Index.valueOf(m_v));
		removedDMatV = RandomWalkBetweeness.removeGivenRowAndColumnForTest(DMat, verticesToRemoveV);
		removedMMat = RandomWalkBetweeness.removeGivenRowAndColumnForTest(MMat, verticesToRemoveV);
		
		Matrix inverseDv = (removedDMatV.inverse());
		Matrix inverseDvEmbedded = new Matrix(m_graph.getNumberOfVertices(), m_graph.getNumberOfVertices());
		int k = 0, l;
		for ( int i = 0; i < m_graph.getNumberOfVertices(); i++ )
		{
			l = 0;
			if (!verticesToRemoveV.contains(Index.valueOf(i)))
			{
				for (int j = 0; j < m_graph.getNumberOfVertices(); j++)
				{
					if (!verticesToRemoveV.contains(Index.valueOf(j)))
						inverseDvEmbedded.set(i, j, inverseDv.get(k, l++));
				}
				k++;
			}
		}
		
		Matrix inverse = (IMat.minus(removedMMat)).inverse();
		Matrix inverseEmbedded = new Matrix(m_graph.getNumberOfVertices(), m_graph.getNumberOfVertices());
		k = 0; l = 0;
		for ( int i = 0; i < m_graph.getNumberOfVertices(); i++ )
		{
			l = 0;
			if (!verticesToRemoveV.contains(Index.valueOf(i)))
			{
				for (int j = 0; j < m_graph.getNumberOfVertices(); j++)
				{
					if (!verticesToRemoveV.contains(Index.valueOf(j)))
						inverseEmbedded.set(i, j, inverse.get(k, l++));
				}
				k++;
			}
		}
		
		Matrix leftSide = (inverseDvEmbedded.times(inverseEmbedded)).times(sVectorV);
		
		/** Calculate right side of the equation. */
		/** Dt */
		FastList<Index> verticesToRemoveT = new FastList<Index>();
		verticesToRemoveT.add(Index.valueOf(m_t));
		
		removedDMatT = RandomWalkBetweeness.removeGivenRowAndColumnForTest(DMat, verticesToRemoveT);
		removedAMat = RandomWalkBetweeness.removeGivenRowAndColumnForTest(AMat, verticesToRemoveT);
		
		Matrix inverse2 = (removedDMatT.minus(removedAMat)).inverse();
		Matrix inverseEmbedded2 = new Matrix(m_graph.getNumberOfVertices(), m_graph.getNumberOfVertices());
		k = 0; l = 0;
		for ( int i = 0; i < m_graph.getNumberOfVertices(); i++ )
		{
			l = 0;
			if (!verticesToRemoveT.contains(Index.valueOf(i)))
			{
				for (int j = 0; j < m_graph.getNumberOfVertices(); j++)
				{
					if (!verticesToRemoveT.contains(Index.valueOf(j)))
						inverseEmbedded2.set(i, j, inverse2.get(k, l++));
				}
				k++;
			}
		}
		Matrix rightSide = inverseEmbedded2.times(sVectorT);
		
//		NumberFormat formatter = new DecimalFormat("0.000");
		
		double [][] left = leftSide.getArray();
		double [][] right = rightSide.getArray();
		for (int i = 0; i < leftSide.getRowDimension(); i++)
		{
			for (int j = 0; j < leftSide.getColumnDimension(); j++)
			{
				System.out.println(left[i][j]);
				System.out.println(right[i][j]);
				System.out.println();
//				assertEquals(formatter.format(left[i][j]), formatter.format(right[i][j]));
			}
			System.out.println("-------------------------------------------");
		}
	}
	
	public Matrix removeGivenRowAndColumnForTest(Matrix original, FastList<Index> verticesToRemove)
    {
    	Matrix removed = new Matrix(original.getRowDimension(), original.getColumnDimension());

    	for (int i = 0; i < original.getRowDimension(); i++)
    	{
    		if (!verticesToRemove.contains(Index.valueOf(i)))
    		{
    			for (int j = 0; j < original.getColumnDimension(); j++)
    			{
    				if (!verticesToRemove.contains(Index.valueOf(j)))
    					removed.set(i, j, original.get(i, j));
    				else
    					removed.set(i, j, 0);
    			}
    		}
    		else for (int j = 0; j < original.getColumnDimension(); j++)
			{
				removed.set(i, j, 0);
			}	
    	}

    	return removed;
    }
}

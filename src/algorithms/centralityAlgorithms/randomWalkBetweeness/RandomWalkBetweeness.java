package algorithms.centralityAlgorithms.randomWalkBetweeness;

import java.util.ArrayList;
import java.util.Arrays;

import javolution.util.FastList;
import javolution.util.Index;
import Jama.Matrix;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 22/10/2007
 * Time: 11:43:26
 * To change this template use File | Settings | File Templates.
 */
public class RandomWalkBetweeness
{
    /** public Matrix(int m, int n) Parameters: m - Number of rows, n - Number of columns. */
    /** public void set(int i, int j, double s) Parameters: i - Row index. j - Column index. s - A(i,j). */

    private double[][] m_D;
	private Matrix m_T = null;;
	private double[] m_rwbetweenness = null;
	/** TODO: Find out - Does G hold for each vertex the list of its neighbors? */
    private ArrayList<FastList<Index>> m_G;

	// Initialize the random walk.
	public RandomWalkBetweeness(double [][] degrees, double [][] adjacencyMatrix, ArrayList<FastList<Index>> G, FastList<Index> verticesToRemove )
	{
		m_rwbetweenness = new double[G.size()];
		Arrays.fill(m_rwbetweenness, Double.NaN);
		
		m_G = G;
		m_D = degrees;
		Matrix DMat = new Matrix(m_D);
		Matrix AMat = new Matrix(adjacencyMatrix);

		Matrix subMat = DMat.minus(AMat);

		Matrix removed = null;
		removed = removeGivenRowAndColumn(subMat, verticesToRemove);

		Matrix p = new Matrix(removed.inverse().getArray());

		m_T = new Matrix(subMat.getRowDimension(), subMat.getColumnDimension());
		int k = 0, l;
		for ( int i = 0; i < subMat.getRowDimension (); i++ )
		{
			l = 0;
			if (!verticesToRemove.contains(Index.valueOf(i)))
			{
				for (int j = 0; j < subMat.getColumnDimension(); j++)
				{
					if (!verticesToRemove.contains(Index.valueOf(j)))
						m_T.set(i, j, p.get(k, l++));
				}
				k++;
			}
		}
	}

	// Calculate the flow.
	public double iist(int i, int s, int t)
	{
		if (i == s)
			return 1;
		if (i == t)
			return 1;
		FastList<Index> neighbors = m_G.get(i);
		double flow = 0;
		int j;
        for (FastList.Node<Index> neighbor = neighbors.head(), end = neighbors.tail(); (neighbor = neighbor.getNext()) != end;)
        {
            j = neighbor.getValue().intValue();
			flow += Math.abs ( m_T.get(i, s) - m_T.get(i, t) - m_T.get(j, s) + m_T.get(j, t));
		}
		flow /= 2;
		return flow;
	}

	// This function calculates the vertex betweeness.
	public double getVertexBetweeness(int i)
	{
		if (Double.isNaN(m_rwbetweenness[i]))
		{
			double bi = 0;
			for ( int s = 0; s < m_G.size(); s++ )
				for (int t = s + 1; t < m_G.size(); t++)
					bi += iist(i, s, t);
			bi /= (0.5 * m_G.size() * (m_G.size() - 1));
			return bi;
		}
		return m_rwbetweenness[i]; 
	}

	// This function calculates the sum betweeness.
	public double getSumBetweeness(FastList<Index> S)
	{
		double SumBetweeness = 0;
		for ( int i = 0; i < S.size(); i++ )
			SumBetweeness += getVertexBetweeness (S.get(i).intValue());
		return SumBetweeness;
	}

//    private Matrix removeLastRowAndColumn(Matrix original)
//    {
//        Matrix removed = new Matrix(original.getRowDimension() - 1, original.getColumnDimension() - 1);
//
//        for (int i = 0; i < original.getRowDimension() - 1; i++ )
//            for (int j = 0; j < original.getColumnDimension() - 1; j++ )
//                removed.set(i, j, original.get(i, j));
//        return removed;
//    }
    
    public Matrix removeGivenRowAndColumn(Matrix original, FastList<Index> verticesToRemove)
    {
    	Matrix removed = new Matrix(original.getRowDimension() - verticesToRemove.size(), original.getColumnDimension() - verticesToRemove.size());

    	int k = 0, l;
    	for (int i = 0; i < original.getRowDimension(); i++)
    	{
    		l = 0;
    		if (!verticesToRemove.contains(Index.valueOf(i)))
    		{
    			for (int j = 0; j < original.getColumnDimension(); j++)
    			{
    				if (!verticesToRemove.contains(Index.valueOf(j)))
    					removed.set(k, l++, original.get(i, j));
    			}
    			k++;
    		}
    	}

    	return removed;
    }
    
    public static Matrix removeGivenRowAndColumnForTest(Matrix original, FastList<Index> verticesToRemove)
    {
    	Matrix removed = new Matrix(original.getRowDimension() - verticesToRemove.size(), original.getColumnDimension() - verticesToRemove.size());

    	int k = 0, l;
    	for (int i = 0; i < original.getRowDimension(); i++)
    	{
    		l = 0;
    		if (!verticesToRemove.contains(Index.valueOf(i)))
    		{
    			for (int j = 0; j < original.getColumnDimension(); j++)
    			{
    				if (!verticesToRemove.contains(Index.valueOf(j)))
    					removed.set(k, l++, original.get(i, j));
    			}
    			k++;
    		}
    	}

    	return removed;
    }
    
//    public static Matrix removeGivenRowAndColumnForTest(Matrix original, int row, int column)
//    {
//    	Matrix removed1 = new Matrix(original.getRowDimension() - 1, original.getColumnDimension());
//
//        for (int i = 0; i < row; i++ )
//            for (int j = 0; j < original.getColumnDimension(); j++ )
//                removed1.set(i, j, original.get(i, j));
//        
//        for (int i = row; i < original.getRowDimension() - 1; i++ )
//            for (int j = 0; j < original.getColumnDimension(); j++ )
//                removed1.set(i, j, original.get(i + 1, j));
//        
//        Matrix removed2 = new Matrix(original.getRowDimension() - 1, original.getColumnDimension() - 1);
//        for (int j = 0; j < column; j++ )
//            for (int i = 0; i < removed1.getRowDimension(); i++ )
//                removed2.set(i, j, removed1.get(i, j));
//
//        for (int j = column; j < removed1.getColumnDimension() - 1; j++ )
//            for (int i = 0; i < removed1.getRowDimension(); i++ )
//                removed2.set(i, j, removed1.get(i, j + 1));
//
//        return removed2;
//    }
}

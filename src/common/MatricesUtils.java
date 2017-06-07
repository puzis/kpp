package common;

import java.io.IOException;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: puzis
 * Date: Aug 16, 2007
 * Time: 12:55:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatricesUtils
{
	
	public static double[][] normalize(double[][] m){
		double[][] normalizedM = new double[m.length][m[0].length];
		
		double sum = 0.0;
		for (int i=0; i<m.length; i++){
			for (int j=0; j<m[0].length; j++){
				sum += m[i][j];
			}
		}
		
		for (int i=0; i<m.length; i++){
			for (int j=0; j<m[0].length; j++){
				normalizedM[i][j] = m[i][j]/sum;
			}
		}
		
		return normalizedM;
	}
	
	public static double getAbsoluteDifference(double[][] ma, double[][] mb){
		double res = 0.0;
		
		for (int i=0; i<ma.length; i++){
			for (int j=0; j<ma[0].length; j++){
				res += Math.abs(ma[i][j]-mb[i][j]);
			}
		}
		
		return res;
	}
	
	public static double getMeanRelativeError(double[][] ma, double[][] mb){
		double sumRelativeError = 0.0;
		
		for (int i=0; i<ma.length; i++){
			for (int j=0; j<ma[0].length; j++){
				double error = Math.abs((double)ma[i][j]-(double)mb[i][j]);
				double relativeError = 0.0;
				if (error>0.0)
					relativeError = (mb[i][j]==0)? 1: error/mb[i][j];
				sumRelativeError += relativeError;
			}
		}
		double meanRelativeError = sumRelativeError/(double) (ma.length*ma[0].length);
		return meanRelativeError;
	}
	
	// TODO: Finish modifying according to an appropriate formula.
	public static double getSquaredRelativeError(double[][] ma, double[][] mb){
		double sumRelativeError = 0.0;
		
		for (int i=0; i<ma.length; i++){
			for (int j=0; j<ma[0].length; j++){
				double squaredError = Math.pow(((double)ma[i][j]-(double)mb[i][j]), 2.0);
				double relativeError = 0.0;
				if (squaredError>0.0)
					relativeError = (mb[i][j]==0)? 1: squaredError/Math.pow(mb[i][j], 2.0);
				sumRelativeError += relativeError;
			}
		}
		double meanRelativeError = sumRelativeError/(double) (ma.length*ma[0].length);
		return meanRelativeError;
	}
	
	public static double getPearsonCorrelation(double[][] ma, double[][] mb){
		double pearsonCorrelation = 0.0;
		
		double sumProduct = 0.0;
		double sumEstimatedSquare = 0.0, sumEstimated = 0.0; 
		double sumActualSquare = 0.0, sumActual = 0.0;
		
		for (int i=0; i<ma.length; i++){
			for (int j=0; j<ma[0].length; j++){
				sumProduct += ma[i][j] * mb[i][j];
				sumEstimated += ma[i][j];
				sumEstimatedSquare += ma[i][j] * ma[i][j];
				sumActual += mb[i][j];
				sumActualSquare += mb[i][j] * mb[i][j];
			}
		}
		double n = ma[0].length*ma.length;
		double numerator = n*sumProduct - sumEstimated*sumActual;
		double denominator_p1 = n*sumEstimatedSquare-Math.pow(sumEstimated, 2.0);
		double denominator_p2 = n*sumActualSquare-Math.pow(sumActual, 2.0);
		double denominator = Math.sqrt(denominator_p1*denominator_p2);
		pearsonCorrelation = numerator/denominator;
				
		return pearsonCorrelation;
	}
	
    public static double[][] getDefaultWeights(int n)
    {
        double[][] weights = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (i == j) weights[i][j] = 0;
                else weights[i][j] = 1;

        return weights;
    }
    
    public static boolean isZeroMatrix(double[][] matrix){
    	boolean isZero = true;
    	for (int i=0; i<matrix.length; i++){
    		
    		if (!isZeroRow(matrix[i])){
    			isZero = false;
    			break;
    		}
    	}
    	return isZero;
    }
    
    public static boolean isZeroRow(double[] row){
    	boolean isZero = true;
    	
    	double[] r = Arrays.copyOf(row, row.length);
		Arrays.sort(r, 0, r.length);
		// If the largest element is zero, 
		// then the whole row is zero.  
		if (r[r.length-1]>0.0)
			isZero = false;
		
    	return isZero;
    }
    
    public static void printMatrix(double [][] matrix, int n) throws IOException
	{
		printMatrix(matrix, n, System.out);
	}
    
    public static void printMatrix(double [][] matrix, int n, OutputStream out) throws IOException
    {
    	for (int i = 0; i < n; i++)
    	{
    		for (int j = 0; j < n; j++){
    			Double d=new Double (matrix[i][j]);
    			out.write((d.floatValue() + ", ").getBytes());
    		}
    		out.write(("\n").getBytes());
    	}
    }
    
    public static void printMatrix(int [][] matrix, int n, OutputStream out)
    {
    	try{
    		for (int i = 0; i < n; i++)
    		{
    			for (int j = 0; j < n; j++)
    				out.write((matrix[i][j] + ", ").getBytes());
    			out.write(("\n").getBytes());
    		}
    	}
    	catch(IOException ex)
    	{}
    }
	
	public static void printMatrix(int [][] matrix, int n)
	{
		printMatrix(matrix, n, System.out);
	}
	
	public static int [] permute(int verticesNumber)
    {
    	int [] permutation = new int [verticesNumber];
    	for (int i = 0; i < verticesNumber; i++) permutation[i] = i;
    	
    	int j = 0;
    	for (int i = 0; i < verticesNumber; i++)
    	{
    		Random random = new Random(verticesNumber);
    		j = random.nextInt(verticesNumber);
    		int temp = permutation[i];
    		permutation[i] = permutation[j];
    		permutation[j] = temp;
    	}
    	return permutation;
    }
	
	public static double nanosToSecs(long c) {
        return ((double)c)/1e9;}
}

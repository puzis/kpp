package algorithms.saritKraus;

import java.util.HashSet;
import java.util.Random;

import javolution.util.Index;

import org.apache.commons.math.complex.Complex;

import topology.BasicVertexInfo;
import topology.GraphInterface;
import Jama.Matrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathFactory;

import common.mathlink.EigPair;
import common.mathlink.MathLink;

public class SKAlg {

	public static int[] sk(GraphInterface<Index,BasicVertexInfo> graph, Index[] placeholders) {
		int k = (int) (Math.sqrt(placeholders.length / 2.0));
		return sk(graph, placeholders, k);
	}

	/**
	 * 
	 * @param graph
	 *            the graph with placeholders
	 * @param placeholders
	 *            the placeholders that need to be clustered
	 * @param k
	 *            desired number of clusters
	 * @return an array of int[placeholders.length] whith a cluster label for eahc placeholder
	 */
	public static int[] sk(GraphInterface<Index,BasicVertexInfo> graph, Index[] placeholders,
			int k) {
		/*
		 * basic algorithm description:
		 * 
		 * let n be the number of nodes let k be the number of placeholders 1.
		 * create an (n x n) affinity matrix A representing the "distance"
		 * between every pair of nodes 2. define D to be the diagonal matrix
		 * whose (i,i) element is the sum of i'th row of A 3. compute the matrix
		 * L = D^(-1/2)AD(^-1/2) 4. find the k largest orthogonal eigenvectors
		 * of L and form X by stacking them into columns 5. extract the rows
		 * from X that correspond to a placeholder node to form Y 6. renormalize
		 * each of Y's rows to have unit length 7. cluster the rows of Y 8.
		 * assign placeholder nodes to clusters in the same the rows of Y were
		 * clusters i.e placeholder i is assigned to cluster j iff row i of Y
		 * was assigned to cluster j.
		 */

		int n = graph.getNumberOfVertices();
		// System.out.printf("nodes: %d edges: %d\n",n,m);

		assert k > 0 : k;

		System.out.print("computing affinity matrix..");
		// step 1
		double[][] mA = affinity(graph);

		System.out.println("done.");

		System.out.print("comuting D..");
		// step 2 and copute D^(-1/2) for step 3
		double[] mD = new double[n];
		for (int i = 0; i < n; i++) {
			double sum = 0;
			for (int j = 0; j < n; j++) {
				sum += mA[i][j];
			}
			mD[i] = Math.pow(sum, -0.5);
		}
		System.out.println("done.");

		System.out.print("computing L..");
		// step 3 in place (mA)
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				mA[i][j] *= mD[j];
				mA[i][j] *= mD[i];
			}
		}
		System.out.println("done.");

		MathLink mlink = new MathLink();
		if (!mlink.start()) {
			System.out.println("error starting math link kernel");
			return null;
		}

		System.out.print("getting eigsys..");

		// step 4
		// assuming ml.eigSys returns a result sorted by value
		EigPair[] eigSys = mlink.eigSys(mA, k);
		System.out.println("done.");
		
		mlink.stop();

		System.out.print("generating X matrix..");
		Matrix mX = new Matrix(n, k);
		for (int i = 0; i < eigSys.length; i++) {
			for (int j = 0; j < eigSys[i].vec.length; j++) {
				Complex c = eigSys[i].vec[j];
				mX.set(j, i, c.abs());
			}
		}
		System.out.println("done.");

		System.out.print("extracting rows..");
		// step 5
		// rows that correspond to placeholder nodes
		int[] rows = new int[placeholders.length];
		for (int i = 0; i < placeholders.length; i++) {
			rows[i] = placeholders[i].intValue();
		}

		// all columns
		int[] columns = new int[k];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = i;
		}
		Matrix mY = mX.getMatrix(rows, columns);
		System.out.println("done.");

		System.out.print("renormalizing rows..");
		// step 6
		for (int i = 0; i < placeholders.length; i++) {
			Matrix row = mY.getMatrix(i, i, 0, k - 1);
			vecNorm(row.getArray()[0]);
			mY.setMatrix(i, i, 0, k - 1, row);
		}
		System.out.println("done.");
		// step 7
		double[][] data = mY.getArray();
		
		System.out.print("clustering..");
		int[] labels = SKAlg.kmeans(data, k);
		System.out.println("done.");

		System.out.println("done");
		return labels;
	}

//	// finds the index of vector v in an array of vectors vecs
//	private static int findVec(double[] v, double[][] vecs) {
//		for (int i = 0; i < vecs.length; i++) {
//			if (v.length != vecs[i].length)
//				continue;
//			boolean found = true;
//			for (int j = 0; j < vecs[i].length; j++) {
//				if (v[j] != vecs[i][j])
//					found = false;
//				break;
//			}
//			if (found)
//				return i;
//		}
//		return -1;
//	}

	private static double[][] affinity(GraphInterface<Index,BasicVertexInfo> graph) {
		double[][] d = getDistancesMatrix(graph);
		int n = graph.getNumberOfVertices();
		double[][] a = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				double dist = d[i][j];
				if (Double.isNaN(dist) || Double.isInfinite(dist)) {
					a[i][j] = 1.0 / Math.pow(n + 2, 2);
				} else {
					a[i][j] = 1.0 / Math.pow(d[i][j] + 1, 2);
				}
			}
		}
		return a;
	}

	private static double[][] getDistancesMatrix(GraphInterface<Index,BasicVertexInfo> graph) {
		ShortestPathAlgorithmInterface shortestPathAlg = ShortestPathFactory
				.getShortestPathAlgorithm(ShortestPathFactory
						.getDefaultAlgorithmType(), graph);
		int n = graph.getNumberOfVertices();
		double[][] distance = new double[n][n];

		for (int s = 0; s < n; s++) {
			shortestPathAlg.run(s);
			System.arraycopy(shortestPathAlg.getDistanceArray(), 0,
					distance[s], 0, n);
		}
		for (int i = 0; i < distance.length; i++) {
			for (int j = 0; j < distance[i].length; j++) {
				if (Double.isNaN(distance[i][j])) {
					distance[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		return distance;
	}

	private static void vecNorm(double[] vec) {
		double d = 0;
		for (int i = 0; i < vec.length; i++) {
			if (vec[i] != 0)
				d += Math.pow(vec[i], 2);
		}
		d = Math.sqrt(d);
		if (d == 0)
			return;
		for (int i = 0; i < vec.length; i++) {
			vec[i] /= d;
		}
	}
	
	private static int[] kmeans(double[][] data, int k)
	{
		if (data.length <= 0) return null;
		
		int m = data[0].length;
		int n = data.length;
		
		HashSet<Integer>[] clusters = new HashSet[k];
		for (int i = 0; i < clusters.length; i++) {
			clusters[i] = new HashSet<Integer>();
		}
		
		int[] samples = new int[n];
		
		double[][] centroids = new double[k][m];
		
		// set initial centroids to a random sample
		Random r = new Random();
		
		for (int i = 0; i < clusters.length; i++) {			
			int sample = r.nextInt(n);
			System.arraycopy(data[sample], 0, centroids[i], 0, m);
		}
		
		int MAX_TERATIONS = 100;
		
		boolean changed = true;
		while (MAX_TERATIONS > 0 && changed)
		{
			changed = false;
			MAX_TERATIONS--;
			
			// clear clusters
			for (int i = 0; i < clusters.length; i++) {
				clusters[i].clear();
			}
			
			// assign samples to clusters based on minimum distance from centroid
			for (int i = 0; i < samples.length; i++) {
				int minc = samples[i];
				double mindist = Double.MAX_VALUE;
				for (int j = 0; j < centroids.length; j++) {
					double dist = euclideanDist(centroids[j], data[i]);
					if (dist<mindist)
					{
						mindist = dist;
						minc = j;
					}
				}
				
				if (minc != samples[i])
					changed = true;
				samples[i] = minc;
				clusters[minc].add(i);								
			}
			
			// compute new centroids
			for (int i = 0; i < clusters.length; i++) {
				for (int j = 0; j < m; j++) {
					centroids[i][j] = 0;
				}
				for (Integer sample : clusters[i]) {					
					for (int j = 0; j < m; j++) {
						centroids[i][j] += data[sample][j] ;
					}
				}
				for (int j = 0; j < m; j++) {
					centroids[i][j] /= clusters[i].size();
				}
			}
			
			
			// repeat...
		}
		System.out.println(MAX_TERATIONS);
		return samples;
	}
	
	private static double euclideanDist(double[] v1, double[] v2)
	{
		double dist = 0;
		for (int i = 0; i < v2.length; i++) {
			double diff = v1[i] - v2[i]; 
			dist += diff*diff;
		}
		dist = Math.sqrt(dist);
		return dist;
	}
	
	public static void main(String[] args)
	{
		int k = 3;
		int n = 10;
		int m = 1;
		double[][] data = new double[30][];
		Random r = new Random();
		
		for (int i = 0; i < data.length; i++) {
			data[i] = new double[m];
			for (int j = 0; j < m; j++) {
				data[i][j] = r.nextDouble();
			}
		}
		
		
		int[] labels = SKAlg.kmeans(data,k);
		for (int i = 0; i < k; i++) {
			System.out.println("cluster "+i+":");
			for (int j = 0; j < labels.length; j++) {
				if (labels[j] == i)
				{
					System.out.print("\t");
					for (int j2 = 0; j2 < data[j].length; j2++) {						
						System.out.print(data[j][j2]+",");
					}
					System.out.println();
				}
			}
		}
	}
	
}

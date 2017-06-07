package common.mathlink;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.math.complex.Complex;

import server.common.ServerConstants;
import Jama.Matrix;

import com.wolfram.jlink.Expr;
import com.wolfram.jlink.ExprFormatException;
import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

public class MathLink {

	private static final String[] mathArgs = { "-linkmode", "launch",
			"-linkname",
			"D:/Program Files/Wolfram Research/Mathematica/8.0/MathKernel.exe" };

	// private static KernelLink kl;
	private KernelLink kl;

	public MathLink() {
	}

	public boolean start(String mathKernelPath) {
		mathArgs[2] = mathKernelPath;
		return this.start();
	}

	public boolean start() {
		if (kl != null) {
			this.stop();
		}
		try {
			kl = MathLinkFactory.createKernelLink(mathArgs);
			kl.discardAnswer();
		} catch (MathLinkException e) {
			e.printStackTrace();
			System.out.println("Error opening link to mathematica");
			return false;
		}
		return true;
	}

	public boolean stop() {
		if (kl != null) {
			kl.close();
		}
		return true;
	}

	// just a test
	public int calc(String s) {
		try {
			kl.evaluate("2+2");
			kl.waitForAnswer();
			return kl.getInteger();
		} catch (MathLinkException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * returns the k highest eigenvalues and corresponding eigenvectors
	 * 
	 * @param m
	 *            the matrix
	 * @param k
	 *            number of eigenvectors and values
	 * @return
	 */
	public EigPair[] eigSys(double[][] m, int k) {
		// assert k < m.length: k;
		Expr exp = null;
		try {
			kl.evaluate(new Expr(new Expr(Expr.SYMBOL, "Eigensystem"),
					new Expr[] { new Expr(m), new Expr(k) }));
			kl.waitForAnswer();
			exp = kl.getExpr();
		} catch (MathLinkException e) {
			e.printStackTrace();
			return null;
		}
		Expr[] args = exp.args();
		Expr valsList = args[0];
		Expr vecsList = args[1];
		Expr[] vecs = vecsList.args();
		Expr[] vals = valsList.args();
		EigPair[] result = new EigPair[k];

		for (int i = 0; i < vals.length; i++) {
			EigPair ep = new EigPair(vecs[i].length());
			try {
				ep.val = mathNumToComplex(vals[i]);

				for (int j = 0; j < vecs[i].length(); j++) {
					// first part is "List" symbol
					ep.vec[j] = mathNumToComplex(vecs[i].part(j + 1));
				}
			} catch (Exception e) {
				System.out.println("val: "+vals[i]);
				System.out.println("vec: "+vecs);
				// needs batter handling...
				e.printStackTrace();
				return null;
			}
			result[i] = ep;
		}
		return result;
	}

	private Complex mathNumToComplex(Expr e) {
		try {
			if (e.complexQ()) {
				return new Complex(e.re(), e.im());
			} else {
				return new Complex(e.asDouble(), 0);
			}
		} catch (ExprFormatException ex) {
			// shouldn't happen
			ex.printStackTrace();
			return null;
		}
	}

	public double[][] eigenVals(Object[] mt) {

		double[][] m = objectToDoubleMatrix(mt);

		return eigenVals(m);
	}

	public double[][] eigenVals(String mfile) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(ServerConstants.DATA_DIR
					+ "/" + mfile));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			return null;
		}
		System.out.println("reading file");
		String line = "";
		try {
			line = reader.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		int n = Integer.parseInt(line);
		double[][] m = new double[n][n];
		for (int i = 0; i < n; i++) {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			Scanner s = new Scanner(line);
			for (int j = 0; j < n; j++) {
				m[i][j] = s.nextDouble();
			}
		}
		System.out.println("done, computing vals");

		return eigenVals(m);
	}

	public Object[] eigenVecs(String mfile) {
		File f = new File(ServerConstants.DATA_DIR + "/" + mfile);
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("reading file");
		int n = s.nextInt();
		double[][] m = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				m[i][j] = s.nextDouble();
			}
		}
		System.out.println("done, computing vecs");
		return eigenVecs(m);
	}

	public double[][] eigenVals(double[][] m) {

		Expr exp = null;
		try {
			System.out.println(" vals evaluate");
			kl.evaluate(new Expr(new Expr(Expr.SYMBOL, "Eigenvalues"),
					new Expr[] { new Expr(m) }));
			kl.waitForAnswer();
			System.out.println("vals got answer");
			exp = kl.getExpr();
		} catch (MathLinkException e) {
			e.printStackTrace();
			return null;
		}

		Expr[] args = exp.args();
		double[][] result = new double[args.length][2];

		for (int i = 0; i < args.length; i++) {
			if (args[i].complexQ()) {
				try {
					result[i][0] = args[i].re();
					result[i][1] = args[i].im();
				} catch (ExprFormatException e) {
					// can't happen
					e.printStackTrace();
				}
			} else {
				try {
					result[i][0] = args[i].asDouble();
					result[i][1] = 0;
				} catch (ExprFormatException e) {
					// shouldn't happen
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public Object[] eigenVecs(Object[] mt) {

		double[][] m = objectToDoubleMatrix(mt);

		return eigenVecs(m);
	}

	private Object[] eigenVecs(double[][] m) {

		Expr exp = null;
		try {
			Expr command = new Expr(new Expr(Expr.SYMBOL, "Eigenvectors"),
					new Expr[] { new Expr(m) });
			System.out.println("vecs evaluate");
			kl.evaluate(command);
			kl.waitForAnswer();
			System.out.println("vecs got answer");
			exp = kl.getExpr();

		} catch (MathLinkException e) {
			e.printStackTrace();
			return null;
		}

		Expr[] args = exp.args();
		Object[] result = new Object[args.length];

		for (int i = 0; i < args.length; i++) {
			Expr[] vec = args[i].args();
			Object[] v = new Object[vec.length];

			for (int j = 0; j < vec.length; j++) {
				if (vec[i].complexQ()) {
					try {
						v[j] = new Object[] { vec[i].re(), vec[i].im() };
					} catch (ExprFormatException e) {
						// can't happen
						e.printStackTrace();
					}
				} else {
					try {
						v[j] = new Object[] { vec[i].asDouble(), 0 };
					} catch (ExprFormatException e) {
						// shouldn't happen
						e.printStackTrace();
					}
				}
			}
			result[i] = v;

		}

		return result;

	}

	/**
	 * 
	 * @param data
	 *            the data to be clustered, each data[i] is treaded as a vector
	 * @param k
	 *            number of clusters
	 * @return a 3d double array, result[i][j][k] is the k'th coordinate of the
	 *         j'th vector of the i'th cluster
	 */
	public double[][][] cluster(double[][] data, int k) {
		Expr exp = null;
		try {

			Expr[] dlistargs = new Expr[data.length];
			for (int i = 0; i < dlistargs.length; i++) {
				dlistargs[i] = new Expr(data[i]);
			}
			Expr dlist = new Expr(new Expr(Expr.SYMBOL, "List"), dlistargs);
			Expr command = new Expr(new Expr(Expr.SYMBOL, "FindClusters"),
					new Expr[] { dlist, new Expr(k) });
			System.out.println();
			System.out.println("sending command");
			kl.evaluate(command);
			System.out.println("sent");
			kl.waitForAnswer();
			System.out.println("got answer");
			exp = kl.getExpr();

		} catch (MathLinkException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("parsing result");
		Expr[] clusters = exp.args();

		assert clusters.length == k : clusters.length;

		double[][][] result = new double[k][0][0];
		for (int i = 0; i < clusters.length; i++) {
			Expr[] samples = clusters[i].args();
			result[i] = new double[samples.length][0];
			for (int j = 0; j < samples.length; j++) {
				Expr[] sample = samples[j].args();
				result[i][j] = new double[sample.length];
				for (int l = 0; l < sample.length; l++) {
					try {
						result[i][j][l] = sample[l].asDouble();
					} catch (ExprFormatException e) {
						// shouldn't happen
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("done.");
		return result;
	}

	private double[][] objectToDoubleMatrix(Object[] mt) {
		System.out.println("casting object to doulbe");
		double[][] m = new double[mt.length][mt.length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				m[i][j] = (Double) (((Object[]) (mt[i]))[j]);
			}
		}
		System.out.println("done");
		return m;
	}

	public double[][] dot(double[][] a, double[][] b) {
		Expr exp = null;
		try {
			Expr command = new Expr(new Expr(Expr.SYMBOL, "Dot"), new Expr[] {
					new Expr(a), new Expr(b) });
			System.out.println("dot evaluate");
			kl.evaluate(command);
			kl.waitForAnswer();
			System.out.println("dot got answer");
			exp = kl.getExpr();

		} catch (MathLinkException e) {
			e.printStackTrace();
			return null;
		}
		Expr[] rows = exp.args();

		double[][] result = new double[a.length][b[0].length];
		for (int i = 0; i < rows.length; i++) {
			Expr[] elements = rows[i].args();
			for (int j = 0; j < elements.length; j++) {
				try {
					result[i][j] = elements[j].asDouble();
				} catch (ExprFormatException e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		return result;
	}

	public static void main(String[] args) {
		MathLink ml = new MathLink();
		int[] a = new int[0];
		ml.start();
		for (int i = 0; i < 100; i++) {
			Matrix m = Matrix.random(50, 50);
			System.out.println(ml.eigSys(m.getArray(), 51));
		}
		ml.stop();
	}
}

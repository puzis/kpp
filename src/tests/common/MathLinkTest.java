package tests.common;

import junit.framework.TestCase;

import common.mathlink.MathLink;

public class MathLinkTest extends TestCase{
	
	public void testBasic() {
		MathLink ml = new MathLink();
		ml.start();
		double[][] m = new double[10][10];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				m[i][j] = i+j*2;
			}
		}
		
		//Expr res = ml.eigensVals(m);
		//assertEquals(4, ml.calc());
		ml.stop();
	}

}

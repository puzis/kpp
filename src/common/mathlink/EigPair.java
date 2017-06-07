package common.mathlink;

import org.apache.commons.math.complex.Complex;

public class EigPair {

	public Complex val;
	public Complex[] vec;
	
	public EigPair() {
		init(0);
	}

	public EigPair(int k) {
		init(k);
	}

	private void init(int k) {
		this.val = new Complex(0, 0);
		this.vec = new Complex[k];
		for (int i = 0; i < this.vec.length; i++) {
			this.vec[i] = new Complex(0, 0);
		}
	}
	

	public String toString() {
		return "Val: " + this.val.toString() + "\nVec: " + this.vec.toString();
	}
}

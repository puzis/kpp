package common;

public interface Rational <T> extends  Comparable<Rational<T>> {
    /**
     * 
     * @return if this object is identically zero
     */
	public boolean isZero ();
	/**
	 * make the object zero
	 */
    public void Zero ();
    /**
     * @return the double equivalent to this rational number
     */
    public double toDouble();
    /** 
     * @return string representation of (this)
     */
    public String toString();

    /**
     * this * b
     */ 
    public void times(T b);
    /**
     * this + b
     */ 
    public void add(T b);
    /**
     * - this
     */ 
    public void negate();
    /**
     * this - b
     */ 
    public void minus(T b);
    /**
     * this ^ -1
     */ 
    public void reciprocal();
    /**
     * this / b
     */ 
    public void divide(T b);
    
    
    
}

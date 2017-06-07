/**
 * 
 */
package common;

/**
 * @author zoharo
 *
 */
public class RationalGCD implements Rational<RationalGCD> {

	   private long numerator, denominator;
	   private final long GCDTHRESHOLD = Long.MAX_VALUE >> 2;
	   //public static RationalGCD NaN = new RationalGCD (1,0);
	   
	   //-----------------------------------------------------------------
	   //  Constructor: Sets up the rational number by ensuring a nonzero
	   //  denominator and making only the numerator signed.
	   //-----------------------------------------------------------------
	   public RationalGCD (long numer, long denom)
	   {
	      set(numer,denom);
	   }
	   
	   public void set (long numer,long denom){
	   	  if (denom == 0 && numer==0)//zero
	         denom = 1;
	      // Make the numerator "store" the sign
	      if (denom < 0)
	      {
	         numer = numer * -1;
	         denom = denom * -1;
	      }
	      numerator = numer;
	      denominator = denom;
	      if (isNaN()) return;
	      long common = gcd (Math.abs(numerator), denominator);
	      numerator = numerator / common;
	      denominator = denominator / common; 
	   }

	  
	   //-----------------------------------------------------------------
	   //  Returns the reciprocal of this rational number.
	   //-----------------------------------------------------------------
	   public void reciprocal ()
	   {
	       long temp=numerator;
	       this.numerator = this.denominator;
	       this.denominator=temp;
	   }

	   //-----------------------------------------------------------------
	   //  Adds this rational number to the one passed as a parameter.
	   //  A common denominator is found by multiplying the individual
	   //  denominators.
	   //-----------------------------------------------------------------
	   public void add (RationalGCD op2)
	   {
     	  if (op2.isNaN()) {
     		  this.denominator=0;  return; }
     	  if (isNaN()|| op2.isZero()) return;
		  if (isZero()){
			  numerator=op2.numerator;
			  denominator=op2.denominator;
		  }
		  else {
			  this.numerator *= op2.denominator;
			  long numerator2 = op2.numerator * denominator;
			  this.numerator += numerator2;
			  this.denominator *=  op2.denominator;
		  }
		  
		  reduce();
	   }


	   //-----------------------------------------------------------------
	   //  Multiplies this rational number by the one passed as a
	   //  parameter.
	   //-----------------------------------------------------------------
	   public void times (RationalGCD op2)
	   {
		  this.numerator *= op2.numerator;
	      if (this.numerator==0) return;
	      this.denominator *= op2.denominator;
	      if (isNaN()) return;
	      reduce();
	   }

	   //-----------------------------------------------------------------
	   //  Divides this rational number by the one passed as a parameter
	   //  by multiplying by the reciprocal of the second rational.
	   //-----------------------------------------------------------------
	   public void divide (RationalGCD op2)
	   {
		   if (op2.isNaN()) {
	     		  this.denominator=0;  return; }
		  op2.reciprocal();
	      this.times (op2);
	      op2.reciprocal();
	      if (isNaN()) return;
	      reduce();
	   }
	   
		/* (non-Javadoc)
		 * @see common.Rational#minus(java.lang.Object)
		 */
		@Override
		public void minus(RationalGCD op2) {
			op2.numerator=-op2.numerator;
		    this.add(op2);
		    op2.numerator=-op2.numerator;
		    reduce();
		}


	/* (non-Javadoc)
	 * @see common.Rational#negate()
	 */
	@Override
	public void negate() {
		this.numerator=-this.numerator;
	}


	/* (non-Javadoc)
	 * @see common.Rational#toDouble()
	 */
	@Override
	public double toDouble() {
		if (isZero()) return 0.0;
		return ((double)numerator)/denominator;
	}


	/* (non-Javadoc)
	 * @see common.Rational#Zero()
	 */
	@Override
	public void Zero() {
		this.numerator=0;
		
	}

	public final long num (){
		return this.numerator;
	}

	public final long denom (){
		return this.denominator;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Rational<RationalGCD> b) {
        double lhs =this.toDouble();// ((this.whole*this.den)+this.num) * b.den;
        double rhs = b.toDouble();//this.den * (b.num+(b.whole*b.den));
        if (lhs < rhs) return -1;
        if (lhs > rhs) return +1;
		return 0;
	}


	//-----------------------------------------------------------------
	   //  Determines if this rational number is equal to the one passed
	   //  as a parameter.  Assumes they are both reduced.
	   //-----------------------------------------------------------------
	   public boolean equals (RationalGCD op2)
	   {
	      return ( numerator == op2.numerator &&
	               denominator == op2.denominator );
	   }

	   //-----------------------------------------------------------------
	   //  Returns this rational number as a string.
	   //-----------------------------------------------------------------
	   public String toString ()
	   {
	      String result;

	      if (numerator == 0)
	         result = "0";
	      else
	         if (denominator == 1)
	            result = numerator + "";
	         else
	            result = numerator + "/" + denominator;
	    
	      return result;
	   }

	   //-----------------------------------------------------------------
	   //  Reduces this rational number by dividing both the numerator
	   //  and the denominator by their greatest common divisor.
	   //-----------------------------------------------------------------
	   private void reduce ()
	   {
		  if (numerator != 0)// && (GCDTHRESHOLD<=this.denominator || GCDTHRESHOLD<=this.numerator)) 
	      {
	         long common = gcd (Math.abs(numerator), denominator);
	         numerator = numerator / common;
	         denominator = denominator / common;
	      }
	   }

	   //-----------------------------------------------------------------
	   //  Computes and returns the greatest common divisor of the two
	   //  positive parameters. Uses Euclid's algorithm.
	   //-----------------------------------------------------------------
	   /**
	    * Return the greatest common divisor
	    */
	    
	    public long gcd(long a, long b) {
	    
	      if (b==0) 
	        return a;
	      else
	        return gcd(b, a % b);
	    } 
	    
	    public boolean isNaN (){
	    	return this.denominator==0 && this.numerator!=0;
	    }
	    
	    /* (non-Javadoc)
		 * @see common.Rational#isZero()
		 */
		@Override
		public boolean isZero() {
			return this.numerator==0;
		}
	    
	    public static  RationalGCD NaN (){
	    	return new RationalGCD (1,0);	        
	    }
	    
	    public static  RationalGCD makeZero (){
	    	return new RationalGCD (0,1);	        
	    }
	    
	    public static  RationalGCD makeOne (){
	    	return new RationalGCD (1,1);	        
	    }
	    
	    public static void main (String [] args){
	    	double nan= Double.NaN;
	    	double res =  nan + 3;
	    	System.out.print(res);
	    }

}

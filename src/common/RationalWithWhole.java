package common;

public class RationalWithWhole implements Rational<RationalWithWhole> {
    

    private int num;   // the numerator
    private int den;   // the denominator
    private int  whole=0;
    private boolean neg=false;

    // create and initialize a new Rational object
    public RationalWithWhole(long numerator, long denominator) {
    	if (numerator==0 || denominator==0)
    		this.Zero();
    	else{
    		if (denominator < 0 && numerator > 0 || denominator>0 && numerator < 0) 
	        	neg=true;
    		numerator = Math.abs(numerator);
    		denominator = Math.abs(denominator);
	        
	        while (numerator>=denominator){
	        	numerator-=denominator;
	    		whole+=1;
	    	}
	        num = (int) numerator;
	        den = (int) denominator;
    	}
    }
    
    private void wholeReduce (){
//    	assert num>0;
//    	assert den>0;
    	
    	whole +=num/den;
    	num = num%den;
//    	while (num>=den){
//    		num-=den;
//    		whole+=1;
//    	}
    }

    //return the numerator and denominator of (this)
    //public int numerator()   { return num; }
    //public int denominator() { return den; }

    // return double precision representation of (this)
    public boolean isZero (){
    	return (this.whole == 0) && (this.num == 0);
    }
    
    public void Zero (){
    	this.num=0;
    	this.whole=0;
    }
    
    public int isNeg(){
    	return neg?-1:1;
    }
    
    public double toDouble() {
        return (double) isNeg() * (whole * den + num) / den;
    }

    // return string representation of (this)
    public String toString() {
    	return (neg?"-":"")+(whole==0?"":whole+" ")+(den==1? num:num+"/"+den);
    }

    // return { -1, 0, +1 } if a < b, a = b, or a > b
    public int compareTo(Rational b) {
        double lhs =this.toDouble();// ((this.whole*this.den)+this.num) * b.den;
        double rhs = b.toDouble();//this.den * (b.num+(b.whole*b.den));
        if (lhs < rhs) return -1;
        if (lhs > rhs) return +1;
        return 0;
    }

    // is this Rational object equal to y?
    public boolean equals(Object y) {
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        RationalWithWhole b = (RationalWithWhole) y;
        return compareTo(b) == 0;
    }

    // hashCode consistent with equals() and compareTo()
    public int hashCode() {
        return this.toString().hashCode();
    }

    // return a * b, staving off overflow as much as possible by cross-cancellation
    public void times(RationalWithWhole b) {
    	if (b.isZero())
    		this.Zero();
    	else 
	    	this.times(b.whole,b.num,b.den,b.neg);
    }
    
    private void times (int whole, int num, int den,boolean neg){
    	if (/*num == 0*/false){
    		this.num=0;
    		this.whole=0;
    	}
    	else {
	    	this.num=((this.whole*this.den)+this.num)*((whole*den)+num);
	    	this.den*=den;
	    	this.whole=0;
	    	this.neg= this.neg ^ neg;
	    	this.wholeReduce();
    	}
    }

    /* adding in the following way
      						  (a2+a1*a3)*b3 + (b2+b1*b3)*a3
      a1(a2/a3) + b1(b2/b3) = =============================
             					  		(a3 * b3)
     */ 
    public void add(RationalWithWhole b) {
        if (b.isZero()) return ;//special case, optimization
        this.num=isNeg() * (this.num + (this.whole * this.den)) * b.den; //(a2+a1*a3)*b3 
        int bnum = b.isNeg() * (b.num + (b.whole * b.den)) * this.den;    //(b2+b1*b3)*a3
        this.num+=bnum;
        this.den*=b.den;  // (a3 * b3)
        this.whole=0;
        if (this.num<0){
        	this.num=-this.num;
        	this.neg=!this.neg;
        }
        this.wholeReduce();
    }
    

    // return -a
    public void negate() {
        this.neg=!this.neg;
    }

    // return a - b
    public void minus(RationalWithWhole b) {
       b.negate();
       this.add(b);
       b.negate();
    }

    public void reciprocal() { 
    	int temp = this.num + this.whole * this.den;
    	this.num = this.den;
    	this.den = temp;
    }

    // return a / b by multiplying a * 1/b
    public void divide(RationalWithWhole b) {
    	if (b.isZero()) throw new ArithmeticException("Division by zero");
        this.times(0,b.den,b.num + b.whole * b.den,b.neg);
    }


    // test client
    public static void main(String[] args) {
        RationalWithWhole x, y, z;

        // 1/2 + 1/3 = 5/6
        x = new RationalWithWhole(1, 2);
        y = new RationalWithWhole(1, 3);
        x.add(y);
        System.out.println(x);

        // 8/9 + 1/9 = 1
        x = new RationalWithWhole(8, 9);
        y = new RationalWithWhole(1, 9);
        x.add(y);
        System.out.println(x);

        // 1/20000 + 1/30000 = 1/12000
        x = new RationalWithWhole(1, 20000);
        y = new RationalWithWhole(1, 30000);
        x.add(y);
        System.out.println(x);

        // 1073741789/20 + 1073741789/30 = 1073741789/12
        x = new RationalWithWhole(1073741789, 20);
        y = new RationalWithWhole(1073741789, 30);
        x.add(y);
        System.out.println(x);

        //  4/17 * 17/4 = 1
        x = new RationalWithWhole(4, 17);
        y = new RationalWithWhole(17, 4);
        x.times(y);
        System.out.println(x);

        // 3037141/3247033 * 3037547/3246599 = 841/961 
        x = new RationalWithWhole(3037141, 3247033);
        y = new RationalWithWhole(3037547, 3246599);
        x.times(y);
        System.out.println(x);

        // 1/6 - -4/-8 = -1/3
        x = new RationalWithWhole( 1,  6);
        y = new RationalWithWhole(-4, -8);
        x.minus(y);
        System.out.println(x);
        
        x = new RationalWithWhole( 5,  3);
        System.out.println(x);
        y = new RationalWithWhole(0, 2);
        System.out.println(y);
        x.divide(y);
        System.out.println(x);
    }

}
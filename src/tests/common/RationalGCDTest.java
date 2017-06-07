package tests.common;

import junit.framework.TestCase;

import org.junit.Before;

import common.RationalGCD;

public class RationalGCDTest extends TestCase {

	private RationalGCD testiee = null;
	RationalGCD x, y, z;
	@Before
	public void setUp() throws Exception {
		
		
	}
	
	public void testAddSub(){
	       
	        // 1/2 + 1/3 = 5/6
	        x = new RationalGCD(1, 2);
	        y = new RationalGCD(1, 3);
	        x.add(y);
	        assertEquals(5.0/6,x.toDouble() );
	        System.out.println(x);
	        
	        y = new RationalGCD(2, 6);
	        x.minus(y);
	        assertEquals(3.0/6,x.toDouble() );

	        // 8/9 + 1/9 = 1
	        x = new RationalGCD(8, 9);
	        y = new RationalGCD(1, 9);
	        x.add(y);
	        assertEquals(1.0,x.toDouble() );
	        System.out.println(x);

	        //adding 0
	        y = new RationalGCD(0, 0);
	        x.add(y);
	        assertEquals(1.0,x.toDouble() );
	        
	        x.minus(y);
	        assertEquals(1.0,x.toDouble() );
	        
	        // 1/20000 + 1/30000 = 1/12000
	        x = new RationalGCD(1, 20000);
	        y = new RationalGCD(1, 30000);
	        x.add(y);
	        assertEquals(1.0/12000,x.toDouble() );
	        System.out.println(x);

	        // 5 + 0 = 5
	        x = new RationalGCD(5, 1);
	        y = new RationalGCD(0, 0);
	        x.add(y);
	        assertEquals(5.0,x.toDouble() );
	        System.out.println(x);
	        
	        // 5 -10 = -5
	        x = new RationalGCD(5, 1);
	        y = new RationalGCD(-10, 1);
	        x.add(y);
	        assertEquals(-5.0,x.toDouble() );
	        System.out.println(x);
	        
	        // 5 -10 = -5
	        x = new RationalGCD(5, 1);
	        y = new RationalGCD(10, 1);
	        x.minus(y);
	        System.out.println(x);
	        assertEquals(-5.0,x.toDouble() );
	        
	        
	        

	}
	
		public void testMulDiv (){
			 //  4/17 * 17/4 = 1
	        x = new RationalGCD(4, 17);
	        y = new RationalGCD(17, 4);
	        x.times(y);
	        assertEquals(1.0,x.toDouble() );
	        System.out.println(x);

	        y = new RationalGCD(0,4);// 0 
	        x.times(y);
	        assertEquals(0.0,x.toDouble() );
	        System.out.println(x);
	        
	        

	        // 1/6 - -4/-8 = -1/3
	        x = new RationalGCD( 1,  6);
	        y = new RationalGCD(-4, -8);
	        x.minus(y);
	        assertEquals(-1.0/3,x.toDouble() );
	        System.out.println(x);
	        
	   /*     x = new RationalGCD( 5,  3);
	        System.out.println(x);
	        y = new RationalGCD(0, 2);
	        System.out.println(y);
	        x.divide(y);
	        System.out.println(x);*/
			
		}
		
		public void testSpecialCase (){
			//test NAN
			x = RationalGCD.NaN();
			assertEquals(x.isNaN(),true);
			y = new RationalGCD(-4, -8);
			x.add(y);
			assertEquals(x.isNaN(),true);
			x.times(y);
			assertEquals(x.isNaN(),true);
			y=new RationalGCD(1,0);
			assertEquals(x.isNaN(),true);
			x = new RationalGCD(3,4);
			x.times(y);
			assertEquals(x.isNaN(),true);
			x = new RationalGCD(3,4);
			x.divide(y);
			assertEquals(x.isNaN(),true);
			//test zero
			x=RationalGCD.makeZero();
			assertEquals(x.isZero(),true);
			y = new RationalGCD(-4, -8);
			x.times(y);
			assertEquals(x.isZero(),true);
			x.add(y);
			assertEquals(x.toDouble(),y.toDouble());
			y=RationalGCD.makeZero();
			x.divide(y);
			assertEquals(x.isNaN(),true);
			x = new RationalGCD(3,4);
			x.add(y);
			assertEquals(x.toDouble(),(new RationalGCD(3,4)).toDouble());
			x.times(y);
			assertEquals(x.isZero(), true);
		}
		
	

}

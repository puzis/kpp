package tests.common;


import junit.framework.TestCase;

import org.junit.Before;

import common.RationalWithWhole;

public class RationalWholeTest extends TestCase {

	private RationalWithWhole testiee = null;
	RationalWithWhole x, y, z;
	@Before
	public void setUp() throws Exception {
		
		
	}
	
	public void testAddSub(){
	       
	        // 1/2 + 1/3 = 5/6
	        x = new RationalWithWhole(1, 2);
	        y = new RationalWithWhole(1, 3);
	        x.add(y);
	        assertEquals(5.0/6,x.toDouble() );
	        System.out.println(x);
	        
	        y = new RationalWithWhole(2, 6);
	        x.minus(y);
	        assertEquals(3.0/6,x.toDouble() );

	        // 8/9 + 1/9 = 1
	        x = new RationalWithWhole(8, 9);
	        y = new RationalWithWhole(1, 9);
	        x.add(y);
	        assertEquals(1.0,x.toDouble() );
	        System.out.println(x);

	        //adding 0
	        y = new RationalWithWhole(0, 0);
	        x.add(y);
	        assertEquals(1.0,x.toDouble() );
	        
	        x.minus(y);
	        assertEquals(1.0,x.toDouble() );
	        
	        // 1/20000 + 1/30000 = 1/12000
	        x = new RationalWithWhole(1, 20000);
	        y = new RationalWithWhole(1, 30000);
	        x.add(y);
	        assertEquals(1.0/12000,x.toDouble() );
	        System.out.println(x);

	        // 5 + 0 = 5
	        x = new RationalWithWhole(5, 1);
	        y = new RationalWithWhole(0, 0);
	        x.add(y);
	        assertEquals(5.0,x.toDouble() );
	        System.out.println(x);
	        
	        // 5 -10 = -5
	        x = new RationalWithWhole(5, 1);
	        y = new RationalWithWhole(-10, 1);
	        x.add(y);
	        assertEquals(-5.0,x.toDouble() );
	        System.out.println(x);
	        
	        // 5 -10 = -5
	        x = new RationalWithWhole(5, 1);
	        y = new RationalWithWhole(10, 1);
	        x.minus(y);
	        System.out.println(x);
	        assertEquals(-5.0,x.toDouble() );
	        
	        
	        

	}
	
		public void testMulDiv (){
			 //  4/17 * 17/4 = 1
	        x = new RationalWithWhole(4, 17);
	        y = new RationalWithWhole(17, 4);
	        x.times(y);
	        assertEquals(1.0,x.toDouble() );
	        System.out.println(x);

	        y = new RationalWithWhole(0,4);// 0 
	        x.times(y);
	        assertEquals(0.0,x.toDouble() );
	        System.out.println(x);
	        
	        

	        // 1/6 - -4/-8 = -1/3
	        x = new RationalWithWhole( 1,  6);
	        y = new RationalWithWhole(-4, -8);
	        x.minus(y);
	        assertEquals(-1.0/3,x.toDouble() );
	        System.out.println(x);
	        
	   /*     x = new RationalWithWhole( 5,  3);
	        System.out.println(x);
	        y = new RationalWithWhole(0, 2);
	        System.out.println(y);
	        x.divide(y);
	        System.out.println(x);*/
			
		}
		
	

}

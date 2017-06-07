package tests.common;

import java.util.Random;

import junit.framework.TestCase;

import common.RationalGCD;
import common.RationalWithWhole;


public class RationalPerformanceTest extends TestCase {
	
	private RationalGCD ratgcd = null;
	private RationalWithWhole ratwhole = null;
	private Random rand;
	public void setUp() throws Exception {
		rand= new Random ();
		int a = rand.nextInt(10000)+10000;
		int b = rand.nextInt(10000)+10000;
		ratgcd=new RationalGCD (a,b);
		ratwhole = new RationalWithWhole (a,b);
	}
	
	public void testLownumbers(){
		
		int a = rand.nextInt(10000)+10000;
		int b = rand.nextInt(10000)+10000;
		RationalGCD gc=new RationalGCD (a,b);
		RationalWithWhole ww = new RationalWithWhole (a,b);
		
		double timewhole=0,timegcd=0;
		long tic = 0;
		double realres= ratwhole.toDouble();
		for (long i=1; i<1000;i++){
			tic=System.nanoTime();
			realres += ww.toDouble();
//			System.out.println("Real res add: "+realres);
			realres *= ww.toDouble();
//			System.out.println("Real res times: "+realres);
			realres -= ww.toDouble();
//			System.out.println("Real res minus: "+realres);
			realres /= ww.toDouble();
//			System.out.println("Real res divide: "+realres);
			timewhole+=nanosToSecs(System.nanoTime()-tic);
//			System.out.println("Real res : "+realres);
//			ratwhole.add(ww);
////			System.out.println("whole add: "+ratwhole.toDouble());
//			ratwhole.times(ratwhole);
////			System.out.println("whole times: "+ratwhole.toDouble());
//			ratwhole.minus(ww);
////			System.out.println("whole minus: "+ratwhole.toDouble());
//			ratwhole.divide(ww);
//			timewhole+=nanosToSecs(System.nanoTime()-tic);
//			System.out.println("whole: "+ratwhole.toDouble());			
			
			tic=System.nanoTime();
			ratgcd.add(gc);
//			System.out.println("gcd add: "+ratgcd.toDouble());
			ratgcd.times(gc);
//			System.out.println("gcd times: "+ratgcd.toDouble());
			ratgcd.minus(gc);
//			System.out.println("gcd minus: "+ratgcd.toDouble());
			ratgcd.divide(gc);
			timegcd+=nanosToSecs(System.nanoTime()-tic);
//			System.out.println("gcd : "+ratgcd+", "+ratgcd.toDouble());
			
			
			//assertEquals(ratgcd.toDouble(), realres);
			
		}
		
		System.out.println("Rational Whole time: "+timewhole);
		System.out.println("Real res : "+realres);
		System.out.println("Rational GCD time: "+timegcd);
		System.out.println("gcd : "+ratgcd.toDouble());
		
	}
	
	public void testHighnumbers(){
		
	}
	
	private static double nanosToSecs(long c) {
        return ((double)c)/1e9;}

}

package tests.common;

import junit.framework.TestCase;

import common.Heap;
import common.Pair;

public class HeapTest extends TestCase {

	Heap<String> h;
	public void setUp() {
		h = new Heap<String>(10);
		h.insert(1.0, "One");
		h.insert(2.0, "Two");
		h.insert(3.0, "Three");
		h.insert(4.0, "Four");
		h.insert(5.0, "Five");
		h.insert(6.0, "Six");
		h.insert(7.0, "Seven");
		h.insert(8.0, "Eight");
		h.insert(9.0, "Nine");
		h.insert(10.0, "Ten");
	}
	
	public void testDeleteMin() {		
		assertEquals("One", h.deleteMin());		
		assertEquals("Two", h.deleteMin());		
		assertEquals("Three", h.deleteMin());		
		assertEquals("Four", h.deleteMin());		
		assertEquals("Five", h.deleteMin());		
		assertEquals("Six", h.deleteMin());		
		assertEquals("Seven", h.deleteMin());			
		assertEquals("Eight", h.deleteMin());		
		assertEquals("Nine", h.deleteMin());		
		assertEquals("Ten", h.deleteMin());		
	}
	
	public void testUpdateKey() {
		h.updateKey("One", 100.0);
		h.updateKey("Ten", -10.0);
		assertEquals("Ten", h.deleteMin());
		assertEquals("Two", h.deleteMin());		
		assertEquals("Three", h.deleteMin());		
		assertEquals("Four", h.deleteMin());		
		assertEquals("Five", h.deleteMin());		
		assertEquals("Six", h.deleteMin());		
		assertEquals("Seven", h.deleteMin());			
		assertEquals("Eight", h.deleteMin());		
		assertEquals("Nine", h.deleteMin());		
		assertEquals("One", h.deleteMin());
	}
	
	public void testPeek() {
		Pair<Double, String> p = h.peek();
		assertEquals("One", p.getValue2());		
		assertEquals(1.0, p.getValue1());		
		
		h.deleteMin();
		p = h.peek();
		assertEquals("Two", p.getValue2());		
		assertEquals(2.0, p.getValue1());
	}
	
	public void testRemove() {
		h.remove("Three");
		String sExp = "One Two Six Four Five Ten Seven Eight Nine ";		
		assertEquals(sExp, h.toString());
		
		h.remove("Three");
		sExp = "One Two Six Four Five Ten Seven Eight Nine ";		
		assertEquals(sExp, h.toString());
		
		h.remove("Two");
		sExp = "One Four Six Eight Five Ten Seven Nine ";		
		assertEquals(sExp, h.toString());
	}
}

package common;

import java.io.Serializable;

import javolution.util.Index;



public class Triple<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> implements Serializable,Comparable<Triple<T1, T2, T3>> {
	

	private static final long serialVersionUID = 1L;
	
	private T1 m_value1;
	private T2 m_value2;
	private T3 m_value3;

	public Triple(T1 v1, T2 v2, T3 v3) {
		m_value1 = v1;
		m_value2 = v2;
		m_value3 = v3;
	}
	
	/**
	 * sets all values to null
	 */
	public Triple (){
		m_value1 = null;
		m_value2 = null;
		m_value3 = null;
	}
	
	public Triple<T1,T2,T3>  set (T1 v1, T2 v2, T3 v3){
		m_value1 = v1;
		m_value2 = v2;
		m_value3 = v3;
		return this;
	}

	public T1 getValue1() {
		return m_value1;
	}

	public T2 getValue2() {
		return m_value2;
	}

	public T3 getValue3() {
		return m_value3;
	}
	

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Triple){
			return this.m_value1.equals(((Triple)obj).getValue1())
			&& this.m_value2.equals(((Triple)obj).getValue2())
			&& this.m_value3.equals(((Triple)obj).getValue3());
		}
		else return false;
	}
	
	
	@Override
	public int compareTo(Triple<T1, T2, T3> other){
		int tmp=0;
		if (this.equals(other)) 
			return 0;
		tmp=this.m_value1.compareTo(other.getValue1());
		if (tmp!=0) return tmp;
		tmp=this.m_value2.compareTo(other.getValue2());
		if (tmp!=0) return tmp;
		tmp=this.m_value3.compareTo(other.getValue3());
		if (tmp!=0) return tmp;
		else return 0;
	}
	
	
	
	@Override 
	public int hashCode() // spelled correctly
	{
		return (this.m_value1.hashCode() +this.m_value2.hashCode() +this.m_value3.hashCode()) /3;
	}
	
	
	
	@Override
	public String toString() {
		return '('+this.m_value1.toString()+','+this.m_value2.toString()+','+this.m_value3.toString()+')';
	}

	public static void main (String [] args){
	Triple<Index,Index,Index> s1 = new Triple <Index,Index,Index> (Index.valueOf(1), Index.valueOf(2), Index.valueOf(6));
	Triple<Index,Index,Index> s2 = new Triple <Index,Index,Index> (Index.valueOf(1), Index.valueOf(2), Index.valueOf(5));
	System.out.println(s1.compareTo(s2));
	s1.equals(s1);
	System.out.println(s1.toString());
	}
}


	
	


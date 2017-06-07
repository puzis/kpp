package common;

public class KeyValuePair<T1 extends Comparable<T1>, T2> extends Pair<T1,T2> implements Comparable<KeyValuePair<T1, T2>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KeyValuePair(T1 v1, T2 v2) {
		super(v1, v2);
	}

	@Override
	public int compareTo(KeyValuePair<T1, T2> other){
		int tmp=0;
		if (this.equals(other)) 
			return 0;
		tmp=this.m_value1.compareTo(other.getValue1());
		if (tmp!=0) return tmp;
		else return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KeyValuePair){
			Object k = ((KeyValuePair)obj).getValue1();
			return this.m_value1.equals(k);
		}
		else return false;
	}
	
	public void set(T1 k, T2 v){
		m_value1=k;
		m_value2=v;
	}
	
}

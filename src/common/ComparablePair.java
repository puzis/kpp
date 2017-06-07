package common;

public class ComparablePair<T1 extends Comparable<T1>, T2 extends Comparable<T2>> extends Pair<T1,T2> implements Comparable<ComparablePair<T1,T2>> {

	private static final long serialVersionUID = 1L;

	public ComparablePair(T1 v1, T2 v2) {
		super(v1, v2);
	}

	@Override
	public int compareTo(ComparablePair<T1, T2> other) {
		int tmp=0;
		if (this.equals(other)) 
			return 0;
		tmp=this.m_value1.compareTo(other.getValue1());
		if (tmp!=0) return tmp;
		tmp=this.m_value2.compareTo(other.getValue2());
		if (tmp!=0) return tmp;
		else return 0;
	}
}

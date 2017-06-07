package common;

import java.util.Map;

import javolution.util.FastMap;

@SuppressWarnings("unchecked")
public class Heap<V>{
	protected Map<V,Integer> m_valueToIndex; 
	protected Pair<Double, V>[] m_pairs;
	protected int sz = 0;
	

	public Heap(Double[] keys, V[] values, int max_sz){
		m_pairs = new Pair[max_sz];
		for(int i=0; i<keys.length; i++){
			insert(keys[i], values[i]);
		}
		m_valueToIndex = new FastMap<V,Integer>();
	}
	
	public Heap(int max_sz){
		m_pairs = new Pair[max_sz];
		m_valueToIndex = new FastMap<V,Integer>();
	}
	
	public boolean isEmpty(){
		return sz == 0;
	}
	
	public void insert(Double k, V v){
		if(sz == m_pairs.length)
			throw new IllegalStateException();
		m_pairs[sz] = new Pair<Double, V>(k, v);
		m_valueToIndex.put(v, sz);
		sz++;
		heapify(sz-1);
	}
	
	public void updateKey(V v, Double k){
		if(m_valueToIndex.containsKey(v)) {
			int i = m_valueToIndex.get(v);
			m_pairs[i].setValue1(k);
			heapify(i);
		}
	}
	
	public void remove(V v) {
		if(m_valueToIndex.containsKey(v)) {
			int i = m_valueToIndex.get(v);
			if(i < sz-1) { 
			exchange(i,sz-1);
			deleteLast();	
			heapify(i);
			}
			else {
				deleteLast();
			}
		}
	}
	public V deleteMin(){
		if(sz == 0)
			return null;
		V temp = m_pairs[0].getValue2();
		exchange(0, sz-1);
		deleteLast();
		heapify(0);
		return temp;
	}

	public Pair<Double, V> peek(){
		if(sz == 0)
			return new Pair<Double, V>(Double.POSITIVE_INFINITY, null);
		return m_pairs[0];
	}
	/*
	 * which 0 => parent
	 * 		 1 => left child	
	 *       2 => right child
	 */   
	protected void heapify(int i){
		int j = -1;
		int parent = (i-1)/2;
		int lChild = (2*i + 1);
		int rChild = (2*i + 2);
		if(i > 0 && m_pairs[parent].getValue1() > m_pairs[i].getValue1()){
			j = parent;
		}
		else {
			if( lChild < sz && (m_pairs[lChild].getValue1() < m_pairs[i].getValue1())) {
				j = lChild;
			}
			if( rChild < sz && (m_pairs[rChild].getValue1() < m_pairs[i].getValue1())) {
				if((m_pairs[rChild].getValue1() < m_pairs[lChild].getValue1())){
					j = rChild;
				}
			}

		}
		
		if(j < 0) return;
		exchange(i, j);
		heapify(j);
	}
	
	protected void  exchange(int i, int j){
		m_valueToIndex.put(m_pairs[j].getValue2(), i);
		m_valueToIndex.put(m_pairs[i].getValue2(), j);
		Pair<Double, V> temp = m_pairs[i];
		m_pairs[i] = m_pairs[j];
		m_pairs[j] = temp;
	}
	
	protected void deleteLast(){
		m_valueToIndex.remove(m_pairs[sz-1].getValue2());
		m_pairs[sz-1] = null;
		sz--;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<sz;i++){
			sb.append(m_pairs[i].getValue2()).append(" ");
		}
		return sb.toString();
	}
}

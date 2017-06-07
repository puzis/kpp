package common;

import javolution.util.FastMap;
import javolution.util.Index;

public class ShadowedHistoriedCache implements ShadowedHistoriedCacheInterface{
	public static final int CACHEID = 1;
	private FastMap<Triple<Index, Index, Index>,Double> m_cache;
	private FastMap<Triple<Index, Index, Index>,Double> m_cache_shadow=null;
	private FastMap<Triple<Index, Index, Index>,Double> m_currentwriteable_cache;
	private FastMap<Triple<Index, Index, Index>,Double> m_history_cache=null;
	
	private boolean mShadowable=false;
	private boolean mHistorySavable=false;	
	public final int m_DontCare;
	
	private boolean mShadowed=false;
	private boolean mSaveHistory=false;	
	private Triple<Index, Index, Index> mkey;
	
	public ShadowedHistoriedCache(int capacity,boolean shadowable,boolean historyable) {
		m_DontCare=capacity;
		int cap3=capacity*capacity*capacity;
		this.m_cache=new FastMap<Triple<Index, Index, Index>,Double>(cap3);
		mShadowable=shadowable;
		mHistorySavable=historyable;
		if (mShadowable){
			this.m_cache_shadow=new FastMap<Triple<Index, Index, Index>,Double>(capacity*capacity);
		}
		if (mHistorySavable){
			m_history_cache=new FastMap<Triple<Index, Index, Index>, Double>(capacity*capacity);
		}
		mkey = new Triple<Index, Index, Index>(Index.valueOf(-1),
				  Index.valueOf(-1),
				  Index.valueOf(-1));
		//m_currentwriteable_cache=m_cache;
	}
	/**
	 * put a key/value pair in the cache. overwrite if exists.
	 * @param key
	 * @param value
	 * @return
	 */
	public final Double put(int i, int j, int k,double value){
		mkey = new Triple<Index, Index, Index>(Index.valueOf(i),
				  Index.valueOf(j),
				  Index.valueOf(k));
//		mkey.set(Index.valueOf(i), Index.valueOf(j), Index.valueOf(k));
		if (mSaveHistory && !m_history_cache.containsKey(mkey)){
			m_history_cache.put(mkey,m_cache.get(mkey));
		}
		if (this.mShadowed)
			return this.m_cache_shadow.put(mkey, value);	
		else return this.m_cache.put(mkey, value);
	}
	
	/**
	 * gets a value that maches the key. null if not found
	 * @param key
	 * @return
	 */
	public final Double get(int i, int j, int k){
		mkey = new Triple<Index, Index, Index>(Index.valueOf(i),
				  Index.valueOf(j),
				  Index.valueOf(k));
//		mkey.set(Index.valueOf(i), Index.valueOf(j), Index.valueOf(k));
		return this.m_cache.get(mkey);
	}
	/**
	 * @ finds if a key is in the cache
	 * @param key
	 * @return
	 */
	public final boolean containsKey(int i, int j, int k){
		mkey = new Triple<Index, Index, Index>(Index.valueOf(i),
				  Index.valueOf(j),
				  Index.valueOf(k));
//		mkey.set(Index.valueOf(i), Index.valueOf(j), Index.valueOf(k));
		return this.m_cache.containsKey(mkey);
		
	}
	
	/**
	 * shadows the cache: all put operation will be written to a second cache, until user decides to write the to main cache
	 */
	public void StartShadowing (){
		if (mShadowable){
			this.m_cache_shadow.clear();
			mShadowed=true;
		}
	}
	/**
	 * apply all changes from shadow cache to main cache.
	 */
	public void StopShadowing(){
		if (mShadowed){
//			m_currentwriteable_cache=m_cache;
//			m_cache=m_cache_shadow;
//			m_cache_shadow=m_currentwriteable_cache;
//			m_currentwriteable_cache=m_cache;
			this.m_cache.putAll(this.m_cache_shadow);
			mShadowed=false;
		}
	}
	
	/**
	 * save current cache state, in order to restore it to the state it was when this funcion was called
	 */
	public void saveState(){
		if (mHistorySavable)
			mSaveHistory=true;
	}
	
	/**
	 * restores the cache to the state it was when the last saveState call was made.
	 */
	public void restoreState(){
		if (mSaveHistory){
			this.m_cache.putAll(m_history_cache);
			m_history_cache.clear();
			m_cache_shadow.clear();
			
			mSaveHistory=false;
		}
	}
	
	public int DONTCARE (){
		return this.m_DontCare;
	}

}

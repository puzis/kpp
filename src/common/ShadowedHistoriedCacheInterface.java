package common;

public interface ShadowedHistoriedCacheInterface {
	public final int CACHEID = 0;
	/**
	 * put a key/value pair in the cache. overwrite if exists.
	 * @param key
	 * @param value
	 * @return
	 */
	public Double put(int i, int j, int k ,double value);	
	
	/**
	 * gets a value that maches the key. null if not found
	 * @param key
	 * @return
	 */
	public Double get(int i, int j, int k );
	
	/**
	 * @ finds if a key is in the cache
	 * @param key
	 * @return
	 */
	public boolean containsKey(int i, int j, int k );
	
	/**
	 * shadows the cache: all put operation will be written to a second cache, until user decides to write the to main cache
	 */
	public void StartShadowing ();
	
	/**
	 * apply all changes from shadow cache to main cache.
	 */
	public void StopShadowing();
	/**
	 * save current cache state, in order to restore it to the state it was when this funcion was called
	 */
	public void saveState();
	/**
	 * restores the cache to the state it was when the last saveState call was made.
	 */
	public void restoreState();
	
	public int DONTCARE ();
	
	
	
}

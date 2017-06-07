package common;

import java.util.Arrays;

public class ShadowedHistoriedCacheArr implements ShadowedHistoriedCacheInterface {
	public static final int CACHEID = 2;
	private double [][][] m_cache;
	
	//private FastMap<Triple<Index, Index, Index>,Double> m_cache_shadow=null;
	private double [][][] m_cache_shadow1;
	private double [][][] m_cache_shadow2;
	private double [][][] m_cache_current_writeable;
	private double [][][] m_cache_current_readable;
	private double [][][] m_history_cache;
	private int [] mCandidatesindexes;
	private final int mCandsize;
	//private FastMap<Triple<Index, Index, Index>,Double> m_history_cache=null;
	
	
	public final int m_DontCare;
	private boolean mShadowable=false;
	private boolean mHistorySavable=false;	
	
	private boolean mShadowed=false;
	private boolean mSaveHistory=false;	
	
	public static double EMPTY = -1.0; 
	
	
	
	public ShadowedHistoriedCacheArr(int capacity,boolean shadowable,boolean historyable,int candnum,int [] candidates) {
		m_DontCare=capacity;
		mCandsize=candnum;
		this.m_cache=new double [capacity+1][capacity+1][capacity+1];
		mShadowable=shadowable;
		mHistorySavable=historyable;
		if (mShadowable){
			if (null==candidates || (candidates.length!=capacity && candnum<=capacity)) {this.mShadowable=false; return;}
			mCandidatesindexes=candidates;
			this.m_cache_shadow1 = new double [candnum+1][candnum+1][capacity+1];
			this.m_cache_shadow2 = new double [candnum+1][candnum+1][capacity+1];
			this.m_cache_current_writeable=m_cache_shadow1;
			this.m_cache_current_readable=m_cache_shadow2;
			for (int i=0;i<=candnum;i++)
				for (int j=0;j<=candnum;j++){
					Arrays.fill(m_cache_shadow1[i][j],(double) EMPTY);
					Arrays.fill(m_cache_shadow2[i][j],(double) EMPTY);
				}
			
		}
		if (mHistorySavable){
			this.m_history_cache = new double [candnum+1][candnum+1][capacity+1];
			for (int i=0;i<=candnum;i++)
				for (int j=0;j<=candnum;j++)
					Arrays.fill(m_history_cache[i][j],(double) EMPTY);
		}
		for (int i=0;i<=capacity;i++)
			for (int j=0;j<=capacity;j++)
				Arrays.fill(m_cache[i][j],(double) EMPTY);
	}
	
	public ShadowedHistoriedCacheArr(int capacity,boolean shadowable,boolean historyable) {
		this (capacity,false,false,-1,null);
	}
	/**
	 * put a key/value pair in the cache. overwrite if exists.
	 * @param key
	 * @param value
	 * @return
	 */
	
	public final Double put(int i, int j, int k ,double value){
		if (this.mShadowed||this.mSaveHistory){
			int loc_i = i<m_DontCare ? mCandidatesindexes[i] : mCandsize;
			int loc_j = j<m_DontCare ? mCandidatesindexes[j] : mCandsize;
			this.m_cache_current_writeable[loc_i][loc_j][k] = value;
		}
		else {
			this.m_cache[i][j][k]  = value;
		}
		return value;
	}
	
	/**
	 * gets a value that maches the key. null if not found
	 * @param key
	 * @return
	 */
	public final Double get(int i, int j, int k){
		Double res;
		if (this.mShadowed||this.mSaveHistory){
			int loc_i = i<m_DontCare ? mCandidatesindexes[i] : mCandsize;
			int loc_j = j<m_DontCare ? mCandidatesindexes[j] : mCandsize;
			if (loc_i<0 || loc_j<0)
				res=m_cache[i][j][k];
			else
				res=m_cache_current_readable[loc_i][loc_j][k];
		}
		else
			res=m_cache[i][j][k];
		return res;
	}
	
	/**
	 * @ finds if a key is in the cache
	 * @param key
	 * @return
	 */
	public final boolean containsKey(int i, int j, int k){
		//return m_cache[key.getValue1().intValue()][key.getValue2().intValue()][key.getValue3().intValue()]!=-1;
		boolean res = false;
		if (this.mShadowed||this.mSaveHistory){
			int loc_i = i<m_DontCare ? mCandidatesindexes[i] : mCandsize;
			int loc_j = j<m_DontCare ? mCandidatesindexes[j] : mCandsize;
			if (loc_i<0 || loc_j<0)
				res=m_cache[i][j][k]!=EMPTY;
			else
				res=m_cache_current_readable[loc_i][loc_j][k]!=EMPTY;
		}
		else
			res=m_cache[i][j][k]!=EMPTY;
		return res;
	}
	
	public final boolean containsKeyInOriginal(int i, int j, int k){
		return m_cache[i][j][k]!=EMPTY;
	}
	
	/**
	 * shadows the cache: all put operation will be written to a second cache, until user decides to write the to main cache
	 */
	public void StartShadowing (){
		if (mShadowable){
			mShadowed=true;
		}
	}
	/**
	 * apply all changes from shadow cache to main cache.
	 */
	public void StopShadowing(){
		if (mShadowed){
			if (m_cache_current_writeable==m_cache_shadow1){
				m_cache_current_readable=m_cache_shadow1;
				m_cache_current_writeable=m_cache_shadow2;
			}
			else{
				m_cache_current_writeable=m_cache_shadow1;
				m_cache_current_readable=m_cache_shadow2;
			}
			mShadowed=false;		
		}
	}
	
	/**
	 * save current cache state, in order to restore it to the state it was when this funcion was called
	 */
	public void saveState(){
//				long tic = System.nanoTime();
				if (mHistorySavable){
					int loc_s,loc_v;
					for (int i=0;i<m_cache.length;i++){
						if (i==m_DontCare)
							loc_s=mCandsize;
						else if (mCandidatesindexes[i]!=-1)
							loc_s = this.mCandidatesindexes[i];
						else continue;
						for (int k=0;k<m_cache[i].length;k++){
							if (k==m_DontCare)
								loc_v=mCandsize;
							else if (mCandidatesindexes[k]!=-1)
								loc_v = this.mCandidatesindexes[k];
							else continue;
							for (int j=0;j<m_cache[i][k].length;j++){
								double val = m_cache[i][k][j];
								m_cache_shadow1[loc_s][loc_v][j]=val;
								m_cache_shadow2[loc_s][loc_v][j]=val;
								m_history_cache[loc_s][loc_v][j]=val;
							}
						}
					}

				mSaveHistory=true;
				}
//				System.out.println("savestate: "+nanosToSecs(System.nanoTime()-tic));
				
				
				
/*				for (int i=0;i<m_cache.length;i++){
					for (int j=0;j<m_cache[i].length;j++){
						for (int k=0;k<m_cache[i][j].length;k++){
							double val = m_cache[i][j][k];
							m_cache_shadow1[i][j][k]=val;
							m_cache_shadow2[i][j][k]=val;
							m_history_cache[i][j][k]=val;
						}
					}
				}*/

	}
	
	/**
	 * restores the cache to the state it was when the last saveState call was made.
	 */
	public void restoreState(){
		if (mSaveHistory){
			mSaveHistory=false;
		}
	}

	public int DONTCARE (){
		return this.m_DontCare;
	}
	
	public static double nanosToSecs(long c) {
	    return ((double)c)/1e9;}
	
	

	public void printcache(int which){
		double [][][] printcache = null;
		switch (which){
			case 1:System.out.println("Original Cache");
				   printcache = this.m_cache; break;
			case 2:System.out.println("current writable ");
				printcache = this.m_cache_current_writeable; break;
			case 3:System.out.println("current readable ");
					printcache = this.m_cache_current_readable; break;
		
		}
		
		for (int i = 0 ; i<printcache.length; i++){
			System.out.println(i+" th matrix:");
			for (int j = 0 ; j<printcache.length; j++){
				for (int k = 0 ; k<printcache.length; k++){
					System.out.print(printcache[i][j][k]+", ");
				}
				System.out.println();
			}
			System.out.println();
		}
		
	}
	
	public void printgetAll (){
		System.out.println("*****************************************");
		for (int i = 0 ; i<this.m_cache.length; i++){
			for (int j = 0 ; j<this.m_cache.length; j++){
				for (int k = 0 ; k<this.m_cache.length; k++){
					System.out.print(this.get(i, j, k)+", ");
				}
				System.out.println();
			}
			System.out.println("\n");
		}
	}
	
	public void printAllcachedim (int i){
		System.out.println("Original Cache ");
		for (int j = 0 ; j<this.m_cache[i].length; j++){
			for (int k = 0 ; k<this.m_cache[i][j].length; k++){
				System.out.print(this.m_cache[i][j][k]+", ");
			}
			System.out.println();
		}
		
		System.out.println("current writable ");
		for (int j = 0 ; j<this.m_cache_current_writeable[i].length; j++){
			for (int k = 0 ; k<this.m_cache_current_writeable[i][j].length; k++){
				System.out.print(this.m_cache_current_writeable[i][j][k]+", ");
			}
			System.out.println();
		}
		
		System.out.println("current readable ");
		for (int j = 0 ; j<this.m_cache_current_readable[i].length; j++){
			for (int k = 0 ; k<this.m_cache_current_readable[i][j].length; k++){
				System.out.print(this.m_cache_current_readable[i][j][k]+", ");
			}
			System.out.println();
		}
		
	}
	
	
	public static void main(String[] args) {
		int [] cands=new int [] {-1,0,1};
		int candsnum=2;
		int graphsize=cands.length;
		ShadowedHistoriedCacheArr shca = new ShadowedHistoriedCacheArr(graphsize,true,true,candsnum,cands);
		for (int i = 0 ; i<graphsize+1; i++){
			for (int j = 0 ; j<graphsize+1; j++){
				for (int k = 0 ; k<graphsize+1; k++){
					shca.put (i,j,k,7.0);
				}
			}
		}
		shca.printcache(1);
		
		System.out.println("save state");
		shca.saveState();
			for (int a=0; a<5;a++){
				shca.printgetAll();
				System.out.println("Shadowing");
				shca.StartShadowing();
					for ( int i = 1 ; i<graphsize+1; i++){
						for (int j = 1 ; j<graphsize+1; j++){
							for (int k = 0 ; k<graphsize+1; k++){
								shca.put (i,j,k,a*1.0);
							}
						}
					}
					shca.printgetAll();
				System.out.println("Stop Shadowing");
				shca.StopShadowing();
			}	
				
			
			
			
		System.out.println("state restored");
		shca.restoreState();
		System.out.println(shca.get(0,0,0)==666.6);
		System.out.println(shca.get(1, 1, 0)==3.3);
		System.out.println(shca.get(1, 2, 0)==6.3);
		System.out.println(shca.get(shca.DONTCARE(), shca.DONTCARE(), shca.DONTCARE())==1.1);
		
	}
	

}
package common;

public class ShadowedHistoriedCacheArrRational  {
	public static final int CACHEID = 2;
	
	//access to the cache is done in pairs, of 2 longs
	private long [][][] m_cache;
	private long [][][] m_cache_shadow1;
	private long [][][] m_cache_shadow2;
	private long [][][] m_cache_current_writeable;
	private long [][][] m_cache_current_readable;
	private long [][][] m_history_cache;
	private int [] mCandidatesindexes;
	private final int mCandsize;
	
	public final int m_DontCare;
	private boolean mShadowable=false;
	private boolean mHistorySavable=false;	
	
	private boolean mShadowed=false;
	private boolean mSaveHistory=false;	
	
	public static RationalGCD EMPTY = RationalGCD.NaN();
	

	public ShadowedHistoriedCacheArrRational(int capacity,boolean shadowable,boolean historyable,int candnum,int [] candidates) {
		m_DontCare=capacity;
		mCandsize=candnum;
		mShadowable=shadowable;
		mHistorySavable=historyable;
		
		this.m_cache=new long [(capacity+1)][(capacity+1)][(capacity+1)*2];
		if (mShadowable){
			//inits shadow caches
			if (null==candidates || (candidates.length!=capacity && candnum<=capacity)) 
				{this.mShadowable=false; return;}
			mCandidatesindexes=candidates;
			this.m_cache_shadow1 = new long [(candnum+1)][(candnum+1)][(capacity+1)*2];
			this.m_cache_shadow2 = new long [(candnum+1)][(candnum+1)][(capacity+1)*2];
			this.m_cache_current_writeable=m_cache_shadow1;
			this.m_cache_current_readable=m_cache_shadow2;
			for (int i=0;i<=candnum;i++)
				for (int j=0;j<candnum;j++)
					for (int k=0;k<(capacity*2+1);k+=2){
						this.m_cache_shadow1[i][j][k]=1;
						this.m_cache_shadow1[i][j][k+1]=0;
						this.m_cache_shadow2[i][j][k]=1;
						this.m_cache_shadow2[i][j][k+1]=0;
					}
		}
		if (mHistorySavable){
			//inits history cache
			this.m_history_cache = new long [candnum+1][candnum+1][(capacity+1)*2];
			for (int i=0;i<=candnum;i++)
				for (int j=0;j<=candnum;j++)
					for (int k=0;k<(capacity)*2+1;k+=2){
						this.m_history_cache[i][j][k]=1;
						this.m_history_cache[i][j][k+1]=0;
					}
		}
		
		//inits main cache
		for (int i=0;i<=capacity;i++)
			for (int j=0;j<=capacity;j++)
				for (int k=0;k<(capacity)*2+1;k+=2){
					m_cache[i][j][k]=1;
					m_cache[i][j][k+1]=0;
				}
//		this.printcache (1);
	}
	
	public ShadowedHistoriedCacheArrRational(int capacity,boolean shadowable,boolean historyable) {
		this (capacity,false,false,-1,null);
	}
	/**
	 * put a key/value pair in the cache. overwrite if exists.
	 * @param key
	 * @param value
	 * @return
	 */
	
	public final void put(int i, int j, int k ,RationalGCD arg){
		k=2*k; //index fix
		if (this.mShadowed||this.mSaveHistory){
			int loc_i = i<m_DontCare ? mCandidatesindexes[i] : mCandsize;
			int loc_j = j<m_DontCare ? mCandidatesindexes[j] : mCandsize;
			this.m_cache_current_writeable[loc_i][loc_j][k] = arg.num();
			this.m_cache_current_writeable[loc_i][loc_j][k+1] = arg.denom();
		}
		else {
			this.m_cache[i][j][k]  = arg.num();
			this.m_cache[i][j][k+1]  = arg.denom();
		}
	}
	
	/**
	 * gets a value that maches the key. null if not found
	 * @param key
	 * @return
	 */
	public void get(int i, int j, int k, RationalGCD arg){
		k=2*k; //index fix
		
		if (this.mShadowed||this.mSaveHistory){
			int loc_i = i<m_DontCare ? mCandidatesindexes[i] : mCandsize;
			int loc_j = j<m_DontCare ? mCandidatesindexes[j] : mCandsize;
			if (loc_i<0 || loc_j<0)
				arg.set (m_cache[i][j][k],
						 m_cache[i][j][k+1]);
			else
				arg.set(m_cache_current_readable[loc_i][loc_j][k],
				        m_cache_current_readable[loc_i][loc_j][k+1]);
		}
		else
			arg.set (m_cache[i][j][k],
					 m_cache[i][j][k+1]);
	}
	
	
	/**
	 * @ finds if a key is in the cache and return it in arg if it is
	 * 			otherwise, arg is unchanged.
	 * @param key
	 * @return
	 */
	public final boolean containsKey(int i, int j, int k, RationalGCD arg){
		this.get(i, j, k, arg);
		return !arg.isNaN();
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
								long val = m_cache[i][k][j];
								m_cache_shadow1[loc_s][loc_v][j]=val;
								m_cache_shadow2[loc_s][loc_v][j]=val;
								m_history_cache[loc_s][loc_v][j]=val;
							}
						}
					}

				mSaveHistory=true;
				}

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
		long [][][] printcache = null;
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
			for (int j = 0 ; j<printcache[i].length; j++){
				for (int k = 0 ; k<printcache[i][j].length; k++){
					System.out.print(printcache[i][j][k]+", ");
				}
				System.out.println();
			}
			System.out.println();
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
	
	public void printgetAll (){
		System.out.println("*****************************************");
		RationalGCD rat = RationalGCD.makeOne();
		for (int i = 0 ; i<this.m_cache.length; i++){
			for (int j = 0 ; j<this.m_cache.length; j++){
				for (int k = 0 ; k<this.m_cache.length; k++){
					this.get(i, j, k,rat);
					System.out.print(rat.toDouble()+", ");
				}
				System.out.println();
			}
			System.out.println("\n");
		}
	}
	
	public static void main(String[] args) {
		int [] cands=new int [] {-1,0,1};
		int candsnum=2;
		int graphsize=cands.length;
		ShadowedHistoriedCacheArrRational shca = new ShadowedHistoriedCacheArrRational(graphsize,true,true,candsnum,cands);
		for (int i = 0 ; i<graphsize+1; i++){
			for (int j = 0 ; j<graphsize+1; j++){
				for (int k = 0 ; k<graphsize+1; k++){
					shca.put (i,j,k,new RationalGCD(7,0));
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
								shca.put (i,j,k,new RationalGCD(a*1,0));
							}
						}
					}
					shca.printgetAll();
				System.out.println("Stop Shadowing");
				shca.StopShadowing();
			}	
				
			
			
    	RationalGCD rat = RationalGCD.makeOne();	
		System.out.println("state restored");
		shca.restoreState();
	/*	System.out.println(shca.get(0,0,0,rat)==666.6);
		System.out.println(shca.get(1, 1, 0,rat)==3.3);
		System.out.println(shca.get(1, 2, 0,rat)==6.3);
		System.out.println(shca.get(shca.DONTCARE(), shca.DONTCARE(), shca.DONTCARE())==1.1);*/
		
	}
	

}
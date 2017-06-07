/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.cache;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 *
 * @author bennyl
 */
public class PostfixLRUCache<V> {
    
    LRUCache<V> cache;
    
    public PostfixLRUCache(int maxEntries) {
        cache = new LRUCache<V>(maxEntries);
    }
    
    public void insert(CachePath k, V v) {
        for (int i = k.length(); i > 0; i--) {
            cache.put(new CacheRecord(k, i), v);
        }
    }
    
    private V retreiveLongestRecord(CacheRecord temp){
        for (int i = temp.path.length(); i > 0; i--) {
            temp.length = i;
            V res = cache.get(temp);
            if (res != null) {
                return res;
            }
            
        }
        
        return null;
    }
    
    public TreeCacheResult retreive(CachePath k) {
        final CacheRecord cacheRecord = new CacheRecord(k, -1);
        V r = retreiveLongestRecord(cacheRecord);
        return new TreeCacheResult(cacheRecord.length, r);
    }
    
    public static class TreeCacheResult<V> {
        
        private int prefixSizeMatch;
        private V value;
        
        public TreeCacheResult(int prefixSizeMatch, V value) {
            this.prefixSizeMatch = prefixSizeMatch;
            this.value = value;
        }
        
        public int getPrefixSizeMatch() {
            return prefixSizeMatch;
        }
        
        public V getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return "TreeCacheResult{" + "prefixSizeMatch=" + prefixSizeMatch + ", value=" + value + '}';
        }
    }
    
    private class LRUCache<V> extends LinkedHashMap<CacheRecord, V> {
        
        int maxEntries;
        
        public LRUCache(int maxEntries) {
            super(maxEntries + 1, 1.0f, true);
            this.maxEntries = maxEntries;
        }
        
        @Override
        protected boolean removeEldestEntry(Entry<CacheRecord, V> eldest) {
            if (super.size() > maxEntries){
                CacheRecord e = new CacheRecord(eldest.getKey().path, -1);
                retreiveLongestRecord(e);
                remove(e);
            }
            
            return false;
        }
    }
    
    private static class CacheRecord {
        
        CachePath path;
        int length;
        
        public CacheRecord(CachePath path, int length) {
            this.path = path;
            this.length = length;
        }

        @Override
        public String toString() {
            return "CacheRecord{" + "path=" + path + ", length=" + length + '}';
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + (this.path != null ? this.path.hashCode(length) : 0);
            hash = 59 * hash + this.length;
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CacheRecord other = (CacheRecord) obj;
            
            if (this.length != other.length) {
                return false;
            }
            
            if (this.path != other.path && (this.path == null || !this.path.equals(other.path, length))) {
                return false;
            }
            
            return true;
        }
    }
    
    public static void main(String[] args){
        PostfixLRUCache<String> cache = new PostfixLRUCache<String>(10000);
        for (int i=0; i<1000000; i++){
            cache.insert(new IntArrayCachePath(i,2,i,3,i), "test " + i);
        }
        
        for (int i=0; i<1000000; i++){
            cache.retreive(new IntArrayCachePath(i,2,i,3,i));
        }
    }
    
}

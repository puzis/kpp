/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class SimpleLRUCache<K, V> extends LinkedHashMap<K, V> {

    int maxEntries;

    public SimpleLRUCache(int maxEntries) {
        super(maxEntries + 1, 1.0f, true);
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return super.size() > maxEntries;
    }
}

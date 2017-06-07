/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils;

/**
 *
 * @author bennyl
 */
public class BinUtils {
    public static long convertToLong(boolean[] bits){
        if (bits.length > 64) {
            throw new UnsupportedOperationException("BinUtils.convertToLong supports up to 64 bits");
        }

        long v = 0;
        for (int i = 0; i < bits.length; i++) {
            v = (v << 1) | (bits[i] ? 1 : 0);
        }

        return v;
    }
}

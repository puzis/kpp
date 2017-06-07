/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.cache;

import algorithms.featuredep.utils.cache.CachePath;
import java.util.Arrays;

/**
 *
 * @author bennyl
 */
public class IntArrayCachePath implements CachePath {

    int[] array;

    public IntArrayCachePath(int... array) {
        this.array = array;
    }

    public int[] getArray() {
        return array;
    }

    @Override
    public int length() {
        return array.length;
    }

    @Override
    public String toString() {
        return "IntArrayPath{" + "array=" + Arrays.toString(array) + '}';
    }

    @Override
    public boolean equals(Object other, int length) {
        int[] oa = ((IntArrayCachePath) other).array;
        if (oa.length < length ||  array.length < length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (oa[i] != array[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode(int length) {
        if (array == null) {
            return 0;
        }

        int result = 1;
        for (int i = 0; i < length; i++) {
            result = 31 * result + array[i];
        }

        return result;
    }
}

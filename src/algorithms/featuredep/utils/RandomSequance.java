/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils;

import java.util.Random;

/**
 * representation of a theoretically endless random sequence using O(1) mem.
 *
 * @author bennyl
 */
public class RandomSequance {

    public static final long BIG_PRIME = 10123457689L;
    public static final int SMALL_PRIME = 101111;
    long seed;
    private Random r;

    public RandomSequance(long seed) {
        this.seed = seed;
        r = new Random();
    }

    public RandomSequance() {
        seed = new Random().nextInt();
    }

    public int getIthPositiveInteger(long i) {
        r.setSeed(seed * BIG_PRIME + i * SMALL_PRIME);
        return Math.abs(r.nextInt());
    }

    public boolean getIthBoolean(long i) {
        return getIthPositiveInteger(i) % 2 == 0;
    }

    /**
     * Returns the i'th pseudorandom, uniformly distributed {@code double} value
     * between {@code 0.0} and {@code 1.0} from this random number generator's
     * sequence.
     *
     * @param i
     * @return
     */
    public double getIthDouble(long i) {
        r.setSeed(seed * BIG_PRIME + i * SMALL_PRIME);
        return r.nextDouble();
    }

}

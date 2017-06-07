/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils;

import java.util.Arrays;

/**
 *
 * @author bennyl
 */
public abstract class BooleanBruteForceRunner implements Runnable {

    int arraySize;

    public BooleanBruteForceRunner(int arraySize) {
        this.arraySize = arraySize;
    }

    @Override
    public void run() {
        boolean[] a = new boolean[arraySize];
        Arrays.fill(a, false);
        handle(a);
        while (true) {
            boolean found = false;
            for (int i = a.length-1; i >= 0; i--) {
                if (a[i]) {
                    a[i] = false;
                } else {
                    a[i] = true;
                    found = true;
                    handle(a);
                    break;
                }
            }

            if (!found) {
                break;
            }
        }
    }

    protected abstract void handle(boolean[] value);

    public static void main(String[] args) {
        BooleanBruteForceRunner bfr = new BooleanBruteForceRunner(5) {
            @Override
            protected void handle(boolean[] value) {
                System.out.println(Arrays.toString(value));
            }
        };
        
        bfr.run();
    }
}

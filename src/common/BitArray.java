/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.util.BitSet;

/**
 * much more compact version of {@link BitSet} but without the option of dynamic
 * resizing
 *
 * @author bennyl
 */
public abstract class BitArray {

    private int length;

    public BitArray(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public abstract boolean get(int pos);

    public abstract void set(int pos, boolean value);

    public static BitArray create(int length) {
        if (length <= 32) {
            return new SmallBitArray(length);
        }

        return new LargeBitArray(length);
    }

    protected void validateAccess(int pos) {
        if (pos >= length) {
            throw new IndexOutOfBoundsException("length is " + length + " requested index is " + pos);
        }
    }

    private static final class SmallBitArray extends BitArray {

        int array = 0;

        public SmallBitArray(int length) {
            super(length);
        }

        @Override
        public boolean get(int pos) {
            validateAccess(pos);
            return (array & (1 << pos)) != 0;
        }

        @Override
        public void set(int pos, boolean value) {
            validateAccess(pos);
            if (value) {
                array |= (1 << pos);
            } else {
                array &= (~(1 << pos));
            }
        }
    }

    private static final class LargeBitArray extends BitArray {

        int[] array;

        public LargeBitArray(int length) {
            super(length);
            this.array = new int[(int) Math.ceil(((double) length) / 32.0)];
        }

        @Override
        public boolean get(int pos) {
            validateAccess(pos);
            int apos = (int) Math.floor(((double) pos) / 32.0);
            pos -= apos * 32;
            return (array[apos] & (1 << pos)) != 0;
        }

        @Override
        public void set(int pos, boolean value) {
            validateAccess(pos);
            int apos = (int) Math.floor(((double) pos) / 32.0);
            pos -= apos * 32;

            if (value) {
                array[apos] |= (1 << pos);
            } else {
                array[apos] &= (~(1 << pos));
            }
        }
    }

}

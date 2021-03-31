package ru.vsu.cs.skofenko;

class SmoothSort {

    static final int[] LP = {1, 1, 3, 5, 9, 15, 25, 41, 67, 109,
            177, 287, 465, 753, 1219, 1973, 3193, 5167, 8361, 13529, 21891,
            35421, 57313, 92735, 150049, 242785, 392835, 635621, 1028457,
            1664079, 2692537, 4356617, 7049155, 11405773, 18454929, 29860703,
            48315633, 78176337, 126491971, 204668309, 331160281, 535828591,
            866988873};

    public static <T extends Comparable<T>> void sort(T[] data) {
        int head = 0;
        int last = data.length-1;

        // These variables need a little explaining. If our string of heaps
        // is of length 38, then the heaps will be of size 25+9+3+1, which are
        // Leonardo numbers 6, 4, 2, 1.
        // Turning this into a binary number, we get b01010110 = 0x56. We represent
        // this number as a pair of numbers by right-shifting all the zeros and
        // storing the mantissa and exponent as "p" and "pshift".
        // This is handy, because the exponent is the index into L[] giving the
        // size of the rightmost heap, and because we can instantly find out if
        // the rightmost two heaps are consecutive Leonardo numbers by checking
        // (p&3)==3

        int mantissa = 1; // the bitmap of the current standard concatenation >> pshift
        int pshift = 1;

        while (head < last) {
            if ((mantissa & 3) == 3) {
                // Add 1 by merging the first two blocks into a larger one.
                // The next Leonardo number is one bigger.
                sift(data, pshift, head);
                mantissa >>>= 2;
                pshift += 2;
            } else {
                // adding a new block of length 1
                if (LP[pshift - 1] >= last - head) {
                    // this block is its final size.
                    trinkle(data, mantissa, pshift, head, false);
                } else {
                    // this block will get merged. Just make it trusty.
                    sift(data, pshift, head);
                }

                if (pshift == 1) {
                    // LP[1] is being used, so we add use LP[0]
                    mantissa <<= 1;
                    pshift--;
                } else {
                    // shift out to position 1, add LP[1]
                    mantissa <<= (pshift - 1);
                    pshift = 1;
                }
            }
            mantissa |= 1;
            head++;
        }

        trinkle(data, mantissa, pshift, head, false);

        while (pshift != 1 || mantissa != 1) {
            if (pshift <= 1) {
                // block of length 1. No fiddling needed
                int trail = Integer.numberOfTrailingZeros(mantissa & ~1);
                mantissa >>>= trail;
                pshift += trail;
            } else {
                mantissa <<= 2;
                mantissa ^= 7;
                pshift -= 2;
                // This block gets broken into three bits. The rightmost
                // bit is a block of length 1. The left hand part is split into
                // two, a block of length LP[pshift+1] and one of LP[pshift].
                // Both these two are appropriately heapified, but the root
                // nodes are not necessarily in order. We therefore semitrinkle
                // both of them
                trinkle(data, mantissa >>> 1, pshift + 1, head - LP[pshift] - 1, true);
                trinkle(data, mantissa, pshift, head - 1, true);
            }
            head--;
        }
    }

    private static <T extends Comparable<T>> void sift(T[] data, int pshift, int head) {
        T val = data[head];
        while (pshift > 1) {
            int rt = head - 1;
            int lf = head - 1 - LP[pshift - 2];

            if (val.compareTo(data[lf]) >= 0 && val.compareTo(data[rt]) >= 0)
                break;
            if (data[lf].compareTo(data[rt]) >= 0) {
                data[head] = data[lf];
                head = lf;
                pshift -= 1;
            } else {
                data[head] = data[rt];
                head = rt;
                pshift -= 2;
            }
        }
        data[head] = val;
    }

    private static <T extends Comparable<T>> void trinkle(T[] data, int mantissa, int pshift, int head, boolean isTrusty) {
        T val = data[head];
        while (mantissa != 1) {
            int stepson = head - LP[pshift];

            if (data[stepson].compareTo(val) <= 0)
                break; // current node is greater than head. Sift.

            // no need to check this if we know the current node is trusty,
            // because we just checked the head (which is val, in the first
            // iteration)
            if (!isTrusty && pshift > 1) {
                int rt = head - 1;
                int lf = head - 1 - LP[pshift - 2];
                if (data[rt].compareTo(data[stepson]) >= 0
                        || data[lf].compareTo(data[stepson]) >= 0)
                    break;
            }

            data[head] = data[stepson];

            head = stepson;
            int trail = Integer.numberOfTrailingZeros(mantissa & ~1);
            mantissa >>>= trail;
            pshift += trail;
            isTrusty = false;
        }
        if (!isTrusty) {
            data[head] = val;
            sift(data, pshift, head);
        }
    }
}
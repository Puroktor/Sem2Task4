package ru.vsu.cs.skofenko;

class SmoothSort {
    // by keeping these constants, we can avoid the tiresome business
// of keeping track of Dijkstra's b and c. Instead of keeping
// b and c, I will keep an index into this array.

    static final int LP[] = { 1, 1, 3, 5, 9, 15, 25, 41, 67, 109,
            177, 287, 465, 753, 1219, 1973, 3193, 5167, 8361, 13529, 21891,
            35421, 57313, 92735, 150049, 242785, 392835, 635621, 1028457,
            1664079, 2692537, 4356617, 7049155, 11405773, 18454929, 29860703,
            48315633, 78176337, 126491971, 204668309, 331160281, 535828591,
            866988873 // the next number is > 31 bits.
    };

    public static <C extends Comparable<? super C>> void sort(C[] m,
                                                              int lo, int hi) {
        int head = lo; // the offset of the first element of the prefix into m

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

        int p = 1; // the bitmap of the current standard concatenation >> pshift
        int pshift = 1;

        while (head < hi) {
            if ((p & 3) == 3) {
                // Add 1 by merging the first two blocks into a larger one.
                // The next Leonardo number is one bigger.
                sift(m, pshift, head);
                p >>>= 2;
                pshift += 2;
            } else {
                // adding a new block of length 1
                if (LP[pshift - 1] >= hi - head) {
                    // this block is its final size.
                    trinkle(m, p, pshift, head, false);
                } else {
                    // this block will get merged. Just make it trusty.
                    sift(m, pshift, head);
                }

                if (pshift == 1) {
                    // LP[1] is being used, so we add use LP[0]
                    p <<= 1;
                    pshift--;
                } else {
                    // shift out to position 1, add LP[1]
                    p <<= (pshift - 1);
                    pshift = 1;
                }
            }
            p |= 1;
            head++;
        }

        trinkle(m, p, pshift, head, false);

        while (pshift != 1 || p != 1) {
            if (pshift <= 1) {
                // block of length 1. No fiddling needed
                int trail = Integer.numberOfTrailingZeros(p & ~1);
                p >>>= trail;
                pshift += trail;
            } else {
                p <<= 2;
                p ^= 7;
                pshift -= 2;

                // This block gets broken into three bits. The rightmost
                // bit is a block of length 1. The left hand part is split into
                // two, a block of length LP[pshift+1] and one of LP[pshift].
                // Both these two are appropriately heapified, but the root
                // nodes are not necessarily in order. We therefore semitrinkle
                // both of them

                trinkle(m, p >>> 1, pshift + 1, head - LP[pshift] - 1, true);
                trinkle(m, p, pshift, head - 1, true);
            }

            head--;
        }
    }

    private static <C extends Comparable<? super C>> void sift(C[] m, int pshift,
                                                               int head) {
        // we do not use Floyd's improvements to the heapsort sift, because we
        // are not doing what heapsort does - always moving nodes from near
        // the bottom of the tree to the root.

        C val = m[head];

        while (pshift > 1) {
            int rt = head - 1;
            int lf = head - 1 - LP[pshift - 2];

            if (val.compareTo(m[lf]) >= 0 && val.compareTo(m[rt]) >= 0)
                break;
            if (m[lf].compareTo(m[rt]) >= 0) {
                m[head] = m[lf];
                head = lf;
                pshift -= 1;
            } else {
                m[head] = m[rt];
                head = rt;
                pshift -= 2;
            }
        }

        m[head] = val;
    }

    private static <C extends Comparable<? super C>> void trinkle(C[] m, int p,
                                                                  int pshift, int head, boolean isTrusty) {

        C val = m[head];

        while (p != 1) {
            int stepson = head - LP[pshift];

            if (m[stepson].compareTo(val) <= 0)
                break; // current node is greater than head. Sift.

            // no need to check this if we know the current node is trusty,
            // because we just checked the head (which is val, in the first
            // iteration)
            if (!isTrusty && pshift > 1) {
                int rt = head - 1;
                int lf = head - 1 - LP[pshift - 2];
                if (m[rt].compareTo(m[stepson]) >= 0
                        || m[lf].compareTo(m[stepson]) >= 0)
                    break;
            }

            m[head] = m[stepson];

            head = stepson;
            int trail = Integer.numberOfTrailingZeros(p & ~1);
            p >>>= trail;
            pshift += trail;
            isTrusty = false;
        }

        if (!isTrusty) {
            m[head] = val;
            sift(m, pshift, head);
        }
    }
}
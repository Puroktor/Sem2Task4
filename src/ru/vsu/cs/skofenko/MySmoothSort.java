package ru.vsu.cs.skofenko;

class MySmoothSort {
    static final int[] L = {1, 1, 3, 5, 9, 15, 25, 41, 67, 109,
            177, 287, 465, 753, 1219, 1973, 3193, 5167, 8361, 13529, 21891,
            35421, 57313, 92735, 150049, 242785, 392835, 635621, 1028457,
            1664079, 2692537, 4356617, 7049155, 11405773, 18454929, 29860703,
            48315633, 78176337, 126491971, 204668309, 331160281, 535828591,
            866988873};

    public static <T extends Comparable<T>> void sort(T[] data) {
        int mantissa = 0;
        int p = 0;
        for (int i = 0; i < data.length; i++) {
            if ((mantissa & 3) == 3 || (mantissa & 5) == 5) {
                //объединяем L[n-1] и L[n] в L[n+1]
                mantissa >>>= 2;
                p += mantissa & 1;
                p += 2;
                mantissa |= 1;
                shiftDown(data, i, p);
            } else {
                if (p == 0 && (mantissa & 1) == 1) {
                    //L[0] уже занято
                    mantissa |= 2;
                } else {
                    //L[0] свободно
                    mantissa <<= p;
                    mantissa |= 1;
                    p = 0;
                }
            }
        }

        for (int i = data.length - 1; i > 0; i--) {
            int toSwapWith = i;
            int pj = 0;
            int temp = mantissa;
            int j = 0;
            int index = i;
            //ищем максимальную вершину у куч
            while (temp != 0) {
                if ((temp & 1) == 1) {
                    index -= L[p + j];
                    if (data[index].compareTo(data[toSwapWith]) > 0) {
                        toSwapWith = index;
                        pj = p + j;
                    }
                }
                temp >>>= 1;
                j++;
            }

            if (toSwapWith != i) {
                swap(data, toSwapWith, i);
                shiftDown(data, toSwapWith, pj);
            }
            mantissa &= ~1; //меняем последнюю цифру в мантиссе на 0;
            if (p == 0) {
                int z = Integer.numberOfTrailingZeros(mantissa);
                mantissa >>= z;
                p += z;
            } else {
                for (int k = 0; k < 2; k++) {
                    mantissa <<= 1;
                    mantissa |= 1;
                    p--;
                }
            }
        }
    }

    private static <T extends Comparable<T>> void shiftDown(T[] data, int head, int p) {
        while (p > 1) {
            int right = head - 1;
            int left = head - 1 - L[p - 2]; //ибо количество элементов = числу Леонардо

            if (data[head].compareTo(data[right]) >= 0 && data[head].compareTo(data[left]) >= 0)
                break;

            if (data[left].compareTo(data[right]) >= 0) {
                swap(data, head, left);
                head = left;
                p -= 1;
            } else {
                swap(data, head, right);
                head = right;
                p -= 2;
            }
        }
    }

    private static <T extends Comparable<T>> void swap(T[] data, int i1, int i2) {
        T temp = data[i1];
        data[i1] = data[i2];
        data[i2] = temp;
    }
}
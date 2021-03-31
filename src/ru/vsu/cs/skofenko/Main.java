package ru.vsu.cs.skofenko;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Integer[] arr = {9, 5, 0, 4, 3, 6, 2, 1, 7, 8};
        Integer[] arr2 = arr.clone();

        long startTime = System.currentTimeMillis();
        SmoothSort.sort(arr);
        long endTime = System.currentTimeMillis();
        System.out.println("SmoothSort:"+ (endTime - startTime));

        startTime = System.currentTimeMillis();
        Arrays.sort(arr2);
        endTime = System.currentTimeMillis();
        System.out.println("Timsort:"+ (endTime - startTime));
    }

    private static Integer[] newArr(int size) {
        Integer[] arr = new Integer[size];
        final Random RND = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = RND.nextInt();
        }
        return arr;
    }
}

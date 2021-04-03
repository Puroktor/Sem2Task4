package ru.vsu.cs.skofenko;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Integer[] arr = newArr((int)(1e6)); //в моей реализации не больше 7*1e6
        Integer[] arr2 = arr.clone();
        Integer[] arr3 = arr.clone();

        long startTime = System.currentTimeMillis();
        SmoothSort.sort(arr, 0, arr.length - 1);
        long endTime = System.currentTimeMillis();
        System.out.println("Wikibooks SmoothSort : " + (endTime - startTime));

        startTime = System.currentTimeMillis();
        MySmoothSort.sort(arr2);
        endTime = System.currentTimeMillis();
        System.out.println("MySmoothSort: " + (endTime - startTime));

        startTime = System.currentTimeMillis();
        Arrays.sort(arr3);
        endTime = System.currentTimeMillis();
        System.out.println("Timsort: " + (endTime - startTime));

        boolean is = true;
        for (int i = 0; i < arr.length; i++) {
            if (!arr2[i].equals(arr3[i])) {
                is = false;
                break;
            }
        }
        System.out.println(is);
    }

    private static Integer[] newArr(int size) {
        Integer[] arr = new Integer[size];
        final Random RND = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = RND.nextInt(30);
        }
        return arr;
    }

    private static Integer[] newSortedArr(int size) {
        Integer[] arr = new Integer[size];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        return arr;
    }
}
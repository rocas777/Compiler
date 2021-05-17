package io;

import java.util.Arrays;

public class io {
    public static void printarray(int[] array){
        System.out.println(Arrays.toString(array));
    }

    public static void printsearch(int nr){
        if(nr==-1){
            System.out.println("Number not present in array");
        }
        else{
            System.out.println("Number found at index "+nr);
        }
    }

    public static void printprime(int result, int nr) {
        System.out.println("Number "+nr+" has "+ result + " divisors");
    }
}

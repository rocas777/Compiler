import java.util.Arrays;
import java.util.Scanner; 


public class io {
    public io() {
    }

    public static void printarray(int[] var0) {
        System.out.println(Arrays.toString(var0));
    }

    public static void printsearch(int var0) {
        if (var0 == -1) {
            System.out.println("Number not present in array");
        } else {
            System.out.println("Number found at index " + var0);
        }

    }

    public static void println(int var){
        System.out.println(var);
    }
    
    public static void println(){
        System.out.println();
    }
    
    public static void print(int var){
        System.out.print(var);
    }
    
    public static int read(){
	Scanner scan = new Scanner(System.in);
	String s = scan.next();
	return scan.nextInt();
    }

    public static void printprime(int var0, int var1) {
        System.out.println("Number " + var1 + " has " + var0 + " divisors");
    }
}


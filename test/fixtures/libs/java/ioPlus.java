import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ioPlus {
    public static void printHelloWorld()
    {
        System.out.println("Hello, World!");
    }

    public static void printResult(int num)
    {
        System.out.println(num);
    }

    public static int requestNumber()
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            String input = reader.readLine();
            return Integer.parseInt(input);
        }
        catch (IOException ignored)
        {
            return 0;
        }
    }
}

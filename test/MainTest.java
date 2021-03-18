import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;

import pt.up.fe.specs.util.SpecsIo;

public class MainTest {

    @Test
    public void parse1() {
        Main main = new Main();
        main.parse("import io;class Fac {public int ComputeFac(int num){int num_aux ;if (num < 1)num_aux = 1;else num_aux =num * (this.ComputeFac(num-1));return num_aux;}public static void main(String[]args){io.println(new Fac().ComputeFac(10));}}");
    }

    @Test
    public void parseTestPass1()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/FindMaximum.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test
    public void parseTestPass2()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/HelloWorld.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test
    public void parseTestPass3()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/Lazysort.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test
    public void parseTestPass4()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/Life.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test
    public void parseTestPass5()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/MonteCarloPi.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test
    public void parseTestPass6()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/QuickSort.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test
    public void parseTestPass7()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/Simple.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test
    public void parseTestPass8()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/TicTacToe.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test
    public void parseTestPass9()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/WhileAndIF.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

}
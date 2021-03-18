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

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail1()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/arr_index_not_int.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail2()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/arr_size_not_int.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail3()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/badArguments.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail4()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/binop_incomp.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail5()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/funcNotFound.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail6()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/simple_length.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail7()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/var_exp_incomp.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail8()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/var_lit_incomp.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail9()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/var_undef.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail10()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/fail/semantic/varNotInit.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail1()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/fail/syntactical/BlowUp.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail2()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/fail/syntactical/CompleteWhileTest.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSyntacticalFail3()
    // {
    //     String fileContent = SpecsIo.read("test/fixtures/public/fail/syntactical/LengthError.jmm");
    //     Main main = new Main();
    //     main.parse(fileContent);
    // }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail4()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/fail/syntactical/MissingRightPar.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail5()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/fail/syntactical/MultipleSequential.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail6()
    {
        String fileContent = SpecsIo.read("test/fixtures/public/fail/syntactical/NestedLoop.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }
}
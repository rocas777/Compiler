import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

public class MainTest {

    @Test
    public void parseTestPass1() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/FindMaximum.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void parseTestPass2() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/HelloWorld.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void parseTestPass3() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/Lazysort.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void parseTestPass4() {

        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/Life.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void parseTestPass5() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm"));
        TestUtils.noErrors(result.getReports());

    }

    @Test
    public void parseTestPass6() {

        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/QuickSort.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void parseTestPass7() {

        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/Simple.jmm"));
        TestUtils.noErrors(result.getReports());

    }

    @Test
    public void parseTestPass8() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/TicTacToe.jmm"));
        TestUtils.noErrors(result.getReports());

    }

    @Test
    public void parseTestPass9() {

        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/WhileAndIF.jmm"));
        TestUtils.noErrors(result.getReports());
    }


    @Test(expected = RuntimeException.class)
    public void parseTestSemanticFail1() {
        String fileContent =
                SpecsIo.read("test/fixtures/fail/semantic/arr_index_not_int.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    /*@Test(expected = RuntimeException.class)
    public void parseTestSemanticFail2() {
        String fileContent =
                SpecsIo.read("test/fixtures/fail/semantic/arr_size_not_int.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }*/

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail3()
    // {
    // String fileContent =
    // SpecsIo.read("test/fixtures/fail/semantic/badArguments.jmm");
    // Main main = new Main();
    // main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail4()
    // {
    // String fileContent =
    // SpecsIo.read("test/fixtures/fail/semantic/binop_incomp.jmm");
    // Main main = new Main();
    // main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail5()
    // {
    // String fileContent =
    // SpecsIo.read("test/fixtures/fail/semantic/funcNotFound.jmm");
    // Main main = new Main();
    // main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail6()
    // {
    // String fileContent =
    // SpecsIo.read("test/fixtures/fail/semantic/simple_length.jmm");
    // Main main = new Main();
    // main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail7()
    // {
    // String fileContent =
    // SpecsIo.read("test/fixtures/fail/semantic/var_exp_incomp.jmm");
    // Main main = new Main();
    // main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail8()
    // {
    // String fileContent =
    // SpecsIo.read("test/fixtures/fail/semantic/var_lit_incomp.jmm");
    // Main main = new Main();
    // main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail9()
    // {
    // String fileContent =
    // SpecsIo.read("test/fixtures/fail/semantic/var_undef.jmm");
    // Main main = new Main();
    // main.parse(fileContent);
    // }

    // @Test(expected = RuntimeException.class)
    // public void parseTestSemanticFail10()
    // {
    // String fileContent =
    // SpecsIo.read("test/fixtures/fail/semantic/varNotInit.jmm");
    // Main main = new Main();
    // main.parse(fileContent);
    // }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail1() {
        String fileContent = SpecsIo.read("test/fixtures/public/fail/syntactical/BlowUp.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail2() {
        String fileContent = SpecsIo.read("test/fixtures/public/fail/syntactical/CompleteWhileTest.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail3() {
        String fileContent =
                SpecsIo.read("test/fixtures/public/fail/syntactical/LengthError.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail4() {
        String fileContent =
                SpecsIo.read("test/fixtures/public/fail/syntactical/MissingRightPar.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail5() {
        String fileContent =
                SpecsIo.read("test/fixtures/public/fail/syntactical/MultipleSequential.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }

    @Test(expected = RuntimeException.class)
    public void parseTestSyntacticalFail6() {
        String fileContent =
                SpecsIo.read("test/fixtures/public/fail/syntactical/NestedLoop.jmm");
        Main main = new Main();
        main.parse(fileContent);
    }
}
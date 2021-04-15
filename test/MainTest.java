import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.report.Report;
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


    @Test
    public void parseTestSemanticFail1() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/semantic/arr_index_not_int.jmm"));
        var result2 = TestUtils.analyse(result);
        TestUtils.mustFail(result2.getReports());
    }

    @Test
    public void parseTestSemanticFail2() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/semantic/bool_op_incomp.jmm"));
        var result2 = TestUtils.analyse(result);
        TestUtils.mustFail(result2.getReports());
    }

    @Test
    public void parseTestSemanticFail3() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/semantic/badArguments.jmm"));
        var result2 = TestUtils.analyse(result);
        TestUtils.mustFail(result2.getReports());
    }

    @Test
    public void parseTestSemanticFail4() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/semantic/funcNotFound.jmm"));
        var result2 = TestUtils.analyse(result);
        TestUtils.mustFail(result2.getReports());
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

    /*@Test
    public void parseTestSyntacticalFail1() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/syntactical/BlowUp.jmm"));
        TestUtils.mustFail(result.getReports());
    }*/

    @Test
    public void parseTestSyntacticalFail2() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/syntactical/CompleteWhileTest.jmm"));
        TestUtils.mustFail(result.getReports());
    }

    @Test
    public void parseTestSyntacticalFail3() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/syntactical/LengthError.jmm"));
        TestUtils.mustFail(result.getReports());
    }

    @Test
    public void parseTestSyntacticalFail4() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/syntactical/MissingRightPar.jmm"));
        TestUtils.mustFail(result.getReports());
    }

    @Test
    public void parseTestSyntacticalFail5() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/syntactical/MultipleSequential.jmm"));
        TestUtils.mustFail(result.getReports());
    }

    @Test
    public void parseTestSyntacticalFail6() {
        var result = TestUtils.parse(SpecsIo.getResource("fixtures/public/fail/syntactical/NestedLoop.jmm"));
        TestUtils.mustFail(result.getReports());
    }
}
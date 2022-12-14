/**
 * Copyright 2021 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.specs.util.SpecsIo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class BackendTest {

    @Test
    public void testHelloWorld() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/HelloWorld.jmm"));
        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testFindMaximum() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/FindMaximum.jmm"));
        TestUtils.noErrors(result.getReports());

        var output = result.run();
    }

    @Test
    public void testLazySort() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/Lazysort.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        var output = result.run();
    }

    @Test
    public void testQuickSort() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/QuickSort.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        var output = result.run();
    }

    @Test
    public void testLife() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/Life.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        var output = result.run("\n");
    }

    @Test
    public void testMonteCarlo() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        result.run("3000\n");
    }
    @Test
    public void testTicTacToe() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/TicTacToe.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        var output = result.run(SpecsIo.getResource("fixtures/public/TicTacToe.input"));
    }
    @Test
    public void testWhileAndIf() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/WhileAndIF.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        var output = result.run();
    }




    @Test
    public void testSimple() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/Simple.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        var output = result.run();
        assertEquals("30", output.trim());
    }
    
    @Test
    public void testDivisors() {
        try {
            Path bytes = Files.copy(
                    new File("executables/io.class").toPath(),
                    new File("test/fixtures/libs/compiled/io.class").toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES,
                    LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        var result = TestUtils.backend(SpecsIo.getResource("examples/Divisors.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        System.out.println("it need our io class");
        result.run();
    }

    @Test
    public void testBinarySearch() {
        try {
            Path bytes = Files.copy(
                    new File("executables/io.class").toPath(),
                    new File("test/fixtures/libs/compiled/io.class").toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES,
                    LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var result = TestUtils.backend(SpecsIo.getResource("examples/BinarySearch.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        result.run();
    }

    @Test
    public void testMergeSort() {
        try {
            Path bytes = Files.copy(
                    new File("executables/io.class").toPath(),
                    new File("test/fixtures/libs/compiled/io.class").toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES,
                    LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var result = TestUtils.backend(SpecsIo.getResource("examples/MergeSort.jmm"));
        TestUtils.noErrors(result.getReports());
        result.compile(new File("./executables")).setExecutable(true);
        result.run();
    }
}

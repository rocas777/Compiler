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

import jasmin.Jasmin;
import org.junit.Test;
import org.specs.comp.ollir.parser.OllirParser;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class BackendTest {

    //@Test
    public void testHelloWorld() {

        var result = TestUtils.backend("");
        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test


    public void testHelloWorld1() {
        var p_result = TestUtils.parse(SpecsIo.getResource("test2.jmm"));

        var a_result = TestUtils.analyse(p_result);
        /*String oc = "" +

                "HelloWorld {\n" +
                "\n" +
                "   .construct HelloWorld().V {\n" +
                "      invokespecial(this, \"<init>\").V;\n" +
                "   }\n" +
                "\n" +
                "   .method public static main(args.array.String).V {\n" +
                "      invokestatic(ioPlus, \"printHelloWorld\").V;\n" +
                "\n" +
                "   }\n" +
                "}";
                "Fac {.construct Fac().V {invokespecial(this, \"<init>\").V;}" +
                "   .method public compFac(num.i32).i32 {" +
                "       aux1.i32 :=.i32 num.i32 -.i32 1.i32;" +
                "       ret.i32 aux1.i32;" +
                "   }" +
                "   .method public static main(args.array.String).V {" +
                "       aux1.Fac :=.Fac new(Fac).Fac;" +
                "       invokespecial(aux1.Fac,\"<init>\").V;" +
                "       aux2.i32 :=.i32 invokevirtual(aux1.Fac,\"compFac\",10.i32).i32;" +
                "       invokestatic(io, \"println\", aux2.i32).V;" +
                "       ret.V;" +
                "}" +
                "}";

        OllirResult o = new OllirResult(a_result, oc, new ArrayList<>());*/
        var result = TestUtils.optimize(SpecsIo.getResource("test2.jmm"));
        JasminResult j = new Jasmin().toJasmin(result);
        File f = j.compile();
        j.run();
    }
}

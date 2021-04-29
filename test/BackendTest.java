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
        var p_result = TestUtils.parse(SpecsIo.getResource("tt.jmm"));

        var a_result = TestUtils.analyse(p_result);
        String oc = "" +
                "Ex1 {             	" +
                "   .construct public myClass().V {" +
                "      invokespecial(this, \"<init>\").V; " +
                "   } " +
                "   .method public sum(A.array.i32).i32 { " +
                "       sum.i32 :=.i32 0.i32; " +
                "       i.i32 :=.i32 0.i32; " +
                "   Loop: " +
                "       t1.i32 :=.i32 arraylength($1.A.array.i32).i32; " +
                "       if (i.i32 >=.i32 t1.i32) " +
                "           goto End; " +
                "       t2.i32 :=.i32 $1.A[i.i32].i32; " +
                "       sum.i32 :=.i32 sum.i32 +.i32 t2.i32; " +
                "       i.i32 :=.i32 i.i32 +.i32 1.i32;" +
                "       goto Loop; " +
                "   End: " +
                "       ret.i32 sum.i32; " +
                "   } " +
                "}";

        OllirResult o = new OllirResult(a_result, oc, new ArrayList<>());
        JasminResult j = new Jasmin().toJasmin(o);
        File f = j.compile();
    }
}

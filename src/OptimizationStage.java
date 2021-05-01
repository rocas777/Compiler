import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.SpecsIo;

/**
 * Copyright 2021 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class OptimizationStage implements JmmOptimization {

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {

        JmmNode node = semanticsResult.getRootNode();
        MySymbolTable table = (MySymbolTable) semanticsResult.getSymbolTable();
        String ollirCode = "";
        String className = table.getClassName();
        String superName = table.getSuper();

        ollirCode += className + ((superName == null) ? "" : (" extends " + superName)) + " {\n";

        var fields = table.getFields();
        for (Symbol symbol : fields) {
            ollirCode += ".field private " + symbol.getName() + "." + OllirHelper.processType(symbol.getType()) + ";\n";
        }

        ollirCode += ".construct " + className + "().V {\n";
        ollirCode += "invokespecial(this, \"<init>\").V;\n";
        ollirCode += "}\n";

        OllirMethodVisitor ollirMethodVisitor = new OllirMethodVisitor();
        ollirMethodVisitor.visit(node, table);
        var methodMap = ollirMethodVisitor.getMap();

        
        for (Map.Entry<String, String> methodEntry : methodMap.entrySet())
        {
            ollirCode += methodEntry.getValue() + "\n";
        }

        ollirCode += "}\n";
        System.out.println(ollirCode);

        // Convert the AST to a String containing the equivalent OLLIR code
         // Convert node ...

        // More reports from this stage
        List<Report> reports = new ArrayList<>();

        return new OllirResult(semanticsResult, ollirCode, reports);
    }

    @Override
    public JmmSemanticsResult optimize(JmmSemanticsResult semanticsResult) {
        // THIS IS JUST FOR CHECKPOINT 3
        return semanticsResult;
    }

    @Override
    public OllirResult optimize(OllirResult ollirResult) {
        // THIS IS JUST FOR CHECKPOINT 3
        return ollirResult;
    }

}

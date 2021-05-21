import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            ollirCode += ".field private " + OllirHelper.sanitizeVariableName(symbol.getName()) + "." + OllirHelper.processType(symbol.getType()) + ";\n";
        }

        ollirCode += ".construct " + className + "().V {\n";
        ollirCode += "invokespecial(this, \"<init>\").V;\n";
        ollirCode += "}\n";

        if (Main.shouldOptimizeWithOptionO)
        {
            var methods = table.getMethods();
            for (String method : methods) {
                var locals = table.getLocalVariables(method);
                for (var local : locals) {
                    var varUseVisitor = new VarUseVisitor(local.getName(), method);
                    varUseVisitor.visit(node, table);
                    if (varUseVisitor.isConst())
                    {
                        replaceVarUseWithConst(local.getName(), method, varUseVisitor.getFirstVal(), node, table);
                    }
                }
            }
        }

        OllirMethodVisitor ollirMethodVisitor = new OllirMethodVisitor();
        ollirMethodVisitor.visit(node, table);
        var methodMap = ollirMethodVisitor.getMap();


        for (Map.Entry<String, String> methodEntry : methodMap.entrySet()) {
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






    private void replaceVarUseWithConst(String varName, String methodName, JmmNode value, JmmNode root, MySymbolTable table)
    {
        //Should check if is integerLiteral or true or false?
        String valueNodeKind = value.getKind();
        if (valueNodeKind.equals("IntegerLiteral") || valueNodeKind.equals("True") || valueNodeKind.equals("False"))
        {
            var propagVisitor = new ConstPropagVisitor(varName, methodName, value);
            propagVisitor.visit(root, table);
        }
    }
}

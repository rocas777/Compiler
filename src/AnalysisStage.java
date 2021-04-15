import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.Arrays;
import java.util.List;

public class AnalysisStage implements JmmAnalysis {

    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) {

        if (TestUtils.getNumReports(parserResult.getReports(), ReportType.ERROR) > 0) {
            var errorReport = new Report(ReportType.ERROR, Stage.SEMANTIC, -1,
                    "Started semantic analysis but there are errors from previous stage");
            return new JmmSemanticsResult(parserResult, null, Arrays.asList(errorReport));
        }

        if (parserResult.getRootNode() == null) {
            var errorReport = new Report(ReportType.ERROR, Stage.SEMANTIC, -1,
                    "Started semantic analysis but AST root node is null");
            return new JmmSemanticsResult(parserResult, null, Arrays.asList(errorReport));
        }

        JmmNode node = parserResult.getRootNode();

        // System.out.println("Dump tree with Visitor where you control tree traversal");
        // ExampleVisitor visitor = new ExampleVisitor("Identifier", "id");
        // System.out.println(visitor.visit(node, ""));

        // System.out.println("Dump tree with Visitor that automatically performs preorder tree traversal");
        // var preOrderVisitor = new ExamplePreorderVisitor("Identifier", "id");
        // System.out.println(preOrderVisitor.visit(node, ""));

        // System.out.println(
        //         "Create histogram of node kinds with Visitor that automatically performs postorder tree traversal");
        // var postOrderVisitor = new ExamplePostorderVisitor();
        // var kindCount = new HashMap<String, Integer>();
        // postOrderVisitor.visit(node, kindCount);
        // System.out.println("Kinds count: " + kindCount + "\n");

        // System.out.println(
        //         "Print variables name and line, and their corresponding parent with Visitor that automatically performs preorder tree traversal");
        // var varPrinter = new ExamplePrintVariables("Variable", "name", "line");
        // varPrinter.visit(node, null);

        //Fill symbol table
        var symbolTable = new MySymbolTable();

        var importVisitor = new ImportVisitor();
        importVisitor.visit(node, symbolTable);

        var classVisitor = new ClassVisitor();
        classVisitor.visit(node, symbolTable);

        var fieldVisitor = new FieldVisitor();
        fieldVisitor.visit(node, symbolTable);

        var methodVisitor = new MethodVisitor();
        methodVisitor.visit(node, symbolTable);
        
        var variableExistsVisitor = new VariableExistsVisitor();
        variableExistsVisitor.visit(node, symbolTable);

        var arrayAccessIndexVisitor = new ArrayAccessVisitor();
        arrayAccessIndexVisitor.visit(node, symbolTable);

        var boolOperationVisitor = new BoolOperationVisitor();
        boolOperationVisitor.visit(node, symbolTable);

        var importMethodVisitor = new ImportMethodVisitor();
        importMethodVisitor.visit(node, symbolTable);

        var funcCallVisitor = new FuncCallVisitor();
        funcCallVisitor.visit(node, symbolTable);

        for (Report report : Main.semanticReports) {
            System.out.println(report.toString());
        }
        //Semantic analysis
        //var 


        //Testing prints
        var imports = symbolTable.getImports();
        System.out.println("Imports: ");
        for (String string : imports) {
            System.out.println("\t" + string);
        }
        System.out.println("Class Name: " + symbolTable.getClassName());
        System.out.println("Super Class Name: " + symbolTable.getSuper());
        var fields = symbolTable.getFields();
        System.out.println("Fields: ");
        for (Symbol symbol : fields) {
            var type = symbol.getType();
            String printLine = type.getName() + ((type.isArray()) ? "[]" : "");
            printLine += " " + symbol.getName();
            System.out.println("\t" + printLine);
        }
        var methods = symbolTable.getMethods();
        System.out.println("METHODS: ");
        for (String string : methods) {
            System.out.println("Method Name: " + string);
            var type = symbolTable.getReturnType(string);
            System.out.println("Return Type: " + type.getName() + ((type.isArray()) ? "[]" : ""));
            var params = symbolTable.getParameters(string);
            for (Symbol param : params) {
                var paramType = param.getType();
                System.out.println("Parameter: " + paramType.getName() + ((paramType.isArray()) ? "[]" : "") + " " + param.getName());
            }
            var locals = symbolTable.getLocalVariables(string);
            for (Symbol localVar : locals) {
                var localType = localVar.getType();
                System.out.println("Local Variable: " + localType.getName() + ((localType.isArray()) ? "[]" : "") + " " + localVar.getName());
            }
        }

        return new JmmSemanticsResult(parserResult, symbolTable, Main.semanticReports);

    }

}
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class FuncCallVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    public FuncCallVisitor() {
        addVisit("Method", this::processCall);
    }

    public Boolean processCall(JmmNode node, MySymbolTable table) {
        var children = node.getChildren();
        var firstChild = children.get(0);
        var secondChild = children.get(1);
        var thirdChild = children.get(2);
        String nodeMethodName = SearchHelper.getMethodName(node);

        boolean methodTargetBelongsToThisClass = false;
        if (firstChild.getKind().equals("Object") || firstChild.getKind().equals("VariableName")) {
            Symbol objectSymbol = table.getVariable(firstChild.get("name"), nodeMethodName);
            if (objectSymbol != null) {
                Type objectType = objectSymbol.getType();
                if (objectType.getName().equals(table.getClassName())) methodTargetBelongsToThisClass = true;
            }
        }

        if (firstChild.getKind().equals("This") || methodTargetBelongsToThisClass) {
            String methodName = secondChild.get("name");
            var parameters = table.getParameters(methodName);
            var args = thirdChild.getChildren();

            var superClass = table.getSuper();

            boolean shouldCheck = true;

            if (parameters == null) {
                if (superClass == null)
                    Main.semanticReports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(secondChild.get("line")), Integer.parseInt(secondChild.get("column")), "Method called hasn't been declared"));
            } else {
                if (parameters.size() != args.size())
                    Main.semanticReports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(secondChild.get("line")), Integer.parseInt(secondChild.get("column")), "Number of arguments in method call and number of parameters in declaration don't match"));
                else {
                    for (int i = 0; i < args.size(); i++) {
                        var currentParam = parameters.get(i);
                        var currentArg = args.get(i);

                        Type paramType = currentParam.getType();
                        Type argType;

                        switch (currentArg.getKind()) {
                            case "Add": {
                                argType = new Type("int", false);
                                break;
                            }
                            case "Sub": {
                                argType = new Type("int", false);
                                break;
                            }
                            case "Mul": {
                                argType = new Type("int", false);
                                break;
                            }
                            case "Div": {
                                argType = new Type("int", false);
                                break;
                            }
                            case "IntegerLiteral": {
                                argType = new Type("int", false);
                                break;
                            }

                            case "ArrayAccess": {
                                argType = new Type("int", false);
                                break;
                            }
                            case "LessThan": {
                                argType = new Type("boolean", false);
                                break;
                            }
                            case "AND": {
                                argType = new Type("boolean", false);
                                break;
                            }
                            case "Neg": {
                                argType = new Type("boolean", false);
                                break;
                            }
                            case "VariableName": {
                                Symbol varData = table.getVariable(currentArg.get("name"), nodeMethodName);
                                if (varData == null) argType = null;
                                else argType = varData.getType();
                                break;
                            }
                            case "Method": {
                                var methodFirstChild = currentArg.getChildren().get(0);
                                var methodSecondChild = currentArg.getChildren().get(1);
                                boolean subMethodTargetBelongsToThisClass = false;
                                if (methodFirstChild.getKind().equals("Object") || methodFirstChild.getKind().equals("VariableName")) {
                                    Symbol objectSymbol = table.getVariable(methodFirstChild.get("name"), nodeMethodName);
                                    if (objectSymbol != null) {
                                        Type objectType = objectSymbol.getType();
                                        if (objectType.getName().equals(table.getClassName()))
                                            subMethodTargetBelongsToThisClass = true;
                                    }
                                }

                                if (methodFirstChild.getKind().equals("This") || subMethodTargetBelongsToThisClass) {
                                    argType = table.getReturnType(methodSecondChild.get("name"));
                                    if (argType == null && superClass != null) argType = paramType;
                                    else if (argType == null && superClass == null) shouldCheck = false;
                                } else argType = paramType;
                                break;
                            }
                            default: {
                                argType = null;
                                break;
                            }
                        }
                        if (shouldCheck) {
                            if (!paramType.equals(argType)) {
                                var descendantWithLineAndCol = findFirstDescendantWithLineAndCol(currentArg);
                                Main.semanticReports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(descendantWithLineAndCol.get("line")), Integer.parseInt(descendantWithLineAndCol.get("column")), "Parameter and argument types don't match"));
                            }
                        }

                    }
                }
            }
        }

        return true;
    }


    private JmmNode findFirstDescendantWithLineAndCol(JmmNode node) {
        JmmNode currentNode = node;

        while (true) {
            System.out.println(currentNode.getKind());
            try {
                if (currentNode.get("line") != null) break;
            } catch (NullPointerException e1) {
                try {
                    currentNode = currentNode.getChildren().get(0);
                } catch (IndexOutOfBoundsException e2) {
                    currentNode = currentNode.getParent().getChildren().get(1);
                }
            }
        }

        return currentNode;
    }
}

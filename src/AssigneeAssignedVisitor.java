import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;

public class AssigneeAssignedVisitor extends PreorderJmmVisitor<MySymbolTable, List<Report>> {
    public AssigneeAssignedVisitor() {
        addVisit("Assign", this::processOperation);
    }

    List<Report> processOperation(JmmNode node, MySymbolTable table) {
        List<Report> reports = new ArrayList<>();

        var children = node.getChildren();

        if (children.size() == 2) {
            String varName;
            String methodName;
            Type a1Type;
            if (children.get(0).getKind().equals("Assigned")) {
                varName = children.get(0).get("name");
                methodName = SearchHelper.getMethodName(node);
                a1Type = table.getVariable(varName, methodName).getType();

            } else if (children.get(0).getKind().equals("ArrayAccess")) {
                varName = children.get(0).getChildren().get(0).get("name");
                methodName = SearchHelper.getMethodName(node);
                a1Type = table.getVariable(varName, methodName).getType();
            } else {
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 1, 1, "Assign error"));
                return reports;
            }
            switch (children.get(1).getKind()) {
                case "Method": {
                    var call = children.get(1).getChildren().get(1);
                    var rType = table.getReturnType(call.get("name"));
                    if (a1Type.isArray() && !rType.isArray()) {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(call.get("line")), Integer.parseInt(call.get("column")), "Trying to assign a non array to an array "));
                    } else if (!a1Type.isArray() && rType.isArray()) {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(call.get("line")), Integer.parseInt(call.get("column")), "Trying to assign an  array to a non array "));
                    } else if (!a1Type.getName().equals(rType.getName())) {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(call.get("line")), Integer.parseInt(call.get("column")), "Trying to assign a " + rType.getName() + " to a " + a1Type.getName()));
                    }
                    break;
                }
                case "IntegerLiteral": {
                    if (!a1Type.getName().equals("int")) {
                        Report r = SearchHelper.CheckIfInteger(varName, table, "Trying to assign an integer to a " + a1Type.getName(), 0, 0);
                        reports.add(r);
                    }
                    break;
                }
                case "True":
                case "False": {
                    if (!a1Type.getName().equals("boolean")) {
                        Report r = SearchHelper.CheckIfBoolean(varName, table, "Trying to assign a boolean to a " + a1Type.getName(), 0, 0);
                        reports.add(r);
                    }
                    break;
                }
                case "VariableName": {
                    var symbol = table.getVariable(children.get(1).get("name"), methodName);

                    var call = children.get(1);
                    var rType = symbol.getType();
                    if (a1Type.isArray() && !rType.isArray()) {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(call.get("line")), Integer.parseInt(call.get("column")), "Trying to assign a non array to an array "));
                    } else if (!a1Type.isArray() && rType.isArray()) {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(call.get("line")), Integer.parseInt(call.get("column")), "Trying to assign an  array to a non array "));
                    } else if (!a1Type.getName().equals(rType.getName())) {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(call.get("line")), Integer.parseInt(call.get("column")), "Trying to assign a " + rType.getName() + " to a " + a1Type.getName()));
                    }
                    break;
                }
                case "ArrayAccess": {
                    children = children.get(1).getChildren();
                    var call = children.get(0);
                    var symbol = table.getVariable(call.get("name"), methodName);
                    var rType = symbol.getType();
                    System.out.println();
                    if (!a1Type.getName().equals(rType.getName())) {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(call.get("line")), Integer.parseInt(call.get("column")), "Trying to assign a " + rType.getName() + " to a " + a1Type.getName()));
                    }
                    break;
                }
                case "ArrayInitializer": {
                    var call = children.get(1).getChildren().get(0);
                    if (!a1Type.getName().equals("int") || !a1Type.isArray()) {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(call.get("line")), Integer.parseInt(call.get("column")), "Trying to assign a array to a " + a1Type.getName()));
                    }
                    break;
                }
                case "Mul":
                case "Div":
                case "Sub":
                case "Add": {
                    if ((a1Type.getName().equals("int") && !a1Type.isArray()) || children.get(0).getKind().equals("ArrayAccess")) {

                    } else {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(children.get(0).get("line")), Integer.parseInt(children.get(1).get("column")), "Trying to assign the result of a arithmetic operation to a different non int variable"));
                    }
                    break;
                }
                case "AND":
                case "LessThan":
                case "Neg": {
                    if (a1Type.getName().equals("boolean")) {

                    } else {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(children.get(0).get("line")), Integer.parseInt(children.get(1).get("column")), "Trying to assign the result of a logic operation to a different non boolean variable"));
                    }
                    break;
                }
                case "AttributeCall": {
                    if ((a1Type.getName().equals("int") && !a1Type.isArray()) || children.get(0).getKind().equals("ArrayAccess")) {

                    } else {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(children.get(0).get("line")), Integer.parseInt(children.get(1).get("column")), "Trying to assign the result of a length attribute to a different non int variable"));
                    }
                    break;
                }
                case "ConstructorCall": {
                    String constructorClassName = children.get(1).getChildren().get(0).get("name");
                    if (a1Type.getName().equals(constructorClassName)) {

                    } else {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(children.get(1).get("line")), Integer.parseInt(children.get(1).get("line")), "Trying to assign the instation of a class to a variable that belongs to a different class"));
                    }
                    break;
                }
                default: {
                    try {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(children.get(1).get("line")), Integer.parseInt(children.get(1).get("column")), "Trying to assign a value to a different type recipient"));
                    } catch (Exception ignored) {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, 0, "Trying to assign a value to a different type recipient " + children.get(1)));
                    }
                }
            }
        } else {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, 0, "Assign error"));
        }

        Main.semanticReports.addAll(reports);

        return reports;
    }
}

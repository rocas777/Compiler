import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;

public class BoolOperationVisitor extends PreorderJmmVisitor<MySymbolTable, List<Report>> {
    public BoolOperationVisitor() {
        addVisit("AND", this::processOperation);
        addVisit("LessThan", this::intVerifier);
        addVisit("Neg", this::processOperation);

        addVisit("If", this::processConditionals);
        addVisit("While", this::processConditionals);
    }

    List<Report> processOperation(JmmNode node, MySymbolTable table) {
        List<Report> reports = new ArrayList<>();

        var children = node.getChildren();

        for (JmmNode jmmNode : children) {
            String kind = jmmNode.getKind();

            switch (kind) {
                case "False":
                case "True":
                case "AND":
                case "LessThan":
                case "Neg": {
                    continue;
                }
                case "Method": {
                    var child = jmmNode.getChildren().get(1);

                    String methodName = child.get("name");
                    Report report = SearchHelper.CheckIfBoolean(methodName, table, "Method " + methodName + " does not return boolean type for boolean operation", Integer.parseInt(child.get("line")), Integer.parseInt(child.get("column")));
                    if (report != null) reports.add(report);
                    break;
                }
                case "VariableName": {
                    String varName = jmmNode.get("name");
                    String methodName = SearchHelper.getMethodName(node);
                    Report report = SearchHelper.CheckIfBoolean(varName, methodName, table, "Variable " + varName + " is not a boolean", Integer.parseInt(jmmNode.get("line")), Integer.parseInt(jmmNode.get("column")));
                    if (report != null) reports.add(report);

                    break;
                }
                default: {
                    //TODO fix line
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1, "Symbol not allowed in a " + node.getKind()));
                    break;
                }

            }
        }

        Main.semanticReports.addAll(reports);

        return reports;
    }


    List<Report> processConditionals(JmmNode node, MySymbolTable table) {


        List<Report> reports = new ArrayList<>();

        var children = node.getChildren();

        for (JmmNode jmmNode : children) {

            String kind = jmmNode.getKind();
            switch (kind) {
                case "False":
                case "True":
                case "AND":
                case "LessThan":
                case "Neg":
                case "Body": {
                    continue;
                }
                case "Method": {
                    var child = jmmNode.getChildren().get(1);

                    String methodName = child.get("name");
                    Report report = SearchHelper.CheckIfBoolean(methodName, table, "Method " + methodName + " does not return boolean type for " + node.getKind() + " expression", Integer.parseInt(child.get("line")), Integer.parseInt(child.get("column")));
                    if (report != null) reports.add(report);
                    break;
                }
                case "VariableName": {
                    String varName = jmmNode.get("name");
                    String methodName = SearchHelper.getMethodName(node);
                    Report report = SearchHelper.CheckIfBoolean(varName, methodName, table, "Variable " + varName + " is not a boolean", Integer.parseInt(jmmNode.get("line")), Integer.parseInt(jmmNode.get("column")));
                    if (report != null) reports.add(report);

                    break;
                }
                default: {
                    //todo fix line
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, "Conditional expression " + node.getKind() + " must result in a boolean"));
                    break;
                }

            }
        }

        Main.semanticReports.addAll(reports);

        return reports;
    }

    List<Report> intVerifier(JmmNode node, MySymbolTable table) {

        List<Report> reports = new ArrayList<>();

        var children = node.getChildren();

        for (JmmNode jmmNode : children) {
            String kind = jmmNode.getKind();

            switch (kind) {
                case "IntegerLiteral":
                case "Add":
                case "Sub":
                case "Mul":
                case "Div":
                case "ArrayAccess": {
                    continue;
                }
                case "Method": {
                    var child = jmmNode.getChildren().get(1);

                    String methodName = child.get("name");
                    Report report = SearchHelper.CheckIfInteger(methodName, table, "Method does not return int type", Integer.parseInt(child.get("line")), Integer.parseInt(child.get("column")));
                    if (report != null) reports.add(report);

                }
                case "VariableName": {
                    String methodName = SearchHelper.getMethodName(node);
                    Report report = SearchHelper.CheckIfInteger(jmmNode.get("name"), methodName, table, "Variable is not an int", Integer.parseInt(jmmNode.get("line")), Integer.parseInt(jmmNode.get("column")));
                    if (report != null) reports.add(report);

                    break;
                }
                default: {
                    //todo fix line
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, "Symbol " + jmmNode.getKind() + " not allowed in a " + node.getParent().getKind()));
                    break;
                }

            }
        }

        Main.semanticReports.addAll(reports);

        return reports;
    }


}

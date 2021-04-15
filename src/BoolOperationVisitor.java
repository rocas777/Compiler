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
        addVisit("LessThan", this::processOperation);
        addVisit("Neg", this::processOperation);
    }

    List<Report> processOperation(JmmNode node, MySymbolTable table) {
        List<Report> reports = new ArrayList<>();

        var children = node.getChildren();

        for (JmmNode jmmNode : children) {
            String kind = jmmNode.getKind();

            switch (kind) {
                case "AND":
                case "LessThan":
                case "Neg": {
                    continue;
                }
                case "Method": {
                    var child = jmmNode.getChildren().get(1);

                    String methodName = child.get("name");
                    Report report = SearchHelper.CheckIfBoolean(methodName, table, "Method " + methodName + " does not return boolean type");
                    if (report != null) reports.add(report);
                    break;
                }
                case "VariableName": {
                    String varName = jmmNode.get("name");
                    String methodName = SearchHelper.getMethodName(node);
                    Report report = SearchHelper.CheckIfBoolean(varName, methodName, table, "Variable " + varName + " is not a boolean");
                    if (report != null) reports.add(report);

                    break;
                }
                case "True":
                case "False": {
                    break;
                }
                default: {
                    //todo fix line
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, "Symbol not allowed in a " + node.getParent().getKind()));
                    break;
                }

            }
        }

        Main.semanticReports.addAll(reports);

        return reports;
    }
}

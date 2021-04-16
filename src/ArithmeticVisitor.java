import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticVisitor extends PreorderJmmVisitor<MySymbolTable, List<Report>> {
    public ArithmeticVisitor() {
        addVisit("Add", this::processOperation);
        addVisit("Sub", this::processOperation);
        addVisit("Mul", this::processOperation);
        addVisit("Div", this::processOperation);
    }

    List<Report> processOperation(JmmNode node, MySymbolTable table) {
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
                    Report report = SearchHelper.CheckIfInteger(methodName, table, "Method " + methodName + " does not return int type",Integer.parseInt(jmmNode.get("line")));
                    if (report != null) reports.add(report);

                }
                case "VariableName": {
                    String methodName = SearchHelper.getMethodName(node);

                    Report report = SearchHelper.CheckIfInteger(jmmNode.get("name"), methodName, table, "Variable " + jmmNode.get("name") + jmmNode + " is not an int",Integer.parseInt(jmmNode.get("line")),Integer.parseInt(jmmNode.get("column")));
                    if (report != null) reports.add(report);
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

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;

public class LengthVisitor extends PreorderJmmVisitor<MySymbolTable, List<Report>> {
    public LengthVisitor() {
        addVisit("AttributeCall", this::processOperation);
    }

    List<Report> processOperation(JmmNode node, MySymbolTable table) {
        List<Report> reports = new ArrayList<>();

        var children = node.getChildren();

        if (children.size() == 1) {
            switch (children.get(0).getKind()) {
                case "IntegerLiteral":
                case "ArrayAccess": {
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, 0, ".length not Suported for type Int"));
                    break;
                }
                case "True":
                case "False": {
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, 0, ".length not Suported for type Boolean"));
                }
                case "VariableName": {
                    String varName = children.get(0).get("name");
                    String methodName = SearchHelper.getMethodName(node);
                    String varType = table.getVariable(varName, methodName).getType().getName();
                    Report report = SearchHelper.CheckIfArray(varName, methodName, table, ".length not supported for '" + varName + "' type(" + (varType) + ")");
                    if (report != null) reports.add(report);
                    break;
                }
                case "Method": {
                    String methodName = children.get(0).getChildren().get(1).get("name");
                    int line = Integer.parseInt(children.get(0).getChildren().get(1).get("line"));
                    int col = Integer.parseInt(children.get(0).getChildren().get(1).get("column"));
                    Report report = SearchHelper.CheckIfBoolean(methodName, table, ".length not supported for " + methodName + "() return type", line, col);
                    if (report != null) reports.add(report);
                    break;
                }
            }
        }

        Main.semanticReports.addAll(reports);

        return reports;
    }
}

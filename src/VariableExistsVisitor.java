import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;

public class VariableExistsVisitor extends PreorderJmmVisitor<MySymbolTable, List<Report>> {
    public VariableExistsVisitor() {
        addVisit("VarDeclaration", this::processOperation);
    }

    List<Report> processOperation(JmmNode node, MySymbolTable table) {
        List<Report> reports = new ArrayList<>();

        var children = node.getChildren();
        if (children.size() != 2) {
            //todo add error
        }

        String name = node.getChildren().get(1).get("name");
        String methodName = SearchHelper.getMethodName(node);
        if (table.getVariable(name, methodName) != null) {
            return reports;
        }
        //todo fix line
        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, "Symbol " + name + " not found"));
        for (Report report : reports) {
            Main.reports.add(report);
        }
        return reports;
    }
}

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;

public class VariableExistsVisitor extends PreorderJmmVisitor<MySymbolTable, List<Report>> {
    public VariableExistsVisitor() {
        addVisit("VarName", this::processOperation);
        addVisit("VariableName", this::processOperation);
        addVisit("Assigned", this::processOperation);
    }

    List<Report> processOperation(JmmNode node, MySymbolTable table) {
        List<Report> reports = new ArrayList<>();

        String name = node.get("name");
        String methodName = SearchHelper.getMethodName(node);
        if (table.getVariable(name, methodName) != null) {
            return reports;
        }
        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("column")), "Symbol " + name + " not found"));

        Main.reports.addAll(reports);
        return reports;
    }
}

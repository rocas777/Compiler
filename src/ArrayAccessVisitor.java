import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;

public class ArrayAccessVisitor extends PreorderJmmVisitor<MySymbolTable, List<Report>> {
    public ArrayAccessVisitor() {
        addVisit("ArrayAccess", this::processOperation);
    }

    List<Report> processOperation(JmmNode node, MySymbolTable table) {
        List<Report> reports = new ArrayList<>();

        var children = node.getChildren();
        if (children.size() != 2) {
            //todo add error
        }

        JmmNode arrayIndexNode = children.get(1).getChildren().get(0);
        switch (arrayIndexNode.getKind()) {
            case "IntegerLiteral": {
                break;
            }
            case "Add": {
                break;
            }
            case "Sub": {
                break;
            }
            case "Mul": {
                break;
            }
            case "Div": {
                break;
            }
            case "ArrayAccess": {
                break;
            }
            case "Method": {
                var child = arrayIndexNode.getChildren().get(1);
                String methodName = child.get("name");
                Report report = SearchHelper.searchMethod(methodName, table, "Array Access Index is not an Integer ");
                if (report != null) reports.add(report);

            }
            case "VariableName": {
                String methodName = SearchHelper.getMethodName(node);
                System.out.println("NOME " + methodName);
                Report report = SearchHelper.searchIdentifier(arrayIndexNode.get("name"), methodName, table, "Array Access Index is not an Integer ");
                if (report != null) reports.add(report);

                break;
            }
            default: {
                //todo fix line
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, "Array Access Index is not an Integer "));
                break;
            }

        }
        for(Report report: reports){
            Main.reports.add(report);
        }
        return reports;
    }
}

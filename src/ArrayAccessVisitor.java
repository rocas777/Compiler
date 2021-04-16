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
        List<Report> reports = checkIfArrayIsBeingAccessed(node, table);

        var children = node.getChildren();
        if (children.size() != 2) {
            //todo add error
        }

        JmmNode arrayIndexNode = children.get(1).getChildren().get(0);
        switch (arrayIndexNode.getKind()) {
            case "IntegerLiteral":
            case "Add":
            case "Sub":
            case "Mul":
            case "Div":
            case "ArrayAccess": {
                break;
            }
            case "Method": {
                var child = arrayIndexNode.getChildren().get(1);
                String methodName = child.get("name");
                Report report = SearchHelper.CheckIfInteger(methodName, table, "Array Access Index is not an Integer ",Integer.parseInt(arrayIndexNode.get("line")),Integer.parseInt(child.get("line")));
                if (report != null) reports.add(report);

            }
            case "VariableName": {
                String methodName = SearchHelper.getMethodName(node);
                System.out.println("NOME " + methodName);
                Report report = SearchHelper.CheckIfInteger(arrayIndexNode.get("name"), methodName, table, "Array Access Index is not an Integer ",Integer.parseInt(arrayIndexNode.get("line")),Integer.parseInt(arrayIndexNode.get("column")));
                if (report != null) reports.add(report);

                break;
            }
            default: {
                //todo fix line
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, "Array Access Index is not an Integer "));
                break;
            }

        }
        for (Report report : reports) {
            Main.semanticReports.add(report);
        }
        return reports;
    }

    List<Report> checkIfArrayIsBeingAccessed(JmmNode node, MySymbolTable table)
    {
        List<Report> reports = new ArrayList<>();

        String nodeMethodName = SearchHelper.getMethodName(node);
        var firstChild = node.getChildren().get(0);

        switch (firstChild.getKind())
        {
            case "VariableName":
            {
                String arrayName = node.getChildren().get(0).get("name");
                Report newReport = SearchHelper.CheckIfArray(arrayName, nodeMethodName, table, "Array Access is being used on a variable which is not an array ");
                if (newReport != null) reports.add(newReport);
                break;
            }
            case "Method":
            {
                String methodName = firstChild.getChildren().get(1).get("name");
                Report newReport = SearchHelper.CheckIfArray(methodName, table, "Array Access is being used with a function which doesn't return an array ");
                if (newReport != null) reports.add(newReport);
                break;
            }
            default:
            {
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, 0, "Array Access is being used on an element which is not an array "));
                break;
            }
        }

        return reports;
    }
}

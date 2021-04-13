import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

//todo fix lines
public class SearchHelper {
    static Report checkIfInteger(String methodName, MySymbolTable table, String error) {
        Type returnType = table.getReturnType(methodName);
        if (returnType == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, error);
        if (!returnType.getName().equals("int") || returnType.isArray())
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, error);
        return null;
    }

    static Report CheckIfInteger(String name, String methodName, MySymbolTable table, String error) {
        Type type = table.getVariable(name, methodName).getType();
        if (!type.getName().equals("int") || type.isArray())
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, error);
        return null;
    }

    static String getMethodName(JmmNode node) {
        while (true) {
            if (node.getParent().getKind().contains("NormalMethodDeclaration")) {
                return node.getParent().getChildren().get(1).get("name");
            } else if (node.getParent().getKind().contains("MainMethodDeclaration"))
                return "main";
            node = node.getParent();
        }
    }
}

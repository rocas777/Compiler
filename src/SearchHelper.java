import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

//todo fix lines
public class SearchHelper {
    static Report CheckIfInteger(String methodName, MySymbolTable table, String error, int line) {
        Type returnType = table.getReturnType(methodName);
        if (returnType == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line, error);
        if (!returnType.getName().equals("int") || returnType.isArray())
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line, error);
        return null;
    }

    static Report CheckIfInteger(String name, String methodName, MySymbolTable table, String error, int line, int col) {
        var variable = table.getVariable(name, methodName);
        if (variable == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line, col, error);
        Type type = variable.getType();
        if (type == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line, col, error);
        if (!type.getName().equals("int") || type.isArray())
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line, col, error);
        return null;
    }

    static Report CheckIfBoolean(String methodName, MySymbolTable table, String error, int line) {
        Type returnType = table.getReturnType(methodName);
        if (returnType == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line,  error);
        if (!returnType.getName().equals("boolean"))
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line,  error);
        return null;
    }

    static Report CheckIfBoolean(String name, String methodName, MySymbolTable table, String error, int line, int col) {
        var variable = table.getVariable(name, methodName);
        if (variable == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line, col, error);
        Type type = variable.getType();
        if (type == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line, col, error);
        if (!type.getName().equals("boolean"))
            return new Report(ReportType.ERROR, Stage.SEMANTIC, line, col, error);
        return null;
    }

    static Report CheckIfArray(String methodName, MySymbolTable table, String error) {
        Type returnType = table.getReturnType(methodName);
        if (returnType == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, 0, error);
        if (!returnType.isArray())
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, 0, error);
        return null;
    }

    static Report CheckIfArray(String name, String methodName, MySymbolTable table, String error) {
        var variable = table.getVariable(name, methodName);
        if (variable == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, 0, error);
        Type type = variable.getType();
        if (type == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, 0, error);
        if (!type.isArray())
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, 0, error);
        return null;
    }

    static String getMethodName(JmmNode node) {
        while (true) {
            if (node.getParent().getKind().contains("NormalMethodDeclaration")) {
                return node.getParent().getChildren().get(1).get("name");
            } else if (node.getParent().getKind().contains("MainMethodDeclaration"))
                return "main";
            node = node.getParent();
            if (node == null || node.getParent() == null) break;
        }
        return null;
    }
}

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

//todo fix lines
public class SearchHelper {
    static Report searchMethod(String methodName, MySymbolTable table, String error) {
        Type returnType = table.getReturnType(methodName);
        if (returnType == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, error);
        if (!returnType.getName().equals("int") || returnType.isArray())
            return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, error);
        return null;
    }

    static Report searchIdentifier(String name, String methodName, MySymbolTable table, String error) {
        var locals = table.getLocalVariables(methodName);
        var parameters = table.getParameters(methodName);
        var fields = table.getFields();

        for (Symbol local : locals) {
            if (local.getName().equals(name)) {
                Type type = local.getType();
                if (!type.getName().equals("int") || type.isArray())
                    return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, error);
            }
        }

        for (Symbol parameter : parameters) {
            if (parameter.getName().equals(name)) {
                Type type = parameter.getType();
                if (!type.getName().equals("int") || type.isArray())
                    return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, error);
            }
        }

        for (Symbol field : fields) {
            if (field.getName().equals(name)) {
                Type type = field.getType();
                if (!type.getName().equals("int") || type.isArray())
                    return new Report(ReportType.ERROR, Stage.SEMANTIC, 0, error);
            }
        }
        return null;
    }

    static String getMethodName(JmmNode node) {
        while (!(node.getParent().getKind().contains("MethodDeclaration"))) node = node.getParent();
        return node.getKind();
    }
}

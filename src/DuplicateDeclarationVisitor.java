import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class DuplicateDeclarationVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    public DuplicateDeclarationVisitor()
    {
        addVisit("VarDeclaration", this::processDeclaration);
    }

    public Boolean processDeclaration(JmmNode node, MySymbolTable table) {
        var children = node.getChildren();
        var typeChild = children.get(0);
        var nameChild = children.get(1);
        int nameChildLine = Integer.parseInt(nameChild.get("line"));
        int nameChildColumn = Integer.parseInt(nameChild.get("column"));

        String varName = nameChild.get("name");
        String methodName = SearchHelper.getMethodName(node);
        var varData = table.getVariable(varName, methodName);
        if (varData != null)
        {
            Type type = varData.getType();
            if (type != null)
            {
               if (!type.getName().equals(typeChild.get("type"))) Main.reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, nameChildLine, nameChildColumn, "Redeclaration of variable with different type")); 
            }
        }

        return true;
    }
    
}

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class ArithmeticVisitor extends PreorderJmmVisitor<MySymbolTable,List<Report>> {
    public ArithmeticVisitor()
    {
        // addVisit("Add", this::processOperation);
        // addVisit("Sub", this::processOperation);
        // addVisit("Mul", this::processOperation);
        // addVisit("Div", this::processOperation);
    }
    
    // List<Report> processOperation(JmmNode node, MySymbolTable table)
    // {
    //     List<Report> reports = new ArrayList<>();

    //     var children = node.getChildren();

    //     for (JmmNode jmmNode : children) {
    //         String kind = jmmNode.getKind();

    //         switch (kind)
    //         {
    //             case "IntegerLiteral":
    //             {
    //                 continue;
    //             }
    //             case "Add":
    //             {
    //                 continue;
    //             }
    //             case "Sub":
    //             {
    //                 continue;
    //             }
    //             case "Mul":
    //             {
    //                 continue;
    //             }
    //             case "Div":
    //             {
    //                 continue;
    //             }
    //             case "ArrayAccess":
    //             {
    //                 continue;
    //             }
    //             case "Method":
    //             {
    //                 var child = jmmNode.getChildren().get(1);
                  
    //                 String methodName = child.get("name");
    //                 Report report = searchMethod(methodName, table);
    //                 if (report != null) reports.add(report);
                    
    //             }
    //             case "VariableName":
    //             {
                   
    //                 String methodName = getMethodName(node);
    //                 Report report = searchIdentifier(jmmNode.get("name"), methodName, table);
    //                 if (report != null) reports.add(report);
                   
    //                 break;
    //             }
    //             default:
    //             {
    //                 reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, ,));
    //                 break;
    //             }

    //         }
    //     }

    //     return reports;
    // }

    // Report searchMethod(String methodName, MySymbolTable table)
    // {
    //     Type returnType = table.getReturnType(methodName);
    //     if (returnType == null) return new Report(ReportType.ERROR, Stage.SEMANTIC, ,);
    //     if (!returnType.getName().equals("int") || returnType.isArray()) return new Report(ReportType.ERROR, Stage.SEMANTIC,,);
    // }

    // Report searchIdentifier(String name, String methodName, MySymbolTable table)
    // {
    //     var locals = table.getLocalVariables(methodName);
    //     var parameters = table.getParameters(methodName);
    //     var fields = table.getFields();

    //     for (Symbol local : locals) {
    //         if (local.getName().equals(name))
    //         {
    //             Type type = local.getType();
    //             if (!type.getName().equals("int") || type.isArray()) return new Report(ReportType.ERROR, Stage.SEMANTIC, , );
    //         }
    //     }

    //     for (Symbol parameter : parameters) {
    //         if (parameter.getName().equals(name))
    //         {
    //             Type type = parameter.getType();
    //             if (!type.getName().equals("int") || type.isArray()) return new Report(ReportType.ERROR, Stage.SEMANTIC, , );
    //         }
    //     }

    //     for (Symbol field : fields) {
    //         if (field.getName().equals(name))
    //         {
    //             Type type = field.getType();
    //             if (!type.getName().equals("int") || type.isArray()) return new Report(ReportType.ERROR, Stage.SEMANTIC, , );
    //         }
    //     }
    //     return null;
    // }

    // String getMethodName(JmmNode node)
    // {
    //     while (!(node.getParent().getKind().contains("MethodDeclaration"))) node = node.getParent();
    //     return node.getKind();
    // }
}

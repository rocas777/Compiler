import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class MethodVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    public MethodVisitor()
    {
        addVisit("MainMethodDeclaration", this::addMain);
        addVisit("NormalMethodDeclaration", this::addNormalMethod);
    }
 
    
    private Boolean addNormalMethod(JmmNode node, MySymbolTable table)
    {
        var children = node.getChildren();
        String funcName = children.get(1).get("name");
        String returnTypeString = children.get(0).get("type");
        boolean isArray = returnTypeString.contains("[]");
        String returnTypeName = "";
        if (isArray) returnTypeName = returnTypeString.substring(0, returnTypeString.length() - 2);
        else returnTypeName = returnTypeString;
        Type returnType = new Type(returnTypeName, isArray);
        boolean hasParameters = children.get(2).getKind().equals("Args");
        var bodyChild = hasParameters ? children.get(3) : children.get(2);
        List<Symbol> parameters = hasParameters ? getParameters(children.get(2).getChildren()) : new ArrayList<>();

        table.addFunction(funcName, new FunctionTable(returnType, parameters, getLocals(bodyChild.getChildren())));
        return true;
    }
    
    private Boolean addMain(JmmNode node, MySymbolTable table)
    {
        var children = node.getChildren();
        var firstChild = children.get(0);
        List<Symbol> parameters = new ArrayList<>();
        Type returnType = new Type("void", false);
        Type paramType = new Type("String", true);
        parameters.add(new MySymbol(paramType, firstChild.get("name"), Integer.parseInt(firstChild.get("line")), Integer.parseInt(firstChild.get("column"))));
        List<Symbol> locals = getLocals(children.get(1).getChildren());
        table.addFunction("main", new FunctionTable(returnType, parameters, locals));
        return true;
    }

    List<Symbol> getParameters(List<JmmNode> nodes)
    {
        List<Symbol> parameters = new ArrayList<>();
        int i = 0;
        JmmNode currNode = null;
        while ((currNode = nodes.get(i++)).getKind().equals("Parameter"))
        {
            parameters.add(FieldVisitor.processVarDeclaration(currNode));
            if (i >= nodes.size()) break;
        }
        return parameters;
    }

    List<Symbol> getLocals(List<JmmNode> nodes)
    {
        List<Symbol> locals = new ArrayList<>();

        int i = 0;
        JmmNode currNode = null;
        while ((currNode = nodes.get(i++)).getKind().equals("VarDeclaration"))
        {
            locals.add(FieldVisitor.processVarDeclaration(currNode));
            if (i >= nodes.size()) break;
        }
        return locals;
    }
}

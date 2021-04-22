import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class OllirMethodVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    Map<String, String> methodNameAndOllirCode;
    
    public OllirMethodVisitor()
    {
        methodNameAndOllirCode = new HashMap<>();
        addVisit("NormalMethodDeclaration", this::processNormalMethod);
        addVisit("MainMethodDeclaration", this::processMainMethod);
    }
    
    private Boolean processNormalMethod(JmmNode node, MySymbolTable table)
    {
        String methodName = node.getChildren().get(1).get("name");
        processMethod(methodName, node, table);
        return true;
    }

    private Boolean processMainMethod(JmmNode node, MySymbolTable table)
    {
        processMethod("main", node, table);
        return true;
    }

    private void processMethod(String methodName, JmmNode node, MySymbolTable table)
    {
        boolean isMain = methodName.equals("main");

        String ollirString = "";

        ollirString += ".method public";


        if (isMain) ollirString += " static";
        ollirString += methodName;

        var parameters = table.getParameters(methodName);
        var returnType = table.getReturnType(methodName);

        ollirString += processParameters(parameters) + ".";
        ollirString += OllirNodeProcessor.processType(returnType);

        JmmNode bodyNode;
        var children = node.getChildren();
        if (isMain) bodyNode = children.get(children.size() - 1);
        else bodyNode = children.get(children.size() - 2);

        ollirString += " {";
        ollirString += processMethodBody(methodName, bodyNode, table, isMain);
        ollirString += "}";

        methodNameAndOllirCode.put(methodName, ollirString);
    }

    private String processParameters(List<Symbol> parameters)
    {
        String parameterString = "(";

        for (int i = 0; i < parameters.size(); i++) {
            Symbol symbol = parameters.get(i);
            parameterString += processSymbol(symbol);
            if (i < (parameters.size() - 1)) parameterString += ", ";
        }

        parameterString += ")";

        return parameterString;
    }

    private String processSymbol(Symbol symbol)
    {
        String symbolString = "";

        String name = OllirNodeProcessor.sanitizeVariableName(symbol.getName());
        Type type = symbol.getType();
        
        symbolString += name + ".";
        symbolString += OllirNodeProcessor.processType(type);
    
        return symbolString;
    }

    private String processMethodBody(String methodName, JmmNode bodyNode, MySymbolTable table, boolean isStatic)
    {
        String methodString = "";

        var locals = table.getLocalVariables(methodName);
        var parameters = table.getParameters(methodName);
        var bodyChildren = bodyNode.getChildren();
        Integer tempVarCount = 0;

        boolean hasLocalVariables = (locals != null && locals.size() > 0);
        int startingIndex; 
        if (hasLocalVariables) startingIndex = locals.size();
        else startingIndex = 0;

        Map<String, Integer> structureCount = new HashMap<>();

        for (int i = startingIndex; i < bodyChildren.size(); i++)
        {
            methodString += OllirNodeProcessor.processNode(bodyChildren.get(i), tempVarCount, locals, parameters, structureCount, table, isStatic);
        }

        return methodString;
    }

    public Map<String, String> getMap() {
        return methodNameAndOllirCode;
    }
}

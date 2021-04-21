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
        ollirString += processType(returnType);

        JmmNode bodyNode;
        var children = node.getChildren();
        if (isMain) bodyNode = children.get(children.size() - 1);
        else bodyNode = children.get(children.size() - 2);

        ollirString += " {";
        ollirString += processMethodBody(methodName, bodyNode, table);
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

        String name = symbol.getName();
        Type type = symbol.getType();
        
        symbolString += name + ".";
        symbolString += processType(type);
    
        return symbolString;
    }

    private String processType(Type type)
    {
        String typeString = "";
        String typeName = type.getName();

        if (type.isArray()) typeString += "array.";

        switch (typeName)
        {
            case "int":
            {
                typeString += "i32";
                break;
            }
            case "boolean":
            {
                typeString += "bool";
                break;
            }
            case "String":
            {
                typeString += "String";
                break;
            }
            default:
            {
                typeString += typeName;
                break;
            }
        }
        return typeString;
    }

    private String sanitizeVariableName(String varName)
    {
        //TODO 
        //FINISH THIS
        //MAKE SURE VAR NAME ISNT t1,t2,etc
        return varName;
    }

    private String processMethodBody(String methodName, JmmNode bodyNode, MySymbolTable table)
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
            methodString += processMethodBodyNode(bodyChildren.get(i), tempVarCount, locals, parameters, structureCount);
        }

        return methodString;
    }

    private String processMethodBodyNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount)
    {
        String ollirString = "";
        var children = node.getChildren();

        switch (node.getKind())
        {
            case "If":
            {
                int structureNumber = determineNumberForStructure("If", structureCount);
                ollirString += "if (";
                ollirString += processMethodBodyNode(children.get(0), tempVarCount, locals, parameters, structureCount); // if the expression inside the if needs to be broken down then the code returned needs to appear before the if in the ollir code
                ollirString += ") goto ifbody" + structureNumber + ";";
                ollirString += "goto elsebody" + structureNumber + ";";
                break;
            }
            default:
            {
                System.out.println("Invalid node kind!");
                break;
            }
        }

        return ollirString;
    }

    private int determineNumberForStructure(String structure, Map<String, Integer> structureCount)
    {
        Integer currentCount = structureCount.get(structure);
        if (currentCount == null)
        {
            structureCount.put(structure, Integer.valueOf(1));
            return 1;
        }
        else
        {
            Integer updatedCount = currentCount++;
            structureCount.put(structure, updatedCount);
            return updatedCount.intValue();
        }
    }

    public Map<String, String> getMap() {
        return methodNameAndOllirCode;
    }
}

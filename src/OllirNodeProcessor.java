import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.JmmNode;

class OllirNodeProcessor {
    
    public static String processNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        String nodeKind = node.getKind();
        switch (nodeKind)
        {
            case "If":
            {
                ollirString += processIfNode(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "LessThan":
            {
                ollirString += processLessThanNode(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "IntegerLiteral":
            {
                ollirString += processIntegerLiteral(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Assigned":
            case "VariableName":
            {
                ollirString += processVariableName(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Body":
            {
                var children = node.getChildren();
                for (JmmNode jmmNode : children) {
                    ollirString += processNode(jmmNode, tempVarCount, locals, parameters, structureCount, table, isStatic);
                }
                break;
            }
            case "Else":
            {
                ollirString += processElseNode(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Assign":
            {
                ollirString += processAssignNode(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Add":
            case "Sub":
            case "Mul":
            case "Div":
            {
                ollirString += processOperNode(node, tempVarCount, locals, parameters, structureCount, table, isStatic, nodeKind);
                break;
            }
            case "ArrayAccess":
            {
                ollirString += processArrayAccessNode(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Method":
            {
                ollirString += processMethodCallNode(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "This":
            {
                ollirString += processThisNode(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Args":
            {
                ollirString += processArgsNode(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
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

    private static String processThisNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        Type classType = new Type(table.getClassName(), false);
        String typeString = OllirHelper.processType(classType);

        ollirString += "t" + (tempVarCount) + "." + typeString + " :=." + typeString + " $0.this." + typeString + ";"; 

        return ollirString;
    }

    private static String processArgsNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
        List<String> tempVars = new ArrayList<>();

        for (int i = 0; i < (childrenData.size() / 2); i++) {
            String currentString = childrenData.get(i);
            ollirString += currentString;
            String currentLastTempVar = OllirHelper.extractLastTempVar(currentString);
            tempVars.add(currentLastTempVar);
        }        

        ollirString += String.join(",", tempVars);

        return ollirString;
    }

    private static String processMethodCallNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var children = node.getChildren();

        var firstChild = children.get(0);
        var secondChild = children.get(1);
        var thirdChild = children.get(2);

        String firstChildString = processNode(firstChild, tempVarCount, locals, parameters, structureCount, table, isStatic);
        String thirdChildString = processNode(thirdChild, tempVarCount, locals, parameters, structureCount, table, isStatic);
        String methodName = secondChild.get("name");

        String firstInvokeParameter = "";

        if (!firstChild.getKind().equals("VariableName")) ollirString += firstChildString;

        firstInvokeParameter = OllirHelper.extractLastTempVar(firstChildString);

        String[] thirdChildLines = thirdChildString.split(";");
        String lastThirdChildLine = thirdChildLines[thirdChildLines.length - 1];
        for (int i = 0; i < (thirdChildLines.length - 1); i++ ) {
            ollirString += thirdChildLines[i];
        }
        
        Type methodReturnType = OllirHelper.determineMethodReturnType(methodName, table, node);
        String typeString = OllirHelper.processType(methodReturnType);

        boolean methodBelongsToClass = table.getMethods().contains(methodName);
        boolean methodBeingCalledIsStatic = OllirHelper.determineIfMethodIsStatic(methodName, table, node);

        String invokeType = "";
        if (methodBelongsToClass) invokeType = "invokespecial";
        else if (methodBeingCalledIsStatic) invokeType = "invokevirtual";
        else invokeType = "invokevirtual";

        boolean methodIsVoid = typeString.equals("V");

        if (methodIsVoid) ollirString += invokeType + "(" + firstInvokeParameter + (lastThirdChildLine.equals("") ? "," : "") + lastThirdChildLine + ").V;";
        else ollirString += "t" + (tempVarCount++) + "." + typeString + " :=." + typeString + " " + invokeType + "(" + firstInvokeParameter + (lastThirdChildLine.equals("") ? "," : "") + lastThirdChildLine + ")." + typeString + ";";

        return ollirString;
    }

    private static String processArrayAccessNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, tempVarCount, locals, parameters, structureCount, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2); 
        String rightTempVar = childrenData.get(3);

        ollirString = leftChild + rightChild;

        ollirString += "t" + (tempVarCount++) + ".i32 :=.i32 " + OllirHelper.trimType(leftTempVar) + "[" + rightTempVar + "].i32;";

        return ollirString;
    }

    private static String processOperNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic, String operation)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, tempVarCount, locals, parameters, structureCount, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2); 
        String rightTempVar = childrenData.get(3);

        ollirString = leftChild + rightChild;
        String operationChar = "";
        switch (operation)
        {
            case "Add":
            {
                operationChar = "+";
                break;
            }
            case "Sub":
            {
                operationChar = "-";
                break;
            }
            case "Mul":
            {
                operationChar = "*";
                break;
            }
            case "Div":
            {
                operationChar = "/";
                break;
            }
            default:
            {
                System.out.println("Invalid operation in ollir conversion!");
                break;
            }
        }

        ollirString += "t" + (tempVarCount++) + ".i32 :=.i32 " + leftTempVar + " " + operationChar + ".i32 " + rightTempVar + ";";

        return ollirString;
    }

    private static String processAssignNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, tempVarCount, locals, parameters, structureCount, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2); 
        String rightTempVar = childrenData.get(3); 

        String[] splitLeftVar = leftTempVar.split(".");
        String typeString = splitLeftVar[splitLeftVar.length - 1];

        ollirString = leftChild + rightChild;
        ollirString += leftTempVar + " :=." + typeString + " " + rightTempVar + ";";

        return ollirString;
    }

    private static String processVariableName(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var fields = table.getFields();

        String varName = node.get("name");
        
        boolean isLocal = false;
        boolean isParameter = false;

        int indexInList = OllirHelper.lookupVarName(locals, varName);
        isLocal = (indexInList != -1);

        Type varType = null;
        String rhs = "";
        String lhs = "";
        String typeString = "";

        if (isLocal)
        {
            varType = locals.get(indexInList).getType();
        }
        else
        {
            indexInList = OllirHelper.lookupVarName(parameters, varName);
            varName = OllirHelper.sanitizeVariableName(varName);
            isParameter = (indexInList != -1);
            if (isParameter)
            {
                int paramNum = (isStatic) ? indexInList : (indexInList + 1);
                varName = OllirHelper.sanitizeVariableName(varName);
                varName = "$" + paramNum + "." + varName;
                varType = locals.get(indexInList).getType();
            }
            else
            {
                indexInList = OllirHelper.lookupVarName(fields, varName);
                if (indexInList < 0)
                {
                    return varName;  
                } 
                varType = fields.get(indexInList).getType();

                varName = OllirHelper.sanitizeVariableName(varName);
                Integer currentTempVarCount = tempVarCount;
                typeString = OllirHelper.processType(varType);
                ollirString += "t" + (tempVarCount++) + "." + typeString + " :=." + typeString + "getfield(this, " +  varName + "." + typeString + ")." + typeString + ";";
                varName = "t" + currentTempVarCount.toString();
            }
        }

        typeString = OllirHelper.processType(varType);
        
        rhs = varName + "." + typeString;
        lhs = "t" + (tempVarCount++) + "." + typeString;

        ollirString += lhs + " :=." + typeString + " " + rhs + ";";

        return ollirString;
    }

    private static String processElseNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        int structureNumber = OllirHelper.determineNumberForStructure("Else", structureCount);

        ollirString += "elsebody" + structureNumber + ":\n";

        var children = node.getChildren();
        for (JmmNode jmmNode : children) {
            ollirString += processNode(jmmNode, tempVarCount, locals, parameters, structureCount, table, isStatic);
        }

        ollirString += "endifbody" + structureNumber + ":\n";

        return ollirString;
    }

    private static String processIntegerLiteral(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        String name = node.get("name");

        ollirString += "t" + (tempVarCount++) + ".i32 :=.i32 " + name + ".i32;";

        return ollirString;
    }

    private static String processIfNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";
        var children = node.getChildren();
        int structureNumber = OllirHelper.determineNumberForStructure("If", structureCount);

        String ifExpression = processNode(children.get(0), tempVarCount, locals, parameters, structureCount, table, isStatic);

        String lhsLastAssign = OllirHelper.extractLastTempVar(ifExpression);

        ollirString += ifExpression;
        ollirString += "if (";
        ollirString += lhsLastAssign;
        ollirString += ") goto ifbody" + structureNumber + ";";
        ollirString += "goto elsebody" + structureNumber + ";";
        ollirString += "ifbody" + structureNumber + ":\n";
        ollirString += processNode(children.get(1), tempVarCount, locals, parameters, structureCount, table, isStatic);
        ollirString += "goto endifbody" + structureNumber + ";";

        return ollirString;
    }

    private static String processLessThanNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, tempVarCount, locals, parameters, structureCount, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2); 
        String rightTempVar = childrenData.get(3);

        ollirString += leftChild + rightChild;
        ollirString += "t" + (tempVarCount++) + ".bool :=.bool " + leftTempVar + " <.bool " + rightTempVar + ";";        

        return ollirString;
    }

    private static List<String> extractChildrenData(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        List<String> childrenVars = new ArrayList<>();
        List<String> tempVars = new ArrayList<>();

        var children = node.getChildren();

        for (JmmNode jmmNode : children) {
            String currentChild = OllirNodeProcessor.processNode(jmmNode, tempVarCount, locals, parameters, structureCount, table, isStatic);
            childrenVars.add(currentChild);
            String currentTempVar = OllirHelper.extractLastTempVar(currentChild);
            tempVars.add(currentTempVar);
        }
        
        childrenVars.addAll(tempVars);

        return childrenVars;
    }

}
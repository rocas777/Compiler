import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.JmmNode;

class OllirNodeProcessor {

    public static int tempVarCount = 0;
    
    public static String processNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        String nodeKind = node.getKind();
        switch (nodeKind)
        {
            case "If":
            {
                ollirString += processIfNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "LessThan":
            {
                ollirString += processLessThanNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "IntegerLiteral":
            {
                ollirString += processIntegerLiteral(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Assigned":
            case "Object":
            case "VariableName":
            {
                ollirString += processVariableName(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Body":
            {
                var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);
                for (int i = 0; i < (childrenData.size() / 2); i++) {
                    ollirString += childrenData.get(i);
                }
                break;
            }
            case "Else":
            {
                ollirString += processElseNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Assign":
            {
                ollirString += processAssignNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Add":
            case "Sub":
            case "Mul":
            case "Div":
            {
                ollirString += processOperNode(node, locals, parameters, structureCount, table, isStatic, nodeKind);
                break;
            }
            case "ArrayAccess":
            {
                ollirString += processArrayAccessNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Method":
            {
                ollirString += processMethodCallNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "This":
            {
                ollirString += processThisNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Args":
            {
                ollirString += processArgsNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "ConstructorCall":
            {
                ollirString += processConstructorCallNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "AttributeCall":
            {
                ollirString += processLengthNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "While":
            {
                //TODO
                //IMPLEMENT THIS
            }
            case "ArrayIndex":
            {
                ollirString += processArrayIndexNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "AND":
            {
                ollirString += processAndNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Neg":
            {
                ollirString += processNegNode(node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "True":
            {
                ollirString += processBoolConst(true, node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "False":
            {
                ollirString += processBoolConst(false, node, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "NEW":
            {
                //TODO
                //IMPLEMENT THIS
            }
            default:
            {
                System.out.println("Invalid node kind!");
                break;
            }
        }

        return ollirString;
    }

    private static String processConstructorCallNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var child = node.getChildren().get(0);
        String className = child.get("name");

        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + "." + className + " :=." + className + " new(" + className + ")." + className + ";\n";
        ollirString += "invokespecial(t" + (OllirNodeProcessor.tempVarCount - 1) + "." + className + ", \"<init>\").V;\n";

        return ollirString;
    }

    private static String processLengthNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);

        String child = childrenData.get(0);
        String childTempVar = childrenData.get(1);

        ollirString += child;
        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 arraylength(" + childTempVar + ").i32;\n";        

        return ollirString;
    }

    private static String processArrayIndexNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);

        String child = childrenData.get(0);
        String childTempVar = childrenData.get(1);

        ollirString += child;
        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 " + childTempVar + ";\n";        

        return ollirString;
    }

    private static String processNegNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);

        String child = childrenData.get(0);
        String childTempVar = childrenData.get(1);

        ollirString += child;
        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".bool :=.bool " + childTempVar + " !.bool " + childTempVar + ";\n";        

        return ollirString;
    }

    private static String processAndNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2);
        String rightTempVar = childrenData.get(3);

        ollirString += leftChild + rightChild;
        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".bool :=.bool " + leftTempVar + " &&.bool " + rightTempVar + ";\n";        

        return ollirString;
    }

    private static String processBoolConst(boolean value, JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".bool :=.bool " + (value ? "1" : "0") + ".bool;\n";

        return ollirString;
    }

    private static String processThisNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        Type classType = new Type(table.getClassName(), false);
        String typeString = OllirHelper.processType(classType);

        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + "." + typeString + " :=." + typeString + " $0.this." + typeString + ";\n"; 

        return ollirString;
    }

    private static String processArgsNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);
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

    private static String processMethodCallNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var children = node.getChildren();

        var firstChild = children.get(0);
        var secondChild = children.get(1);
        var thirdChild = children.get(2);

        String firstChildString = processNode(firstChild, locals, parameters, structureCount, table, isStatic);
        String thirdChildString = processNode(thirdChild, locals, parameters, structureCount, table, isStatic);
        String methodName = secondChild.get("name");

        String firstInvokeParameter = "";

        if (!firstChild.getKind().equals("VariableName")) ollirString += firstChildString;

        firstInvokeParameter = OllirHelper.extractLastTempVar(firstChildString);

        String[] thirdChildLines = thirdChildString.split(";\n");
        String lastThirdChildLine = thirdChildLines[thirdChildLines.length - 1];
        for (int i = 0; i < (thirdChildLines.length - 1); i++ ) {
            ollirString += thirdChildLines[i] + ";\n";
        }
        
        Type methodReturnType = OllirHelper.determineMethodReturnType(methodName, table, node);
        String typeString = OllirHelper.processType(methodReturnType);

        boolean methodBeingCalledIsStatic = OllirHelper.determineIfMethodIsStatic(methodName, table, node);

        String invokeType = "";
        if (methodBeingCalledIsStatic) invokeType = "invokestatic";
        else invokeType = "invokevirtual";

        boolean methodIsVoid = typeString.equals("V");

        String commonPart = invokeType + "(" + firstInvokeParameter + ", " + "\"" + methodName + "\"" + (lastThirdChildLine.equals("") ? "" : ", ") + lastThirdChildLine;

        if (methodIsVoid) ollirString += commonPart + ").V;\n";
        else ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + "." + typeString + " :=." + typeString + " " + commonPart + ")." + typeString + ";\n";

        return ollirString;
    }

    private static String processArrayAccessNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2); 
        String rightTempVar = childrenData.get(3);

        ollirString = leftChild + rightChild;

        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 " + OllirHelper.trimType(leftTempVar) + "[" + rightTempVar + "].i32;\n";

        return ollirString;
    }

    private static String processOperNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic, String operation)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);

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

        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 " + leftTempVar + " " + operationChar + ".i32 " + rightTempVar + ";\n";

        return ollirString;
    }

    private static String processAssignNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2); 
        String rightTempVar = childrenData.get(3); 

        String[] splitLeftVar = leftTempVar.split("\\.");
        String typeString = splitLeftVar[splitLeftVar.length - 1];

        ollirString = leftChild + rightChild;
        ollirString += leftTempVar + " :=." + typeString + " " + rightTempVar + ";\n";

        return ollirString;
    }

    private static String processVariableName(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
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
                typeString = OllirHelper.processType(varType);
                ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + "." + typeString + " :=." + typeString + "getfield(this, " +  varName + "." + typeString + ")." + typeString + ";\n";
                return ollirString;
            }
        }

        typeString = OllirHelper.processType(varType);
        
        rhs = varName + "." + typeString;
        //lhs = "t" + (OllirNodeProcessor.tempVarCount++) + "." + typeString;

        ollirString += rhs;// + " :=." + typeString + " " + rhs + ";\n";

        return ollirString;
    }

    private static String processElseNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        int structureNumber = OllirHelper.determineNumberForStructure("Else", structureCount);

        ollirString += "elsebody" + structureNumber + ":\n";

        var children = node.getChildren();
        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);

        for (int i = 0; i < (childrenData.size() / 2); i++) {
            ollirString += childrenData.get(i);
        }

        ollirString += "endifbody" + structureNumber + ":\n";

        return ollirString;
    }

    private static String processIntegerLiteral(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        String name = node.get("name");

        ollirString += name + ".i32";

        return ollirString;
    }

    private static String processIfNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";
        var children = node.getChildren();
        int structureNumber = OllirHelper.determineNumberForStructure("If", structureCount);

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);
        String ifExpression = childrenData.get(0);

        String lhsLastAssign = OllirHelper.extractLastTempVar(ifExpression);

        ollirString += ifExpression;
        ollirString += "if (";
        ollirString += lhsLastAssign;
        ollirString += ") goto ifbody" + structureNumber + ";\n";
        ollirString += "goto elsebody" + structureNumber + ";\n";
        ollirString += "ifbody" + structureNumber + ":\n";
        ollirString += processNode(children.get(1), locals, parameters, structureCount, table, isStatic);
        ollirString += "goto endifbody" + structureNumber + ";\n";

        return ollirString;
    }

    private static String processLessThanNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, structureCount, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2);
        String rightTempVar = childrenData.get(3);

        ollirString += leftChild + rightChild;
        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".bool :=.bool " + leftTempVar + " <.bool " + rightTempVar + ";\n";        

        return ollirString;
    }

    public static List<String> extractChildrenData(JmmNode node, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        List<String> childrenVars = new ArrayList<>();
        List<String> tempVars = new ArrayList<>();

        var children = node.getChildren();

        for (JmmNode jmmNode : children) {
            String currentChild = OllirNodeProcessor.processNode(jmmNode, locals, parameters, structureCount, table, isStatic);
            String codePart = OllirHelper.extractCode(currentChild);
            childrenVars.add(codePart);
            String currentTempVar = OllirHelper.extractLastTempVar(currentChild);
            tempVars.add(currentTempVar);
        }
        
        childrenVars.addAll(tempVars);

        return childrenVars;
    }

}
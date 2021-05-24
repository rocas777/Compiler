import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.JmmNode;

class OllirNodeProcessor {

    public static int tempVarCount = 0;
    public static Map<String, Integer> structureCount = new HashMap<>();
    public static Stack<Integer> elseNumStack = new Stack<>();

    public static String processNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        String nodeKind = node.getKind();
        switch (nodeKind)
        {
            case "If":
            {
                ollirString += processIfNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "LessThan":
            {
                ollirString += processLessThanNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "IntegerLiteral":
            {
                ollirString += processIntegerLiteral(node, locals, parameters, table, isStatic);
                break;
            }
            case "Assigned":
            case "Object":
            case "MethodName":
            case "VariableName":
            {
                ollirString += processVariableName(node, locals, parameters, table, isStatic);
                break;
            }
            case "Body":
            {
                var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);
                for (int i = 0; i < (childrenData.size() / 2); i++) {
                    ollirString += childrenData.get(i);
                }
                break;
            }
            case "Else":
            {
                ollirString += processElseNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "Assign":
            {
                ollirString += processAssignNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "Add":
            case "Sub":
            case "Mul":
            case "Div":
            {
                ollirString += processOperNode(node, locals, parameters, table, isStatic, nodeKind);
                break;
            }
            case "ArrayAccess":
            {
                ollirString += processArrayAccessNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "Method":
            {
                ollirString += processMethodCallNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "This":
            {
                ollirString += processThisNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "Args":
            {
                ollirString += processArgsNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "ConstructorCall":
            {
                ollirString += processConstructorCallNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "AttributeCall":
            {
                ollirString += processLengthNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "While":
            {
                ollirString += processWhileNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "ArrayIndex":
            {
                ollirString += processArrayIndexNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "AND":
            {
                ollirString += processAndNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "Neg":
            {
                ollirString += processNegNode(node, locals, parameters, table, isStatic);
                break;
            }
            case "True":
            {
                ollirString += processBoolConst(true, node, locals, parameters, table, isStatic);
                break;
            }
            case "False":
            {
                ollirString += processBoolConst(false, node, locals, parameters, table, isStatic);
                break;
            }
            case "ArrayInitializer":
            {
                ollirString += processArrayInitializerNode(node, locals, parameters, table, isStatic);
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

    private static String processArrayInitializerNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);
        String childString = childrenData.get(0);
        String tempVar = childrenData.get(1);

        ollirString += childString;
        var tempConversion = OllirHelper.convertToTempIfNeeded(tempVar);
        ollirString += tempConversion.get(0);
        ollirString += "new(array, " + tempConversion.get(1) + ").array.i32";

        return ollirString;
    }

    private static String processWhileNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String firstChild = childrenData.get(0);
        String secondChild = childrenData.get(1);

        String firstChildTempVar = childrenData.get(2);
        //String secondChildTempVar = childrenData.get(3);
        
        int whileStructureNumber = OllirHelper.determineNumberForStructure("While", structureCount, elseNumStack);

        ollirString += firstChild;
        var conversionToTemp = OllirHelper.convertToTempIfNeeded(firstChildTempVar);
        ollirString += conversionToTemp.get(0);
        ollirString += "if (" + conversionToTemp.get(1) + " &&.bool 1.bool" + ") goto whilebody" + whileStructureNumber + ";\n";
        ollirString += "goto endwhile" + whileStructureNumber + ";\n";
        ollirString += "whilebody" + whileStructureNumber + ":\n";
        ollirString += secondChild;
        ollirString += firstChild;
        ollirString += conversionToTemp.get(0);
        ollirString += "if ( " + conversionToTemp.get(1) + " &&.bool 1.bool" + ") goto whilebody" + whileStructureNumber + ";\n";
        ollirString += "endwhile" + whileStructureNumber + ":\n";

        // boolean isEndOfFunction = OllirHelper.determineIfNodeIsLastInBody(node);
        // if (isEndOfFunction) ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 0.i32;\n"; 

        return ollirString;
    }

    private static String processConstructorCallNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var child = node.getChildren().get(0);
        String className = child.get("name");

        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + "." + className + " :=." + className + " new(" + className + ")." + className + ";\n";
        ollirString += "invokespecial(t" + (OllirNodeProcessor.tempVarCount - 1) + "." + className + ", \"<init>\").V;\n";

        return ollirString;
    }

    private static String processLengthNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String child = childrenData.get(0);
        String childTempVar = childrenData.get(1);

        ollirString += child;
        var tempConversion = OllirHelper.convertToTempIfNeeded(childTempVar);
        ollirString += tempConversion.get(0);
        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 arraylength(" + tempConversion.get(1) + ").i32;\n";        

        return ollirString;
    }

    private static String processArrayIndexNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String child = childrenData.get(0);
        String childTempVar = childrenData.get(1);

        ollirString += child;
        var tempConversion = OllirHelper.convertToTempIfNeeded(childTempVar);
        ollirString += tempConversion.get(0);
        ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 " + tempConversion.get(1) + ";\n";        

        return ollirString;
    }

    private static String processNegNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String child = childrenData.get(0);
        String childTempVar = childrenData.get(1);

        var conversionToTemp = OllirHelper.convertToTempIfNeeded(childTempVar);

        ollirString += child;
        ollirString += conversionToTemp.get(0);
        ollirString += conversionToTemp.get(1) + " !.bool " + conversionToTemp.get(1);        

        return ollirString;
    }

    private static String processAndNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2);
        String rightTempVar = childrenData.get(3);

        ollirString += leftChild + rightChild;

        boolean isInChain = OllirHelper.determineIfOperIsInChain(node);

        var leftConversion = OllirHelper.convertToTempIfNeeded(leftTempVar);
        var rightConversion = OllirHelper.convertToTempIfNeeded(rightTempVar);
        ollirString += leftConversion.get(0);
        ollirString += rightConversion.get(0);

        if (isInChain) ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".bool :=.bool " + leftConversion.get(1) + " &&.bool " + rightConversion.get(1) + ";\n"; 
        else ollirString += leftConversion.get(1) + " &&.bool " + rightConversion.get(1);        

        return ollirString;
    }

    private static String processBoolConst(boolean value, JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        //ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".bool :=.bool " + (value ? "1" : "0") + ".bool;\n";
       
        ollirString += (value ? "1" : "0") + ".bool";

        return ollirString;
    }

    private static String processThisNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        Type classType = new Type(table.getClassName(), false);
        String typeString = OllirHelper.processType(classType);

        //ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + "." + typeString + " :=." + typeString + " $0.this." + typeString + ";\n"; 
        ollirString += "$0.this." + typeString;

        return ollirString;
    }

    private static String processArgsNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";
  
        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);
        List<String> tempVars = new ArrayList<>();

        for (int i = 0; i < (childrenData.size() / 2); i++) {
            String currentString = childrenData.get(i);
            ollirString += currentString;
            var tempVar = childrenData.get(i + (childrenData.size() / 2));
            var tempConversion = OllirHelper.convertToTempIfNeeded(tempVar);
            ollirString += tempConversion.get(0); 
            tempVars.add(tempConversion.get(1));
        }        

        ollirString += String.join(",", tempVars);

        return ollirString;
    }

    private static String processMethodCallNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String firstChildString = childrenData.get(0);
        String thirdChildString = childrenData.get(2);
        String methodName = childrenData.get(4);
        String firstInvokeParameter = childrenData.get(3);

        String lastThirdChildLine = childrenData.get(5);
        ollirString += firstChildString + thirdChildString;
        
        Type methodReturnType = OllirHelper.determineMethodReturnType(methodName, table, node);
        String typeString = OllirHelper.processType(methodReturnType);

        boolean methodBeingCalledIsStatic = OllirHelper.determineIfMethodIsStatic(methodName, table, node);

        String invokeType = "";
        if (methodBeingCalledIsStatic) invokeType = "invokestatic";
        else invokeType = "invokevirtual";

        boolean methodIsVoid = typeString.equals("V");

        var argsData = OllirHelper.parseMethodArgs(lastThirdChildLine);
        for (int i = 0; i < (argsData.size() - 1); i++) {
            ollirString += argsData.get(i);
        }
        lastThirdChildLine = argsData.get(argsData.size() - 1);

        if (firstInvokeParameter.startsWith("$0.this")) firstInvokeParameter = "this";

        String commonPart = invokeType + "(" + firstInvokeParameter + ", " + "\"" + methodName + "\"" + (lastThirdChildLine.equals("") ? "" : ", ") + lastThirdChildLine;

        if (methodIsVoid) ollirString += commonPart + ").V;\n";
        else ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + "." + typeString + " :=." + typeString + " " + commonPart + ")." + typeString + ";\n";

        return ollirString;
    }

    private static String processArrayAccessNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2); 
        String rightTempVar = childrenData.get(3);

        ollirString += leftChild + rightChild;
        var tempConversion = OllirHelper.convertToTempIfNeeded(rightTempVar);
        ollirString += tempConversion.get(0);

        ollirString += OllirHelper.trimType(leftTempVar) + "[" + tempConversion.get(1) + "].i32";

        return ollirString;
    }

    private static String processOperNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic, String operation)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2); 
        String rightTempVar = childrenData.get(3);

        ollirString += leftChild + rightChild;
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

        boolean isInOperationChain = OllirHelper.determineIfOperIsInChain(node);

        var leftConversion = OllirHelper.convertToTempIfNeeded(leftTempVar);
        var rightConversion = OllirHelper.convertToTempIfNeeded(rightTempVar);
        ollirString += leftConversion.get(0);
        ollirString += rightConversion.get(0);

        if (isInOperationChain) ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 " + leftConversion.get(1) + " " + operationChar + ".i32 " + rightConversion.get(1) + ";\n";
        else ollirString += leftConversion.get(1) + " " + operationChar + ".i32 " + rightConversion.get(1);

        return ollirString;
    }

    private static String processAssignNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2); 
        String rightTempVar = childrenData.get(3); 

        Type type = OllirHelper.getTypeFromOllir(leftTempVar);
        String typeString = OllirHelper.processType(type);

        String[] leftChildLines = leftChild.split(";\n");
        String lastLeftChildLine = leftChildLines[leftChildLines.length - 1];

        if (!lastLeftChildLine.contains("getfield"))
        {
            ollirString += leftChild + rightChild;
            ollirString += leftTempVar + " :=." + typeString + " " + rightTempVar + ";\n";
            
            String fieldArraySet = OllirHelper.determineFieldArraySet(node.getChildren().get(0), table);

            if (!fieldArraySet.equals(""))
            {
                String arrayTempVar = OllirHelper.separateArrayTempVarFromAssignment(leftTempVar);
                ollirString += "putfield(this, " + fieldArraySet + ", " + arrayTempVar + ").V;\n";
            }
        }
        else
        {
            for (int i = 0; i < (leftChildLines.length - 1); i++) {
                ollirString += leftChildLines[i] + ";\n";
            }
            ollirString += rightChild;
            ollirString += OllirHelper.convertGetfieldToPutfield(lastLeftChildLine, rightTempVar);
        }

        return ollirString;
    }

    private static String processVariableName(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var fields = table.getFields();

        String varName = node.get("name");
        String unsanitizedName = String.copyValueOf(varName.toCharArray());
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
            varName = OllirHelper.sanitizeVariableName(varName);
        }
        else
        {
            indexInList = OllirHelper.lookupVarName(parameters, unsanitizedName);
            varName = OllirHelper.sanitizeVariableName(varName);
            isParameter = (indexInList != -1);
            if (isParameter)
            {
                int paramNum = (isStatic) ? indexInList : (indexInList + 1);
                varName = OllirHelper.sanitizeVariableName(varName);
                varName = "$" + paramNum + "." + varName;
                varType = parameters.get(indexInList).getType();
            }
            else
            {
                indexInList = OllirHelper.lookupVarName(fields, unsanitizedName);
                if (indexInList < 0)
                {
                    return varName;  
                } 
                varType = fields.get(indexInList).getType();

                varName = OllirHelper.sanitizeVariableName(varName);
                typeString = OllirHelper.processType(varType);
                ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + "." + typeString + " :=." + typeString + " getfield(this, " +  varName + "." + typeString + ")." + typeString + ";\n";
                return ollirString;
            }
        }

        typeString = OllirHelper.processType(varType);
        
        rhs = varName + "." + typeString;
        //lhs = "t" + (OllirNodeProcessor.tempVarCount++) + "." + typeString;

        ollirString += rhs;// + " :=." + typeString + " " + rhs + ";\n";

        return ollirString;
    }

    private static String processElseNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        int structureNumber = OllirHelper.determineNumberForStructure("Else", structureCount, elseNumStack);

        ollirString += "elsebody" + structureNumber + ":\n";

        var children = node.getChildren();
        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        for (int i = 0; i < (childrenData.size() / 2); i++) {
            ollirString += childrenData.get(i);
        }

        ollirString += "endifbody" + structureNumber + ":\n";

        // boolean isEndOfFunction = OllirHelper.determineIfNodeIsLastInBody(node);
        // if (isEndOfFunction) ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 0.i32;\n"; 

        return ollirString;
    }

    private static String processIntegerLiteral(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        String name = node.get("name");

        ollirString += name + ".i32";

        return ollirString;
    }

    private static String processIfNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";
        var children = node.getChildren();
        int structureNumber = OllirHelper.determineNumberForStructure("If", structureCount, elseNumStack);

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);
        String ifExpression = childrenData.get(0);

        String lhsLastAssign = childrenData.get(2);

        ollirString += ifExpression;
        var conversionToTemp = OllirHelper.convertToTempIfNeeded(lhsLastAssign);
        ollirString += conversionToTemp.get(0);
        ollirString += "if (";
        ollirString += conversionToTemp.get(1) + " &&.bool 1.bool";
        ollirString += ") goto ifbody" + structureNumber + ";\n";
        ollirString += "goto elsebody" + structureNumber + ";\n";
        ollirString += "ifbody" + structureNumber + ":\n";
        ollirString += childrenData.get(1);
        ollirString += "goto endifbody" + structureNumber + ";\n";

        return ollirString;
    }

    private static String processLessThanNode(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        var childrenData = extractChildrenData(node, locals, parameters, table, isStatic);

        String leftChild = childrenData.get(0);
        String rightChild = childrenData.get(1);

        String leftTempVar = childrenData.get(2);
        String rightTempVar = childrenData.get(3);

        
        ollirString += leftChild + rightChild;
        boolean isInChain = OllirHelper.determineIfOperIsInChain(node);

        var leftConversion = OllirHelper.convertToTempIfNeeded(leftTempVar);
        var rightConversion = OllirHelper.convertToTempIfNeeded(rightTempVar);
        ollirString += leftConversion.get(0);
        ollirString += rightConversion.get(0);

        if (isInChain) ollirString += "t" + (OllirNodeProcessor.tempVarCount++) + ".bool :=.bool " + leftConversion.get(1) + " <.i32 " + rightConversion.get(1) + ";\n";        
        else ollirString += leftConversion.get(1) + " <.i32 " + rightConversion.get(1);

        return ollirString;
    }

    public static List<String> extractChildrenData(JmmNode node, List<Symbol> locals, List<Symbol> parameters, MySymbolTable table, boolean isStatic)
    {
        List<String> childrenVars = new ArrayList<>();
        List<String> tempVars = new ArrayList<>();

        var children = node.getChildren();

        for (JmmNode jmmNode : children) {
            String currentChild = OllirNodeProcessor.processNode(jmmNode, locals, parameters, table, isStatic);
            String codePart = OllirHelper.extractCode(currentChild);
            childrenVars.add(codePart);
            String currentTempVar = OllirHelper.extractLastTempVar(currentChild);
            tempVars.add(currentTempVar);
        }
        
        childrenVars.addAll(tempVars);

        return childrenVars;
    }

}
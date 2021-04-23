import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.JmmNode;

class OllirNodeProcessor {
    public static String sanitizeVariableName(String varName)
    {
        //TODO 
        //FINISH THIS
        //CHECK FOR $ OR OTHER INVALID CHARS?

        String newVarName = ""; 

        if (varName.matches("t[0-9]+"))
        {
            newVarName = "not_temp_" + varName;
        }
        else newVarName = varName;

        return newVarName;
    }

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
            default:
            {
                System.out.println("Invalid node kind!");
                break;
            }
        }

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

        ollirString += "t" + (tempVarCount++) + ".i32" + " :=.i32 " + leftTempVar + " " + operationChar + ".i32 " + rightTempVar + ";";

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

        int indexInList = lookupVarName(locals, varName);
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
            indexInList = lookupVarName(parameters, varName);
            varName = sanitizeVariableName(varName);
            isParameter = (indexInList != -1);
            if (isParameter)
            {
                int paramNum = (isStatic) ? indexInList : (indexInList + 1);
                varName = sanitizeVariableName(varName);
                varName = "$" + paramNum + "." + varName;
                varType = locals.get(indexInList).getType();
            }
            else
            {
                indexInList = lookupVarName(fields, varName);
                if (indexInList < 0) System.out.println("Undeclared variable passed semantic analysis!");
                varType = fields.get(indexInList).getType();

                varName = sanitizeVariableName(varName);
                Integer currentTempVarCount = tempVarCount;
                typeString = processType(varType);
                ollirString += "t" + (tempVarCount++) + "." + typeString + " :=." + typeString + "getfield(this, " +  varName + "." + typeString + ")." + typeString + ";";
                varName = "t" + currentTempVarCount.toString();
            }
        }

        typeString = processType(varType);
        
        rhs = varName + "." + typeString;
        lhs = "t" + (tempVarCount++) + "." + typeString;

        ollirString += lhs + " :=." + typeString + " " + rhs + ";";

        return ollirString;
    }

    private static String processElseNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";

        int structureNumber = determineNumberForStructure("Else", structureCount);

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
        int structureNumber = determineNumberForStructure("If", structureCount);

        String ifExpression = processNode(children.get(0), tempVarCount, locals, parameters, structureCount, table, isStatic);
        String[] lines = ifExpression.split(";");

        String lhsLastAssign = lines[lines.length - 1].split(":=")[0];

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

    private static int determineNumberForStructure(String structure, Map<String, Integer> structureCount)
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

    private static int lookupVarName(List<Symbol> searchList, String varName)
    {
        for (int i = 0; i < searchList.size(); i++) {
            if (searchList.get(i).getName().equals(varName)) return i;
        }
        return -1;
    }

    public static String processType(Type type)
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

    private static List<String> extractChildrenData(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        List<String> childrenVars = new ArrayList<>();

        var children = node.getChildren();

        String leftChild = processNode(children.get(0), tempVarCount, locals, parameters, structureCount, table, isStatic);
        String rightChild = processNode(children.get(1), tempVarCount, locals, parameters, structureCount, table, isStatic);
        
        String[] leftLines = leftChild.split(";");
        String[] rightLines = rightChild.split(";");

        String leftTempVar = leftLines[leftLines.length - 1].split(":=")[0]; 
        String rightTempVar = rightLines[rightLines.length - 1].split(":=")[0]; 
        
        childrenVars.add(leftChild);
        childrenVars.add(rightChild);
        childrenVars.add(leftTempVar);
        childrenVars.add(rightTempVar);

        return childrenVars;
    }
}
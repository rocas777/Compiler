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

        switch (node.getKind())
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
            case "VariableName":
            {
                ollirString += processVariableName(node, tempVarCount, locals, parameters, structureCount, table, isStatic);
                break;
            }
            case "Body":
            {

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
        
        if (isLocal)
        {
            varType = locals.get(indexInList).getType();
        }
        else
        {
            indexInList = lookupVarName(parameters, varName);
            isParameter = (indexInList != -1);
            if (isParameter)
            {
                varType = locals.get(indexInList).getType();
            }
            else
            {
                indexInList = lookupVarName(fields, varName);
                if (indexInList < 0) System.out.println("Undeclared variable passed semantic analysis!");
                varType = fields.get(indexInList).getType();
            }
        }

        String typeString = processType(varType);
        varName = sanitizeVariableName(varName);
        rhs = varName + "." + typeString;
        lhs = "t" + (tempVarCount++) + "." + typeString;

        ollirString = lhs + " :=." + typeString + " " + rhs;

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
    
        return ollirString;
    }

    private static String processLessThanNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount, MySymbolTable table, boolean isStatic)
    {
        String ollirString = "";
        var children = node.getChildren();

        String leftChild = processNode(children.get(0), tempVarCount, locals, parameters, structureCount, table, isStatic);
        String rightChild = processNode(children.get(1), tempVarCount, locals, parameters, structureCount, table, isStatic);
        
        String[] leftLines = leftChild.split(";");
        String[] rightLines = rightChild.split(";");

        String leftChildTempVar = leftLines[leftLines.length - 1].split(":=")[0];
        String rightChildTempVar = rightLines[rightLines.length - 1].split(":=")[0];

        ollirString += leftChild + rightChild;
        ollirString += "t" + (tempVarCount++) + ".bool :=.bool " + leftChildTempVar + " <.bool " + rightChildTempVar + ";";        

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
}
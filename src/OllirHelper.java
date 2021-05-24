import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class OllirHelper {

    public static String separateArrayTempVarFromAssignment(String arrayAccessString)
    {
        String ollirString = "";

        int openStraightBracketIndex = arrayAccessString.indexOf("[");
        String tempVar = arrayAccessString.substring(0, openStraightBracketIndex);

        ollirString += tempVar + ".array.i32";

        return ollirString;
    }

    public static String determineFieldArraySet(JmmNode node, MySymbolTable table)
    {
        String nodeKind = node.getKind();
        if (nodeKind.equals("ArrayAccess"))
        {
            var children = node.getChildren();
            var firstChild = children.get(0);
            String arrayName = firstChild.get("name");
            var fields = table.getFields();
            for (Symbol field : fields) {
                String fieldName = field.getName();
                if (fieldName.equals(arrayName) && field.getType().isArray()) return fieldName + ".array.i32";
            }
        }

        return "";
    }

    public static Type determineMethodReturnType(String methodName, MySymbolTable table, JmmNode node)
    {
        Type type = table.getReturnType(methodName);
        if (type != null) return type;
        
        var parentNode = node.getParent();
        String parentNodeKind = parentNode.getKind();

        switch (parentNodeKind)
        {
            case "LessThan":
            case "ArrayIndex":
            case "Add":
            case "Sub":
            case "Mul":
            case "ArrayInitializer":
            case "Div": return new Type("int", false);
            case "If":
            case "Neg":
            case "While":
            case "AND": return new Type("boolean", false);
            case "ArrayAccess":
            {
                int childIndex = findIndexOfChild(parentNode, node);
                boolean isArray = false;
                if (childIndex == 0) isArray = true;
                return new Type("int", isArray);
            }
            case "Args":
            {
                var grandparent = parentNode.getParent();
                String chainedMethodName = grandparent.getChildren().get(1).get("name");
                var chainedMethodParams = table.getParameters(chainedMethodName);
                if (chainedMethodParams != null)
                {
                    int childIndex = findIndexOfChild(parentNode, node);
                    return chainedMethodParams.get(childIndex).getType();
                }
            }
            case "Assign":
            {
                var parentChildren = parentNode.getChildren();
                var firstParentChild = parentChildren.get(0);
                if (firstParentChild.getKind().equals("ArrayAccess")) return new Type("int", false);
                String assignedName = firstParentChild.get("name");
                String nodeMethodName = SearchHelper.getMethodName(firstParentChild);
                Symbol symbol = table.getVariable(assignedName, nodeMethodName);
                if (symbol != null) return symbol.getType();
            }
            case "AttributeCall": return new Type("int", true);
            case "Else":
            case "Body": return new Type("void", false);
            default: return null;
        }
    }

    public static boolean compareNodes(JmmNode node1, JmmNode node2)
    {
        String kind1, kind2;
        kind1 = node1.getKind();
        kind2 = node2.getKind();
        if (!kind1.equals(kind2)) return false;

        List<String> attributes1 = node1.getAttributes();
        List<String> attributes2 = node2.getAttributes();
        
        if (attributes1.size() != attributes2.size()) return false;

        for (int i = 0; i < attributes1.size(); i++) 
        {
            if (!attributes1.get(i).equals(attributes2.get(i))) return false;
            String attr1 = node1.get(attributes1.get(i));
            String attr2 = node2.get(attributes2.get(i));
            if (!attr1.equals(attr2)) return false;
        }

        int numChildren1 = node1.getNumChildren();
        int numChildren2 = node2.getNumChildren();

        if (numChildren1 != numChildren2) return false;

        return true;
    }

    public static int findIndexOfChild(JmmNode father, JmmNode child)
    {
        var children = father.getChildren();

        for (int i = 0; i < children.size(); i++) {
            if (compareNodes(children.get(i), child)) return i;
        }

        return -1;
    }

    public static boolean determineIfMethodIsStatic(String methodName, MySymbolTable table, JmmNode node)
    {
        if (methodName.equals("main")) return true;

        Type returnType = table.getReturnType(methodName);
        if (returnType != null) return false;

        var parentNode = node.getParent();
        String parentNodeKind = parentNode.getKind();

        var children = node.getChildren();
        var firstChild = children.get(0);
        String firstChildKind = firstChild.getKind();

        if (firstChildKind.equals("This")) return false;
        else if (firstChildKind.equals("VariableName") || firstChildKind.equals("Object"))
        {
            String varName = firstChild.get("name");
            Symbol varSymbol = table.getVariable(varName, SearchHelper.getMethodName(node));
            if (varSymbol == null) return true;
        }

        return false;
    }

    public static Type getTypeFromOllir(String declaration)
    {
        boolean isArray = false;
        if (declaration.contains(".array.")) isArray = true;
        int lastDotIndex = declaration.lastIndexOf(".");
        String typeFragment = declaration.substring(lastDotIndex + 1);
        String typeName = "";
        switch (typeFragment)
        {
            case "i32":
            {
                typeName = "int";
                break;
            }
            case "bool":
            {
                typeName = "boolean";
                break;
            }
            case "String":
            {
                typeName = "String";
                break;
            }
            default:
            {
                typeName = typeFragment;
                break;
            }
        }
        return new Type(typeName, isArray);
    }

    public static String trimType(String ollirVarDeclaration)
    {
        String trimmedString = "";
        int lastDotIndex = ollirVarDeclaration.lastIndexOf(".");
        trimmedString = ollirVarDeclaration.substring(0, lastDotIndex);
        if (trimmedString.contains(".array")) trimmedString = trimmedString.replaceAll("\\.array", "");
        return trimmedString;
    }

    public static String extractLastTempVar(String ollirString)
    {
        String stringWithoutNewLines = ollirString.replaceAll("\n", "");
        if (stringWithoutNewLines.endsWith(".V;") && stringWithoutNewLines.contains("invokespecial"))
        {
            String[] lines = stringWithoutNewLines.split(";");
            String secToLastLine = lines[lines.length - 2];
            return secToLastLine.split(":=")[0];
        }
        else if (stringWithoutNewLines.endsWith(".V;")) return "";
        if (!stringWithoutNewLines.contains(";")) return stringWithoutNewLines;
        String[] lines = stringWithoutNewLines.split(";");
        String lastLine = lines[lines.length - 1];
        if (!lastLine.contains(":=")) return lastLine;
        String lastTempVar = lastLine.split(":=")[0];
        return lastTempVar.replaceAll("\\s", "");
    }

    public static int determineNumberForStructure(String structure, Map<String, Integer> structureCount, Stack<Integer> elseNumStack)
    {
        if (structure.equals("Else")) return elseNumStack.pop();

        Integer currentCount = structureCount.get(structure);
        if (currentCount == null)
        {
            structureCount.put(structure, Integer.valueOf(1));
            if (structure.equals("If")) elseNumStack.push(1);
            return 1;
        }
        else
        {
            Integer updatedCount = ++currentCount;
            structureCount.put(structure, updatedCount);
            if (structure.equals("If")) elseNumStack.push(updatedCount);
            return updatedCount.intValue();
        }
    }

    public static int lookupVarName(List<Symbol> searchList, String varName)
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
            case "void":
            {
                typeString += "V";
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

    public static String sanitizeVariableName(String varName)
    {
        String newVarName = varName; 

        if (varName.matches("t[0-9]+")) newVarName = "not_temp_" + newVarName;
        
        if (newVarName.contains("$")) newVarName = newVarName.replaceAll("\\$", "dollar_sign_");

        switch (newVarName)
        {
            case "static":
            case "field":
            case "construct":
            case "init":
            case "method":
            case "public":
            case "if":
            case "goto":
            case "V":
            case "array":
            case "bool":
            case "i32":
            case "arraylength":
            case "invokespecial":
            case "invokestatic":
            case "invokevirtual":
            case "putfield":
            case "getfield":
            case "this":
            case "new":
            case "ret":
            {
                newVarName = "not_" + newVarName;
                break;
            }
            default: break;
        }

        return newVarName;
    }

    public static String extractCode(String pseudoOllirString)
    {
        String ollirString = "";
        
        Pattern pattern = Pattern.compile(".*?body[0-9]+:\n", Pattern.DOTALL);

        if (pseudoOllirString.endsWith(";\n") || pattern.matcher(pseudoOllirString).matches()) ollirString = pseudoOllirString;
        else
        {
            String[] lines = pseudoOllirString.split(";\n");
            for (int i = 0; i < (lines.length - 1); i++) {
                ollirString += lines[i] + ";\n";
            }
        }

        return ollirString;
    }

    public static List<String> parseMethodArgs(String lastLine)
    {
        List<String> parseResult = new ArrayList<>();
        List<String> tempVars = new ArrayList<>();

        if (lastLine.contains(","))
        {
            String[] splitLine = lastLine.split(",");
            for (String string : splitLine) {
                var conversionResult = convertToTempIfNeeded(string);
                parseResult.add(conversionResult.get(0));
                tempVars.add(conversionResult.get(1));
            }
        }
        else return convertToTempIfNeeded(lastLine);

        parseResult.add(String.join(",", tempVars));

        return parseResult;
    }

    private static List<String> convertToTempIfNeeded(String ollirString)
    {
        List<String> stringList = new ArrayList<>();

        if (ollirString.contains("+") || ollirString.contains("-") || ollirString.contains("*") || ollirString.contains("/") || ollirString.contains("["))
        {
            String ollirCode = "t" + (OllirNodeProcessor.tempVarCount++) + ".i32 :=.i32 " + ollirString + ";\n";
            stringList.add(ollirCode);
            stringList.add("t" + (OllirNodeProcessor.tempVarCount - 1) + ".i32");
        }
        else if (ollirString.contains("<") || ollirString.contains("&&"))
        {
            String ollirCode = "t" + (OllirNodeProcessor.tempVarCount++) + ".bool :=.bool " + ollirString + ";\n";
            stringList.add(ollirCode);
            stringList.add("t" + (OllirNodeProcessor.tempVarCount - 1) + ".bool");
        }
        else
        {
            stringList.add("");
            stringList.add(ollirString);
        }

        return stringList;
    }

    public static boolean determineIfNodeIsLastInBody(JmmNode node)
    {
        var parent = node.getParent();
        var parentChildren = parent.getChildren();
        var lastChild = parentChildren.get(parentChildren.size() - 1);
        boolean isLastInBody = compareNodes(lastChild, node);
        var grandparent = parent.getParent();
        var grandparentChildren = grandparent.getChildren();
        var lastGrandparentChild = grandparentChildren.get(grandparentChildren.size() - 1);
        boolean thereIsReturnStatement = lastGrandparentChild.getKind().equals("ReturnValue");
        return isLastInBody && !thereIsReturnStatement;
    }
    
    public static String convertGetfieldToPutfield(String getfield, String rightTempVar)
    {
        String ollirString = "";

        int openBracketsIndex = getfield.indexOf("(");
        int closeBracketsIndex = getfield.indexOf(")");

        String getfieldParams = getfield.substring(openBracketsIndex + 1, closeBracketsIndex);
        ollirString += "putfield(" + getfieldParams + ", " + rightTempVar + ").V;\n";

        return ollirString;
    }

    public static boolean determineIfOperIsInChain(JmmNode node)
    {
        boolean result = false;

        String nodeKind = node.getKind();
        var parent = node.getParent();
        String parentKind = parent.getKind();

        if (nodeKind.equals("Add") || nodeKind.equals("Sub") || nodeKind.equals("Mul") || nodeKind.equals("Div"))
        {
            if (parentKind.equals("Add") || parentKind.equals("Sub") || parentKind.equals("Mul") || parentKind.equals("Div") || parentKind.equals("LessThan")) return true;
        }
        else if (nodeKind.equals("AND") || nodeKind.equals("LessThan"))
        {
            if (parentKind.equals("AND") || parentKind.equals("LessThan")) return true;
        }

        return result;
    }
}

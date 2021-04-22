import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.JmmNode;

class OllirNodeProcessor {
    public static String sanitizeVariableName(String varName)
    {
        //TODO 
        //FINISH THIS
        //MAKE SURE VAR NAME ISNT t1,t2,etc
        return varName;
    }

    public static String processNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount)
    {
        String ollirString = "";

        switch (node.getKind())
        {
            case "If":
            {
                ollirString += processIfNode(node, tempVarCount, locals, parameters, structureCount);
                break;
            }
            case "LessThan":
            {
                ollirString += processLessThanNode(node, tempVarCount, locals, parameters, structureCount);
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

    public static String processIfNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount)
    {
        String ollirString = "";
        var children = node.getChildren();
        int structureNumber = determineNumberForStructure("If", structureCount);

        String ifExpression = processNode(children.get(0), tempVarCount, locals, parameters, structureCount);
        String[] lines = ifExpression.split(";");

        String lhsLastAssign = lines[lines.length - 1].split(":=")[0];

        ollirString += "if (";
        ollirString += lhsLastAssign;
        ollirString += ") goto ifbody" + structureNumber + ";";
        ollirString += "goto elsebody" + structureNumber + ";";
    
        return ollirString;
    }

    public static String processLessThanNode(JmmNode node, Integer tempVarCount, List<Symbol> locals, List<Symbol> parameters, Map<String, Integer> structureCount)
    {
        String ollirString = "";
        var children = node.getChildren();

        String leftChild = processNode(children.get(0), tempVarCount, locals, parameters, structureCount);
        String rightChild = processNode(children.get(1), tempVarCount, locals, parameters, structureCount);
        
        String[] leftLines = leftChild.split(";");
        String[] rightLines = rightChild.split(";");

        String leftChildTempVar = leftLines[leftLines.length - 1].split(":=")[0];
        String rightChildTempVar = rightLines[rightLines.length - 1].split(":=")[0];

        //TODO
        //FINISH THIS

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
}
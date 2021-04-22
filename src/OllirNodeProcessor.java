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
        var children = node.getChildren();

        switch (node.getKind())
        {
            case "If":
            {
                ollirString += processIfNode(node, tempVarCount, locals, parameters, structureCount);
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

        ollirString += "if (";
        ollirString += processNode(children.get(0), tempVarCount, locals, parameters, structureCount); // if the expression inside the if needs to be broken down then the code returned needs to appear before the if in the ollir code
        ollirString += ") goto ifbody" + structureNumber + ";";
        ollirString += "goto elsebody" + structureNumber + ";";
    
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
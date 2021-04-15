import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class ImportMethodVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    public ImportMethodVisitor() 
    {
        addVisit("Method", this::processCall); 
    }

    public Boolean processCall(JmmNode node, MySymbolTable table)
    {
        List<String> imports = table.getImports();
        List<String> lastSectionsOfImports = new ArrayList<>();
        for (String importMaterial : imports) {
            lastSectionsOfImports.add(getLastImportSection(importMaterial));
        }
        
        var children = node.getChildren();
        var firstChild = children.get(0);

        if (firstChild.getKind().equals("VariableName"))
        {
            String importedClassName = firstChild.get("name");
            if (!lastSectionsOfImports.contains(importedClassName))
            {
                Main.semanticReports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(firstChild.get("line")), Integer.parseInt(firstChild.get("column")), "External method is being called without a matching import statement"));
            }
        }

        return true;
    }

    private String getLastImportSection(String importMaterial)
    {
        String lastSection = "";

        if (!importMaterial.contains(".")) lastSection = importMaterial;
        else
        {
            for (int i = (importMaterial.length() - 1); i > 0; i--) {
                if (importMaterial.charAt(i) == '.')
                {
                    lastSection = importMaterial.substring(i + 1);
                }
            }
        }

        return lastSection;
    }
}

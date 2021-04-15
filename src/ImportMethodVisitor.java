import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class ImportMethodVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    public ImportMethodVisitor() 
    {
        addVisit("Method", this::processCall);    
    }

    public Boolean processCall(JmmNode node, MySymbolTable table)
    {
        

        return true;
    }

    private String getLastImportSection(String importMaterial)
    {
        String lastSection = "";

        if (!importMaterial.contains(".")) lastSection = importMaterial;
        else
        {
            
        }

        return lastSection;
    }
}

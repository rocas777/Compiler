import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class ImportVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {

    public ImportVisitor() {
        addVisit("ImportMaterial", this::addImport);
    }

    private Boolean addImport(JmmNode node, MySymbolTable table) {
        table.addImport(node.get("name"));
        return true;
    }
}

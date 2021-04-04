import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class ClassVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    public ClassVisitor()
    {
        addVisit("Name", this::addClassName);
        addVisit("ExtendedClassName", this::addSuperClassName);
    }

    public Boolean addClassName(JmmNode node, MySymbolTable table)
    {
        table.setClassName(node.get("name"));
        return true;
    }

    public Boolean addSuperClassName(JmmNode node, MySymbolTable table)
    {
        table.setSuperClassName(node.get("name"));
        return true;
    }
}

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class FieldVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    public FieldVisitor()
    {
        addVisit("Vars", this::addFields);
    }

    public Boolean addFields(JmmNode node, MySymbolTable table)
    {
        var children = node.getChildren();
        for (JmmNode jmmNode : children) {
            table.addField(processVarDeclaration(jmmNode));
        }

        return true;
    }

    public static Symbol processVarDeclaration(JmmNode node)
    {
        var grandChildren = node.getChildren();
        var typeInfo = grandChildren.get(0);
        var symbolInfo = grandChildren.get(1);

        String typeString = typeInfo.get("type");
        boolean isArray = typeString.contains("[]");
        String typeName = "";
        if (isArray) typeName = typeString.substring(0, typeString.length() - 2);
        else typeName = typeString;
        Type type = new Type(typeName, isArray);
        Symbol symbol = new MySymbol(type, symbolInfo.get("name"), Integer.parseInt(symbolInfo.get("line")), Integer.parseInt(symbolInfo.get("column")));
        return symbol;
    }
}

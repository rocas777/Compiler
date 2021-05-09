import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class ConstPropagVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    private String varName;
    private String methodName;
    private JmmNode constVal;

    public ConstPropagVisitor(String varName, String methodName, JmmNode constVal)
    {
        this.varName = varName;
        this.methodName = methodName;
        this.constVal = constVal;
        addVisit("VariableName", this::processUse);
    }

    private Boolean processUse(JmmNode node, MySymbolTable table)
    {
        if (checkIfMethodAndVarNameMatch(node))
        {
            var parent = node.getParent();
            var parentChildren = parent.getChildren();
            int childIndex = -1;
            for (int i = 0; i < parentChildren.size(); i++) {
                if (parentChildren.get(i) == node) childIndex = i;
            }
            parent.removeChild(node);
            try
            {
                parent.add(this.constVal, childIndex);
            }
            catch (IndexOutOfBoundsException e)
            {
                parent.add(this.constVal, childIndex - 1);
            }
        }

        return true;
    }

    private boolean checkIfMethodAndVarNameMatch(JmmNode node)
    {   
        String methodWhereTheCurrentNodeIsLocated = SearchHelper.getMethodName(node);
        String currentNodeVarName = node.get("name");
        boolean varNameMatches = currentNodeVarName.equals(this.varName);
        boolean methodNameMatches = methodWhereTheCurrentNodeIsLocated.equals(this.methodName);
        return varNameMatches && methodNameMatches;
    }
}

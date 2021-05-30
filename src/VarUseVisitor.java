import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class VarUseVisitor extends PreorderJmmVisitor<MySymbolTable, Boolean> {
    private String varName;
    private String methodName;
    private boolean isConst;
    private JmmNode firstVal;

    public VarUseVisitor(String varName, String methodName)
    {
        this.varName = varName;
        this.methodName = methodName;
        this.isConst = true;
        this.firstVal = null;
        addVisit("Assigned", this::processAssign);
    }

    private Boolean processAssign(JmmNode node, MySymbolTable table)
    {
        if (checkIfMethodAndVarNameMatch(node))
        {
            if (firstVal == null)
            {
                var parent = node.getParent();
                var brother = parent.getChildren().get(1);
                this.firstVal = brother;
            }
            else this.isConst = false;
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

    public boolean isConst() {
        return isConst;
    }

    public JmmNode getFirstVal() {
        return firstVal;
    }
}

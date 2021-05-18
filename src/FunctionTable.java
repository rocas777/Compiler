import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class FunctionTable {
    private Type returnType;
    private List<Symbol> parameters;
    private List<Symbol> localVariables;

    public FunctionTable(Type returnType, List<Symbol> parameters, List<Symbol> localVariables) {
        this.returnType = returnType;
        this.parameters = parameters;
        this.localVariables = localVariables;
    }

    public FunctionTable() {
        this.returnType = null;
        this.parameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Symbol> getLocalVariables() {
        return localVariables;
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        String strRepresentation = "";

        strRepresentation += "Return Type:\n" + returnType.toString() + "\n";
        strRepresentation += "Parameters:\n";
        for (Symbol symbol : parameters) {
            strRepresentation += symbol.toString() + "\n";
        }
        strRepresentation += "Local Variables:\n";
        for (Symbol symbol : localVariables) {
            strRepresentation += symbol.toString() + "\n";
        }

        return strRepresentation;
    }
}

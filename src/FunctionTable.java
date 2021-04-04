import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class FunctionTable {
    private Type returnType;
    private List<Symbol> parameters;
    private List<Symbol> localVariables;

    public FunctionTable(Type returnType, List<Symbol> parameters, List<Symbol> localVariables)
    {
        this.returnType = returnType;
        this.parameters = parameters;
        this.localVariables = localVariables;
    }

    public FunctionTable()
    {
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
}

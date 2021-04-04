import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class MySymbolTable implements SymbolTable {
    private List<String> imports;
    private String className;
    private String superClassName;
    private List<Symbol> fields;
    private Map<String, FunctionTable> functions;

    public MySymbolTable()
    {
        this.imports = new ArrayList<>();
        this.className = null;
        this.superClassName = null;
        this.fields = new ArrayList<>();
        this.functions = new HashMap<>();
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public void addImport(String importName)
    {
        this.imports.add(importName);
    }

    public void addField(Symbol field)
    {
        this.fields.add(field);
    }

    public void addFunction(String funcName, FunctionTable funcData)
    {
        this.functions.put(funcName, funcData);
    }





    //METHODS REQUIRED BY INTERFACE
    @Override
    public List<String> getImports() {
        return imports;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getSuper() {
        return superClassName;
    }

    @Override
    public List<Symbol> getFields() {
        return fields;
    }

    @Override
    public List<String> getMethods() {
        List<String> methods = new ArrayList<>();

        for (Map.Entry<String, FunctionTable> mapEntry : functions.entrySet()) {
            methods.add(mapEntry.getKey());
        }

        return methods;
    }

    @Override
    public Type getReturnType(String methodName) {
        return functions.get(methodName).getReturnType();
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        return functions.get(methodName).getParameters();
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        return functions.get(methodName).getLocalVariables();
    }
    
}

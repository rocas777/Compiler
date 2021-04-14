import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class MySymbolTable implements SymbolTable {
    private List<String> imports;
    private String className;
    private String superClassName;
    private List<Symbol> fields;
    private Map<String, FunctionTable> functions;

    public MySymbolTable() {
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

    public void addImport(String importName) {
        this.imports.add(importName);
    }

    public void addField(Symbol field) {
        this.fields.add(field);
    }

    public void addFunction(String funcName, FunctionTable funcData) {
        this.functions.put(funcName, funcData);

        processDuplicates(funcData);
    }

    private void processDuplicates(FunctionTable funcData)
    {
        List<Symbol> symbols = funcData.getLocalVariables();
        symbols.addAll(funcData.getParameters());

        Map<String, List<Symbol>> varNameAndSymbols = new HashMap<>();

        for (Symbol symbol : symbols) {
            String varName = symbol.getName();
            if (varNameAndSymbols.containsKey(varName))
            {
                varNameAndSymbols.get(varName).add(symbol);
            }
            else
            {
                List<Symbol> symbolList = new ArrayList<>();
                symbolList.add(symbol);
                varNameAndSymbols.put(varName, symbolList);
            } 
        }

        for (Map.Entry<String, List<Symbol>> mapEntry : varNameAndSymbols.entrySet())
        {
            var declarations = mapEntry.getValue();
            if (declarations.size() > 1)
            {
                int minLine = Integer.MAX_VALUE;
                int minCol = Integer.MAX_VALUE;
                MySymbol firstSymbol = null;

                for (Object declaration : declarations) {
                    MySymbol symbol = (MySymbol) declaration;
                    if (symbol.getLine() < minLine)
                    {
                        firstSymbol = symbol;
                        minLine = symbol.getLine();
                        minCol = symbol.getColumn();
                    } 
                    else if (symbol.getLine() == minLine && symbol.getColumn() < minCol)
                    {
                        firstSymbol = symbol;
                        minLine = symbol.getLine();
                        minCol = symbol.getColumn();
                    }
                }

                for (Symbol declaration : declarations)
                {
                    MySymbol symbol = (MySymbol) declaration;
                    if (symbol.getLine() > minLine || symbol.getColumn() > minCol)
                    {
                        Main.reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, symbol.getLine(), symbol.getColumn(), "Duplicate local variable"));
                    }
                }
            }
        }
    }

    public Symbol getVariable(String variableName, String methodName) {
        if (methodName != null)
        {
            for (Symbol parameter : getParameters(methodName)) {
                if (parameter.getName().equals(variableName)) {
                    return parameter;
                }
            }
            for (Symbol local : getLocalVariables(methodName)) {
                if (local.getName().equals(variableName)) {
                    return local;
                }
            }
        }
       
        for (Symbol field : fields) {
            if (field.getName().equals(variableName)) {
                return field;
            }
        }
        return null;
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
        var func = functions.get(methodName);
        if (func == null) return null;
        return func.getReturnType();
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        var func = functions.get(methodName);
        if (func == null) return null;
        return func.getParameters();
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        var func = functions.get(methodName);
        if (func == null) return null;
        return func.getLocalVariables();
    }

}

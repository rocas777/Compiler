import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class MySymbol extends Symbol {
    private int line;
    private int column;

    public MySymbol(Type type, String name, int line, int column) {
        super(type, name);
        this.line = line;
        this.column = column;
    }
    
    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}

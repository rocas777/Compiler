package jasmin;

import org.specs.comp.ollir.*;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;

import java.util.ArrayList;
import java.util.Locale;

public class Jasmin implements JasminBackend {
    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        String outCode = "";
        outCode += ".class " + ollirResult.getOllirClass().getClassName() + "\n";
        String superClass = ollirResult.getOllirClass().getSuperClass();
        if (superClass != null)
            outCode += ".super " + superClass + "\n";
        else {
            superClass = "java/lang/Object";
            outCode += ".super java/lang/Object\n";
        }
        //field private balance D = 0.0
        for (Field f : ollirResult.getOllirClass().getFields()) {
            outCode += "field " + f.getFieldAccessModifier().name() + " " + f.getFieldName() + " " + f.getFieldType() + "\n";
        }

        outCode += ".method public <init>()V\n" +
                "   aload_0\n" +
                "   invokenonvirtual " + superClass + "/<init>()V\n" +
                "   return\n" +
                ".end method\n";

        //.method public static sum([I)I
        for (Method m : ollirResult.getOllirClass().getMethods()) {
            outCode += processMethod(m);
        }

        System.out.println();
        System.out.println(outCode);


        ollirResult.getOllirClass().getFields();


        return new JasminResult(ollirResult, outCode, new ArrayList<>());
    }

    private String processMethod(Method m) {
        if (m.isConstructMethod())
            return "";
        String out = "";
        out += ".method " + m.getMethodAccessModifier().name().toLowerCase(Locale.ROOT) + (m.isStaticMethod() ? "static " : " ") + m.getMethodName() + "(";
        for (Element e : m.getParams()) {
            switch (e.getType().toString()) {
                case "ARRAYREF": {
                    out += "[I;";
                    break;
                }
                case "INT32": {
                    out += "I;";
                    break;
                }
                case "BOOLEAN": {
                    out += "Z;";
                    break;
                }
            }
        }
        out = out.substring(0, out.length() - 1);
        out += ")";
        switch (m.getReturnType().toString()) {
            case "ARRAYREF": {
                out += "[I";
                break;
            }
            case "INT32": {
                out += "I";
                break;
            }
            case "BOOLEAN": {
                out += "Z";
                break;
            }
            case "VOID": {
                out += "V";
                break;
            }
        }
        out += "\n";

        out += "    .limit locals 99\n" +
                "   .limit stack 99\n";


        m.buildVarTable();
        m.buildCFG();
        for (Instruction i : m.getInstructions()) {
            out += processInstruction(i, m);
        }
        System.out.println(OllirAccesser.getVarTable(m).keySet());

        out += ".end method\n";
        return out;
    }

    private String processInstruction(Instruction i, Method m) {
        String out = new String();
        switch (i.getInstType()) {
            case ASSIGN -> {
                AssignInstruction a = (AssignInstruction) i;
                Operand o = (Operand) a.getDest();
                out += processInstruction(a.getRhs(), m);
                switch (o.getType().getTypeOfElement()) {
                    case INT32 -> {
                        out += "    istore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + "\n";
                    }
                }
            }
            case NOPER -> {
                SingleOpInstruction o = (SingleOpInstruction) i;
                if (o.getSingleOperand().isLiteral()) {
                    LiteralElement l = (LiteralElement) o.getSingleOperand();
                    out += "    ldc " + l.getLiteral() + "\n";
                    return out;
                }
            }
        }
        return out;
    }
}


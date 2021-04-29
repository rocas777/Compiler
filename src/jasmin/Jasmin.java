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
                "   invokespecial " + superClass + "/<init>()V\n" +
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
        out += ".method " + m.getMethodAccessModifier().name().toLowerCase(Locale.ROOT) + (m.isStaticMethod() ? " static " : " ") + m.getMethodName() + "(";
        if (m.getMethodName().equals("main")) {
            out += "[Ljava/lang/String;";
        } else
            for (Element e : m.getParams()) {
                switch (e.getType().toString()) {
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
                }
            }
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
                "    .limit stack 99\n";


        m.buildVarTable();
        m.buildCFG();
        for (Instruction i : m.getInstructions()) {
            out += processInstruction(i, m);
        }
        System.out.println(OllirAccesser.getVarTable(m).keySet());

        out += "    return\n";
        out += ".end method\n";
        return out;
    }

    private String processInstruction(Instruction i, Method m) {
        String out = new String();
        switch (i.getInstType()) {
            case ASSIGN: {
                AssignInstruction a = (AssignInstruction) i;
                Operand o = (Operand) a.getDest();
                out += processInstruction(a.getRhs(), m);
                switch (o.getType().getTypeOfElement()) {
                    case INT32: {
                        out += "    istore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store integer " + o.getName() + "\n";
                        break;
                    }
                    case ARRAYREF:
                    case OBJECTREF: {
                        //out += "    astore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store reference " + o.getName() + "\n";
                        break;
                    }
                    case BOOLEAN: {
                        out += "    zstore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store boolean " + o.getName() + "\n";
                        break;
                    }
                }
                break;
            }
            case NOPER: {
                SingleOpInstruction o = (SingleOpInstruction) i;
                if (o.getSingleOperand().isLiteral()) {
                    LiteralElement l = (LiteralElement) o.getSingleOperand();
                    out += "    ldc " + l.getLiteral() + "\n";
                    return out;
                } else {
                    switch (o.getInstType()) {

                    }
                }
                break;
            }
            case CALL: {
                CallInstruction c = (CallInstruction) i;

                if (OllirAccesser.getCallInvocation(c) == CallType.NEW) {
                    //todo
                    out += "    new " + ((Operand) c.getFirstArg()).getName() + "\n";
                    out += "    dup\n";
                    break;
                }
                String className = ((Operand) c.getFirstArg()).getName();
                String funcName = ((LiteralElement) c.getSecondArg()).getLiteral();
                if (funcName.substring(1, funcName.length() - 1).equals("<init>")) {
                    Operand o = ((Operand) c.getFirstArg());
                    ClassType t = (ClassType) o.getType();
                    out += "    invokespecial " + t.getName() + "/<init>()V\n";
                    out += "    astore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store reference " + o.getName() + "\n";
                    break;
                }
                String par = "";
                for (int it = 0; it < c.getListOfOperands().size(); it++) {
                    if (c.getListOfOperands().get(it).isLiteral()) {
                        out += "    ldc " + ((LiteralElement) c.getListOfOperands().get(it)).getLiteral() + "\n";
                        par += "I";
                        continue;
                    }
                    Operand o = (Operand) c.getListOfOperands().get(it);
                    switch (o.getType().getTypeOfElement()) {
                        case INT32: {
                            out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load integer " + o.getName() + "\n";
                            par += "I";
                            break;
                        }
                        case ARRAYREF:
                        case OBJECTREF: {
                            out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                            par += "L" + o.getType().getTypeOfElement().name();
                            break;
                        }
                        case BOOLEAN: {
                            out += "    zload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load boolean " + o.getName() + "\n";
                            par += "Z";
                            break;
                        }
                    }
                }

                var list = className.split("/.");
                out += "    " + OllirAccesser.getCallInvocation(c).name() + " " + list[list.length - 1] + "/" + funcName.substring(1, funcName.length() - 1) + "(" + par + ")" + typeConversion(c.getReturnType().getTypeOfElement()) + "\n";
                break;
            }
            case RETURN: {
                ReturnInstruction r = (ReturnInstruction) i;
                Operand o = (Operand) r.getOperand();
                switch (o.getType().getTypeOfElement()) {
                    case INT32: {
                        out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load integer " + o.getName() + "\n";
                        out += "    ireturn\n";
                        break;
                    }
                    case ARRAYREF:
                    case OBJECTREF: {
                        out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                        out += "    areturn\n";
                        break;
                    }
                    case BOOLEAN: {
                        out += "    zload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load boolean " + o.getName() + "\n";
                        out += "    zreturn\n";
                        break;
                    }
                }
                break;
            }
        }
        return out;
    }

    private String typeConversion(ElementType type) {
        switch (type) {
            case BOOLEAN:
                return "Z";
            case ARRAYREF:
                return "[I";
            case INT32:
                return "I";
            case OBJECTREF:
                return "L" + type.name();
        }
        return "V";
    }
}


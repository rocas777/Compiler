package jasmin;

import org.specs.comp.ollir.*;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Jasmin implements JasminBackend {
    List<String> imports = new ArrayList<>();
    boolean returned = false;

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        imports = ollirResult.getOllirClass().getImports();
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
            outCode += ".field " + (f.getFieldAccessModifier().name()).toLowerCase() + " " + f.getFieldName() + " " + typeConversion(f.getFieldType().getTypeOfElement()) + "\n";
        }

        outCode += ".method public <init>()V\n" +
                "   aload_0\n" +
                "   invokespecial " + superClass + "/<init>()V\n";

        outCode += "   return\n" +
                ".end method\n";

        //.method public static sum([I)I
        for (Method m : ollirResult.getOllirClass().getMethods()) {
            outCode += processMethod(m);
        }


        ollirResult.getOllirClass().getFields();
        System.out.println(outCode);

        System.out.println("Done Compiling");


        return new JasminResult(ollirResult, outCode, new ArrayList<>());
    }

    private String processMethod(Method m) {
        returned = false;
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

        if (!returned)
            out += "    return\n";
        out += ".end method\n";
        return out;
    }

    private String processInstruction(Instruction i, Method m) {
        String out = new String();
        switch (i.getInstType()) {
            case BINARYOPER: {
                BinaryOpInstruction b = (BinaryOpInstruction) i;
                out += loadOp(b.getLeftOperand(), m);
                out += loadOp(b.getRightOperand(), m);
                switch (b.getUnaryOperation().getOpType()) {
                    case ADD:
                    case ADDI32: {
                        out += "    iadd\n";
                        break;
                    }
                    case SUB:
                    case SUBI32: {
                        out += "    isub\n";
                        break;
                    }
                    case MUL:
                    case MULI32: {
                        out += "    imul\n";
                        break;
                    }
                    case DIV:
                    case DIVI32: {
                        out += "    idiv\n";
                        break;
                    }
                }
                break;
            }
            case ASSIGN: {
                AssignInstruction a = (AssignInstruction) i;
                Operand o = (Operand) a.getDest();
                switch (o.getType().getTypeOfElement()) {
                    case INT32: {
                        try {
                            ArrayOperand ao = (ArrayOperand) o;
                            out += "    aload " + OllirAccesser.getVarTable(m).get(ao.getName()).getVirtualReg() + "\n";
                            out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) ao.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                            out += processInstruction(a.getRhs(), m);
                            out += "    iastore\n";
                        } catch (Exception e) {
                            out += processInstruction(a.getRhs(), m);
                            out += "    istore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store integer " + o.getName() + "\n";
                        }
                        break;
                    }
                    case ARRAYREF:
                    case OBJECTREF: {
                        try {
                            ArrayOperand ao = (ArrayOperand) o;
                            out += "    aload " + OllirAccesser.getVarTable(m).get(ao.getName()).getVirtualReg() + "\n";
                            out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) ao.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                            out += processInstruction(a.getRhs(), m);
                            out += "    aastore\n";
                        } catch (Exception e) {
                            out += processInstruction(a.getRhs(), m);
                            out += "    astore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store reference " + o.getName() + "\n";
                        }
                        break;
                    }
                    case BOOLEAN: {
                        try {
                            ArrayOperand ao = (ArrayOperand) o;
                            out += "    aload " + OllirAccesser.getVarTable(m).get(ao.getName()).getVirtualReg() + "\n";
                            out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) ao.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                            out += processInstruction(a.getRhs(), m);
                            out += "    zastore\n";
                        } catch (Exception e) {
                            out += processInstruction(a.getRhs(), m);
                            out += "    zstore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store boolean " + o.getName() + "\n";
                        }
                        break;
                    }
                }
                break;
            }
            case NOPER: {
                SingleOpInstruction o = (SingleOpInstruction) i;
                try {
                    ArrayOperand a = (ArrayOperand) o.getSingleOperand();
                    out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                    out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                    out += "    iaload\n";
                } catch (Exception e) {
                    out += loadOp(((SingleOpInstruction) i).getSingleOperand(), m);
                }

                break;
            }
            case CALL: {
                CallInstruction c = (CallInstruction) i;

                if (OllirAccesser.getCallInvocation(c) == CallType.NEW) {
                    if (c.getReturnType().getTypeOfElement() == ElementType.ARRAYREF) {
                        out += "    ldc 5\n";
                        out += "    newarray int\n";
                    } else {
                        out += "    new " + ((Operand) c.getFirstArg()).getName() + "\n";
                        out += "    dup\n";
                    }
                    break;
                }
                String className = ((Operand) c.getFirstArg()).getName();
                String funcName = ((LiteralElement) c.getSecondArg()).getLiteral();
                if (funcName.substring(1, funcName.length() - 1).equals("<init>")) {
                    Operand o = ((Operand) c.getFirstArg());
                    ClassType t = (ClassType) o.getType();
                    out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                    out += "    invokespecial " + t.getName() + "/<init>()V\n";
                    out += "    astore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store reference " + o.getName() + "\n";
                    break;
                }

                if (c.getFirstArg().getType().getTypeOfElement() == ElementType.THIS) {
                    out += "    aload_0\n";
                } else if (c.getFirstArg().getType().getTypeOfElement() == ElementType.OBJECTREF) {
                    out += "    aload " + OllirAccesser.getVarTable(m).get(((Operand) c.getFirstArg()).getName()).getVirtualReg() + "\n";
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
                            try {
                                ArrayOperand a = (ArrayOperand) o;
                                out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                out += "    iaload\n";
                            } catch (Exception e) {
                                out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load integer " + o.getName() + "\n";
                            }
                            par += "I";
                            break;
                        }
                        case ARRAYREF:
                        case OBJECTREF: {
                            try {
                                ArrayOperand a = (ArrayOperand) o;
                                out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                out += "    aaload\n";
                            } catch (Exception e) {
                                out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                            }
                            par += "L" + o.getType().getTypeOfElement().name();
                            break;
                        }
                        case BOOLEAN: {
                            try {
                                ArrayOperand a = (ArrayOperand) o;
                                out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                out += "    zaload\n";
                            } catch (Exception e) {
                                out += "    zload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load boolean " + o.getName() + "\n";
                            }
                            par += "Z";
                            break;
                        }
                    }
                }
                if (className.equals("this")) {
                    ClassType cl = (ClassType) OllirAccesser.getVarTable(m).get("this").getVarType();
                    className = cl.getName();
                } else if (OllirAccesser.getCallInvocation(c).name().equals("invokevirtual")) {
                    ClassType cl = (ClassType) OllirAccesser.getVarTable(m).get(className).getVarType();
                    className = cl.getName();
                }
                out += "    " + OllirAccesser.getCallInvocation(c).name() + " " + className + "/" + funcName.substring(1, funcName.length() - 1) + "(" + par + ")" + typeConversion(c.getReturnType().getTypeOfElement()) + "\n";
                break;
            }
            case RETURN: {
                ReturnInstruction r = (ReturnInstruction) i;
                if (r.getOperand() == null) {
                    out += "    return\n";
                    returned = true;
                    break;
                }
                try {
                    Operand o = (Operand) r.getOperand();
                    switch (o.getType().getTypeOfElement()) {
                        case INT32: {
                            try {
                                ArrayOperand a = (ArrayOperand) o;
                                out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                out += "    iaload\n";
                            } catch (Exception e) {
                                out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load integer " + o.getName() + "\n";
                            }
                            out += "    ireturn\n";
                            returned = true;
                            break;
                        }
                        case ARRAYREF:
                        case OBJECTREF: {
                            try {
                                ArrayOperand a = (ArrayOperand) o;
                                out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                out += "    aaload\n";
                            } catch (Exception e) {
                                out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                            }
                            out += "    areturn\n";
                            returned = true;
                            break;
                        }
                        case BOOLEAN: {

                            try {
                                ArrayOperand a = (ArrayOperand) o;
                                out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                out += "    zaload\n";
                            } catch (Exception e) {
                                out += "    zload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load boolean " + o.getName() + "\n";
                            }
                            out += "    zreturn\n";
                            returned = true;
                            break;
                        }
                    }
                } catch (Exception ignored) {
                    LiteralElement l = (LiteralElement) r.getOperand();
                    out += "    ldc " + l.getLiteral() + "\n";
                    out += "    ireturn" + "\n";
                    returned = true;
                }
                break;
            }
            case PUTFIELD: {
                PutFieldInstruction p = (PutFieldInstruction) i;
                String class_name = "";
                var fo = (Operand) p.getFirstOperand();
                if (fo.getName().equals("this")) {
                    class_name = ((ClassType) OllirAccesser.getVarTable(m).get("this").getVarType()).getName();
                    out += "    aload_0\n";
                } else {
                    class_name = fo.getName();
                    out += loadOp(p.getFirstOperand(), m);
                }
                out += loadOp(p.getThirdOperand(), m);
                out += "    putfield " + class_name + "/" + ((Operand) p.getSecondOperand()).getName() + " " + typeConversion(p.getSecondOperand().getType().getTypeOfElement()) + "\n";

                break;
            }
            case GETFIELD: {
                GetFieldInstruction g = (GetFieldInstruction) i;
                String class_name = "";
                var fo = (Operand) g.getFirstOperand();

                if (fo.getName().equals("this")) {
                    class_name = ((ClassType) OllirAccesser.getVarTable(m).get("this").getVarType()).getName();
                    out += "    aload_0\n";
                } else {
                    class_name = fo.getName();
                    out += loadOp(g.getFirstOperand(), m);
                }

                out += "    getfield " + class_name + "/" + ((Operand) g.getSecondOperand()).getName() + " " + typeConversion(g.getSecondOperand().getType().getTypeOfElement()) + "\n";
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

    private String loadOp(Element e, Method m) {
        String out = "";
        if (e.isLiteral()) {
            return "    ldc " + ((LiteralElement) e).getLiteral() + "\n";
        } else {
            Operand o = (Operand) e;
            switch (o.getType().getTypeOfElement()) {
                case INT32: {
                    try {
                        ArrayOperand a = (ArrayOperand) o;
                        out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                        out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                        out += "    iaload\n";
                    } catch (Exception i) {
                        out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load integer " + o.getName() + "\n";
                    }
                    break;
                }
                case ARRAYREF:
                case OBJECTREF: {
                    try {
                        ArrayOperand a = (ArrayOperand) o;
                        out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                        out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                        out += "    aaload\n";
                    } catch (Exception i) {
                        out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                    }
                    break;
                }
                case BOOLEAN: {
                    try {
                        ArrayOperand a = (ArrayOperand) o;
                        out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                        out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                        out += "    zaload\n";
                    } catch (Exception i) {
                        out += "    zload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load boolean " + o.getName() + "\n";
                    }
                    break;
                }
            }
        }
        return out;
    }
}


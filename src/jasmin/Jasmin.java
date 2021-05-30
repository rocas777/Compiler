package jasmin;

import org.specs.comp.ollir.*;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class M {


}

public class Jasmin implements JasminBackend {
    List<String> imports = new ArrayList<>();
    boolean returned = false;
    Integer max = 0;
    Integer stackSize = 0;
    Integer maxStackSize = 0;

    public void deltaStack(int v) {
        stackSize += v;
        maxStackSize = Math.max(stackSize, maxStackSize);
        System.out.println(maxStackSize+" "+stackSize +" "+v);
    }

    public void Locals(int v) {
        max = Math.max(v, max);
    }

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

        for (Method m : ollirResult.getOllirClass().getMethods()) {
            System.out.println();
            System.out.println(m.getMethodName());
            outCode += processMethod(m);
        }


        ollirResult.getOllirClass().getFields();
        System.out.println(outCode);

        System.out.println("Done Compiling");


        return new JasminResult(ollirResult, outCode, new ArrayList<>());
    }

    private String processMethod(Method m) {
        returned = false;
        maxStackSize = 0;
        max = 0;
        stackSize = 0;
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
                        out += "I";
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

        String nOut = "";


        m.buildVarTable();
        m.buildCFG();
        for (Instruction i : m.getInstructions()) {
            nOut += processInstruction(i, m);
        }

        if (!returned)
            nOut += "    return\n";
        nOut += ".end method\n";


        for (int i = 0; i < m.getParams().size()+1; i++) {
            Locals(i);
        }
        out += "    .limit locals " + (max + 1) + "\n" +
                "    .limit stack " + maxStackSize + "\n";

        out += nOut;

        return out;
    }

    private String processInstruction(Instruction i, Method m) {
        String out = new String();
        var labels = m.getLabels(i);
        for (String l : labels) {
            out += "  " + l + ":\n";
        }
        switch (i.getInstType()) {
            case BINARYOPER: {
                BinaryOpInstruction b = (BinaryOpInstruction) i;
                switch (b.getUnaryOperation().getOpType()) {
                    case ADD:
                    case ADDI32: {
                        out += loadOp(b.getLeftOperand(), m);
                        out += loadOp(b.getRightOperand(), m);
                        out += "    iadd\n";
                        deltaStack(-1);
                        break;
                    }
                    case SUB:
                    case SUBI32: {
                        out += loadOp(b.getLeftOperand(), m);
                        out += loadOp(b.getRightOperand(), m);
                        out += "    isub\n";
                        deltaStack(-1);
                        break;
                    }
                    case MUL:
                    case MULI32: {
                        out += loadOp(b.getLeftOperand(), m);
                        out += loadOp(b.getRightOperand(), m);
                        out += "    imul\n";
                        deltaStack(-1);
                        break;
                    }
                    case DIV:
                    case DIVI32: {
                        out += loadOp(b.getLeftOperand(), m);
                        out += loadOp(b.getRightOperand(), m);
                        out += "    idiv\n";
                        deltaStack(-1);
                        break;
                    }
                    case NOT:
                    case NOTB:{
                        out += loadOp(b.getLeftOperand(), m);
                        out += "    ldc 1\n";
                        deltaStack(1);
                        out += "    isub\n";
                        deltaStack(-1);
                        break;
                    }
                    default: {
                        out += loadOp(b.getLeftOperand(), m);
                        out += loadOp(b.getRightOperand(), m);
                        out += boolOp(b.getUnaryOperation());
                        break;
                    }
                }
                break;
            }
            case ASSIGN: {
                AssignInstruction a = (AssignInstruction) i;
                Operand o = (Operand) a.getDest();
                try {
                    int value = 0;
                    if (o.getType().getTypeOfElement() == ElementType.INT32) {
                        if (a.getRhs().getInstType() == InstructionType.BINARYOPER) {
                            BinaryOpInstruction b = (BinaryOpInstruction) a.getRhs();
                            if (!b.getLeftOperand().isLiteral()) {
                                Operand operand = (Operand) b.getLeftOperand();
                                if (!operand.getName().equals(o.getName())) {
                                    throw new Exception();
                                }
                            } else {
                                throw new Exception();
                            }
                            if (b.getRightOperand().isLiteral()) {
                                LiteralElement l = (LiteralElement) b.getRightOperand();
                                value = Integer.parseInt(l.getLiteral());
                            } else {
                                throw new Exception();
                            }
                            switch (b.getUnaryOperation().getOpType()) {
                                case ADD:
                                case ADDI32: {
                                    out += "    iinc " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " " + value + "\n";
                                    ;
                                    deltaStack(1);
                                    return out;
                                }
                                case SUB:
                                case SUBI32: {
                                    out += "    iinc " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " -" + value + "\n";
                                    deltaStack(1);
                                    return out;
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {

                }
                switch (o.getType().getTypeOfElement()) {
                    case INT32: {
                        try {
                            ArrayOperand ao = (ArrayOperand) o;
                            out += "    aload " + OllirAccesser.getVarTable(m).get(ao.getName()).getVirtualReg() + "\n";
                            deltaStack(1);
                            out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) ao.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                            deltaStack(1);
                            out += processInstruction(a.getRhs(), m);
                            out += "    iastore\n";
                            deltaStack(-3);
                        } catch (Exception e) {
                            out += processInstruction(a.getRhs(), m);
                            out += "    istore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store integer " + o.getName() + "\n";
                            deltaStack(-1);
                        }
                        Locals(OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg());
                        break;
                    }
                    case ARRAYREF:
                    case OBJECTREF: {
                        try {
                            ArrayOperand ao = (ArrayOperand) o;
                            out += "    aload " + OllirAccesser.getVarTable(m).get(ao.getName()).getVirtualReg() + "\n";
                            deltaStack(1);
                            out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) ao.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                            deltaStack(1);
                            out += processInstruction(a.getRhs(), m);
                            out += "    aastore\n";
                            deltaStack(-3);
                        } catch (Exception e) {
                            out += processInstruction(a.getRhs(), m);
                            out += "    astore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store reference " + o.getName() + "\n";
                            deltaStack(-1);
                        }
                        Locals(OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg());
                        break;
                    }
                    case BOOLEAN: {
                        try {
                            ArrayOperand ao = (ArrayOperand) o;
                            out += "    aload " + OllirAccesser.getVarTable(m).get(ao.getName()).getVirtualReg() + "\n";
                            deltaStack(1);
                            out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) ao.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                            deltaStack(1);
                            out += processInstruction(a.getRhs(), m);
                            out += "    iastore\n";
                            deltaStack(-3);
                        } catch (Exception e) {
                            out += processInstruction(a.getRhs(), m);
                            out += "    istore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store boolean " + o.getName() + "\n";
                            deltaStack(-1);
                        }
                        Locals(OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg());
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
                    deltaStack(1);
                    out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                    deltaStack(1);
                    out += "    iaload\n";
                    deltaStack(-1);
                } catch (Exception e) {
                    out += loadOp(((SingleOpInstruction) i).getSingleOperand(), m);
                }

                break;
            }
            case CALL: {
                CallInstruction c = (CallInstruction) i;


                if(c.getInvocationType() == CallType.arraylength){
                    out += loadOp(c.getFirstArg(),m);
                    out += "    arraylength\n";
                    deltaStack(0);
                }
                else {
                    if (OllirAccesser.getCallInvocation(c) == CallType.NEW) {
                        if (c.getReturnType().getTypeOfElement() == ElementType.ARRAYREF) {
                            for (int it = 0; it < c.getListOfOperands().size(); it++) {
                                if (c.getListOfOperands().get(it).isLiteral()) {
                                    out += "    ldc " + ((LiteralElement) c.getListOfOperands().get(it)).getLiteral() + "\n";
                                    deltaStack(1);
                                    continue;
                                }
                                Operand o = (Operand) c.getListOfOperands().get(it);
                                switch (o.getType().getTypeOfElement()) {
                                    case INT32: {
                                        try {
                                            ArrayOperand a = (ArrayOperand) o;
                                            out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                            deltaStack(1);
                                            out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                            deltaStack(1);
                                            out += "    iaload\n";
                                            deltaStack(-1);
                                        } catch (Exception e) {
                                            out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load integer " + o.getName() + "\n";
                                            deltaStack(1);
                                        }
                                        break;
                                    }
                                    case ARRAYREF:
                                    case OBJECTREF: {
                                        try {
                                            ArrayOperand a = (ArrayOperand) o;
                                            out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                            deltaStack(1);
                                            out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                            deltaStack(1);
                                            out += "    aaload\n";
                                            deltaStack(-1);
                                        } catch (Exception e) {
                                            out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                                            deltaStack(1);
                                        }
                                        break;
                                    }
                                    case BOOLEAN: {
                                        try {
                                            ArrayOperand a = (ArrayOperand) o;
                                            out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                            deltaStack(1);
                                            out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                            deltaStack(1);
                                            out += "    iaload\n";
                                            deltaStack(-1);
                                        } catch (Exception e) {
                                            out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load boolean " + o.getName() + "\n";
                                            deltaStack(1);
                                        }
                                        break;
                                    }
                                }
                            }


                            out += "    newarray int\n";
                            deltaStack(0);
                        } else {
                            out += "    new " + ((Operand) c.getFirstArg()).getName() + "\n";
                            out += "    dup\n";
                            deltaStack(2);
                        }
                        break;
                    }
                    String className = ((Operand) c.getFirstArg()).getName();
                    String funcName = ((LiteralElement) c.getSecondArg()).getLiteral();
                    if (funcName.substring(1, funcName.length() - 1).equals("<init>")) {
                        Operand o = ((Operand) c.getFirstArg());
                        ClassType t = (ClassType) o.getType();
                        out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                        deltaStack(1);
                        out += "    invokespecial " + t.getName() + "/<init>()V\n";
                        deltaStack(-1);
                        out += "    astore " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;store reference " + o.getName() + "\n";
                        deltaStack(-1);
                        Locals(OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg());
                        break;
                    }

                    int pad = 0;

                    if (c.getFirstArg().getType().getTypeOfElement() == ElementType.THIS) {
                        out += "    aload_0\n";
                        pad++;
                        deltaStack(1);
                    } else if (c.getFirstArg().getType().getTypeOfElement() == ElementType.OBJECTREF) {
                        out += "    aload " + OllirAccesser.getVarTable(m).get(((Operand) c.getFirstArg()).getName()).getVirtualReg() + "\n";
                        pad++;
                        deltaStack(1);
                    }

                    String par = "";
                    for (int it = 0; it < c.getListOfOperands().size(); it++) {
                        if (c.getListOfOperands().get(it).isLiteral()) {
                            deltaStack(1);
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
                                    deltaStack(1);
                                    out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                    deltaStack(1);
                                    out += "    iaload\n";
                                    deltaStack(-1);
                                } catch (Exception e) {
                                    out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load integer " + o.getName() + "\n";
                                    deltaStack(1);
                                }
                                par += "I";
                                break;
                            }
                            case ARRAYREF:
                            case OBJECTREF: {
                                try {
                                    ArrayOperand a = (ArrayOperand) o;
                                    out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                    deltaStack(1);
                                    out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                    deltaStack(1);
                                    out += "    aaload\n";
                                    deltaStack(-1);
                                } catch (Exception e) {
                                    out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                                    deltaStack(1);
                                }
                                par += typeConversion(o.getType().getTypeOfElement());
                                break;
                            }
                            case BOOLEAN: {
                                try {
                                    ArrayOperand a = (ArrayOperand) o;
                                    out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                    deltaStack(1);
                                    out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                    deltaStack(1);
                                    out += "    iaload\n";
                                    deltaStack(-1);
                                } catch (Exception e) {
                                    out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load boolean " + o.getName() + "\n";
                                    deltaStack(1);
                                }
                                par += "I";
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
                    deltaStack(-c.getNumOperands() + 2 - pad);
                    System.out.println(c.getNumOperands());
                    System.out.println(funcName.substring(1, funcName.length() - 1)+" "+m.getMethodName());
                    out += "    " + OllirAccesser.getCallInvocation(c).name() + " " + className + "/" + funcName.substring(1, funcName.length() - 1) + "(" + par + ")" + funcTypeConversion(c.getReturnType().getTypeOfElement()) + "\n";
                    if(c.getReturnType().getTypeOfElement() != ElementType.VOID){
                        deltaStack(+1);
                    }
                    ;
                }
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
                                deltaStack(1);
                                out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                deltaStack(1);
                                out += "    iaload\n";
                                deltaStack(-1);
                            } catch (Exception e) {
                                out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load integer " + o.getName() + "\n";
                                deltaStack(1);
                            }
                            deltaStack(-1);
                            out += "    ireturn\n";
                            returned = true;
                            break;
                        }
                        case ARRAYREF:
                        case OBJECTREF: {
                            try {
                                ArrayOperand a = (ArrayOperand) o;
                                out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                deltaStack(1);
                                out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                deltaStack(1);
                                out += "    aaload\n";
                                deltaStack(-1);
                            } catch (Exception e) {
                                out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                                deltaStack(1);
                            }
                            out += "    areturn\n";
                            deltaStack(-1);
                            returned = true;
                            break;
                        }
                        case BOOLEAN: {

                            try {
                                ArrayOperand a = (ArrayOperand) o;
                                out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                                deltaStack(1);
                                out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                                deltaStack(1);
                                out += "    iaload\n";
                                deltaStack(-1);
                            } catch (Exception e) {
                                out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load boolean " + o.getName() + "\n";
                                deltaStack(1);
                            }
                            out += "    ireturn\n";
                            deltaStack(-1);
                            returned = true;
                            break;
                        }
                    }
                } catch (Exception ignored) {
                    LiteralElement l = (LiteralElement) r.getOperand();
                    out += "    ldc " + l.getLiteral() + "\n";
                    deltaStack(1);
                    out += "    ireturn" + "\n";
                    deltaStack(-1);
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
                    deltaStack(1);
                } else {
                    class_name = fo.getName();
                    out += loadOp(p.getFirstOperand(), m);
                }
                out += loadOp(p.getThirdOperand(), m);
                out += "    putfield " + class_name + "/" + ((Operand) p.getSecondOperand()).getName() + " " + typeConversion(p.getSecondOperand().getType().getTypeOfElement()) + "\n";
                deltaStack(-2);
                break;
            }
            case GETFIELD: {
                GetFieldInstruction g = (GetFieldInstruction) i;
                String class_name = "";
                var fo = (Operand) g.getFirstOperand();

                if (fo.getName().equals("this")) {
                    class_name = ((ClassType) OllirAccesser.getVarTable(m).get("this").getVarType()).getName();
                    out += "    aload_0\n";
                    deltaStack(1);
                } else {
                    class_name = fo.getName();
                    out += loadOp(g.getFirstOperand(), m);
                }

                out += "    getfield " + class_name + "/" + ((Operand) g.getSecondOperand()).getName() + " " + typeConversion(g.getSecondOperand().getType().getTypeOfElement()) + "\n";
                deltaStack(0);
                break;
            }
            case BRANCH: {
                try {
                    CondBranchInstruction ci = (CondBranchInstruction) i;
                    out += branchOp(ci, m);

                } catch (Exception ignored) {

                }
                break;
            }
            case GOTO: {
                GotoInstruction gi = (GotoInstruction) i;
                out += "    goto " + gi.getLabel() + "\n";
                break;
            }
            default: {
            }
        }
        return out;
    }

    private String typeConversion(ElementType type) {
        switch (type) {
            case BOOLEAN:
                return "I";
            case ARRAYREF:
                return "[I";
            case INT32:
                return "I";
            case OBJECTREF:
                return "L" + type.name();
        }
        return "V";
    }

    private String funcTypeConversion(ElementType type) {
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
            deltaStack(1);
            return "    ldc " + ((LiteralElement) e).getLiteral() + "\n";
        } else {
            Operand o = (Operand) e;
            switch (o.getType().getTypeOfElement()) {
                case INT32: {
                    try {
                        ArrayOperand a = (ArrayOperand) o;
                        out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                        deltaStack(1);
                        out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                        deltaStack(1);
                        out += "    iaload\n";
                        deltaStack(-1);
                    } catch (Exception i) {
                        out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load integer " + o.getName() + "\n";
                        deltaStack(1);
                    }
                    break;
                }
                case ARRAYREF:
                case OBJECTREF: {
                    try {
                        ArrayOperand a = (ArrayOperand) o;
                        out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                        deltaStack(1);
                        out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                        deltaStack(1);
                        out += "    aaload\n";
                        deltaStack(-1);
                    } catch (Exception i) {
                        out += "    aload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load reference " + o.getName() + "\n";
                        deltaStack(1);
                    }
                    break;
                }
                case BOOLEAN: {
                    try {
                        ArrayOperand a = (ArrayOperand) o;
                        out += "    aload " + OllirAccesser.getVarTable(m).get(a.getName()).getVirtualReg() + "\n";
                        deltaStack(1);
                        out += "    iload " + OllirAccesser.getVarTable(m).get(((Operand) a.getIndexOperands().get(0)).getName()).getVirtualReg() + "\n";
                        deltaStack(1);
                        out += "    iaload\n";
                        deltaStack(-1);
                    } catch (Exception i) {
                        out += "    iload " + OllirAccesser.getVarTable(m).get(o.getName()).getVirtualReg() + " ;load boolean " + o.getName() + "\n";
                        deltaStack(1);
                    }
                    break;
                }
            }
        }
        return out;
    }

    private String boolOp(Operation o) {
        String out = "";
        switch (o.getOpType()) {
            case LTHI32:
            case LTH: {
                System.out.println("while");
                out += "    isub\n";
                deltaStack(-2);
                deltaStack(1);
                out += "    ldc 63\n";
                deltaStack(1);
                out += "    iushr\n";
                deltaStack(-1);
                break;
            }
            case ANDB:
            case ANDI32:
            case AND: {
                out += "    iand\n";
                deltaStack(-1);
                break;
            }
        }
        return out;
    }

    private String branchOp(CondBranchInstruction ci,Method m) {
        String out = "";
        var o = ci.getCondOperation();
        var label = ci.getLabel();
        switch (o.getOpType()) {
            case LTHI32:
            case LTH: {
                out += loadOp(ci.getLeftOperand(), m);
                out += loadOp(ci.getRightOperand(), m);
                deltaStack(-2);
                System.exit(3);
                out += "    if_icmplt " + label + "\n";
                break;
            }
            case ANDB:
            case ANDI32:
            case AND: {
                out += loadOp(ci.getLeftOperand(), m);
                out += loadOp(ci.getRightOperand(), m);
                deltaStack(-2);
                out += "    iand\n";
                deltaStack(+1);
                out += "    ifne " + label + "\n";
                deltaStack(-1);
                break;
            }
            case NOT:
            case NOTB:{
                out += loadOp(ci.getLeftOperand(), m);
                out += "    ifeq " + label + "\n";
                deltaStack(-1);
                break;
            }
        }
        return out;
    }
}


package IR;

import Helper.SyntaxException;
import IR.Instruction.*;
import IR.IrType.ArrayType;
import IR.IrType.ClassType;
import IR.IrType.IntType;
import IR.IrType.PointerType;
import IR.Operand.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IRPrinter implements IRVisitor {
    private PrintStream code;
    private Map<Operand, String> operandStringMap;
    private Map<BB, String> BBMap;
    private Set<BB> bbSet;
    int constStrcnt;
    int oprcnt;

    public IRPrinter(PrintStream printStream) {
        this.code = printStream;
        operandStringMap = new HashMap<>();
        BBMap = new HashMap<>();
        bbSet = new HashSet<>();
        constStrcnt = 0;
        oprcnt = 0;
    }

    public String operandToStr(Operand operand) throws SyntaxException {
        if (operand instanceof Const) return operand.toString();
        if (!operandStringMap.containsKey(operand)) {
            if (operand == null) {
                int b = 1;
            }
//            String a = operand.getId();
            if (operand instanceof ConstString) {
                operandStringMap.put(operand, "constString" + constStrcnt++);
            }
            else {
                operandStringMap.put(operand, operand.getId() + oprcnt++);
            }
        }
        if (operand instanceof ConstString) return "@" + operandStringMap.get(operand);
        if (operand instanceof Register) {
            if (operand instanceof Pointer) {
                if (((Pointer) operand).isGlobal()) return "@" + operandStringMap.get(operand);
                else return "%" + operandStringMap.get(operand);
            }
            else return "%" + operandStringMap.get(operand);
        }
        else throw new SyntaxException("not a legal operand");
    }

    public String bbToStr(BB bb) {
        if (BBMap.get(bb) != null) return BBMap.get(bb);
        BBMap.put(bb, bb.getLable().toString() + bb.type);
        return BBMap.get(bb);
    }

    public void println(String str) {
        code.print(str + "\n");
    }

    public void print(String str) {
        code.print(str);
    }

    @Override
    public void visit(Module module) {
        for (var i : module.getConstStrings()) {
            println(operandToStr(i) + " = " + "\"" + i.toString()+ "\"");
        }
        for (var i : module.getFuncs()) {
            visit(i);
        }
    }

    @Override
    public void visit(Function function) {
        if (function.getInBB() == null) return;
        print("func " + function.getFuncName());
        if (function.getObj() != null) print(" " + operandToStr(function.getObj()));
        for (var i : function.getParas()) {
            print(" " + operandToStr(i));
        }
//        print(")");
        print(" ");
        println("{");
        function.getInBB().accept(this);
//        for (var i : function.getPreOrderBBList()) {
//            i.accept(this);
//        }
//        function.getPreOrderBBList().forEach(i->i.accept(this));
        println("}");
    }

    @Override
    public void visit(BB block) {
        if (bbSet.contains(block)) return;
        println(bbToStr(block) + ":");
        if (!block.isEmpty()) {
            for (BaseInstruction i = block.getHead(); i != null; i = i.getNext()) {
                print("    ");
                i.accept(this);
            }
        }
        bbSet.add(block);

        if (block.getTail() instanceof Jump) ((Jump) block.getTail()).getToBB().accept(this);
        else if (block.getTail() instanceof Branch) {
            ((Branch) block.getTail()).getB1().accept(this);
            ((Branch) block.getTail()).getB2().accept(this);
        }
    }

    @Override
    public void visit(Allocate inst) {
        println(operandToStr(inst.getPointer()) + " = alloc " + operandToStr(inst.getSize()));
    }

    @Override
    public void visit(UnaryOp inst) {
        println(operandToStr(inst.getResult()) + " = " + inst.getOp().toString() + " " + operandToStr(inst.getRs()));
    }

    @Override
    public void visit(BinaryOp inst) {
        operandToStr(inst.getLs());
        operandToStr(inst.getRs());
        println(operandToStr(inst.getResult()) + " = " + inst.getOp().toString() + " " + operandToStr(inst.getLs()) + " " + operandToStr(inst.getRs()));
    }

    @Override
    public void visit(Branch inst) {
        println("br " + operandToStr(inst.getCond()) + " " + bbToStr(inst.getB1()) + " " +  bbToStr(inst.getB2()));
    }

    @Override
    public void visit(Call inst) {
        StringBuilder str = new StringBuilder();
        if (inst.getObj() != null) {
            str.append(" ");
            str.append(operandToStr(inst.getObj()));
        }
        for (var i : inst.getParas()) {
            str.append(" ");
            str.append(operandToStr(i));
        }
        if (inst.getResult() != null) {
            println(operandToStr(inst.getResult()) +  " = call " + inst.getFunc().getFuncName() + str);
        }
        else {
            println("call " + inst.getFunc().getFuncName() + str);
        }
    }

    @Override
    public void visit(Move inst) {
        println(operandToStr(inst.getDest()) + " = move " + operandToStr(inst.getSrc()));
    }

    @Override
    public void visit(Jump inst) {
        println("jump " + bbToStr(inst.getToBB()));
    }

    @Override
    public void visit(Load inst) {
        println(operandToStr(inst.getReg()) + " = load " + operandToStr(inst.getPointer()));
    }

    @Override
    public void visit(PhiNode inst) {}

    @Override
    public void visit(Return inst) {
        if (inst.getRetVar() != null) println("ret " + operandToStr(inst.getRetVar()));
        else println("ret");
    }

    @Override
    public void visit(Store inst) {
        if (inst.getVal() == null) {
            int  a = 0;
        }
        if (inst.getPointer() == null) {
            int b = 0;
        }
        println("store " + operandToStr(inst.getVal()) + " " + operandToStr(inst.getPointer()));
    }

    @Override
    public void visit(ArrayType type) {}

    @Override
    public void visit(ClassType type) {}

    @Override
    public void visit(IntType type) {}

    @Override
    public void visit(PointerType type) {}

    @Override
    public void visit(Operand operand) {}

    @Override
    public void visit(Const const_) {}

    @Override
    public void visit(Pointer pointer) {}

    @Override
    public void visit(Int32 int32) {}

    @Override
    public void visit(ConstString constString) {}

    @Override
    public void visit(Register register) {}
}

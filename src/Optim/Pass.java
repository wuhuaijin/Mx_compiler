package Optim;
import Backend.BackFunction;
import IR.Function;
import IR.Instruction.BaseInstruction;
import IR.Instruction.BinaryOp;
import IR.Module;
import IR.Operand.Const;
import IR.Operand.Operand;

import java.util.HashSet;
import java.util.List;

public class Pass {
    protected Module module;

    public Pass(Module module) {
        this.module = module;
    }
    public void run() {
        for (Function func : module.getFuncs()) {
            if (func.isSystem()) continue;
            replaceOp(func);
        }
    }

    private void replaceOp(Function func) {
        for(var BB : func.getPreOrderBBList()){
            BaseInstruction nextIns;
            for(var ins = BB.getHead(); ins != null; ins = nextIns){
                nextIns = ins.getNext();

                if(ins instanceof BinaryOp && ((BinaryOp) ins).getOp() == BinaryOp.Op.mul){
                    Operand lhs = null;
                    int rhs = -1;

                    boolean hasImm = false;
                    if(((BinaryOp) ins).getLs() instanceof Const){
                        rhs = ((Const) ((BinaryOp) ins).getLs()).getValue();
                        lhs = ((BinaryOp) ins).getRs();
                        hasImm = true;
                    }
                    else if(((BinaryOp) ins).getRs() instanceof Const){
                        rhs = ((Const) ((BinaryOp) ins).getRs()).getValue();
                        lhs = ((BinaryOp) ins).getLs();
                        hasImm = true;
                    }

                    if (hasImm && powOfTwo(rhs)) {
                        ins.replaceInst(new BinaryOp(BB, false, BinaryOp.Op.shl, lhs, new Const(changeToPow(rhs)), ((BinaryOp) ins).getResult()));
                    }
                }
                else if(ins instanceof BinaryOp && ((BinaryOp) ins).getOp() == BinaryOp.Op.div){
                    Operand lhs = null;
                    int rhs = 1;

                    boolean hasImm = false;
                    if(((BinaryOp) ins).getRs() instanceof Const){
                        rhs = ((Const) ((BinaryOp) ins).getRs()).getValue();
                        lhs = ((BinaryOp) ins).getLs();
                        hasImm = true;
                    }

                    if (hasImm && powOfTwo(rhs)) {
                        ins.replaceInst(new BinaryOp(BB, false, BinaryOp.Op.shr, lhs, new Const(changeToPow(rhs)), ((BinaryOp) ins).getResult()));
                    }
                }
            }
        }
    }

    public boolean powOfTwo(int x) {
        return (x & (x-1)) == 0 && x > 0;
    }
    public int changeToPow(int x) {
        return x == 1 ? 0 : (1 + changeToPow(x >> 1));
    }
}
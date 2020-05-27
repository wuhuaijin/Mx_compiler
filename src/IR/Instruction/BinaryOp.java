package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BinaryOp extends BaseInstruction{

    //%dest = add %src1 %src2
    //    %dest = sub %src1 %src2
    //    %dest = mul %src1 %src2
    //    %dest = div %src1 %src2
    //    %dest = mod %src1 %src2
    //    %dest = shl %src1 %src2
    //    %dest = shr %src1 %src2
    //    %dest = and %src1 %src2
    //    %dest = or  %src1 %src2
    //    %dest = xor %src1 %src2
    //Logical:
    //    %dest = slt %src1 %src2
    //    %dest = sle %src1 %src2
    //    %dest = seq %src1 %src2
    //    %dest = sne %src1 %src2
    //    %dest = sge %src1 %src2
    //    %dest = sgt %src1 %src2

    public enum Op {
        add, sub, mul, div, mod,
        shl, shr, or, xor, and,
        seq, sne, slt, sle, sgt, sge;

        public String toRiscv() {
            switch (this) {
                case add:
                    return "add";
                case sub:
                    return "sub";
                case mul:
                    return "imul";
                case div:
                    return "div";
                case mod:
                    return "mod";
                case shl:
                    return "shl";
                case shr:
                    return "shr";
                case and:
                    return "and";
                case or:
                    return "or";
                case xor:
                    return "xor";
                case slt:
                    return "setl";
                case sgt:
                    return "setg";
                case sle:
                    return "setle";
                case sge:
                    return "setge";
                case seq:
                    return "sete";
                case sne:
                    return "setne";
                default:
                    return null;
            }
        }
        public String toString() {
            switch (this) {
                case add:
                    return "add";
                case sub:
                    return "sub";
                case mul:
                    return "mul";
                case div:
                    return "div";
                case mod:
                    return "mod";
                case shl:
                    return "shl";
                case shr:
                    return "shr";
                case and:
                    return "and";
                case or:
                    return "or";
                case xor:
                    return "xor";
                case slt:
                    return "lt";
                case sgt:
                    return "gt";
                case sle:
                    return "le";
                case sge:
                    return "ge";
                case seq:
                    return "eq";
                case sne:
                    return "neq";
                default:
                    return null;
            }
        }
    }

    private Op op;
    private Operand ls, rs, result;

    public BinaryOp(BB basicBlock, boolean ifTerminal, Op op, Operand ls, Operand rs, Operand result) {
        super(basicBlock, ifTerminal);
        this.ls = ls;
        this.rs = rs;
        this.result = result;
        this.op = op;
    }

//    @Override
//    public String toString() {
//        return result + "=" + op.toString() + ls + " '," + rs;
//    }

    public Op getOp() {
        return op;
    }

    public Operand getLs() {
        return ls;
    }

    public Operand getRs() {
        return rs;
    }

    public Operand getResult() {
        return result;
    }

    public void setResult(Operand result) {
        this.result = result;
    }

    public void setOp(Op op) {
        this.op = op;
    }

    @Override
    public List<Operand> getOpr() {
        return Arrays.asList(ls, rs, result);
    }

    @Override
    public List<BB> getBB() {
        return Collections.emptyList();
    }

    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new BinaryOp(bb, ifTerminal, op, oprList.get(0), oprList.get(1), oprList.get(2));
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

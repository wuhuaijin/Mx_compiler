package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UnaryOp extends BaseInstruction {

    public enum Op {
        NEG, NOT;

        public String toString() {
            switch (this) {
                case NEG:
                    return "neg";
                case NOT:
                    return "com";
            }
            return null;
        }
    }

    private Operand result;
    private Operand rs;
    private Op op;

    public UnaryOp(BB basicBlock, boolean ifTerminal, Operand result, Operand rs, Op op) {
        super(basicBlock, ifTerminal);
        this.result = result;
        this.rs = rs;
        this.op = op;
    }

    public Operand getResult() {
        return result;
    }

    public Operand getRs() {
        return rs;
    }

    public Op getOp() {
        return op;
    }

//    @Override
//    public String toString() {
//        return result.toString() + " = " + op.name() + " " + rs.toString();
//    }

    @Override
    public List<Operand> getOpr() {
        return Arrays.asList(result, rs);
    }

    @Override
    public List<BB> getBB() {
        return Collections.emptyList();
    }

    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new UnaryOp(bb, ifTerminal, oprList.get(0), oprList.get(1), op);
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUseOpr(Operand _old, Operand _new) {
        if (rs == _old) rs = _new;
    }

    @Override
    public void replaceDefOpr(Operand _new) {
        result = _new;
    }

    @Override
    public List<VirtualRegister> getUseOpr() {
        return rs instanceof VirtualRegister ? Collections.singletonList((VirtualRegister) rs) : Collections.emptyList();
    }

    @Override
    public VirtualRegister getDefOpr() {
        return result instanceof VirtualRegister ? (VirtualRegister) result : null;
    }
}


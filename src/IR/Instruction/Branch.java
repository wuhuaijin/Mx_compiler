package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Branch extends BaseInstruction {
    private Operand cond;
    private BB b1;
    private BB b2;

    public Branch(BB basicBlock, boolean ifTerminal, Operand cond, BB b1, BB b2) {
        super(basicBlock, ifTerminal);
        this.b1 = b1;
        this.b2 = b2;
        this.cond = cond;
    }

    public BB getB1() {
        return b1;
    }

    public BB getB2() {
        return b2;
    }

    public Operand getCond() {
        return cond;
    }

    @Override
    public List<Operand> getOpr() {
        return Collections.singletonList(cond);
    }

    @Override
    public List<BB> getBB() {
        return Arrays.asList(b1, b2);
    }

    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new Branch(bb, ifTerminal, oprList.get(0), bbList.get(0), bbList.get(1));
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUseOpr(Operand _old, Operand _new) {
        if (cond == _old) cond = _new;
    }

    @Override
    public void replaceDefOpr(Operand _new) {
        assert false;
    }

    @Override
    public List<VirtualRegister> getUseOpr() {
        return cond instanceof VirtualRegister ? Collections.singletonList((VirtualRegister)cond) : Collections.emptyList();
    }

    @Override
    public VirtualRegister getDefOpr() {
        return null;
    }


    public void replaceTargetBB(BB _old, BB _new){
        if (b1 == _old) b1 = _new;
        if (b2 == _old) b2 = _new;
    }
}

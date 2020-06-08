package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.Operand;
import IR.Operand.VirtualRegister;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Move extends BaseInstruction{

    private Operand src;
    private Operand dest;

    public Move(BB basicBlock, boolean ifTerminal, Operand src, Operand dest) {
        super(basicBlock, ifTerminal);
        this.src = src;
        this.dest = dest;
    }

    public Operand getSrc() {
        return src;
    }

    public Operand getDest() {
        return dest;
    }

    @Override
    public List<Operand> getOpr() {
        return Arrays.asList(src, dest);
    }

    @Override
    public List<BB> getBB() {
        return Collections.emptyList();
    }

    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new Move(bb, ifTerminal, oprList.get(0), oprList.get(1));
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUseOpr(Operand _old, Operand _new) {
        if (src == _old) src = _new;
    }

    @Override
    public void replaceDefOpr(Operand _new) {
        dest = _new;
    }

    @Override
    public List<VirtualRegister> getUseOpr() {
        return src instanceof VirtualRegister ? Collections.singletonList((VirtualRegister) src) : Collections.emptyList();
    }

    @Override
    public VirtualRegister getDefOpr() {
        return dest instanceof VirtualRegister ? (VirtualRegister) dest : null;
    }
}

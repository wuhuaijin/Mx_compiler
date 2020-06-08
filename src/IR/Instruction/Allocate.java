package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.Operand;
import IR.Operand.VirtualRegister;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Allocate extends BaseInstruction {
    private Operand size;
    private Operand pointer;

    public Allocate(BB basicBlock, boolean ifTerminal, Operand size, Operand pointer) {
        super(basicBlock, ifTerminal);
        this.size = size;
        this.pointer = pointer;
    }

    public Operand getSize() {
        return size;
    }

    public Operand getPointer() {
        return pointer;
    }

    @Override
    public List<Operand> getOpr() {
        return Arrays.asList(size, pointer);
    }

    @Override
    public List<BB> getBB() {
        return Collections.emptyList();
    }

    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new Allocate(bb, ifTerminal, oprList.get(0), oprList.get(1));
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUseOpr(Operand _old, Operand _new) {
        if (size == _old) size = _new;
    }

    @Override
    public void replaceDefOpr(Operand _new) {
        pointer = _new;
    }

    @Override
    public List<VirtualRegister> getUseOpr() {
        return size instanceof VirtualRegister ? Collections.singletonList((VirtualRegister)size) : Collections.emptyList();
    }

    @Override
    public VirtualRegister getDefOpr() {
        return ((VirtualRegister) pointer);
    }
}

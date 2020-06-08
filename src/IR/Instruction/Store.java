package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.*;

import java.util.*;

public class Store extends BaseInstruction {

    private Operand val;
    private Operand pointer;

    public Store(BB basicBlock, boolean ifTerminal, Operand val, Operand pointer) {
        super(basicBlock, ifTerminal);
        this.val = val;
        this.pointer = pointer;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUseOpr(Operand _old, Operand _new) {
        if (val == _old) val = _new;
        if (pointer == _old) pointer = _new;
    }

    @Override
    public void replaceDefOpr(Operand _new) {
        assert false;
    }

    @Override
    public List<VirtualRegister> getUseOpr() {
        List<VirtualRegister> registerList = new ArrayList<>();
        if(val instanceof VirtualRegister) registerList.add((VirtualRegister)val);
        if(pointer instanceof VirtualRegister) registerList.add((VirtualRegister)pointer);
        return registerList;
    }

    @Override
    public VirtualRegister getDefOpr() {
        return null;
    }

    public Operand getPointer() {
        return pointer;
    }

    public Operand getVal() {
        return val;
    }

    @Override
    public List<Operand> getOpr() {
        return Arrays.asList(val, pointer);
    }


    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new Store(bb, ifTerminal, oprList.get(0), oprList.get(1));
    }
    @Override
    public List<BB> getBB() {
        return Collections.emptyList();
    }
}

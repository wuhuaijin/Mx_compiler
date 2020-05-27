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

package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.IrType.IrType;
import IR.Operand.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Load extends BaseInstruction {
    private Operand reg;
    private Operand pointer;

    public Load(BB basicBlock, boolean ifTerminal, Operand reg, Operand pointer) {
        super(basicBlock, ifTerminal);
        this.reg = reg;
        this.pointer = pointer;
    }


    public Operand getPointer() {
        return pointer;
    }

    public Operand getReg() {
        return reg;
    }

    @Override
    public List<Operand> getOpr() {
        return Arrays.asList(reg, pointer);
    }

    @Override
    public List<BB> getBB() {
        return Collections.emptyList();
    }


    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new Load(bb, ifTerminal, oprList.get(0), oprList.get(1));
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

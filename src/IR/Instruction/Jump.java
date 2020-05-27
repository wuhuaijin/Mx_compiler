package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Jump extends BaseInstruction{

    private BB toBB;

    public Jump(BB basicBlock, boolean ifTerminal, BB toBB) {
        super(basicBlock, ifTerminal);
        this.toBB = toBB;
    }

    public BB getToBB() {
        return toBB;
    }

    @Override
    public List<Operand> getOpr() {
        return Collections.emptyList();
    }

    @Override
    public List<BB> getBB() {
        return Collections.singletonList(toBB);
    }

    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new Jump(bb, ifTerminal, bbList.get(0));
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

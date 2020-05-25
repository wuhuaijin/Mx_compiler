package Backend.Inst;

import Backend.RiscvVisitor;
import IR.Operand.Register;

import java.util.Collections;
import java.util.List;

public class Return extends Inst {
    @Override
    public void accept(RiscvVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Register> getUseReg() {
        return Collections.emptyList();
    }

    @Override
    public Register getDefReg() {
        return null;
    }

    @Override
    public void replaceUseReg(Register old_, Register new_) {

    }

    @Override
    public void replaceDefReg(Register new_) {

    }

    public Return() {}
}

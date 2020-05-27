package Backend.Inst;

import Backend.BackBB;
import Backend.RiscvVisitor;
import IR.Operand.Register;

import java.util.Collections;
import java.util.List;

public class Jump extends Inst {
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
        assert false;
    }

    @Override
    public void replaceDefReg(Register new_) {
        assert false;
    }

    private BackBB target;

    public Jump(BackBB target) {
        this.target = target;
    }

    public BackBB getTarget() {
        return target;
    }
}

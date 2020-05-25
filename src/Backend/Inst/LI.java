package Backend.Inst;

import Backend.RiscvVisitor;
import IR.Operand.Const;
import IR.Operand.Register;

import java.util.Collections;
import java.util.List;

public class LI extends Inst {
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
        return rd;
    }

    @Override
    public void replaceUseReg(Register old_, Register new_) {
        assert false;
    }

    @Override
    public void replaceDefReg(Register new_) {
        rd = new_;
    }

    private Register rd;
    private Const imm;

    public LI(Register rd, Const imm) {
        this.rd = rd;
        this.imm = imm;
    }

    public Register getRd() {
        return rd;
    }

    public Const getImm() {
        return imm;
    }
}

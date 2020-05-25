package Backend.Inst;

import Backend.RiscvVisitor;
import IR.Operand.ConstString;
import IR.Operand.Register;

import java.util.Collections;
import java.util.List;

public class LA extends Inst {
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
    private ConstString ptr;

    public LA(Register rd, ConstString ptr) {
        this.rd = rd;
        this.ptr = ptr;
    }

    public Register getRd() {
        return rd;
    }

    public ConstString getPtr() {
        return ptr;
    }
}

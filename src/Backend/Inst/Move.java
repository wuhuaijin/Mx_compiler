package Backend.Inst;

import Backend.RiscvVisitor;
import IR.Operand.Register;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Move extends Inst {
    @Override
    public void accept(RiscvVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Register> getUseReg() {
        return Collections.singletonList(src);
    }

    @Override
    public Register getDefReg() {
        return rd;
    }

    @Override
    public void replaceUseReg(Register old_, Register new_) {
        if (src == old_) src = new_;
    }

    @Override
    public void replaceDefReg(Register new_) {
        rd = new_;
    }

    private Register rd;
    private Register src;

    public Move(Register rd, Register src) {
        this.rd = rd;
        this.src = src;
    }

    public Register getRd() {
        return rd;
    }

    public Register getSrc() {
        return src;
    }


}

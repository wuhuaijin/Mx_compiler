package Backend.Inst;

import Backend.BackFunction;
import Backend.RiscvVisitor;
import IR.Operand.Register;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Call extends Inst {
    @Override
    public void accept(RiscvVisitor visitor) {
        visitor.visit(this);
    }

    private BackFunction func;

    public Call(BackFunction func) {
        this.func = func;
    }

    public BackFunction getFunc() {
        return func;
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
    public void replaceUseReg(Register old_, Register new_) { }

    @Override
    public void replaceDefReg(Register new_) {
        assert false;
    }


}

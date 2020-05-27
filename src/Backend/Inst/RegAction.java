package Backend.Inst;

import Backend.RiscvVisitor;
import IR.Operand.Register;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RegAction extends Inst {
    @Override
    public void accept(RiscvVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Register> getUseReg() {
        return Arrays.asList(rs1, rs2);
    }

    @Override
    public Register getDefReg() {
        return rd;
    }

    @Override
    public void replaceUseReg(Register old_, Register new_) {
        if (rs1 == old_) rs1 = new_;
        if (rs2 == old_) rs2 = new_;
    }

    @Override
    public void replaceDefReg(Register new_) {
        rd = new_;
    }

    public enum Op{
        ADD, SUB, MUL, DIV, REM,
        SLL, SRA,
        SLT, SLTU,
        AND, OR, XOR
    }

    private Register rs1, rs2, rd;
    private Op op;

    public RegAction(Register rs1, Register rs2, Register rd, Op op) {
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.rd = rd;
        this.op = op;
    }

    public Register getRs1() {
        return rs1;
    }

    public Register getRs2() {
        return rs2;
    }

    public Register getRd() {
        return rd;
    }

    public Op getOp() {
        return op;
    }
}

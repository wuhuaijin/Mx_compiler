package Backend.Inst;

import Backend.RiscvVisitor;
import IR.Operand.Const;
import IR.Operand.Register;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImmAction extends Inst {
    @Override
    public void accept(RiscvVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Register> getUseReg() {
        return Collections.singletonList(rs1);
    }

    @Override
    public Register getDefReg() {
        return rd;
    }

    public enum Op{
        ADDI, XORI, ORI, ANDI,
        SLTI, SLTIU,
        SLLI, SRAI
    }

    private Register rs1, rd;
    private Op op;
    private Const imm;

    public ImmAction(Register rs1, Register rd, Op op, Const imm) {
        this.rs1 = rs1;
        this.rd = rd;
        this.op = op;
        this.imm = imm;
    }

    public Register getRs1() {
        return rs1;
    }

    public Register getRd() {
        return rd;
    }

    public Op getOp() {
        return op;
    }

    public Const getImm() {
        return imm;
    }


    @Override
    public void replaceUseReg(Register old_, Register new_) {
        if (rs1 == old_) rs1 = new_;
    }

    @Override
    public void replaceDefReg(Register new_) {
        rd = new_;
    }
}

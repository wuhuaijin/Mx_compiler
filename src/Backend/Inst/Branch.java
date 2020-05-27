package Backend.Inst;

import Backend.BackBB;
import Backend.RiscvVisitor;
import IR.Operand.Register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Branch extends Inst {
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
        return null;
    }

    @Override
    public void replaceUseReg(Register old_, Register new_) {
        if (rs1 == old_) rs1 = new_;
        if (rs2 == old_) rs2 = new_;
    }

    @Override
    public void replaceDefReg(Register new_) {
        assert false;
    }

    public enum Opcode {
        beq,bne,ble,bge,blt,bgt
    }
    private Register rs1, rs2;
    private BackBB target;
    private Opcode opcode;

    public Branch(Register rs1, Register rs2, BackBB target, Opcode opcode) {
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.target = target;
        this.opcode = opcode;
    }

    public Register getRs1() {
        return rs1;
    }

    public Register getRs2() {
        return rs2;
    }

    public BackBB getTarget() {
        return target;
    }

    public Opcode getOpcode() {
        return opcode;
    }

}

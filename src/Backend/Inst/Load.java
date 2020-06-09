package Backend.Inst;

import Backend.BackModule;
import Backend.BackendOpr;
import Backend.RiscvVisitor;
import IR.Operand.Pointer;
import IR.Operand.Register;
import IR.Operand.VirtualRegister;

import java.util.Collections;
import java.util.List;

public class Load extends Inst {
    @Override
    public void accept(RiscvVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Register> getUseReg() {
        if (sr1 != null) {
            if (sr1 instanceof Pointer && ((Pointer) sr1).isGlobal())
                return Collections.emptyList();
            else return Collections.singletonList(sr1);
        }
        else if (src != null) return Collections.singletonList(BackModule.phyRegisterHashMap.get("sp"));
        else return Collections.emptyList();
    }

    @Override
    public Register getDefReg() {
        return rd;
    }

    @Override
    public void replaceUseReg(Register old_, Register new_) {
        if (sr1 == old_) sr1 = new_;
    }

    @Override
    public void replaceDefReg(Register new_) {
        rd = new_;
    }

    private Register rd;
    private BackendOpr src;
    private Register sr1;

    private int size;

    public Load(BackendOpr src, Register rd, int size) {
        this.rd = rd;
        this.src = src;
        this.size = size;
        sr1 = null;
    }


    public Load(Register sr1, Register rd, int size) {
        this.rd = rd;
        this.sr1 = sr1;
        this.size = size;
        src = null;
    }
    public Register getRd() {
        return rd;
    }

    public BackendOpr getSrc() {
        return src;
    }

    public int getSize() {
        return size;
    }

    public Register getSr1() {
        return sr1;
    }

    public void setSr1(Register sr1) {
        this.sr1 = sr1;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

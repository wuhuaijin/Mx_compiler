package Backend.Inst;

import Backend.BackendOpr;
import Backend.RiscvVisitor;
import IR.Operand.Pointer;
import IR.Operand.Register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Store extends Inst {
    @Override
    public void accept(RiscvVisitor visitor) {
        visitor.visit(this);
    }

    private BackendOpr ptr;
    private Register rd;
    private Register src;
    private Register src2;
    private int size;


    @Override
    public List<Register> getUseReg() {
        List<Register> list= new ArrayList<>();
        list.add(src);
        if (rd != null && !(rd instanceof Pointer && ((Pointer) rd).isGlobal())) list.add(rd);
        return list;
    }

    @Override
    public Register getDefReg() {
        return src2;
    }

    @Override
    public void replaceUseReg(Register old_, Register new_) {
        if (rd == old_) rd = new_;
        if (src == old_) src = new_;
    }

    @Override
    public void replaceDefReg(Register new_) {
        if (src2 != null) src2 = new_;
    }

    public Store(BackendOpr ptr, Register src, Register src2, int size) {
        this.ptr = ptr;
        this.src = src;
        this.src2 = src2;
        this.size = size;
        this.rd = null;
    }

    public Store(Register rd, Register src, Register src2, int size) {
        this.rd = rd;
        this.src = src;
        this.src2 = src2;
        this.size = size;
    }

    public BackendOpr getPtr() {
        return ptr;
    }

    public Register getSrc() {
        return src;
    }

    public Register getSrc2() {
        return src2;
    }

    public int getSize() {
        return size;
    }

    public Register getRd() {
        return rd;
    }
}

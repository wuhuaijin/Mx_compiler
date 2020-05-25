package Backend.Inst;

import Backend.BackBB;
import Backend.RiscvVisitor;
import IR.Operand.Register;

import java.util.List;

abstract public class Inst {
    private BackBB bb;
    private Inst prev, next;

    public BackBB getBb() {
        return bb;
    }

    public Inst() {
        prev = null;
        next = null;
    }

    public void setBb(BackBB bb) {
        this.bb = bb;
    }

    public Inst getPrev() {
        return prev;
    }

    public void setPrev(Inst prev) {
        this.prev = prev;
    }

    public Inst getNext() {
        return next;
    }

    public void setNext(Inst next) {
        this.next = next;
    }

    public void pushFront(Inst inst) {
        inst.setBb(getBb());
        inst.setPrev(prev);
        if (prev != null) prev.setNext(inst);
        else bb.setHead(inst);
        prev = inst;
        inst.setNext(this);
    }

    public void pushBack(Inst inst) {
        inst.setBb(getBb());
        inst.setNext(next);
        if (next != null) next.setPrev(inst);
        else bb.setTail(inst);
        next = inst;
        inst.setPrev(this);
    }

    public void delete(){
        if (getPrev() == null) {
            if (getNext() == null) {
                getBb().setTail(null);
                getBb().setHead(null);
            }
            else {
                getBb().setHead(getNext());
                getNext().setPrev(null);
            }
        }
        else if (getNext() == null) {
            getBb().setTail(getPrev());
            getPrev().setNext(null);
        }
        else {
            getNext().setPrev(getPrev());
            getPrev().setNext(getNext());
        }
    }

    abstract public void accept(RiscvVisitor visitor);

    abstract public List<Register> getUseReg();

    abstract public Register getDefReg();

    abstract public void replaceUseReg(Register old_, Register new_);

    abstract public void replaceDefReg(Register new_);



}

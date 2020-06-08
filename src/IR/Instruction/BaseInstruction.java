package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.Operand;
import IR.Operand.VirtualRegister;

import java.util.List;

abstract public class BaseInstruction {
    protected BB basicBlock;

    protected BaseInstruction prev;
    protected BaseInstruction next;

    protected boolean ifTerminal;

    public BaseInstruction(BB basicBlock, boolean ifTerminal) {
        this.basicBlock = basicBlock;
        this.ifTerminal = ifTerminal;
        this.prev = null;
        this.next = null;
    }

    public void setBasicBlock(BB basicBlock) {
        this.basicBlock = basicBlock;
    }

    public BB getBasicBlock() {
        return basicBlock;
    }

    public BaseInstruction getNext() {
        return next;
    }

    public BaseInstruction getPrev() {
        return prev;
    }

    public void setNext(BaseInstruction next) {
        this.next = next;
    }

    public void setPrev(BaseInstruction prev) {
        this.prev = prev;
    }

    public void setIfTerminal(boolean ifTerminal) {
        this.ifTerminal = ifTerminal;
    }

    public boolean isIfTerminal() {
        return ifTerminal;
    }

    public void delete() {
        if (getNext() == null && getPrev() == null) {
            getBasicBlock().setHead(null);
            getBasicBlock().setTail(null);
        }
        else if (getPrev() == null) {
            getBasicBlock().setHead(getNext());
            getNext().setPrev(null);
        }
        else if (getNext() == null) {
            getBasicBlock().setTail(getPrev());
            getPrev().setNext(null);
        }
        else {
            getPrev().setNext(getNext());
            getNext().setPrev(getPrev());
        }
    }

    public void replaceInst(BaseInstruction inst) {
        if (getPrev() != null) {
            getPrev().setNext(inst);
            inst.setPrev(getPrev());
        }
        else basicBlock.setHead(inst);
        if (getNext() != null) {
            getNext().setPrev(inst);
            inst.setNext(getNext());
        }
        else basicBlock.setTail(inst);
    }

    public void pushFront(BaseInstruction inst) {
        if (getPrev() != null) {
            getPrev().setNext(inst);
            inst.setPrev(getPrev());
        }
        inst.setNext(this);
        prev = inst;
        if (this == basicBlock.getHead()) {
            basicBlock.setHead(inst);
        }
    }

    public void pushBack(BaseInstruction inst) {
        if (getNext() != null) {
            getNext().setPrev(inst);
            inst.setNext(getNext());
        }
        inst.setPrev(this);
        next = inst;
        if (this == basicBlock.getTail()) {
            basicBlock.setTail(inst);
        }
    }

    abstract public List<Operand> getOpr();
    abstract public List<BB> getBB();
    abstract public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList);
    abstract public void accept(IRVisitor visitor);
    abstract public void replaceUseOpr(Operand _old, Operand _new);
    abstract public void replaceDefOpr(Operand _new);
    abstract public List<VirtualRegister> getUseOpr();
    abstract public VirtualRegister getDefOpr();

}

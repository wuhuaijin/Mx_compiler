package IR.Operand;

import Backend.Inst.Move;
import IR.IRVisitor;

import java.util.HashSet;
import java.util.Set;

public class Register extends Operand {

    public Register(String id) {
        super(id);
    }


    private Register ptr;

    public Register getPtr() {
        return ptr;
    }

    public void setPtr(Register ptr) {
        this.ptr = ptr;
    }


    //allocation
    public int degree;
    public String color;
    public Set<Register> adjSet = new HashSet<>();
    public Register alias;
    public Set<Move> moveSet = new HashSet<>();
    public StackAllocate spillAddr;

    public void clean(){
        adjSet.clear();
        moveSet.clear();
        alias = null;
        spillAddr = null;
    }


    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

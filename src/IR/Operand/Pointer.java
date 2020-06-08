package IR.Operand;

import IR.IRVisitor;

public class Pointer extends VirtualRegister {

    private boolean global = false;

    public Pointer(String id) {
        super(id);
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public VirtualRegister getSSAWithId(int index) {
        return new Pointer(this.getId() + "." + index);
    }

}

package IR.IrType;

import IR.IRVisitor;

abstract public class IrType {
    protected String id;

    public IrType(String id) {
        this.id = id;
    }

    public int getBytes(){
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IrType))
            return false;
        else
            return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
       return id;
    }

    abstract public void accept(IRVisitor visitor);
}

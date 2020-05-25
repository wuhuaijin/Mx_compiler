package IR.IrType;

import IR.IRVisitor;

public class IntType extends IrType {

    public IntType(String id) {
        super(id);
    }

    @Override
    public int getBytes() {
        if (id.equals("int1") || id.equals("int8")) return 1;
        else if (id.equals("int32")) return 8;
        else return 0;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}

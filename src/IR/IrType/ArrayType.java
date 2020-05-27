package IR.IrType;

import IR.IRVisitor;


public class ArrayType extends IrType {

    private IrType baseType;
    private int length;

    public ArrayType(String id, IrType baseType, int length) {
        super(id);
        this.baseType = baseType;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public IrType getBaseType() {
        return baseType;
    }

    @Override
    public int getBytes() {
        return baseType.getBytes() * length;
    }

    @Override
    public String toString() {
        return id + "[" + length + "]";
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

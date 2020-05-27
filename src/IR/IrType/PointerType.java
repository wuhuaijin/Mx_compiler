package IR.IrType;

import IR.IRVisitor;

public class PointerType extends IrType {
    private IrType pointType;

    public PointerType(String id, IrType pointType) {
        super(id);
        this.pointType = pointType;
    }

    public IrType getPointType() {
        return pointType;
    }

    @Override
    public int getBytes() {
        return 8;
    }

    @Override
    public String toString() {
        return pointType.toString() + this.id;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

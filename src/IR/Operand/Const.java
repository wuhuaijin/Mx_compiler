package IR.Operand;

import IR.IRVisitor;

public class Const extends Operand {

    private Integer value;
    public Const(Integer value) {
        super(value.toString());
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}


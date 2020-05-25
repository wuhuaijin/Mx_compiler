package IR.Operand;

import IR.IRVisitor;

public class ConstString extends Operand {

    private String value;

    public ConstString(String value){
        super(value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
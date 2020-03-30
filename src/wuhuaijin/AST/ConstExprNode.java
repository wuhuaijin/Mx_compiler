package AST;

import Helper.Location;
import SymbolTable.Type.*;

public class ConstExprNode extends ExprNode {

    private Object value;
    public enum ConstType {
        INT, BOOL, STRING, NULL
    }
    private ConstType constType;

    public ConstExprNode(Location location, Object value, ConstType constType) {
        super(location);
        this.value = value;
        this.constType = constType;
        if (constType == ConstType.INT) this.setType(new IntType());
        if (constType == ConstType.BOOL) this.setType(new BoolType());
        if (constType == ConstType.STRING) this.setType(new StringType());
        if (constType == ConstType.NULL) this.setType(new NullType());
        setIfLvalue(false);
    }

    public Object getValue() {
        return value;
    }

    public ConstType getConstType() {
        return constType;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}


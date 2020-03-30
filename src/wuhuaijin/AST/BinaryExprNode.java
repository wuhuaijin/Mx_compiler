package AST;

import Helper.Location;

public class BinaryExprNode extends ExprNode {

    public enum Op {
        mul, div, mod,
        add, sub,
        shiftleft, shiftright,
        le, leq, ge, geq,
        eq, neq,
        bitand, bitxor, bitor,
        and, or,
        assign
    }

    private Op op;
    private ExprNode ls, rs;

    public BinaryExprNode(Location location, Op op, ExprNode ls, ExprNode rs) {
        super(location);
        this.op = op;
        this.ls = ls;
        this.rs = rs;
    }

    public Op getOp() {
        return op;
    }

    public ExprNode getLs() {
        return ls;
    }

    public ExprNode getRs() {
        return rs;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}

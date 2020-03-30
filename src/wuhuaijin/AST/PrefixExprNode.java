package AST;

import Helper.Location;

public class PrefixExprNode extends ExprNode{
    public enum Op {
        addadd, subsub,
        add, sub,
        not, //!
        bitnot, //~
    }

    private PrefixExprNode.Op op;
    private ExprNode expr;

    public PrefixExprNode(Location location, PrefixExprNode.Op op, ExprNode expr) {
        super(location);
        this.op = op;
        this.expr = expr;
    }

    public Op getOp() {
        return op;
    }

    public ExprNode getExpr() {
        return expr;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

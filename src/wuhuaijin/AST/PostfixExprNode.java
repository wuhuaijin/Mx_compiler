package AST;

import Helper.Location;

public class PostfixExprNode extends ExprNode{
    public enum Op {
        add, sub
    }

    private Op op;
    private ExprNode expr;

    public PostfixExprNode(Location location, Op op, ExprNode expr) {
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

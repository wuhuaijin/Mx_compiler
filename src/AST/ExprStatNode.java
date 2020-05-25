package AST;

import Helper.Location;

public class ExprStatNode extends StatNode {
    private ExprNode expr;

    public ExprStatNode(Location location, ExprNode expr) {
        super(location);
        this.expr = expr;
    }

    public ExprNode getExpr() {
        return expr;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

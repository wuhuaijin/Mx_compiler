package AST;

import Helper.Location;

public class ReturnStatNode extends StatNode{
    private ExprNode returnExpr;

    public ReturnStatNode(Location location, ExprNode returnExpr) {
        super(location);
        this.returnExpr = returnExpr;
    }

    public ExprNode getReturnExpr() {
        return returnExpr;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

package AST;

import Helper.Location;

public class MemberExprNode extends ExprNode{

    private String id;
    private ThisExprNode thisExprNode;
    private ExprNode expr;

    public MemberExprNode(Location location, String id, ExprNode expr, ThisExprNode thisExprNode) {
        super(location);
        this.id = id;
        this.expr = expr;
        this.thisExprNode = thisExprNode;
    }

    public ExprNode getExpr() {
        return expr;
    }

    public String getId() {
        return id;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ThisExprNode getThisExprNode() {
        return thisExprNode;
    }
}

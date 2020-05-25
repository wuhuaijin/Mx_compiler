package AST;

import Helper.Location;

public class ThisExprNode extends ExprNode {

    public ThisExprNode(Location location) {
        super(location);
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }



}

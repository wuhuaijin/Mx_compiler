package AST;

import Frontend.Scope;
import Helper.Location;

public class IdExprNode extends ExprNode {
    private String id;

    public IdExprNode(Location location, String id) {
        super(location);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

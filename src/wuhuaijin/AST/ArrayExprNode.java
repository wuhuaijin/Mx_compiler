package AST;
import Helper.Location;

public class ArrayExprNode extends ExprNode {

    private ExprNode baseExpr;
    private ExprNode indexExpr;

    public ArrayExprNode(Location location, ExprNode baseExpr, ExprNode indexExpr) {
        super(location);
        this.baseExpr = baseExpr;
        this.indexExpr = indexExpr;
    }

    public ExprNode getBaseExpr() {
        return baseExpr;
    }

    public ExprNode getIndexExpr() {
        return indexExpr;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}

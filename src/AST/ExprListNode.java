package AST;

import Helper.Location;
import java.util.ArrayList;

public class ExprListNode extends Node {
    private ArrayList<ExprNode> exprList;

    public ExprListNode(Location location, ArrayList<ExprNode> exprList) {
        super(location);
        this.exprList = exprList;
    }

    public ArrayList<ExprNode> getExprList() {
        return exprList;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

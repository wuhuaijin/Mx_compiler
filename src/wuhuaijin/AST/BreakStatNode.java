package AST;

import Helper.Location;

public class BreakStatNode extends StatNode {

    public BreakStatNode(Location location) {
        super(location);
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
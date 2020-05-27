package AST;

import Helper.Location;

public class ContinueStatNode extends StatNode {
    public ContinueStatNode(Location location){
        super(location);
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

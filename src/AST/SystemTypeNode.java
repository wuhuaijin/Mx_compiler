package AST;

import Helper.Location;

public class SystemTypeNode extends TypeNode {
    public SystemTypeNode(Location location, String typeName) {
        super(location, typeName);
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

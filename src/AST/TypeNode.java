package AST;

import Helper.Location;

public class TypeNode extends Node {

    private String typeName;

    public TypeNode(Location location, String typeName) {
        super(location);
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeNode)
            return ((TypeNode) obj).typeName.equals(typeName);
        else
            return false;
    }
}

package AST;

import Helper.Location;

public class ArrayTypeNode extends TypeNode{
    private TypeNode baseType;
    private int dimension;

    public ArrayTypeNode(Location location, TypeNode baseType) {
        super(location, baseType.getTypeName());
        if (baseType instanceof ArrayTypeNode){
            this.baseType = ((ArrayTypeNode) baseType).baseType;
            this.dimension = ((ArrayTypeNode) baseType).dimension + 1;
        }
        else {
            this.baseType = baseType;
            dimension = 1;
        }
    }

    public int getDimension() {
        return dimension;
    }

    public TypeNode getBaseType() {
        return baseType;
    }

    @Override
    public int hashCode() {
        return baseType.hashCode() + dimension * 101;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayTypeNode) {
            return baseType.equals((ArrayTypeNode) ((ArrayTypeNode) obj).baseType) && dimension == ((ArrayTypeNode) obj).dimension;
        }
        return false;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

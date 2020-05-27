package AST;
import Helper.Location;
import SymbolTable.Type.ClassType;

import java.util.ArrayList;

public class NewExprNode extends ExprNode{

    private TypeNode typeName;
    private ArrayList<ExprNode> dimList;
    private int dimension;
    private ClassType classType;

    public NewExprNode(Location location, TypeNode typeName, ArrayList<ExprNode> dimList, int dimension) {
        super(location);
        this.typeName = typeName;
        this.dimList = dimList;
        this.dimension = dimension;
    }

    public TypeNode getTypeName() {
        return typeName;
    }

    public ArrayList<ExprNode> getDimList() {
        return dimList;
    }

    public int getDimension() {
        return dimension;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

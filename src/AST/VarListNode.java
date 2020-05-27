package AST;

import Helper.Location;

import java.util.ArrayList;

public class VarListNode extends DefNode {
    private ArrayList<VarDefNode> varDefList;

    public VarListNode(Location location, ArrayList<VarDefNode> varDefList) {
        super(location);
        this.varDefList = varDefList;
    }

    public ArrayList<VarDefNode> getVarDefList() {
        return varDefList;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

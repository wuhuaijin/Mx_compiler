package AST;

import Helper.Location;
import java.util.ArrayList;

public class ParaListNode extends Node{
    private ArrayList<VarDefNode> varList;

    public ParaListNode(Location location, ArrayList<VarDefNode> varList) {
        super(location);
        this.varList = varList;
    }

    public ArrayList<VarDefNode> getVarList() {
        return varList;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

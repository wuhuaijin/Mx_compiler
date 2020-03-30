package AST;

import Helper.Location;
import SymbolTable.Symbol.VarSymbol;

import java.util.ArrayList;

public class VardefStatNode extends StatNode{
    private ArrayList<VarDefNode> varDefList;

    public VardefStatNode(Location location, ArrayList<VarDefNode> varDefList) {
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

package AST;

import Helper.Location;
import java.util.ArrayList;

public class ProgNode extends Node{

    private ArrayList<DefNode> defList;

    public ProgNode(Location location, ArrayList<DefNode> defList) {
        super(location);
        this.defList = defList;
    }

    public ArrayList<DefNode> getDefList() {
        return defList;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

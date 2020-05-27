package AST;

import Helper.Location;

import java.util.ArrayList;

public class BlockStatNode extends StatNode {
    private ArrayList<StatNode> statList;

    public BlockStatNode(Location location, ArrayList<StatNode> statList){
        super(location);
        this.statList = statList;
    }
    public ArrayList<StatNode> getStat() {
        return statList;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

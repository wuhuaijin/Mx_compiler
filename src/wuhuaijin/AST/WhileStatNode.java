package AST;
import Helper.Location;
import java.util.ArrayList;

public class WhileStatNode extends StatNode{

    private ExprNode cond;
    private StatNode statList;

    public WhileStatNode(Location location, ExprNode cond, StatNode statList) {
        super(location);
        this.cond = cond;
        this.statList = statList;
    }

    public ExprNode getCond() {
        return cond;
    }

    public StatNode getStatList() {
        return statList;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

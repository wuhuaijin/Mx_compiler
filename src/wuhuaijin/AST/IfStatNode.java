package AST;

import Helper.Location;

public class IfStatNode extends StatNode {

    private ExprNode cond;

    private StatNode mainContent;
    private int flag;
    private StatNode elseContent;



    public IfStatNode(Location location, ExprNode cond, StatNode mainContent, int flag, StatNode elseContent) {
        super(location);
        this.cond = cond;
        this.mainContent = mainContent;
        this.flag = flag;
        this.elseContent = elseContent;
    }

    public int getFlag() {
        return flag;
    }

    public ExprNode getCond() {
        return cond;
    }

    public StatNode getMainContent() {
        return mainContent;

    }

    public StatNode getElseContent() {
        return elseContent;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }



}

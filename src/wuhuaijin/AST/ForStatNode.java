package AST;


import Helper.Location;

public class ForStatNode extends StatNode{

    private ExprNode init, cond, step;
    private StatNode content;

    public  ForStatNode(Location location, ExprNode init, ExprNode cond, ExprNode step, StatNode content){
        super(location);
        this.init = init;
        this.cond = cond;
        this.step = step;
        this.content = content;
    }

    public ExprNode getInit(){
        return init;
    }

    public ExprNode getCond(){
        return cond;
    }

    public ExprNode getStep(){
        return step;
    }

    public StatNode getContent(){
        return content;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }


}

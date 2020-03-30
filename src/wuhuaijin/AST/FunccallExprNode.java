package AST;

import Helper.Location;
import java.util.ArrayList;

public class FunccallExprNode extends ExprNode{
    private ExprNode funcName;
    private ArrayList<ExprNode> paraList;

    public FunccallExprNode(Location location, ExprNode funcName, ArrayList<ExprNode> paraList) {
        super(location);
        this.funcName = funcName;
        this.paraList = paraList;
    }

    public ExprNode getFuncName() {
        return funcName;
    }

    public ArrayList<ExprNode> getParaList() {
        return paraList;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}

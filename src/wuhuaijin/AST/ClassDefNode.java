package AST;

import Helper.Location;
import java.util.ArrayList;

public class ClassDefNode extends DefNode{

    private String className;
    private ArrayList<VarDefNode> varList;
    private ArrayList<FuncDefNode> funcList;
    private FuncDefNode creator;

    public ClassDefNode(Location location, String className, ArrayList<VarDefNode> varList, ArrayList<FuncDefNode> funcList,
                        FuncDefNode creator) {
        super(location);
        this.className = className;
        this.varList = varList;
        this.funcList = funcList;
        this.creator = creator;
    }

    public String getClassName() {
        return className;
    }

    public ArrayList<VarDefNode> getVarList() {
        return varList;
    }

    public ArrayList<FuncDefNode> getFuncList() {
        return funcList;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public FuncDefNode getCreator() {
        return creator;
    }

}

package AST;

import Helper.Location;
import SymbolTable.Symbol.FuncSymbol;
import SymbolTable.Symbol.Symbol;
import SymbolTable.Symbol.VarSymbol;
import SymbolTable.Type.*;

import java.util.ArrayList;

public class FuncDefNode extends DefNode {

    private String funcName;
    private TypeNode type;
    private ArrayList<VarDefNode> paraList;
    private StatNode statNode;
    private String classname = "";
    private FuncSymbol symbol;
    private Type typetype;

    public FuncDefNode(Location location, String funcName, TypeNode type, ArrayList<VarDefNode> paraList, StatNode statNode) {
        super(location);
        this.funcName = funcName;
        this.type = type;
        this.paraList = paraList;
        this.statNode = statNode;
        this.symbol = null;
    }

    public String getFuncName() {
        return funcName;
    }

    public TypeNode getType() {
        return type;
    }

    public ArrayList<VarDefNode> getParaList() {
        return paraList;
    }

    public StatNode getStatNode() {
        return statNode;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }

    public Type getTypetype() {
        return typetype;
    }

    public void setTypetype(Type typetype) {
        this.typetype = typetype;
    }

    public FuncSymbol getFuncSymbol(FuncSymbol.SymbolType symbolType) {
        ArrayList<VarSymbol> para = new ArrayList<>();
        for (var i : paraList) {
            para.add(i.getVarSymbol(VarSymbol.SymbolType.paralist));
        }
        FuncSymbol funcSymbol = new FuncSymbol(funcName, getLocation(), symbolType, type, para, statNode);
        this.symbol = funcSymbol;
        return funcSymbol;
    }

    public FuncSymbol getSymbol() {
        return symbol;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

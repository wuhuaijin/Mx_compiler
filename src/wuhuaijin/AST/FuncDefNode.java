package AST;

import Helper.Location;
import SymbolTable.Symbol.FuncSymbol;
import SymbolTable.Symbol.VarSymbol;

import java.util.ArrayList;

public class FuncDefNode extends DefNode {

    private String funcName;
    private TypeNode type;
    private ArrayList<VarDefNode> paraList;
    private StatNode statNode;

    public FuncDefNode(Location location, String funcName, TypeNode type, ArrayList<VarDefNode> paraList, StatNode statNode) {
        super(location);
        this.funcName = funcName;
        this.type = type;
        this.paraList = paraList;
        this.statNode = statNode;
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

    public FuncSymbol getFuncSymol(FuncSymbol.SymbolType symbolType) {
        ArrayList<VarSymbol> para = new ArrayList<>();
        for (var i : paraList) {
            para.add(i.getVarSymbol(VarSymbol.SymbolType.paralist));
        }
        return new FuncSymbol(funcName, getLocation(), symbolType, type, para, statNode);
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

package AST;

import Helper.Location;
import SymbolTable.Symbol.Symbol;
import SymbolTable.Symbol.VarSymbol;

public class VarDefNode extends DefNode{
    private TypeNode type;
    private String varName;
    private ExprNode assignExpr;
    private Symbol symbol;

    public VarDefNode(Location location, TypeNode type, String varName, ExprNode assignExpr) {
        super(location);
        this.type = type;
        this.varName = varName;
        this.assignExpr = assignExpr;
    }

    public TypeNode getType() {
        return type;
    }

    public String getVarName() {
        return varName;
    }

    public ExprNode getAssignExpr() {
        return assignExpr;
    }

    public VarSymbol getVarSymbol(VarSymbol.SymbolType symbolType) {
        VarSymbol var = new VarSymbol(varName, getLocation(), symbolType, type, assignExpr);
        symbol = var;
        return var;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}

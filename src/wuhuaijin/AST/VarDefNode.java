package AST;

import Helper.Location;
import SymbolTable.Symbol.VarSymbol;

public class VarDefNode extends DefNode{
    private TypeNode type;
    private String varName;
    private ExprNode assignExpr;

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
        return new VarSymbol(varName, getLocation(), symbolType, type, assignExpr);
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}

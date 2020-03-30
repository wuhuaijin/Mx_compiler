package SymbolTable.Symbol;

import AST.*;
import Helper.Location;

public class VarSymbol extends Symbol {
    public enum SymbolType {
        global, local, paralist, member
    }

    private SymbolType symbolType;
    private TypeNode type;
    private ExprNode assignExpr;

    public VarSymbol(String id, Location location, SymbolType symbolType,
                     TypeNode type, ExprNode assignExpr) {
        super(id, location);
        this.symbolType = symbolType;
        this.type = type;
        this.assignExpr = assignExpr;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public TypeNode getType() {
        return type;
    }

    public ExprNode getAssignExpr() {
        return assignExpr;
    }
}


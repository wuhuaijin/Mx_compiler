package SymbolTable.Symbol;

import AST.*;
import Helper.Location;
import IR.Operand.Operand;

public class VarSymbol extends Symbol {
    public enum SymbolType {
        global, local, paralist, member
    }

    private int offset;
    private Operand storage;

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

    public int getOffset() {
        return offset;
    }

    public Operand getStorage() {
        return storage;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setStorage(Operand storage) {
        this.storage = storage;
    }
}



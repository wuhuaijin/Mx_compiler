package SymbolTable.Symbol;

import AST.*;
import Helper.Location;
import IR.Function;

import java.util.ArrayList;

public class FuncSymbol extends Symbol{

    public enum SymbolType {
        func, method, creator
    }

    private SymbolType symbolType;
    private TypeNode returnType;
    private ArrayList<VarSymbol> paraList;
    private StatNode bodyStat;

    private Function function;


    public FuncSymbol(String id, Location location, SymbolType symbolType, TypeNode returnType,
                      ArrayList<VarSymbol> paraList, StatNode bodyStat) {
        super(id, location);
        this.symbolType = symbolType;
        this.returnType = returnType;
        this.paraList = paraList;
        this.bodyStat = bodyStat;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public TypeNode getReturnType() {
        return returnType;
    }

    public ArrayList<VarSymbol> getParaList() {
        return paraList;
    }

    public void setBodyStat(StatNode bodyStat) {
        this.bodyStat = bodyStat;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}

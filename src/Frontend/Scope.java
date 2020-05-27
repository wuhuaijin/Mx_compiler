package Frontend;

import AST.*;
import SymbolTable.*;
import Helper.*;
import SymbolTable.Symbol.*;
import SymbolTable.Type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scope {

    public enum ScopeType {
        loopScope, progScope, funcScope, blockScope, classScope
    }

    private ScopeType scopeType;
    private Scope fatherScope;
    private ArrayList<Scope> childrenScope;

    private Map<String, Symbol> symbols;
    private TypeNode funcReturnType;
    private boolean ifHasReturnStat;
    private Type classType;


    public Scope(ScopeType scopeType, Scope fatherScope, TypeNode funcReturnType, Type classType) {
        this.scopeType = scopeType;
        this.fatherScope = fatherScope;
        if (this.fatherScope != null) fatherScope.childrenScope.add(this);
        this.childrenScope = new ArrayList<>();
        this.symbols = new HashMap<>();
        this.funcReturnType = funcReturnType;
        this.classType = classType;
        ifHasReturnStat = false;
    }

    public void setReturnStat(boolean ifHasReturnStat) {
        this.ifHasReturnStat = ifHasReturnStat;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public Scope getFatherScope() {
        return fatherScope;
    }

    public ArrayList<Scope> getChildrenScope() {
        return childrenScope;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public TypeNode getFuncReturnType() {
        return funcReturnType;
    }

    public Type getClassType() {
        return classType;
    }

    // 内置函数
    public void sysFunc() {
        Location location = new Location(0, 0);
        ArrayList<VarSymbol> paraList;
        FuncSymbol func;
        VarSymbol varSymbol;

        //void print(string str);
        paraList = new ArrayList<>();
        varSymbol = new VarSymbol("str", location, VarSymbol.SymbolType.paralist,
                new SystemTypeNode(location, "string"), null);
        paraList.add(varSymbol);
        func = new FuncSymbol("print", location, FuncSymbol.SymbolType.func,
                new SystemTypeNode(location, "void"), paraList, null);
        symbols.put("print", func);

        //void println(string str);
        paraList = new ArrayList<>();
        varSymbol = new VarSymbol("str", location, VarSymbol.SymbolType.paralist,
                new SystemTypeNode(location, "string"), null);
        paraList.add(varSymbol);
        func = new FuncSymbol("println", location, FuncSymbol.SymbolType.func,
                new SystemTypeNode(location, "void"), paraList, null);
        symbols.put("println", func);

        //void printInt(int n);
        paraList = new ArrayList<>();
        varSymbol = new VarSymbol("n", location, VarSymbol.SymbolType.paralist,
                new SystemTypeNode(location, "int"), null);
        paraList.add(varSymbol);
        func = new FuncSymbol("printInt", location, FuncSymbol.SymbolType.func,
                new SystemTypeNode(location, "void"), paraList, null);
        symbols.put("printInt", func);

        //void printlnInt(int n);
        paraList = new ArrayList<>();
        varSymbol = new VarSymbol("n", location, VarSymbol.SymbolType.paralist,
                new SystemTypeNode(location, "int"), null);
        paraList.add(varSymbol);
        func = new FuncSymbol("printlnInt", location, FuncSymbol.SymbolType.func,
                new SystemTypeNode(location, "void"), paraList, null);
        symbols.put("printlnInt", func);

        //string getString();
        paraList = new ArrayList<>();
        func = new FuncSymbol("getString", location, FuncSymbol.SymbolType.func,
                new SystemTypeNode(location, "string"), paraList, null);
        symbols.put("getString", func);

        //int getInt();
        paraList = new ArrayList<>();
        func = new FuncSymbol("getInt", location, FuncSymbol.SymbolType.func,
                new SystemTypeNode(location, "int"), paraList, null);
        symbols.put("getInt", func);

        //string toString(int i);
        paraList = new ArrayList<>();
        varSymbol = new VarSymbol("i", location, VarSymbol.SymbolType.paralist,
                new SystemTypeNode(location, "int"), null);
        paraList.add(varSymbol);
        func = new FuncSymbol("toString", location, FuncSymbol.SymbolType.func,
                new SystemTypeNode(location, "string"), paraList, null);
        symbols.put("toString", func);

    }

    public void declareSymbol(Location location, Symbol symbol) throws SyntaxException{
        assert symbol != null;
        if (symbols.containsKey(symbol.getId())){
            throw new SyntaxException(location, "duplicate declare !");
        }
        else symbols.put(symbol.getId(), symbol);
    }

    public Symbol getSymbol(String id) {
        if (symbols.containsKey(id))
            return symbols.get(id);
        else if (fatherScope != null)
            return fatherScope.getSymbol(id);
        else return null;
    }

    public VarSymbol getVarSymbol(String id) {
        if (symbols.containsKey(id) && (symbols.get(id) instanceof VarSymbol))
            return (VarSymbol) symbols.get(id);
        else if (fatherScope != null)
            return fatherScope.getVarSymbol(id);
        else return null;
    }

    public FuncSymbol getFuncSymbol(String id) {
        if (symbols.containsKey(id) && (symbols.get(id) instanceof FuncSymbol))
            return (FuncSymbol) symbols.get(id);
        else if (fatherScope != null)
            return fatherScope.getFuncSymbol(id);
        else return null;
    }

    public boolean inFuncScope() {
        if (scopeType == ScopeType.funcScope) return true;
        else if (scopeType == scopeType.progScope) return false;
        else return fatherScope.inFuncScope();
    }

    public boolean inClassScope() {
        if (scopeType == ScopeType.classScope) return true;
        else if (scopeType == scopeType.progScope) return false;
        else return fatherScope.inClassScope();
    }

    public boolean inLoopScope() {
        if (scopeType == ScopeType.loopScope) return true;
        else if (scopeType == scopeType.progScope) return false;
        else return fatherScope.inLoopScope();
    }

    public boolean hasReturnStat(){
        if (ifHasReturnStat) return true;
        else if (getChildrenScope() != null){
            for (var i : getChildrenScope()) {
                if (i.hasReturnStat()) return true;
            }
        }
        return false;
    }
    //method??


}




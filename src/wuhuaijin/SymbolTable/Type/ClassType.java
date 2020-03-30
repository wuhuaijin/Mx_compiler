package SymbolTable.Type;

import SymbolTable.Symbol.FuncSymbol;
import SymbolTable.Symbol.VarSymbol;

import java.util.ArrayList;

public class ClassType extends Type {
    private ArrayList<VarSymbol> members;
    private ArrayList<FuncSymbol> methods;
    private FuncSymbol creator;

    public ClassType(String id, ArrayList<VarSymbol> members, ArrayList<FuncSymbol> methods, FuncSymbol creator) {
        super(id);
        this.members = members;
        this.methods = methods;
        this.creator = creator;
    }

    public ArrayList<VarSymbol> getMembers() {
        return members;
    }

    public ArrayList<FuncSymbol> getMethods() {
        return methods;
    }

    public FuncSymbol getCreator() {
        return creator;
    }

    public boolean hasMember(String id) {
        for (var i : members){
            if (i.getId().equals(id)) return true;
        }
        return false;
    }

    public boolean hasMethod(String id) {
        for (var i : methods){
            if (i.getId().equals(id)) return true;
        }
        return false;
    }

    public VarSymbol getMember(String id) {
        for (var i : members) {
            if (i.getId().equals(id)) return i;
        }
        return null;
    }

    public FuncSymbol getMethod(String id) {
        for (var i : methods) {
            if (i.getId().equals(id)) return i;
        }
        return null;
    }



    //todo with classsymbol
}

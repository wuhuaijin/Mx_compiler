package SymbolTable.Type;

import java.util.ArrayList;
import AST.*;
import SymbolTable.Symbol.*;
import Helper.Location;

public class StringType extends Type {

    private ArrayList<FuncSymbol> systemMethods;

    public StringType() {
        super("string");

        //system func
        systemMethods = new ArrayList<>();
        Location location = new Location(0, 0);
        ArrayList<VarSymbol> paraList;
        FuncSymbol func;

        //int length();
        paraList = new ArrayList<>();
        func = new FuncSymbol("length", location, FuncSymbol.SymbolType.method,
                new SystemTypeNode(location, "int"), paraList, null);
        systemMethods.add(func);

        //string substring(int left, int right);
        paraList = new ArrayList<>();
        VarSymbol var1 = new VarSymbol("left ", location, VarSymbol.SymbolType.paralist,
                new SystemTypeNode(location, "int"), null);
        VarSymbol var2 = new VarSymbol("right ", location, VarSymbol.SymbolType.paralist,
                new SystemTypeNode(location, "int"), null);
        paraList.add(var1);
        paraList.add(var2);
        func = new FuncSymbol("substring", location, FuncSymbol.SymbolType.method,
                new SystemTypeNode(location, "string"), paraList, null);
        systemMethods.add(func);

        //int parseInt();
        paraList = new ArrayList<>();
        func = new FuncSymbol("parseInt", location, FuncSymbol.SymbolType.method,
                new SystemTypeNode(location, "int"), paraList, null);
        systemMethods.add(func);

        //int ord(int pos);
        paraList = new ArrayList<>();
        VarSymbol var3 = new VarSymbol("pos ", location, VarSymbol.SymbolType.paralist,
                new SystemTypeNode(location, "int"), null);
        paraList.add(var3);
        func = new FuncSymbol("ord", location, FuncSymbol.SymbolType.method,
                new SystemTypeNode(location, "int"), paraList, null);
        systemMethods.add(func);

    }

    public ArrayList<FuncSymbol> getSystemMethods() {
        return systemMethods;
    }

    public boolean hasMethod(String id) {
        for (var i : systemMethods){
            if (i.getId().equals(id)) return true;
        }
        return false;
    }

    public FuncSymbol getMethod(String id) {
        for (var i : systemMethods) {
            if (i.getId().equals(id)) return i;
        }
        return null;
    }
}

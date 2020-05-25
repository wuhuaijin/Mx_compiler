package SymbolTable.Type;

import AST.SystemTypeNode;
import AST.TypeNode;
import Helper.Location;
import SymbolTable.Symbol.*;

import java.util.ArrayList;

public class ArrayType extends Type {

    private Type baseType;
    private int dimemsion;

    static public ArrayList<FuncSymbol> systemMethods = new ArrayList<>();


    public ArrayType(Type baseType, int dimemsion) {
        super(baseType.getId());
        this.baseType = baseType;
        this.dimemsion = dimemsion;
    }

    public int getDimemsion() {
        return dimemsion;
    }


    public ArrayList<FuncSymbol> getSystemMethods() {
        return systemMethods;
    }

    public Type getBaseType() {
        return baseType;
    }

    static public void addSystemMethods() {

        Location location = new Location(0, 0);
        ArrayList<VarSymbol> paraList;
        FuncSymbol func;

        //int size();
        paraList = new ArrayList<>();
        func = new FuncSymbol("size", location, FuncSymbol.SymbolType.method,
                new SystemTypeNode(location, "int"), paraList, null);
        systemMethods.add(func);
    }

    public boolean hasMethod(String id) {
        for (var i : systemMethods){
            if (i.getId().equals(id)) return true;
        }
        return false;
    }

    static public FuncSymbol getMethod(String id) {
        for (var i : systemMethods) {
            if (i.getId().equals(id)) return i;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
//        System.out.println("!!!!!?");
//        if (obj instanceof ArrayType) {
//            System.out.println("kuaile" + (baseType instanceof ArrayType));
//            System.out.println(baseType.equals(((ArrayType) obj).baseType) + "?");
//            System.out.println(baseType.getId());
//            System.out.println(((ArrayType) obj).getBaseType().getId());
//            System.out.println(dimemsion);
//            System.out.println(((ArrayType) obj).dimemsion);
//        }

        if (obj instanceof ArrayType)
            return baseType.equals(((ArrayType) obj).baseType) && dimemsion == ((ArrayType) obj).dimemsion;
        else
            return false;
    }
}

package SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

import AST.*;
import SymbolTable.Type.*;
import Helper.*;


public class TypeTable {
    private Map<TypeNode, Type> typeTable;

    public TypeTable() {
        typeTable = new HashMap<>();
        Location location = new Location(0,0);
        //system type
        typeTable.put(new TypeNode(location, "void"), new VoidType());
        typeTable.put(new TypeNode(location, "string"), new StringType());
        typeTable.put(new TypeNode(location, "int"), new IntType());
        typeTable.put(new TypeNode(location, "bool"), new BoolType());
    }

    public Map<TypeNode, Type> getTypeTable() {
        return typeTable;
    }

    public Type getType(TypeNode node){
        if (node instanceof ArrayTypeNode) {
            TypeNode baseType = ((ArrayTypeNode) node).getBaseType();
            int dimension = ((ArrayTypeNode) node).getDimension();
            return new ArrayType(typeTable.get(baseType), dimension);
        }
        else
            return typeTable.get(node);
    }

    public boolean hasType(TypeNode node){
//        System.out.println(node.getTypeName());
//        System.out.println(typeTable.containsKey(node));
//        for (var i: typeTable.keySet()) {
//            System.out.println(i.getTypeName());
//        }
        return typeTable.containsKey(node);
    }

    public Type getType(String str) {
        return typeTable.get(new TypeNode(new Location(0,0), str));
    }

    public void put(TypeNode node, Type type) throws SyntaxException {
        if (hasType(node)) throw new SyntaxException(node.getLocation(), "has a type!");
        else typeTable.put(node, type);
    }
}
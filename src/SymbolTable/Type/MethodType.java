package SymbolTable.Type;

public class MethodType extends Type{
    private Type type;

    public MethodType(String id, Type type) {
        super(id);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}

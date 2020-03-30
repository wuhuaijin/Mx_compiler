package SymbolTable.Type;

abstract public class Type {

    private  String id;

    public Type(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static boolean notEqualType(Type left, Type right) {
//        if (left == null) System.out.println("fuck");
//        System.out.println(left.getId());
//        System.out.println(right.getId());
        if (left instanceof ArrayType || left instanceof ClassType) {
            if (right instanceof NullType)
                return false;
            else
                return !left.equals(right);
        } else
            return !left.equals(right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Type)
            return getId().equals(((Type) obj).getId());
        else
            return false;
    }
}

package IR.Operand;

import IR.IRVisitor;

public class Operand {

    private String id;

    public Operand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //    @Override
//    public int hashCode() {
//        return .hashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null || getClass() != obj.getClass()) return false;
//        else return this.toString().equals(obj.toString());
//    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}

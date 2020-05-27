package IR.IrType;

import IR.IRVisitor;

import java.util.ArrayList;

public class ClassType extends IrType {

    ArrayList<IrType> memberList;

    public ClassType(String id, ArrayList<IrType> memberList) {
        super(id);
        this.memberList = memberList;
    }

    public ArrayList<IrType> getMemberList() {
        return memberList;
    }

    @Override
    public int getBytes() {
        int total = 0;
        for (var i : memberList) {
            total += i.getBytes();
        }
        return total;
    }

    @Override
    public String toString() {
        return "%" + id;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}

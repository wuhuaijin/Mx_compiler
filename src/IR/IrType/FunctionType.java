//package IR.IrType;
//
//import IR.IRVisitor;
//
//import java.util.ArrayList;
//
//public class FunctionType extends IrType {
//    IrType retType;
//
//    ArrayList<IrType> paraList;
//
//    public FunctionType(String id, IrType retType, ArrayList<IrType> paraList) {
//        super(id);
//        this.retType = retType;
//        this.paraList = paraList;
//    }
//
//    public ArrayList<IrType> getParaList() {
//        return paraList;
//    }
//
//    public IrType getRetType() {
//        return retType;
//    }
//
//    @Override
//    public void accept(IRVisitor visitor) {
//        visitor.visit(this);
//    }
//}

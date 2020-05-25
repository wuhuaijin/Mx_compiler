package IR;

import IR.Instruction.*;
import IR.IrType.*;
import IR.Operand.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public interface IRVisitor {
    //Module
    void visit(Module module);

    //Function
    void visit(Function function);

    //BB
    void visit(BB block);

    // ------ Instruction ------
    void visit(Allocate inst);
    void visit(UnaryOp inst);
    void visit(BinaryOp inst);
    void visit(Branch inst);
    void visit(Call inst);
    void visit(Move inst);
    void visit(Jump inst);
    void visit(Load inst);
    void visit(PhiNode inst);
    void visit(Return inst);
    void visit(Store inst);

    //Type
    void visit(ArrayType type);
    void visit(ClassType type);
//    void visit(FunctionType type);
    void visit(IntType type);
    void visit(PointerType type);
//    void visit(VoidType type);

    //Operand
    void visit(Operand operand);
    void visit(Const const_);
    void visit(Pointer pointer);
    void visit(Int32 int32);
    void visit(ConstString constString);
    void visit(Register register);


}
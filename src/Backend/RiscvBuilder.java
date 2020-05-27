package Backend;

import Backend.Inst.*;
import IR.BB;
import IR.Function;
import IR.IRVisitor;
import IR.Instruction.*;
import IR.Instruction.Branch;
import IR.Instruction.Call;
import IR.Instruction.Jump;
import IR.Instruction.Load;
import IR.Instruction.Move;
import IR.Instruction.Return;
import IR.Instruction.Store;
import IR.IrType.ArrayType;
import IR.IrType.ClassType;
import IR.IrType.IntType;
import IR.IrType.PointerType;
import IR.Module;
import IR.Operand.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiscvBuilder implements IRVisitor {

    public static String[] calleeSaveReg = {"s0","s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11"};

    private BackBB curbb;
    private BackFunction curFunc;
    private BackModule module = new BackModule();

    private Map<BB,BackBB> bbTobb = new HashMap<>();
    private Map<Function, BackFunction> funcTofunc = new HashMap<>();
    private Map<String, VirtualRegister> calleeMap = new HashMap<>();

    public static BackFunction mallocFunction = new BackFunction("malloc", true);


    public BackModule getModule() {
        return module;
    }

    @Override
    public void visit(Module mo1) {
        this.module.setGlobalVarList(mo1.getGlobalVars());
        this.module.setStringList(mo1.getConstStrings());

        for (var func : mo1.getFuncs()) {
            BackFunction a = new BackFunction(func.getFuncName(), func.isSystem());
            funcTofunc.put(func, a);
            if (!func.isSystem()) {
                func.setPreOrderBBList();
                func.getPreOrderBBList().forEach(i->bbTobb.put(i, new BackBB(i.getLable().toString())));
            }
        }
        for (var func : mo1.getFuncs()) {
            if (!func.isSystem()) visit(func);
        }

    }

    @Override
    public void visit(Function function) {
        curFunc = funcTofunc.get(function);
        BackFunction backFunction = funcTofunc.get(function);
        curbb = bbTobb.get(function.getInBB());
        module.getFunctionList().add(backFunction);
        curFunc.setInbb(curbb);


        for (var i : module.getCalleeSaveRegisterName()) {
            var localReg = new VirtualRegister("backup." + i);
            calleeMap.put(i, localReg);
            curbb.addInst(new Backend.Inst.Move(localReg, module.getPhyRegisterHashMap().get(i)));
        }
        var ra = new VirtualRegister("backup.ra");
        curbb.addInst(new Backend.Inst.Move(ra, module.getPhyRegisterHashMap().get("ra")));
        calleeMap.put("ra", ra);

        if (function.getObj() != null) function.getParas().add(0, function.getObj());
        int k = Integer.min(function.getParas().size(), 8);
        for (int i = 0; i < k; ++i) {
            curbb.addInst(new Backend.Inst.Move((Register) function.getParas().get(i), module.getPhyRegisterHashMap().get("a" + i)));
        }
        for (int i = 8; i < function.getParas().size(); ++i) {
            curbb.addInst(new Backend.Inst.Load(new StackAllocate(curFunc, true, 4*(i-8)), ((Register) function.getParas().get(i)), 4));
        }

        function.getPreOrderBBList().forEach(this::visit);
        backFunction.setOutbb(bbTobb.get(function.getOutBB()));
        backFunction.makeBBList();

    }

    public Register toReg(Operand opr) {
        if (opr instanceof ConstString) {
            VirtualRegister str = new VirtualRegister("str");
            curbb.addInst(new LA(str, (ConstString) opr));
            return str;
        }
        else if (opr instanceof Const) {
            VirtualRegister imm = new VirtualRegister("imm");
            curbb.addInst(new LI(imm, (Const) opr));
            return imm;
        }
        else return (Register) opr;
    }

    @Override
    public void visit(BB block) {
        curbb = bbTobb.get(block);
        for (var ins = block.getHead(); ins != null; ins = ins.getNext()) {
            ins.accept(this);
        }
    }

    @Override
    public void visit(Allocate inst) {
        curbb.addInst(new Backend.Inst.Move(module.getPhyRegisterHashMap().get("a0"), toReg(inst.getSize())));
        curbb.addInst(new Backend.Inst.Call(mallocFunction));
        curbb.addInst(new Backend.Inst.Move((Register)inst.getPointer(), module.getPhyRegisterHashMap().get("a0")));
    }

    @Override
    public void visit(UnaryOp inst) {
        Register rd = ((Register) inst.getResult());
        Register rs1 = toReg(inst.getRs());
        switch (inst.getOp()) {
            case NEG:{
                curbb.addInst(new RegAction(module.getPhyRegisterHashMap().get("zero"), rs1, rd, RegAction.Op.SUB));
                break;
            }
            case NOT: {
                curbb.addInst(new ImmAction(rs1, rd, ImmAction.Op.XORI, new Const(-1)));
                break;
            }
        }
    }

    @Override
    public void visit(BinaryOp inst) {
        Register rd = toReg(inst.getResult());
        Operand rs1 = inst.getLs();
        Operand rs2 = inst.getRs();
        ImmAction.Op op_I = null;
        RegAction.Op op_R = null;
        switch (inst.getOp()) {
            case add:
                op_I = ImmAction.Op.ADDI;
                op_R = RegAction.Op.ADD;
                break;
            case sub:
                op_I = ImmAction.Op.ADDI;
                op_R = RegAction.Op.SUB;
                break;
            case mul:
                op_R = RegAction.Op.MUL;
                break;
            case div:
                op_R = RegAction.Op.DIV;
                break;
            case and:
                op_I = ImmAction.Op.ANDI;
                op_R = RegAction.Op.AND;
                break;
            case or:
                op_I = ImmAction.Op.ORI;
                op_R = RegAction.Op.OR;
                break;
            case xor:
                op_I = ImmAction.Op.XORI;
                op_R = RegAction.Op.OR;
                break;
            case mod:
                op_R = RegAction.Op.REM;
                break;
            case shl:
                op_I = ImmAction.Op.SLLI;
                op_R = RegAction.Op.SLL;
                break;
            case shr:
                op_I = ImmAction.Op.SRAI;
                op_R = RegAction.Op.SRA;
                break;
            default:
                break;
        }
//        System.out.println(inst.getOp().toString());
        switch (inst.getOp()) {
            case and:
            case or:
            case xor:
            case add: {
                if (rs1 instanceof Const && rs2 instanceof Const) {
                    if (inst.getOp() == BinaryOp.Op.add)
                        curbb.addInst(new LI((Register) rd, new Const(((Const) rs1).getValue() + ((Const) rs2).getValue())));
                    break;
                }
                if (rs1 instanceof Const && !(rs2 instanceof Const)) {
                    Operand tmp = rs1;
                    rs1 = rs2;
                    rs2 = tmp;
                }
                if (rs2 instanceof Const) {
//                    System.out.println(rs1);
                    curbb.addInst(new ImmAction(toReg(rs1), rd, op_I, (Const) rs2));
                } else {
                    curbb.addInst(new RegAction(toReg(rs1), (Register) rs2, rd, op_R));
                }
                break;
            }
            case sub: {
                if (rs2 instanceof Const) {
                    int rs2Imm = -((Const) rs2).getValue();
                    curbb.addInst(new ImmAction(toReg(rs1), rd, op_I, new Const(rs2Imm)));
                } else {
                    curbb.addInst(new RegAction(toReg(rs1), (Register) rs2, rd, op_R));
                }
                break;
            }
            case mul:
                if (rs1 instanceof Const && rs2 instanceof Const)
                    curbb.addInst(new LI((Register) rd, new Const(((Const) rs1).getValue() * ((Const) rs2).getValue())));
                else curbb.addInst(new RegAction(toReg(rs1), toReg(rs2), rd, op_R));
                break;
            case div:
            case mod:
                curbb.addInst(new RegAction(toReg(rs1), toReg(rs2), rd, op_R));
                break;
            case shl:
            case shr: {
                if (rs2 instanceof Const) {
                    curbb.addInst(new ImmAction(toReg(rs1), rd, op_I, (Const) rs2));
                } else {
                    curbb.addInst(new RegAction(toReg(rs1), (Register) rs2, rd, op_R));
                }
                break;
            }
            default:
                break;
        }
        boolean ifReg = inst.getLs() instanceof Register && inst.getRs() instanceof Register;
        switch (inst.getOp()) {
            case slt:
            case sle:
            case sgt:
            case sge:
            case sne:
            case seq: {
                if (rs1 instanceof Const && !(rs2 instanceof Const)) {
                    Operand tmp = rs1;
                    rs1 = rs2;
                    rs2 = tmp;
                    if (inst.getOp() == BinaryOp.Op.slt) inst.setOp(BinaryOp.Op.sgt);
                    else if (inst.getOp() == BinaryOp.Op.sgt) inst.setOp(BinaryOp.Op.slt);
                    else if (inst.getOp() == BinaryOp.Op.sle) inst.setOp(BinaryOp.Op.sge);
                    else if (inst.getOp() == BinaryOp.Op.sge) inst.setOp(BinaryOp.Op.sle);
                }
                break;
            }
            default:
                break;
        }



        switch (inst.getOp()) {
            case slt: {
                Register lhs = toReg(rs1);
                Register rhs = toReg(rs2);
                curbb.addInst(new RegAction(lhs, rhs, rd, RegAction.Op.SLT));
                break;
            }
            case sle:{
                Register lhs = toReg(rs1);
                Register rhs = toReg(rs2);
                Int32 tmp = new Int32("tmple");
                curbb.addInst(new RegAction(rhs, lhs, tmp, RegAction.Op.SLT));
                curbb.addInst(new ImmAction(tmp, rd, ImmAction.Op.XORI, new Const(1)));
                break;
            }
            case sgt: {
                Register lhs = toReg(rs1);
                Register rhs = toReg(rs2);
                Int32 tmp = new Int32("tmpge");
                curbb.addInst(new RegAction(rhs, lhs, rd, RegAction.Op.SLT));
                break;
            }
            case sge:{
                Register lhs = toReg(rs1);
                Register rhs = toReg(rs2);
                Int32 tmp = new Int32("tmpge");
                curbb.addInst(new RegAction(lhs, rhs, tmp, RegAction.Op.SLT));
                curbb.addInst(new ImmAction(tmp, rd, ImmAction.Op.XORI, new Const(1)));
                break;
            }
            case seq: {
                Register lhs = toReg(rs1);
                Register rhs = toReg(rs2);
                Int32 tmp = new Int32("tmpeq");
                curbb.addInst(new RegAction(rhs, lhs, tmp, RegAction.Op.SUB));
                curbb.addInst(new ImmAction(tmp, rd, ImmAction.Op.SLTIU, new Const(1)));
                break;
            }
            case sne: {
                Register lhs = toReg(rs1);
                Register rhs = toReg(rs2);
                Int32 tmp = new Int32("tmpneq");
                curbb.addInst(new RegAction(rhs, lhs, tmp, RegAction.Op.SUB));
                curbb.addInst(new RegAction(module.getPhyRegisterHashMap().get("zero"), tmp, rd, RegAction.Op.SLTU));
                break;
            }
            default:
                break;
        }

    }

    @Override
    public void visit(Branch inst) {
        curbb.addInst(new Backend.Inst.Branch(toReg(inst.getCond()), module.getPhyRegisterHashMap().get("zero"), bbTobb.get(inst.getB1()), Backend.Inst.Branch.Opcode.bne));
        curbb.addInst(new Backend.Inst.Jump(bbTobb.get(inst.getB2())));
    }

    @Override
    public void visit(Call inst) {
        var paraList = new ArrayList<Operand>();
        if(inst.getObj() != null) paraList.add(inst.getObj());
        paraList.addAll(inst.getParas());

        int a = Math.min(paraList.size(), 8);
        for (int i = 0; i < a; ++i) {
            curbb.addInst(new Backend.Inst.Move(module.getPhyRegisterHashMap().get("a" + i), toReg(paraList.get(i))));
        }
        for (int i = 8; i < inst.getParas().size(); ++i) {
//            curbb.addInst(new Backend.Inst.Store(new StackAllocate(curFunc, false, 4*(i-8)), toReg(paraList.get(i)), null ,4));
            curbb.addInst(new Backend.Inst.Store(new StackAllocate(curFunc, false), toReg(paraList.get(i)), null ,4));

        }
        curbb.addInst(new Backend.Inst.Call(funcTofunc.get(inst.getFunc())));
        if (inst.getResult() != null) {
            curbb.addInst(new Backend.Inst.Move((Register) inst.getResult(), module.getPhyRegisterHashMap().get("a0")));
        }
    }

    @Override
    public void visit(Move inst) {
        curbb.addInst(new Backend.Inst.Move((Register) inst.getDest(), toReg(inst.getSrc())));
    }

    @Override
    public void visit(Jump inst) {
        curbb.addInst(new Backend.Inst.Jump(bbTobb.get(inst.getToBB())));
    }

    @Override
    public void visit(Load inst) {
        curbb.addInst(new Backend.Inst.Load((Register) inst.getPointer(), (Register) inst.getReg(), 4));
    }


    @Override
    public void visit(Return inst) {
        if (inst.getRetVar() != null) {
            curbb.addInst(new Backend.Inst.Move(module.getPhyRegisterHashMap().get("a0"), toReg(inst.getRetVar())));
        }
        for (var i : calleeMap.entrySet()) {
            curbb.addInst(new Backend.Inst.Move(module.getPhyRegisterHashMap().get(i.getKey()), i.getValue()));
        }
        curbb.addInst(new Backend.Inst.Return());
    }

    @Override
    public void visit(Store inst) {
        if (inst.getPointer() instanceof Pointer && ((Pointer) inst.getPointer()).isGlobal()) {
            curbb.addInst(new Backend.Inst.Store((Register) inst.getPointer(), toReg(inst.getVal()), new Register("store_tmp"), 4));
        }
        else {
            curbb.addInst(new Backend.Inst.Store((Register) inst.getPointer(), toReg(inst.getVal()), null, 4));
        }
    }

    @Override
    public void visit(PhiNode inst) { }

    @Override
    public void visit(ArrayType type) { }

    @Override
    public void visit(ClassType type) { }

    @Override
    public void visit(IntType type) { }

    @Override
    public void visit(PointerType type) { }

    @Override
    public void visit(Operand operand) { }

    @Override
    public void visit(Const const_) { }

    @Override
    public void visit(Pointer pointer) { }

    @Override
    public void visit(Int32 int32) { }

    @Override
    public void visit(ConstString constString) { }

    @Override
    public void visit(Register register) { }
}

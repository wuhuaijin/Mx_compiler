package Optim;

import IR.BB;
import IR.Function;
import IR.IRVisitor;
import IR.Instruction.*;
import IR.IrType.ArrayType;
import IR.IrType.ClassType;
import IR.IrType.IntType;
import IR.IrType.PointerType;
import IR.Module;
import IR.Operand.*;


import java.util.*;



public class SCCP extends Pass implements IRVisitor {

    boolean isChanged = false;

    private enum Status{
        UNDEFINED, CONSTANT, CONSTANTSTR, MULTIDEFINED
    }

    private static class OperandStatus {
        public Status status;
        public Operand opr;

        public OperandStatus(Operand opr, Status status) {
            this.opr = opr;
            this.status = status;
        }
    }

    Map<Operand, OperandStatus> operandStatusMap = new LinkedHashMap<>();
    Queue<BB> CFGList = new LinkedList<>();
    Queue<Register> workList = new LinkedList<>();
    Set<BB> unExecutedBlock = new LinkedHashSet<>();


    public SCCP(Module module) {
        super(module);
    }


    private OperandStatus getStatus(Operand operand) {
        if (operandStatusMap.containsKey(operand)) return operandStatusMap.get(operand);
        if (operand instanceof ConstString)
            operandStatusMap.put(operand, new OperandStatus(operand, Status.CONSTANTSTR));
        else if (operand instanceof Const)
            operandStatusMap.put(operand, new OperandStatus(operand, Status.CONSTANT));
        else operandStatusMap.put(operand, new OperandStatus(operand, Status.UNDEFINED));
        return operandStatusMap.get(operand);
    }



    @Override
    public boolean run() {
        isChanged = false;
        for (var func : module.getFuncs()) {
            if (func.isSystem()) continue;
            func.setPreOrderBBList();
            computeDefAndUseChain(func);
            clean();
            work(func);
        }
        return isChanged;
    }

    private void clean(){
        CFGList.clear();
        workList.clear();
        unExecutedBlock.clear();
        operandStatusMap.clear();
    }

    private void setUnExecuted(BB bb) {
        if (!unExecutedBlock.contains(bb)) {
            unExecutedBlock.add(bb);
            if (bb == null) {
                int a = 0;
            }
            CFGList.offer(bb);
        }
        else {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if (inst instanceof PhiNode) {
                    inst.accept(this);
                }
                else break;
            }
        }
    }

    private void setMultiDefined(Operand operand) {
        if (operand == null) return;
        if (getStatus(operand).status != Status.MULTIDEFINED) {
            operandStatusMap.replace(operand, new OperandStatus(operand, Status.MULTIDEFINED));
            workList.add(((Register) operand));
        }
    }

    private void setConstant(Register register, Const _const) {
        if (register == null) return;
        if (getStatus(register).status == Status.UNDEFINED) {
            operandStatusMap.replace(register, new OperandStatus(_const, Status.CONSTANT));
            workList.add(register);
        }
    }

    private void setConstantStr(Register register, ConstString constString) {
        if (register == null) return;
        if (getStatus(register).status != Status.CONSTANTSTR) {
            operandStatusMap.replace(register, new OperandStatus(constString, Status.CONSTANTSTR));
            workList.add(register);
        }
    }

    private void work(Function function) {
        if (function.getInBB() == null) {
            int a = 0;
        }
        int count  = 0;
        setUnExecuted(function.getInBB());
        for (var i : function.getParas()) setMultiDefined(i);
        if (function.getObj() != null) setMultiDefined(function.getObj());
        while (!CFGList.isEmpty() || !workList.isEmpty()) {
            while (!CFGList.isEmpty()) {
                count++;
                BB bb = CFGList.poll();
                if (bb == null) {
                    int a =0;
                }
                for (var inst = bb.getHead(); inst != null; inst = inst.getNext())
                    inst.accept(this);
            }
//            System.out.println(count);
            while (!workList.isEmpty()) {
                count++;
                Register register = workList.poll();
                if (used.get(register) == null) {
                    continue;
                }
                used.get(register).forEach(i->i.accept(this));
            }
//            System.out.println(count);
        }
//        System.out.println(count);
        function.getPreOrderBBList().forEach(this::regToConst);
        computeDefAndUseChain(function);
        function.getPreOrderBBList().forEach(this::handleNewMove);
    }

    private void regToConst(BB bb) {
        for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
            OperandStatus operandStatus = getStatus(inst.getDefOpr());
            if (operandStatus.status == Status.CONSTANT) {
                if (!(inst instanceof Move && ((Move) inst).getSrc() instanceof Const)) {
                    inst.replaceInst(new Move(bb, false, operandStatus.opr, inst.getDefOpr()));
                    isChanged = true;
                }
            }
            else if (operandStatus.status == Status.CONSTANTSTR) {
                inst.replaceInst(new Move(bb, false, operandStatus.opr, inst.getDefOpr()));
                isChanged = true;
            }
        }
    }

    private void handleNewMove(BB bb) {
        for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
            if (inst instanceof Move) {
                Operand src = ((Move) inst).getSrc();
                Operand dest = ((Move) inst).getDest();
                if (src instanceof Const) {
                    boolean hasPhi = false;
                    Set<BaseInstruction> oldUsed = new LinkedHashSet<>(used.get(dest));
                    for (var i : oldUsed) {
                        if (i != inst) {
                            if (!(i instanceof PhiNode)) {
                                isChanged = true;
                                used.get(dest).remove(i);
                                i.replaceUseOpr(dest, src);
                            }
                            else {
                                hasPhi = true;
                            }
                        }
                    }
                    if (!hasPhi) inst.delete();
                }
                if (src instanceof VirtualRegister) {
                    boolean hasPhi = false;
                    Set<BaseInstruction> oldUsed = new LinkedHashSet<>(used.get(dest));
                    Set<BaseInstruction> newUsed = used.get(src);
                    for (var i : oldUsed) {
                        if (i != inst) {
                            if (!(i instanceof PhiNode)) {
                                isChanged = true;
                                used.get(dest).remove(i);
                                i.replaceUseOpr(dest, src);
                                newUsed.add(i);
                            }
                            else {
                                hasPhi = true;
                            }
                        }
                    }
                    if (!hasPhi) {
                        newUsed.remove(inst);
                        inst.delete();
                    }
                }
            }
        }
    }

    @Override
    public void visit(Module module) { }

    @Override
    public void visit(Function function) { }

    @Override
    public void visit(BB block) { }

    @Override
    public void visit(Allocate inst) {
        setMultiDefined(inst.getDefOpr());
    }

    @Override
    public void visit(UnaryOp inst) {
        OperandStatus srcStatus = getStatus(inst.getRs());
        if (srcStatus.status == Status.CONSTANT) {
            setConstant(inst.getDefOpr(), unaryCompute((Const) srcStatus.opr, inst.getOp()));
        }
        else if (srcStatus.status == Status.MULTIDEFINED) {
            setMultiDefined(inst.getDefOpr());
        }
    }

    @Override
    public void visit(BinaryOp inst) {
        OperandStatus lhsStatus = getStatus(inst.getLs());
        OperandStatus rhsStatus = getStatus(inst.getRs());
        if (lhsStatus.status == Status.CONSTANT && rhsStatus.status == Status.CONSTANT) {
            setConstant(inst.getDefOpr(), binaryCompute(((Const) lhsStatus.opr), ((Const) rhsStatus.opr), inst.getOp()));
        }
        else if (lhsStatus.status == Status.MULTIDEFINED || rhsStatus.status == Status.MULTIDEFINED) {
            setMultiDefined(inst.getDefOpr());
        }
    }

    @Override
    public void visit(Branch inst) {
        if (inst.getB1() == null || inst.getB2() == null) {
            int a = 0;
        }
        if (inst.getCond() == null) {
            if (inst.getB1() == null) {
                int a = 0;
            }
            setUnExecuted(inst.getB1());
        }
        else {
            OperandStatus cond = getStatus(inst.getCond());
            if (cond.status == Status.CONSTANT) {
                if (inst.getB1() == null || inst.getB2() == null) {
                    int a = 0;
                }
                if (((Const) cond.opr).getValue() > 0)
                    setUnExecuted(inst.getB1());
                else
                    setUnExecuted(inst.getB2());
            }
            else if (cond.status == Status.MULTIDEFINED) {
                setUnExecuted(inst.getB1());
                setUnExecuted(inst.getB2());
            }
        }
    }

    @Override
    public void visit(Call inst) {
        if (inst.getFunc().isSystem()) {
            Function function = inst.getFunc();

        }
        else if (inst.getDefOpr() != null) {
            setMultiDefined(inst.getDefOpr());
        }
    }

    @Override
    public void visit(Move inst) {
        OperandStatus srcStatus = getStatus(inst.getSrc());
        if (srcStatus.status == Status.CONSTANT) {
            setConstant(((Register) inst.getDest()), ((Const) srcStatus.opr));
        }
        else if (srcStatus.status == Status.MULTIDEFINED) {
            setMultiDefined(inst.getDest());
        }
    }

    @Override
    public void visit(Jump inst) {
        setUnExecuted(inst.getToBB());
    }

    @Override
    public void visit(Load inst) {
        setMultiDefined(inst.getDefOpr());
    }

    @Override
    public void visit(PhiNode inst) {
        Const constVal = null;
        for (var entry : inst.getPath().entrySet()) {
            BB bb = entry.getKey();
            OperandStatus operandStatus = getStatus(entry.getValue());
            if (!unExecutedBlock.contains(bb)) {
                setMultiDefined(inst.getDefOpr());
                return;
            }
            if (operandStatus.status == Status.CONSTANT) {
                if (constVal != null) {
                    if (!constVal.getValue().equals(((Const) operandStatus.opr).getValue())) {
                        setMultiDefined(inst.getDefOpr());
                        return;
                    }
                }
                else {
                    constVal = (Const) operandStatus.opr;
                }
            }
            else if (operandStatus.status == Status.MULTIDEFINED) {
                setMultiDefined(inst.getDefOpr());
                return;
            }
        }
        if (constVal != null) {
            setConstant(inst.getDefOpr(), constVal);
        }
    }

    @Override
    public void visit(Return inst) { }

    @Override
    public void visit(Store inst) { }

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


    private Const unaryCompute(Const rs, UnaryOp.Op op) {
        int result;
        switch (op) {
            case NEG:
                result = -rs.getValue();
                break;
            case NOT:
                result = ~rs.getValue();
                break;
            default:
                return null;
        }
        return new Const(result);
    }

    private Const binaryCompute(Const ls, Const rs, BinaryOp.Op op) {
        int result = -1;
        switch (op) {
            case mul:
                result = ls.getValue() * rs.getValue();
                break;
            case add:
                result = ls.getValue() + rs.getValue();
                break;
            case sub:
                result = ls.getValue() - rs.getValue();
                break;
            case mod:
                if (rs.getValue() != 0) result = ls.getValue() % rs.getValue();
                break;
            case div:
                if (rs.getValue() != 0) result = ls.getValue() / rs.getValue();
                break;
            case shl:
                result = ls.getValue() << rs.getValue();
                break;
            case shr:
                result = ls.getValue() >> rs.getValue();
                break;
            case and:
                result = ls.getValue() & rs.getValue();
                break;
            case xor:
                result = ls.getValue() ^ rs.getValue();
                break;
            case or:
                result = ls.getValue() | rs.getValue();
                break;
            case slt:
                result = ls.getValue() < rs.getValue() ? 1 : 0;
                break;
            case sle:
                result = ls.getValue() <= rs.getValue() ? 1 : 0;
                break;
            case sgt:
                result = ls.getValue() > rs.getValue() ? 1 : 0;
                break;
            case sge:
                result = ls.getValue() >= rs.getValue() ? 1 : 0;
                break;
            case sne:
                result = !ls.getValue().equals(rs.getValue()) ? 1 : 0;
                break;
            case seq:
                result = ls.getValue().equals(rs.getValue()) ? 1 : 0;
                break;
            default:
                return null;
        }
        return new Const(result);
    }
}
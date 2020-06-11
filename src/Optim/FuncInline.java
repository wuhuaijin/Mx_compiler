package Optim;

import IR.BB;
import IR.Function;
import IR.Instruction.*;
import IR.Lable;
import IR.Module;
import IR.Operand.Int32;
import IR.Operand.Operand;
import IR.Operand.Pointer;
import IR.Operand.Register;

import java.util.*;

public class FuncInline {
    private Module module;
    private List<Function> functionList;
    private Set<Function> visitedFunc;
    private Map<Function, List<Call>> funcCallList;
    private Map<Function, Integer> funcLen;

    final private int RECUR_TIME = 1;
    final private int WORTH_CODE_LEN = 1200;

    public FuncInline(Module module) {
        this.module = module;
        this.functionList = new ArrayList<>();
        this.visitedFunc = new LinkedHashSet<>();
        this.funcCallList = new LinkedHashMap<>();
        this.funcLen = new LinkedHashMap<>();
    }

    public void run() {
        for (var i : module.getFuncs()) {
            if (!i.isSystem()) functionList.add(i);
        }

        visitedFunc.addAll(functionList);
        removeFunc();
        findCall();
        NoneRecursiveInline();
        RecursiveInline();
    }



    private void removeFunc() {
        for (var i : functionList) {
            if (!refered(i)) {
                module.getFuncs().remove(i);
            }
        }
    }

    private void NoneRecursiveInline() {
        for (boolean changed = true; changed; ){
            changed = false;
            for (var func : functionList) {
                if (!module.getFuncs().contains(func)) continue;
                findCall();
                for(var inst : funcCallList.get(func)) {
                    Function callee = inst.getFunc();
                    if (callee.isSystem()) continue;
                    if (!isRecursive(callee) && !func.getFuncName().equals("__init")) {
                        if (funcLen.get(func) + funcLen.get(callee) > WORTH_CODE_LEN) continue;
                        funcLen.put(func, funcLen.get(func) + funcLen.get(callee));
                        changed = true;
                        visitedFunc.add(func);
                        inline(inst, func);

                        if (!refered(callee)) {
                            module.getFuncs().remove(callee);
                            visitedFunc.remove(callee);
                        }
                    }
                }
            }
        }
    }

    private void RecursiveInline() {
        boolean changed = true;
        int cnt = 0;
        while (changed && cnt < RECUR_TIME) {
            changed = false;
            cnt++;

            for (var func : functionList) {
                if (!module.getFuncs().contains(func)) continue;
                findCall();
                Collections.reverse(funcCallList.get(func));
                for (var inst : funcCallList.get(func)) {
                    Function callee = inst.getFunc();
                    if (callee.isSystem()) continue;
                    if (!func.getFuncName().equals("__init")) {
                        if (funcLen.get(func) + funcLen.get(callee) > WORTH_CODE_LEN) continue;
                        funcLen.put(func, funcLen.get(func) + funcLen.get(callee));
                        changed = true;
                        visitedFunc.add(func);
                        inline(inst, func);
                    }
                }
            }
        }
    }

    private boolean isRecursive(Function func) {
        for (var inst : funcCallList.get(func)) {
            if (inst.getFunc() == func) return true;
        }
        return false;
    }


    private void findCall() {
        for (var i : visitedFunc) {
            i.setPreOrderBBList();
            ArrayList<Call> callList = new ArrayList<>();
            int cnt = 0;
            for (var j : i.getPreOrderBBList()) {
                for (var ins = j.getHead(); ins != null; ins = ins.getNext()) {
                    cnt++;
                    if (ins instanceof Call) {
                        callList.add((Call) ins);
                    }
                }
            }
            funcCallList.put(i, callList);
            funcLen.put(i, cnt);
        }
        visitedFunc.clear();
    }

    private boolean refered(Function func) {
        if (func.getFuncName().equals("__init")) return true;
        for (var i : functionList) {
            if (!module.getFuncs().contains(i)) continue;
            if (i != func) {
                if (visitedFunc.contains(i))
                    findCall();
                for (var inst : funcCallList.get(i)) {
                    if (inst.getFunc() == func) return true;
                }
            }
        }
        return false;
    }

    private void inline(Call ins, Function caller_func) {
        Function callee_func = ins.getFunc();

        if (callee_func == caller_func){
            callee_func = copyFunc(callee_func);
        }
        //split bb
        BB b1 = ins.getBasicBlock();
        BB b2 = new BB(Lable.getLable(), "splitBB");
        if (caller_func.getOutBB() == b1) caller_func.setOutBB(b2);
//        ins.getBasicBlock().getSuccessors().forEach(i->i.replacePre(ins.getBasicBlock(), b2));

        //big bug; next is not constant //todo
        for (BaseInstruction tmpins = ins.getNext(), nextins; tmpins != null; tmpins = nextins) {
            nextins = tmpins.getNext();
            tmpins.delete();
            b2.addInstruction(tmpins);
        }
        ins.setNext(null);
        ins.delete();

        Map<Operand, Operand> tmpOprMap = new LinkedHashMap<>();
        Map<BB, BB> tmpBBMap = new LinkedHashMap<>();

        callee_func.getParas().forEach(i->copyOpr(tmpOprMap, i));
        copyOpr(tmpOprMap, callee_func.getObj());
        for (var bb : callee_func.getPreOrderBBList()) {
            tmpBBMap.put(bb, new BB(Lable.getLable()));
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                inst.getOpr().forEach(i->copyOpr(tmpOprMap, i));
            }
        }

        if (ins.getObj() != null) b1.addInstruction(new Move(b1, false, ins.getObj(), tmpOprMap.get(callee_func.getObj())));
        for (int i = 0; i < ins.getParas().size(); ++i) {
            b1.addInstruction(new Move(b1, false, ins.getParas().get(i), tmpOprMap.get(callee_func.getParas().get(i))));
        }

        for (var bb : callee_func.getPreOrderBBList()) {
            BB tmpbb = tmpBBMap.get(bb);
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                List<Operand> tmpOprList = new ArrayList<>();
                List<BB> tmpBBList = new ArrayList<>();
                inst.getOpr().forEach(i->tmpOprList.add(tmpOprMap.get(i)));
                inst.getBB().forEach(i->tmpBBList.add(tmpBBMap.get(i)));
                tmpbb.addInstruction(inst.copySelf(tmpbb, false, tmpOprList, tmpBBList));
            }
            tmpbb.getTail().setIfTerminal(true);
        }

        BB inBB = tmpBBMap.get(callee_func.getInBB()), exitBB = tmpBBMap.get(callee_func.getOutBB());
        Return retIns = (Return) exitBB.getTail();
        exitBB.getTail().delete();
        if (exitBB.getTail() != null) exitBB.getTail().setIfTerminal(false);
        if (retIns.getRetVar() != null && ins.getResult() != null) {
            exitBB.addInstruction(new Move(exitBB, false, retIns.getRetVar(), ins.getResult()));
            exitBB.getTail().setIfTerminal(true);
        }



        if (b1.getTail() != null) b1.getTail().setIfTerminal(false);
        if (exitBB.getTail() != null) exitBB.getTail().setIfTerminal(false);
        b1.addInstruction(new Jump(b1, true, inBB));
        exitBB.addInstruction(new Jump(exitBB, true, b2));
        b1.getTail().setIfTerminal(true);
        if (b1.getTail() != null) b2.getTail().setIfTerminal(true);
    }

    private void copyOpr(Map<Operand, Operand> map, Operand opr) {
        if (map.containsKey(opr)) return;
        if (opr instanceof Register) {
            if (((Register) opr).getPtr() != null) {
                map.put(opr, opr);
            }
            else if (opr instanceof Pointer){
                if (((Pointer) opr).isGlobal()) {
                    map.put(opr, opr);
                }
                else map.put(opr, new Pointer(opr.getId()));
            }
            else if (opr instanceof Int32) {
                map.put(opr, new Int32(opr.getId()));
            }
        }
        else map.put(opr, opr);
    }


    public Function copyFunc(Function function) {
        Map<BB, BB> tmpBBMap = new LinkedHashMap<>();
        Map<Operand, Operand> regMap = new LinkedHashMap<>();

        var copyFunc = new Function(function.getFuncName() + "copy");

        function.setPreOrderBBList();
        for (var bb : function.getPreOrderBBList()) {
            tmpBBMap.put(bb, new BB(Lable.getLable(), "copy"));
        }
        copyFunc.setInBB(tmpBBMap.get(function.getInBB()));
        copyFunc.setOutBB(tmpBBMap.get(function.getOutBB()));

        if (function.getObj() != null) {
            copyOpr(regMap, function.getObj());
            copyFunc.setObj(((Register) regMap.get(function.getObj())));
        }
        for (var i : function.getParas()) {
            copyOpr(regMap, i);
            copyFunc.getParas().add(regMap.get(i));
        }

        for (var bb : function.getPreOrderBBList()) {
            var newbb = tmpBBMap.get(bb);
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                var oprList = new ArrayList<Operand>();
                var bbList = new ArrayList<BB>();
                for (var i : inst.getOpr()) {
                    copyOpr(regMap, i);
                    oprList.add(regMap.get(i));
                }
                for (var i : inst.getBB()) {
                    bbList.add(tmpBBMap.get(i));
                }
                newbb.addInstruction(inst.copySelf(newbb, inst.isIfTerminal(), oprList, bbList));
            }
            newbb.getTail().setIfTerminal(true);
        }

        copyFunc.setPreOrderBBList();
        return copyFunc;
    }

}

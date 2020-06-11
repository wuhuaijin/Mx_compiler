package Optim;

import IR.Function;
import IR.Instruction.*;
import IR.Operand.*;
import IR.Module;

import java.util.*;
import java.util.logging.LoggingPermission;

public class GlobalVarResolve {
    Module module;
    private List<Function> funcList = new ArrayList<>();
    private Map<Function, Set<Operand>> globalVarInFunc = new LinkedHashMap<>();
    private Map<Function, Set<Operand>> defVarInFunc = new LinkedHashMap<>();
    private Map<Function, Set<Function>> calleeList = new LinkedHashMap<>();
    private Map<Function, Set<Operand>> globalVarRecurInFun = new LinkedHashMap<>();
    private Map<Function, Map<Operand, Operand>> tmpVarMapInFunc = new LinkedHashMap<>();

    public GlobalVarResolve(Module module) {
        this.module = module;
        for (var func : module.getFuncs()) {
            if (!func.isSystem()) funcList.add(func);
        }
    }

    public void run() {
        copyTmpVar();
        scanningFunc();
        loadAndStore();
    }

    public void copyTmpVar() {
        for (var func  : funcList) {
            func.setPreOrderBBList();
            Map<Operand, Operand> globalVarTmpMap = new LinkedHashMap<>();
            Map<Operand, Operand> replaceMap = new LinkedHashMap<>();
            tmpVarMapInFunc.put(func, globalVarTmpMap);
            for (var bb : func.getPreOrderBBList()) {
                for (var ins = bb.getHead(); ins != null; ins = ins.getNext()) {
                    List<Operand> oprList = ins.getOpr();
                    List<Operand> newOprList = new ArrayList<>();
                    for (var opr : oprList) {
                        if (opr instanceof Register && ((Register) opr).getPtr() != null) {
                            if (replaceMap.containsKey(opr)) newOprList.add(replaceMap.get(opr));
                            else {
                                Int32 newOpr = new Int32(opr.getId());
                                newOpr.setPtr(((Register) opr).getPtr());
                                replaceMap.put(opr, newOpr);
                                newOprList.add(newOpr);
                                globalVarTmpMap.put(((Register) opr).getPtr(), newOpr);
                            }
                        }
                        else newOprList.add(opr);
                    }
                    ins.replaceInst(ins.copySelf(bb, ins.isIfTerminal(), newOprList, ins.getBB()));
                }
            }
        }
    }

    public void scanningFunc() {

        for (var func : funcList) {
            Set<Operand> globalVars = new LinkedHashSet<>();
            Set<Function> calleeFunction = new LinkedHashSet<>();
            Set<Operand> defVars = new LinkedHashSet<>();
            globalVarInFunc.put(func, globalVars);
            defVarInFunc.put(func, defVars);
            for (var bb : func.getPreOrderBBList()) {
                for (var ins = bb.getHead(); ins != null; ins = ins.getNext()) {
                    if (ins instanceof Call && !((Call)ins).getFunc().isSystem())
                        calleeFunction.add(((Call) ins).getFunc());

                    List<Operand> oprList = ins.getOpr();
                    for (Operand opr : oprList){
                        if (opr instanceof Register && ((Register) opr).getPtr() != null) {
                            globalVars.add(((Register) opr).getPtr());
                            if (ins.getDefOpr() == opr) {
//                                defVars.add(opr);
                                defVars.add(((Register) opr).getPtr());
                                defVars.add(opr);
                            }
                        }
                    }
                }
            }
            calleeList.put(func, calleeFunction);
            globalVarRecurInFun.put(func, new LinkedHashSet<>(globalVars));
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (var func : funcList) {
                for (var i : calleeList.get(func)) {
                    if (!globalVarRecurInFun.get(func).containsAll(globalVarRecurInFun.get(i))) {
                        changed = true;
                        globalVarRecurInFun.get(func).addAll(globalVarRecurInFun.get(i));
                    }
                }
            }
        }
    }

    public void loadAndStore() {
        for (var func : funcList) {
            var tmpMap = tmpVarMapInFunc.get(func);
            for (var i : globalVarInFunc.get(func)) {
                if(func.getFuncName().equals("__init")){
                    var tmp = func.getInBB().getHead();
                    while(!(tmp instanceof Store) || ((Store) tmp).getPointer() != i) tmp = tmp.getNext();
                    tmp.pushBack(new Load(func.getInBB(), false, tmpMap.get(i), i));
                }
                else {
                    func.getInBB().addInstructionAtFront(new Load(func.getInBB(), false, tmpMap.get(i), i));
                }
                if (defVarInFunc.get(func).contains(i)) {
                    func.getOutBB().getTail().pushFront(new Store(func.getOutBB(), false, tmpMap.get(i), i));
                }
                boolean flag = true;
                if (defVarInFunc.get(func).contains(i)) flag = false;
                for (var j : calleeList.get(func)) {
                    if (defVarInFunc.get(j).contains(i)) flag = false;
                }
                if (!flag || func.getFuncName().equals("main")) {
                    func.getOutBB().getTail().pushFront(new Store(func.getOutBB(), false, tmpMap.get(i), i));
                }
            }
        }
        for (var func : funcList) {
            if (func.getFuncName().equals("__init")) continue;
            var globalVar = globalVarInFunc.get(func);
            for (var bb : func.getPreOrderBBList()) {
                for (var ins = bb.getHead(); ins != null; ins = ins.getNext()) {
                    if (ins instanceof Call && !((Call) ins).getFunc().isSystem()) {
                        var calleeUsed = globalVarRecurInFun.get(((Call) ins).getFunc());
                        var tmpMap = tmpVarMapInFunc.get(func);
                        for (var i : calleeUsed) {
                            if (globalVar.contains(i) && !(ins.getPrev() instanceof Call && !((Call) ins.getPrev()).getFunc().isSystem())) {

                                ins.pushFront(new Store(bb, ins.isIfTerminal(), tmpMap.get(i), i));

                            }
                            if (globalVar.contains(i) && !(ins.getNext() instanceof Call && !((Call) ins.getNext()).getFunc().isSystem())) {

                                ins.pushBack(new Load(bb, ins.isIfTerminal(), tmpMap.get(i), i));
                            }
                        }
                    }
                }
            }
        }
    }
}

package Optim;

import IR.BB;
import IR.Function;
import IR.Instruction.*;
import IR.Module;
import IR.Operand.Pointer;

import java.util.*;

public class DCE extends Pass {

    private Set<BaseInstruction> markList = new LinkedHashSet<>();

    public DCE(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        boolean isChanged = false;
        for (var func : module.getFuncs()) {
            if (func.isSystem()) continue;
            func.setPreOrderBBList();
            computeDefAndUseChain(func);
            revrseDTree(func);
            isChanged |= doIt(func);
        }
        return isChanged;
    }

    private boolean doIt(Function function) {
        mark(function);
        return sweep(function);
    }

    private boolean isCritical(BaseInstruction inst) {
        return inst instanceof Return || inst instanceof Call
                || (inst instanceof Allocate && inst.getDefOpr() instanceof Pointer && ((Pointer) inst.getDefOpr()).isGlobal())
                || inst instanceof Store;
    }

    private void mark(Function function) {
        Set<BaseInstruction> workList = new LinkedHashSet<>();
        markList.clear();
        for (var bb : function.getPreOrderBBList()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if (isCritical(inst)) {
                    workList.add(inst);
                    markList.add(inst);
                }
            }
        }
        while (!workList.isEmpty()) {
            var inst = workList.iterator().next();
            workList.remove(inst);
            for (var u : inst.getUseOpr()) {
                var defU = def.get(u);
                if (defU != null && !markList.contains(defU)) {
                    workList.add(defU);
                    markList.add(defU);
                }
            }
            if (inst instanceof PhiNode) {
                for (var bb : ((PhiNode) inst).getPath().keySet()) {
                    var tailInst = bb.getTail();
                    if (!markList.contains(tailInst)) {
                        markList.add(tailInst);
                        workList.add(tailInst);
                    }
                }
            }
            for (var b : inst.getBasicBlock().postDomFrontier) {
                var j = b.getTail();
                if (!markList.contains(j)) {
                    markList.add(j);
                    workList.add(j);
                }
            }
        }
    }


    private BB findbb(BB bb) {
        bb = bb.postIdom;
        while (true){
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if (markList.contains(inst)) return bb;
            }
            bb = bb.postIdom;
        }
    }

    private boolean sweep(Function function) {
        boolean isChanged = false;
        BaseInstruction nextIns;
        for (var bb : function.getPreOrderBBList()) {
            for (var inst = bb.getHead(); inst != null; inst = nextIns) {
                nextIns = inst.getNext();
                if (!markList.contains(inst)) {
                    if (inst instanceof Branch) {
                        isChanged = true;
                        var nearestbb = findbb(bb);
                        inst.replaceInst(new Jump(bb, true, nearestbb));
                    }
                    else if (!(inst instanceof Jump)) {
                        isChanged = true;
                        inst.delete();
                    }
                }
            }
        }
        return isChanged;
    }

    private void revrseDTree(Function function) {
        var retList = function.makeReverseCopy();
        Map<BB, BB> bbMap = (Map<BB, BB>) retList.get(0);
        Map<BB, BB> rebbMap = (Map<BB, BB>) retList.get(1);
        Function reverseFunc = (Function) retList.get(2);

        DTree.computeDTree(reverseFunc, true);
        DTree.computeDTreeFrontier(reverseFunc);

        for (var bb : function.getPreOrderBBList()) {
            var counterpartBB = bbMap.get(bb);
            bb.postIdom = rebbMap.get(counterpartBB.iDom);
            for (var bd : counterpartBB.domFrontier) {
                bb.postDomFrontier.add(rebbMap.get(bd));
            }

        }


    }
}

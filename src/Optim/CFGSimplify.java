package Optim;

import IR.BB;
import IR.Function;
import IR.Instruction.BaseInstruction;
import IR.Instruction.Branch;
import IR.Instruction.Jump;
import IR.Module;
import IR.Operand.Const;

import java.io.FileNotFoundException;
import java.util.*;

public class CFGSimplify extends Pass{

    public CFGSimplify(Module module) {
        super(module);
    }

    public boolean run() {
        boolean changed = false;
        for (boolean ifChanged = true; ifChanged; ){
            ifChanged = false;
            for (var func : module.getFuncs()) {
                if (func.isSystem()) continue;
                func.setPreOrderBBList();
                ifChanged |= convertConstantBranch(func);
//                ifChanged |= removeUnreachableBlock(func);
                ifChanged |= mergeBB(func);
                ifChanged |= removeSingleJump(func);
            }
        }
        return changed;
    }

    public boolean mergeBB(Function func) {
        boolean changed = false;
        for (int i = func.getPreOrderBBList().size() - 1; i >= 0; --i) {
            BB bb = func.getPreOrderBBList().get(i);
            if (bb.getSuccessors().size() == 1) {
                BB sucbb = bb.getSuccessors().get(0);
                if (sucbb.getPredecessors().size() == 1 && sucbb != func.getInBB()) {
                    changed = true;
                    bb.merge(sucbb);
                    if (sucbb == func.getOutBB()) {
                        func.setOutBB(bb);
                    }
                }
            }
        }

        if (changed) func.setPreOrderBBList();
        return changed;
    }

    public boolean convertConstantBranch(Function func) {
        boolean changed = false;
        boolean resetList = false;
        List<BB> bbList = func.getPreOrderBBList();
        for (var bb : bbList) {
            if (bb.getTail() instanceof Branch) {
                Branch tail = ((Branch) bb.getTail());
                if (tail.getB1() == tail.getB2()) {
                    changed = true;
                    tail.replaceInst(new Jump(bb, true, tail.getB1()));
                }
                else if (tail.getCond() instanceof Const) {
                    resetList = true;
                    changed = true;
                    int cond = ((Const) tail.getCond()).getValue();
                    BB target = cond == 0 ? tail.getB2() : tail.getB1();
                    BB cut  = cond == 0 ? tail.getB1() : tail.getB2();
                    tail.replaceInst(new Jump(bb, true, target));
                    bb.getSuccessors().remove(cut);
                    bb.getPredecessors().remove(bb);
                }
            }
        }
        if (resetList) func.setPreOrderBBList();
        return changed;
    }

    public boolean removeUnreachableBlock(Function func) {
        HashSet<BB> visitedBBSet = new HashSet<>();
        Queue<BB> bbQueue = new LinkedList<>();
        visitedBBSet.add(func.getInBB());
        bbQueue.add(func.getInBB());
        while (!bbQueue.isEmpty()) {
            BB bb = bbQueue.poll();
            for (var i : bb.getSuccessors()) {
                if (!visitedBBSet.contains(i));
                visitedBBSet.add(i);
                bbQueue.add(i);
            }
        }
        for (var i : func.getTotalBBlist()) {
            if (!visitedBBSet.contains(i)){
                i.deleteSelf();
            }
        }
        func.setPreOrderBBList();
        return false;
    }


    public boolean removeSingleJump(Function func) {
        boolean changed = false;
        for (var bb : func.getPreOrderBBList()) {
            if (bb == func.getInBB()) continue;
            if (bb.getHead() instanceof  Jump) {
                changed = true;
                BB target = ((Jump) bb.getHead()).getToBB();
                ((Jump) bb.getHead()).getToBB().getPredecessors().remove(bb);
                for (var prebb : bb.getPredecessors()) {
                    prebb.getSuccessors().remove(bb);
                    prebb.getSuccessors().add(target);
                    target.getPredecessors().add(prebb);
                    BaseInstruction preJump = prebb.getTail();
                    if (preJump instanceof Jump) {
                        ((Jump) preJump).replaceTargetBB(target);
                    }
                    else if (preJump instanceof Branch) {
                        ((Branch) preJump).replaceTargetBB(bb, target);
                    }
                }

            }
        }
        if (changed) func.setPreOrderBBList();
        return changed;
    }


}

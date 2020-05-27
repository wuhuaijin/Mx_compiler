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

    public void run() {
        for (var func : module.getFuncs()) {
            convertConstantBranch(func);
            removeUnreachableBlock(func);
        }
    }

    public void convertConstantBranch(Function func) {
        List<BB> bbList = func.getPreOrderBBList();
        for (var bb : bbList) {
            if (bb.getTail() instanceof Branch) {
                Branch tail = ((Branch) bb.getTail());
                if (tail.getB1() == tail.getB2()) {
                    tail.replaceInst(new Jump(bb, true, tail.getB1()));
                }
                else if (tail.getCond() instanceof Const) {
                    int cond = ((Const) tail.getCond()).getValue();
                    BB target = cond == 0 ? tail.getB2() : tail.getB1();
                    BB cut  = cond == 0 ? tail.getB1() : tail.getB2();
                    tail.replaceInst(new Jump(bb, true, target));
                    bb.getSuccessors().remove(cut);
                    bb.getPredecessors().remove(bb);
                }
            }
        }
        func.setPreOrderBBList();
    }

    public void removeUnreachableBlock(Function func) {
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
    }
}

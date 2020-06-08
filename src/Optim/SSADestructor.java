package Optim;

import IR.BB;
import IR.Function;
import IR.Instruction.Branch;
import IR.Instruction.Jump;
import IR.Instruction.Move;
import IR.Instruction.PhiNode;
import IR.Lable;
import IR.Module;
import IR.Operand.Const;
import IR.Operand.Int32;
import IR.Operand.Operand;
import IR.Operand.VirtualRegister;

import java.util.*;

public class SSADestructor extends Pass {

    static class ParallelCopy {
        VirtualRegister dst;
        Operand src;

        ParallelCopy(VirtualRegister dst, Operand src) {
            this.dst = dst;
            this.src = src;
        }
    }

    private Map<BB, List<ParallelCopy>> parallelCopy = new LinkedHashMap<>();


    public SSADestructor(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for (var func : module.getFuncs()) {
            if (func.isSystem()) continue;
            func.setPreOrderBBList();
            removePhi(func);
            func.setPreOrderBBList();
//            sequentializationParallelCopy(func);
            PCS(func);
        }
        module.setSsa(false);
        return true;
    }

    public void removePhi(Function function) {
        for (var bb : function.getPreOrderBBList()) {
            if (!parallelCopy.containsKey(bb)) parallelCopy.put(bb, new LinkedList<>());
        }
        for (var bb : function.getPreOrderBBList()) {
            List<BB> predecessors = new LinkedList<>(bb.getPredecessors());
            Map<BB, List<ParallelCopy>> pCopy = new LinkedHashMap<>();
            for (BB prebb : predecessors) {
                if (prebb.getSuccessors().size() > 1) {
                    BB bbCopy = new BB(Lable.getLable(), "parallelcopy");
                    bbCopy.addLastInstruction(new Jump(bbCopy, true, bb));
                    ((Branch) prebb.getTail()).replaceTargetBB(bb, bbCopy);
                    prebb.getSuccessors().remove(bb);
                    prebb.getSuccessors().add(bbCopy);

                    bbCopy.getPredecessors().add(prebb);
                    bbCopy.getSuccessors().add(bb);

                    bb.getPredecessors().remove(prebb);
                    bb.getPredecessors().add(bbCopy);
                    parallelCopy.computeIfAbsent(bbCopy, t->new LinkedList<>());
                    pCopy.put(prebb, parallelCopy.get(bbCopy));
                }
                else {
                    pCopy.put(prebb, parallelCopy.get(prebb));
                }
            }
            var inst = bb.getHead();
            for(; inst instanceof PhiNode; inst = inst.getNext()){
                for(Map.Entry<BB, Operand> entry : ((PhiNode) inst).getPath().entrySet()){
                    BB block = entry.getKey();
                    Operand reg = entry.getValue();
                    pCopy.computeIfAbsent(block, k -> new ArrayList<>());
                    pCopy.get(block).add(new ParallelCopy(((PhiNode) inst).getResult(), reg == null ? new Const(0) : reg));
                }
            }
            // remove phi
            bb.setHead(inst);
            if (inst != null) inst.setPrev(null);
        }
    }

    public void PCS(Function function) {
        for (var bb : function.getPreOrderBBList()) {
            copySequentialization(bb);
        }
    }

    public void copySequentialization(BB bb) {
        for (var inst : parallelCopy.get(bb)) {
            if (inst.src instanceof VirtualRegister) {
                if (inst.src != inst.dst) {
                    bb.getTail().pushFront(new Move(bb, false, inst.src, inst.dst));
                }
                else {
                    VirtualRegister tmp = new Int32("_tmp");
                    bb.getTail().pushFront(new Move(bb, false, inst.src, tmp));
                    bb.getTail().pushFront(new Move(bb, false, tmp, inst.dst));
                }
            }
        }
        for (var inst : parallelCopy.get(bb)) {
            if (inst.src instanceof Const) {
                bb.getTail().pushFront(new Move(bb, false, inst.src, inst.dst));
            }
        }
    }

}

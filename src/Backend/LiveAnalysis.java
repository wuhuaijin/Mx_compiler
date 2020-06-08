package Backend;

import Backend.Inst.Call;
import IR.Operand.Register;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class LiveAnalysis {
    private BackFunction function;
    private BackModule module;

    public LiveAnalysis(BackFunction function, BackModule module) {
        this.function = function;
        this.module = module;
    }

    public void run() {
        findDefandUse();
        solveLiveness();
    }

    public void findDefandUse() {
        function.makeBBList();
        for (var bb : function.getBbList()) {
            bb.getDef().clear();
            bb.getUse().clear();
            bb.getLiveIn().clear();
            bb.getLiveOut().clear();
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                for (Register i : inst.getUseReg()) {
                    if (!bb.getDef().contains(i))
                        bb.getUse().add(i);
                }
                if (inst instanceof Call) {
                    for (var i : module.getCallerSaveRegisterName()) {
                        bb.getDef().add(module.getPhyRegisterHashMap().get(i));
                    }
                }
                if (inst.getDefReg() != null) bb.getDef().add(inst.getDefReg());
            }
        }
    }

    public void solveLiveness() {
        for (boolean changed = true; changed; ) {
            changed = false;
            for (int i = function.getBbList().size() - 1; i >= 0; --i) {
                BackBB curbb = function.getBbList().get(i);
                Set<Register> newOut = new LinkedHashSet<>();
                Set<Register> newIn = new LinkedHashSet<>(curbb.getUse());
                Set<Register> tmpSet = new LinkedHashSet<>(curbb.getLiveOut());
                tmpSet.removeAll(curbb.getDef());
                newIn.addAll(tmpSet);
                curbb.getSuccessors().forEach(j->newOut.addAll(j.getLiveIn()));
                if (!newIn.equals(curbb.getLiveIn()) || !newOut.equals(curbb.getLiveOut())) {
                    changed = true;
                    curbb.getLiveIn().clear();
                    curbb.getLiveIn().addAll(newIn);
                    curbb.getLiveOut().clear();
                    curbb.getLiveOut().addAll(newOut);
                }
            }

        }
    }



}

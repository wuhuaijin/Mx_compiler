package Optim;

import Backend.BackModule;
import Backend.Inst.Move;

public class PeepholeOptim {
    private BackModule module;

    public PeepholeOptim(BackModule module) {
        this.module = module;
    }

    public void run() {
        for (var function : module.getFunctionList()) {
            if (function.isSystem()) continue;
            for (var bb : function.getBbList()) {
                for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                    if (inst instanceof Move && (((Move) inst).getRd() == ((Move) inst).getSrc())) {
                        inst.delete();
                    }
                }
            }
        }
    }
}

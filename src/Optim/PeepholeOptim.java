package Optim;

import Backend.BackFunction;
import Backend.BackModule;
import Backend.Inst.Load;
import Backend.Inst.Move;
import Backend.Inst.Store;

public class PeepholeOptim {
    private BackModule module;

    public PeepholeOptim(BackModule module) {
        this.module = module;
    }

    public void run() {
        for (var function : module.getFunctionList()) {
            if (function.isSystem()) continue;
//            removeLoadStore(function);
            removeMove(function);
        }
    }

    private void removeMove(BackFunction function) {
        for (var bb : function.getBbList()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if (inst instanceof Move && (((Move) inst).getRd() == ((Move) inst).getSrc())) {
                    inst.delete();
                }
            }
        }
    }

    private void removeLoadStore(BackFunction function)
    {
        for (var bb : function.getBbList()) {
            for (var inst = bb.getHead().getNext(); inst != null; inst = inst.getNext()) {
                if (inst instanceof Load) {
                    var prev = inst.getPrev();
                    if (prev instanceof Store) {
//                            if (((Store) prev).getRd() == ((Load) inst).getSr1())
                    }
                }
            }
        }
    }
}

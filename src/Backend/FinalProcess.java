package Backend;

import Backend.Inst.ImmAction;
import IR.Operand.Const;

public class FinalProcess {

    private BackModule module;

    public FinalProcess(BackModule module) {
        this.module = module;
    }

    public void run() {
        setSp();
        fitMain();
    }

    private void setSp() {
        for (var func : module.getFunctionList()) {
            if (!func.isSystem()) {
                int a = func.getRealStackSize();
                func.getInbb().getHead().pushFront(new ImmAction(module.getPhyRegisterHashMap().get("sp"), module.getPhyRegisterHashMap().get("sp"), ImmAction.Op.ADDI, new Const(-a)));
                func.getOutbb().getTail().pushFront(new ImmAction(module.getPhyRegisterHashMap().get("sp"), module.getPhyRegisterHashMap().get("sp"), ImmAction.Op.ADDI, new Const(a)));
            }
        }
    }

    private void fitMain() {

        for (var i : module.getFunctionList()) {
            if (i.getId().equals("__init")) i.setId("main");
            else if (i.getId().equals("main")) i.setId("_main");
        }

    }

}

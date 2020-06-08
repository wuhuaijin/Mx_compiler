package Optim;
import Backend.BackFunction;
import IR.Function;
import IR.Instruction.BaseInstruction;
import IR.Instruction.BinaryOp;
import IR.Module;
import IR.Operand.Const;
import IR.Operand.Operand;
import IR.Operand.Register;
import IR.Operand.VirtualRegister;

import java.util.*;

abstract public class Pass {
    protected Module module;

    public Pass(Module module) {
        this.module = module;
    }


    abstract public boolean run();


    protected Map<Register, BaseInstruction> def = new HashMap<>();
    protected Map<Register, Set<BaseInstruction>> used = new HashMap<>();

    public void computeDefAndUseChain(Function function) {
        for (var bb : function.getPreOrderBBList()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                VirtualRegister defReg = inst.getDefOpr();
                if (defReg != null) {
                    def.put(defReg, inst);
                    if (!used.containsKey(defReg)) used.put(defReg, new HashSet<>());
                }
                for (var reg : inst.getUseOpr()) {
                    if (!used.containsKey(reg)) used.put(reg, new HashSet<>());
                    used.get(reg).add(inst);
                }

            }
        }
    }



}

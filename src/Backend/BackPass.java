package Backend;

import Backend.Inst.Inst;
import IR.Function;
import IR.Instruction.BaseInstruction;
import IR.Operand.Register;
import IR.Operand.VirtualRegister;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class BackPass {

    protected Map<Register, Set<Inst>> def = new LinkedHashMap<>();
    protected Map<Register, Set<Inst>> used = new LinkedHashMap<>();

    public void computeDefAndUseChain(BackFunction function) {
        def.clear();
        used.clear();
        for (var bb : function.getBbList()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                Register defReg = inst.getDefReg();
                if (defReg != null) {
                    def.put(defReg, new LinkedHashSet<>());
                    def.get(defReg).add(inst);
                    if (!used.containsKey(defReg)) used.put(defReg, new LinkedHashSet<>());
                }
                for (var reg : inst.getUseReg()) {
                    if (!used.containsKey(reg)) used.put(reg, new LinkedHashSet<>());
                    used.get(reg).add(inst);
                }

            }
        }
    }
}

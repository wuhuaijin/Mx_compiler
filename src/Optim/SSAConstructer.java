package Optim;

import IR.BB;
import IR.Function;
import IR.Instruction.BaseInstruction;
import IR.Instruction.PhiNode;
import IR.Module;
import IR.Operand.Operand;
import IR.Operand.Pointer;
import IR.Operand.VirtualRegister;

import java.util.*;

public class SSAConstructer extends Pass {

    Map<VirtualRegister, Integer> count = new LinkedHashMap<>();
    Map<VirtualRegister, Set<BB>> defBlocks = new HashMap<>();
    Map<VirtualRegister, Stack<Integer>> stack = new LinkedHashMap<>();

    public SSAConstructer(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for (var func : module.getFuncs()) {
            if (func.isSystem()) continue;
            findGlobals(func);
            insertPhi(func);
            renameVars(func);
        }
        module.setSsa(true);
        return false;
    }

    public void findGlobals(Function function) {
        function.getGlobals().clear();

        List<BB> preOrderBBList = function.getPreOrderBBList();
        Set<VirtualRegister> varKill = new HashSet<>();
        for (var bb : preOrderBBList) {
            varKill.clear();
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                for (var reg : inst.getUseOpr()) {
                    if (reg instanceof Pointer && ((Pointer) reg).isGlobal()) continue;
                    if (!varKill.contains(reg)) function.getGlobals().add(reg);
                }
                if (inst.getDefOpr() != null) {
                    varKill.add(inst.getDefOpr());
                    if (!defBlocks.containsKey(inst.getDefOpr())) defBlocks.put(inst.getDefOpr(), new HashSet<>());
                    defBlocks.get(inst.getDefOpr()).add(bb);
                }
            }
        }
    }


    public void insertPhi(Function function) {
        Queue<BB> workList = new LinkedList<>();
        Set<BB> visited = new HashSet<>();
        for (VirtualRegister reg : function.getGlobals()) {
            visited.clear();
            workList.addAll(defBlocks.getOrDefault(reg, Collections.emptySet()));
            while (!workList.isEmpty()) {
                BB b = workList.remove();
                for (BB i : b.domFrontier) {
                    if (!visited.contains(i)) {
                        visited.add(i);
                        i.addInstructionAtFront(new PhiNode(i, false, reg));
                        workList.add(i);
                    }
                }
            }

        }

    }



    public void renameVars(Function function) {
        dealParameters(function);
        rename(function.getInBB());
    }

    public void dealParameters(Function function) {
        VirtualRegister obj = (VirtualRegister) function.getObj();
        if (obj != null) {
            stack.put(obj, new Stack<>());
            count.put(obj, 0);
            stack.get(obj).push(count.get(obj));
            function.setObj(obj.getSSARenamedReg(0));
        }

        int size = function.getParas().size();
        for (int i = 0; i < size; ++i) {
            VirtualRegister para = (VirtualRegister) function.getParas().get(i);
            stack.put(para, new Stack<>());
            count.put(para, 0);
            stack.get(para).push(count.get(para));
            function.getParas().set(i, para.getSSARenamedReg(count.get(para)));
        }

    }


    public void rename(BB bb) {
        for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
            if (!(inst instanceof PhiNode)) {
                changeUseReg2SSA(inst);
            }
            if (inst.getDefOpr() != null) {
                changeDefReg2SSA(inst);
            }
        }
        for (var successor : bb.getSuccessors()) {
            for (var inst = successor.getHead(); inst != null; inst = inst.getNext()) {
                if (!(inst instanceof PhiNode)) break;
                VirtualRegister origin = ((((PhiNode) inst).getResult())).getOriginVar();
                VirtualRegister src = stack.containsKey(origin) && !stack.get(origin).isEmpty() ? origin.getSSARenamedReg(stack.get(origin).peek()) : null;
                ((PhiNode) inst).getPath().put(bb, src);
            }
        }

        for (var successor : bb.iDomChildren) {
            rename(successor);
        }

        for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
            if (inst.getDefOpr() != null) {
                if (inst.getDefOpr().getOriginVar() instanceof Pointer && ((Pointer) inst.getDefOpr().getOriginVar()).isGlobal()) continue;
                stack.get(inst.getDefOpr().getOriginVar()).pop();
            }
        }
    }

    public void changeUseReg2SSA(BaseInstruction inst) {
        List<Operand> oprList = inst.getOpr();
        for (var i : oprList) {
            if (i instanceof Pointer && ((Pointer) i).isGlobal()) continue;
            if (i instanceof VirtualRegister && inst.getUseOpr().contains(i)) {
                int index = stack.get(i).peek();
                inst.replaceUseOpr(i, ((VirtualRegister) i).getSSARenamedReg(index));
            }
        }
    }

    public void changeDefReg2SSA(BaseInstruction inst) {
        VirtualRegister defReg = inst.getDefOpr().getOriginVar();
        if (defReg instanceof Pointer && ((Pointer) defReg).isGlobal()) return;
        if (stack.get(defReg) == null) {
            stack.put(defReg, new Stack<>());
            count.put(defReg, -1);
        }
        int index = (count.get(defReg)) + 1;
        stack.get(defReg).push(index);
        count.put(defReg, index);
        inst.replaceDefOpr(defReg.getSSARenamedReg(index));
    }




}

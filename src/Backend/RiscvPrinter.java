package Backend;

import Backend.Inst.*;
import IR.Operand.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RiscvPrinter implements RiscvVisitor {

    PrintStream output;
    private BackModule module;

    Map<BackBB, String> bbNameMap = new HashMap<>();
    int cnt = 0;
    int ConstStrcnt = 0;

    //for print()

    public void print1(String str) {
        output.print(str);
    }
    public void print2(String str) {
        output.println(str);
    }
    public void print3(String str) {
        output.println("\t" + str);
    }

    public RiscvPrinter(BackModule module) {
        this.module = module;
    }

    public void visit(Register i) {
        print3(".globl\t" + i.getId());
        print2(i.getId() + ":");
        print3(".zero\t4\n");
    }

    public void visit(ConstString i) {
        i.setId("Str" + ConstStrcnt++);
        print3(".globl\t" + i.getId());
        print2(i.getId() + ":");
        print3(".string\t" + "\""+ i.toString() + "\"" + "\n");
    }

    public String getbbName(BackBB bb) {
        if (!bbNameMap.containsKey(bb)) {
            if (bb.getId() == null) {
                bbNameMap.put(bb, "b" + cnt++);
            }
            else {
                bbNameMap.put(bb, "b" + bb.getId() + cnt++);
            }
        }
        return bbNameMap.get(bb);
    }

    public void visit(PrintStream printStream) {
        output = printStream;
        print3(".section" + "\t" + ".data");
        print2("");

        for (var i : module.getGlobalVarList()) {
            visit(i);
        }
        for (var i : module.getStringList()) {
            visit(i);
        }

        print3(".text\n");
        for (var i : module.getFunctionList()) {
            if (!i.isSystem()) visit(i);
        }
    }


    public void visit(BackFunction function) {
        print3("\n.globl\t" + function.getId());
        print2(function.getId() + ":");
        function.getBbList().forEach(this::visit);
    }

    public void visit(BackBB bb) {
        print2(getbbName(bb) + ":");
        for (var i = bb.getHead(); i != null; i = i.getNext()) {
            i.accept(this);
        }
    }

    @Override
    public void visit(Branch inst) {
        print3(inst.getOpcode().toString().toLowerCase() + "\t"
                + inst.getRs1().getId() + ", " + inst.getRs2().getId() + ", " + getbbName(inst.getTarget()));
    }

    @Override
    public void visit(Call inst) {
        print3("call\t" + inst.getFunc().getId());
    }

    @Override
    public void visit(Jump inst) {
        print3("j\t" + getbbName(inst.getTarget()));
    }

    @Override
    public void visit(LA inst) {
        print3("la\t" + inst.getRd().getId() + ", " + inst.getPtr().getId());
    }

    @Override
    public void visit(LI inst) {
        print3("li\t" + inst.getRd().getId() + ", " + inst.getImm().getId());
    }

    @Override
    public void visit(Load inst) {
        if (inst.getSrc() != null) {
            if (((StackAllocate) inst.getSrc()).getOffset() > 2047) {
                print3("li\t" + "t5, " + ((StackAllocate) inst.getSrc()).getOffset());
                print3("add\t" + "t5, sp, t5");
                print3("lw\t" + inst.getRd().getId() + ", 0(t5)");
            }
            else print3("lw\t" + inst.getRd().getId() + ", " + inst.getSrc().toString());
        }
        else if (inst.getSr1() != null) {
            if (inst.getSr1() instanceof VirtualRegister) {
                print3("lw\t" + inst.getRd().getId() + ", " + inst.getSr1().getId());
            }
            else if (inst.getSr1() instanceof PhyRegister) {
                print3("lw\t" + inst.getRd().getId() + ", 0(" + inst.getSr1().getId() + ")");
            }
        }
    }

    @Override
    public void visit(Move inst) {
        print3("mv\t" + inst.getRd().getId() + ", " + inst.getSrc().getId());
    }

    @Override
    public void visit(Return inst) {
        print3("ret");
    }

    @Override
    public void visit(Store inst) {
        if (inst.getSrc2() != null) {
            print3("sw\t" + inst.getSrc().getId() + ", " + inst.getRd().getId() + ", " + inst.getSrc2().getId());
        }
        else {
            if (inst.getRd() != null) {
                print3("sw\t" + inst.getSrc().getId() + ", 0(" + inst.getRd().getId() + ")");
            }
            else if (inst.getPtr() != null) {
                if (((StackAllocate) inst.getPtr()).getOffset() > 2047) {
                    print3("li\t" + "t5, " + ((StackAllocate) inst.getPtr()).getOffset());
                    print3("add\t" + "t5, sp, t5");
                    print3("sw\t" + inst.getSrc().getId() + ", 0(t5)");
                }
                else print3("sw\t" + inst.getSrc().getId() + ", " + inst.getPtr().toString());
            }
        }
    }

    @Override
    public void visit(ImmAction inst) {
        print3(inst.getOp().toString().toLowerCase() + "\t" +
                inst.getRd().getId() + ", " + inst.getRs1().getId() + ", " + inst.getImm().getId());
    }

    @Override
    public void visit(RegAction inst) {
        print3(inst.getOp().toString().toLowerCase() + "\t"
                + inst.getRd().getId() + ", " + inst.getRs1().getId() + ", " + inst.getRs2().getId());
    }
}

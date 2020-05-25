package IR.Instruction;

import IR.BB;
import IR.Function;
import IR.IRVisitor;
import IR.Operand.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Call extends BaseInstruction {
    private Operand result;
    private Function func;
    private List<Operand> paras;
    private Operand obj;

    public Call(BB basicBlock, boolean ifTerminal, Operand result, Function func, List<Operand> paras, Operand obj) {
        super(basicBlock, ifTerminal);
        this.result = result;
        this.func = func;
        this.paras = paras;
        this.obj = obj;
    }

    public Operand getObj() {
        return obj;
    }

    public Operand getResult() {
        return result;
    }

    public Function getFunc() {
        return func;
    }

    public List<Operand> getParas() {
        return paras;
    }

    @Override
    public List<Operand> getOpr() {
        ArrayList<Operand> tmplist = new ArrayList<>();
        tmplist.add(result);
        tmplist.add(obj);
        tmplist.addAll(paras);
        return tmplist;
    }

    @Override
    public List<BB> getBB() {
        return Collections.emptyList();
    }

    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new Call(bb, ifTerminal, oprList.get(0), func, paras.isEmpty()? Collections.emptyList() : oprList.subList(2, oprList.size()), oprList.get(1));
    }

//    @Override
//    public String toString() {
//        StringBuilder parasToString = new StringBuilder();
//        for (var i : paras) {
//            parasToString.append(i.toString());
//            parasToString.append(", ");
//        }
//        return (result == null ? "" : result + " = ") + "call@ " + func.getFuncName() + "(" + parasToString.toString() + ")";
//    }


    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.Operand;
import IR.Operand.VirtualRegister;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PhiNode extends BaseInstruction {

    private VirtualRegister result;
    private Map<BB, Operand> path;

    public PhiNode(BB basicBlock, boolean ifTerminal, VirtualRegister result) {
        super(basicBlock, ifTerminal);
        this.result = result;
        path = new LinkedHashMap<>();
    }

    public VirtualRegister getResult() {
        return result;
    }

    public Map<BB, Operand> getPath() {
        return path;
    }

    public void setPath(Map<BB, Operand> path) {
        this.path = path;
    }

    public void replacePath(BB _old, BB _new) {
        if (_old == _new) return;
        path.put(_new, path.get(_old));
        path.remove(_old);
    }

    public void setResult(VirtualRegister result) {
        this.result = result;
    }

    public void removePath(BB bb) {
        path.remove(bb);
        if (path.size() == 1) {
            var inst = new Move(basicBlock, ifTerminal, path.values().iterator().next(), result);
            replaceInst(inst);
            if (this == basicBlock.getHead()) basicBlock.setHead(inst);
            if (this == basicBlock.getTail()) basicBlock.setTail(inst);
        }
    }

    public void replaceSinglePath() {
        var inst = new Move(basicBlock, ifTerminal, path.values().iterator().next(), result);
        replaceInst(inst);
        if (this == basicBlock.getHead()) basicBlock.setHead(inst);
        if (this == basicBlock.getTail()) basicBlock.setTail(inst);
    }

    @Override
    public List<Operand> getOpr() {
        List<Operand> oprList = new ArrayList<>();
        for (var i : path.entrySet()) oprList.add(i.getValue());
        oprList.add(result);
        return oprList;
    }

    @Override
    public List<BB> getBB() {
        List<BB> bbList = new ArrayList<>();
        for (var i : path.entrySet()) bbList.add(i.getKey());
        return bbList;
    }

    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        var newIns = new PhiNode(bb, ifTerminal, (VirtualRegister) oprList.get(oprList.size() - 1));
        for (int i = 0; i < bbList.size(); ++i) newIns.path.put(bbList.get(i), oprList.get(i));
        return newIns;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUseOpr(Operand _old, Operand _new) {
        for (var i : path.entrySet()) {
            if (i.getValue() == _old) path.put(i.getKey(), _new);
        }
    }

    @Override
    public void replaceDefOpr(Operand _new) {
        result = (VirtualRegister) _new;
    }

    @Override
    public List<VirtualRegister> getUseOpr() {
        List<VirtualRegister> registerList = new ArrayList<>();
        for (var i : path.entrySet()) {
            registerList.add((VirtualRegister) i.getValue());
        }
        registerList.add(result);
        return registerList;
    }

    @Override
    public VirtualRegister getDefOpr() {
        return result;
    }
}

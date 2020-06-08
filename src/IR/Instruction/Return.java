package IR.Instruction;

import IR.BB;
import IR.IRVisitor;
import IR.Operand.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Return extends BaseInstruction {

    public Return(BB basicBlock, boolean ifTerminal, Operand retVar) {
        super(basicBlock, ifTerminal);
        this.retVar = retVar;
    }

    private Operand retVar;

    public Operand getRetVar() {
        return retVar;
    }

    @Override
    public List<Operand> getOpr() {
        return Collections.singletonList(retVar);
    }


    @Override
    public List<BB> getBB() {
        return Collections.emptyList();
    }

    @Override
    public BaseInstruction copySelf(BB bb, boolean ifTerminal, List<Operand> oprList, List<BB> bbList) {
        return new Return(bb, ifTerminal, oprList.get(0));
    }

//    @Override
//    public String toString() {
//        return "ret " + retVar;
//    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUseOpr(Operand _old, Operand _new) {
        if (retVar == _old) retVar = _new;
    }

    @Override
    public void replaceDefOpr(Operand _new) {
        assert false;
    }

    @Override
    public List<VirtualRegister> getUseOpr() {
        return retVar instanceof VirtualRegister ? Collections.singletonList((VirtualRegister) retVar) : Collections.emptyList();
    }

    @Override
    public VirtualRegister getDefOpr() {
        return null;
    }
}

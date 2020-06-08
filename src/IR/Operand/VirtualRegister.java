package IR.Operand;

import java.util.LinkedHashMap;
import java.util.Map;

abstract public class VirtualRegister extends Register {

    private VirtualRegister originVar = this; // the origin register before SSA renaming

    private Map<Integer, VirtualRegister> SSARenamedRegisters;


    public VirtualRegister(String id) {
        super(id);
    }

    public void setOriginVar(VirtualRegister originVar) {
        this.originVar = originVar;
    }

    public VirtualRegister getOriginVar() {
        return originVar;
    }

    public abstract VirtualRegister getSSAWithId(int index);

    public VirtualRegister getSSARenamedReg(int index) {
        if (SSARenamedRegisters == null) SSARenamedRegisters = new LinkedHashMap<>();
        if (SSARenamedRegisters.containsKey(index)) return SSARenamedRegisters.get(index);
        VirtualRegister _new = getSSAWithId(index);
        _new.setOriginVar(this);
        SSARenamedRegisters.put(index, _new);
        return _new;
    }


}
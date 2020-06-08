package IR.Operand;


import IR.Instruction.Return;

public class Int32 extends VirtualRegister {
    public Int32(String id) {
        super(id);
    }


    @Override
    public VirtualRegister getSSAWithId(int index) {
        return new Int32(this.getId() + "." + index);
    }

}
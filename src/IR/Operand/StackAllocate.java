package IR.Operand;

import Backend.BackFunction;
import Backend.BackendOpr;

import java.util.HashMap;
import java.util.*;

public class  StackAllocate extends BackendOpr {

    @Override
    public String toString() {
        if(bt){
            return (offset + function.getRealStackSize()) + "(sp)";
        }
        return offset + "(sp)";
    }


    private int offset;
    private BackFunction function;
    private boolean bt;

    public StackAllocate(BackFunction function, boolean bt) {
        this.offset = function.stackSize;
        this.function = function;
        this.bt = bt;
        function.stackSize += 4;
    }

    public StackAllocate(BackFunction function, boolean bt, int offset) {
        this.offset = offset;
        this.function = function;
        this.bt = bt;
    }

}
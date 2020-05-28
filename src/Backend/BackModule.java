package Backend;

import IR.Operand.ConstString;
import IR.Operand.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BackModule {

    public String[] phyRegName = {		  "zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5",
            "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6" };

    public String[] allocatableRegName = {		  "ra",	"t0", "t1", "t2",
            "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5",
            "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7",
            "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6" };

    public String[] callerSaveRegisterName =
            {	  	  "ra", "t0", "t1", "t2", "a0", "a1", "a2", "a3", "a4", "a5",
                    "a6", "a7", "t3", "t4", "t5", "t6" };

    public String[] calleeSaveRegisterName =
            {  "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
                    "s8", "s9", "s10", "s11"};

    static public HashMap<String, PhyRegister> phyRegisterHashMap = new HashMap<>();
    private List<BackFunction> functionList = new ArrayList<>();
    private List<ConstString> stringList = new ArrayList<>();
    private List<Register> globalVarList = new ArrayList<>();

    public HashMap<String, PhyRegister> getPhyRegisterHashMap() {
        return phyRegisterHashMap;
    }

    public List<BackFunction> getFunctionList() {
        return functionList;
    }

    public List<ConstString> getStringList() {
        return stringList;
    }

    public List<Register> getGlobalVarList() {
        return globalVarList;
    }

    public void setFunctionList(List<BackFunction> functionList) {
        this.functionList = functionList;
    }

    public void setStringList(List<ConstString> stringList) {
        this.stringList = stringList;
    }

    public void setGlobalVarList(List<Register> globalVarList) {
        this.globalVarList = globalVarList;
    }

    public BackModule() {
        for (var i : phyRegName) {
            phyRegisterHashMap.put(i, new PhyRegister(i));
        }
    }

    public String[] getPhyRegName() {
        return phyRegName;
    }

    public String[] getAllocatableRegName() {
        return allocatableRegName;
    }

    public String[] getCallerSaveRegisterName() {
        return callerSaveRegisterName;
    }

    public String[] getCalleeSaveRegisterName() {
        return calleeSaveRegisterName;
    }
}

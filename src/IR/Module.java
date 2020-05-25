package IR;

import Backend.BackFunction;
import IR.Operand.ConstString;
import IR.Operand.Register;

import java.util.*;

public class Module {
    private ArrayList<Function> funcs = new ArrayList<>();
    private ArrayList<ConstString> constStrings = new ArrayList<>();
    private ArrayList<Register> globalVars = new ArrayList<>();

//    private Map<String, Function> funcs = new HashMap<>();


    public void addFunc(Function func) {
        funcs.add(func);
    }
    public void addConstString(ConstString str) {
        constStrings.add(str);
    }
    public void addGlobalVar(Register var) {
        globalVars.add(var);
    }

    public ArrayList<ConstString> getConstStrings() {
        return constStrings;
    }

    public ArrayList<Register> getGlobalVars() {
        return globalVars;
    }

    public List<Function> getFuncs() {
        return funcs;
    }

//    public Function getFunc(String funcName) throws SyntaxException {
//        if (hasFunc(funcName)) return funcs.get(funcName);
//        else throw new SyntaxException(new Location(0, 0), "no function find!");
//    }

//    public boolean hasFunc(String funcName) {
//        return funcs.containsKey(funcName);
//    }

    //    @Override
//    public String toString() {
//        StringBuilder ans = new StringBuilder();
//        for (var i : funcs)
//            ans.append(i);
//        return ans.toString();
//    }

}

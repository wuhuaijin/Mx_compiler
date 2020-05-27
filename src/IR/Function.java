package IR;

import IR.Operand.*;

import java.util.*;

public class Function {
    private String funcName;
    private List<Operand> paras;
    private BB inBB;
    private BB outBB;
    private Register obj;
    private boolean isMethod;
    private boolean isSystem;
    private boolean isextend;
    private List<BB> preOrderBBList;
    private List<BB> totalBBlist;

    public Function(String funcName) {
        this.funcName = funcName;
        this.paras = new ArrayList<>();
        this.totalBBlist = new ArrayList<>();
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public void addPara(Operand para) {
        paras.add(para);
    }

    public List<Operand> getParas() {
        return paras;
    }

    public void setParas(List<Operand> paras) {
        this.paras = paras;
    }

    public BB getInBB() {
        return inBB;
    }

    public void addBB(BB bb) {
        totalBBlist.add(bb);
    }

    public List<BB> getTotalBBlist() {
        return totalBBlist;
    }

    public void setInBB(BB inBB) {
        this.inBB = inBB;
    }

    public BB getOutBB() {
        return outBB;
    }

    public void setOutBB(BB outBB) {
        this.outBB = outBB;
    }

    public Register getObj() {
        return obj;
    }

    public void setObj(Register obj) {
        this.obj = obj;
    }

    public boolean isMethod() {
        return isMethod;
    }

    public void setMethod(boolean method) {
        isMethod = method;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isIsextend() {
        return isextend;
    }

    public void setIsextend(boolean isextend) {
        this.isextend = isextend;
    }

    public void setPreOrderBBList() {
        this.preOrderBBList = new ArrayList<>();
        Set<BB> visited = new HashSet<>();
        dfs(inBB, visited);
    }

    public void dfs(BB bb, Set<BB> visited) {
//        if (bb == null) return;
        if (visited.contains(bb)) return;
        preOrderBBList.add(bb);
        visited.add(bb);
        bb.findSuccessorsAndPre();
        for (var i : bb.getSuccessors()) {
            dfs(i, visited);
        }
    }

    public List<BB> getPreOrderBBList() {
        return preOrderBBList;
    }
    //    public void addBB(BB bb) {
//        this.bbList.add(bb);
//    }
//
//    @Override
//    public String toString() {
//        if (extend) return "";
//        StringBuilder parasToString = new StringBuilder();
//        for (var i : this.paras) {
//            parasToString.append(i.toString());
//            parasToString.append(", ");
//        }
//        StringBuilder bbToString = new StringBuilder();
//        for (var i : this.bbList) {
//            bbToString.append(i.toString());
//        }
//        return "define " + "@" + getFuncName() + "(" + parasToString.toString() + ") {\n" + bbToString.toString() + "}\n\n";
//    }
//
//
//    public void allocOperand(Operand operand) {
//        alloc.setOperand(operand);
//    }
}

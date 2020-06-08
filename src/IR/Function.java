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
    private List<BB> postOrderBBList;
    private List<BB> totalBBlist;
    private Set<VirtualRegister> globals;


    public Function(String funcName) {
        this.funcName = funcName;
        this.paras = new ArrayList<>();
        this.totalBBlist = new ArrayList<>();
        this.globals = new LinkedHashSet<>();
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

    public Set<VirtualRegister> getGlobals() {
        return globals;
    }


    public void setPreOrderBBList() {
        this.preOrderBBList = new ArrayList<>();
        this.postOrderBBList = new ArrayList<>();
        Set<BB> visited = new HashSet<>();
        dfs(inBB, null, visited);
    }

    public void dfs(BB bb, BB parbb, Set<BB> visited) {

        if (bb == null) return;//todo : if need delete ?
        if (visited.contains(bb)) {
            if (bb.getLable().toString().equals("b_55")) {
                int a =0;
            }
            if (!bb.getPredecessors().contains(parbb))
                bb.getPredecessors().add(parbb);
            return;
        }
        if (bb.getLable().toString().equals("b_55")) {
            int a =0;
        }
        if (parbb != null) bb.setPredecessors(new ArrayList<>(Collections.singleton(parbb)));
        else bb.setPredecessors(new ArrayList<>());
        bb.setFather(parbb);
        preOrderBBList.add(bb);
        visited.add(bb);
        bb.findSuccessorsAndPre();
        for (var i : bb.getSuccessors()) {
            dfs(i, bb, visited);
        }
        postOrderBBList.add(bb);
    }

    public List<BB> getPreOrderBBList() {
        return preOrderBBList;
    }

    public List<BB> getPostOrderBBList() {
        return postOrderBBList;
    }


    public List<Object> makeReverseCopy() {
        Map<BB, BB> bbMap = new LinkedHashMap<>();
        Map<BB, BB> rebbMap = new LinkedHashMap<>();
        for (var bb : preOrderBBList) {
            bbMap.put(bb, new BB(Lable.getLable(), "reverse"));
            rebbMap.put(bbMap.get(bb), bb);
        }
        for (var bb : preOrderBBList) {
            var newBB = bbMap.get(bb);
            bb.getPredecessors().forEach(i->newBB.getSuccessors().add(bbMap.get(i)));
            bb.getSuccessors().forEach(i->newBB.getPredecessors().add(bbMap.get(i)));
        }

        var reverseFunc = new Function("reverse" + getFuncName());
        reverseFunc.setOutBB(bbMap.get(inBB));
        reverseFunc.setInBB(bbMap.get(outBB));
        reverseFunc.reverseOrderBBList();

        return Arrays.asList(bbMap, rebbMap, reverseFunc);
    }

    public void reverseOrderBBList() {
        preOrderBBList = new ArrayList<>();
        Set<BB> visited = new HashSet<>();
        reverseDfs(inBB, null, visited);
    }

    public void reverseDfs(BB bb, BB parbb, Set<BB> visited) {
        if (bb == null) return;
        if (visited.contains(bb)) return;
        bb.setFather(parbb);
        preOrderBBList.add(bb);
        visited.add(bb);
        for (var i : bb.getSuccessors()) {
            reverseDfs(i, bb, visited);
        }
    }


}

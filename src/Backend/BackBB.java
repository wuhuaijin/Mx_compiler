package Backend;

import Backend.Inst.Inst;
import IR.Operand.Register;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BackBB {
    private String id;
    private Inst head, tail;

    private List<BackBB> successors;
    private List<BackBB> precessors;
    private BackBB parent;

    private Set<Register> use = new HashSet<>();
    private Set<Register> def = new HashSet<>();
    private Set<Register> liveIn = new HashSet<>();
    private Set<Register> liveOut = new HashSet<>();

    public BackBB getParent() {
        return parent;
    }

    public void setParent(BackBB parent) {
        this.parent = parent;
    }

    public void setLiveIn(Set<Register> liveIn) {
        this.liveIn = liveIn;
    }

    public void setLiveOut(Set<Register> liveOut) {
        this.liveOut = liveOut;
    }

    public List<BackBB> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<BackBB> successors) {
        this.successors = successors;
    }

    public List<BackBB> getPrecessors() {
        return precessors;
    }

    public void setPrecessors(List<BackBB> precessors) {
        this.precessors = precessors;
    }

    public BackBB(String id) {
        this.id = id;
    }

    public Inst getHead() {
        return head;
    }

    public void setHead(Inst head) {
        this.head = head;
    }

    public Inst getTail() {
        return tail;
    }

    public void setTail(Inst tail) {
        this.tail = tail;
    }

    public String getId() {
        return id;
    }


    public int dfn;
    public List<BackBB> bucket = new ArrayList<>();
    public BackBB ancestor;
    public BackBB best;

    public BackBB semiDom;
    public BackBB sameDom;
    public BackBB iDom; 		// i.e. parent in dominator tree
    public List<BackBB> iDomChildren = new ArrayList<>();
    
    
    public void addInst(Inst inst) {
        inst.setBb(this);
        if (head == null) {
            head = tail = inst;
        }
        else {
            tail.setNext(inst);
            inst.setPrev(tail);
            this.setTail(inst);
        }
    }

    public Set<Register> getUse() {
        return use;
    }

    public Set<Register> getDef() {
        return def;
    }

    public Set<Register> getLiveIn() {
        return liveIn;
    }

    public Set<Register> getLiveOut() {
        return liveOut;
    }
}

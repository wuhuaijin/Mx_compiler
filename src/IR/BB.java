package IR;

import Backend.BackBB;
import IR.Instruction.*;

import java.util.*;

public class BB {

    private BB prev;
    private BB next;

    Lable lable;
    String type;

    private BaseInstruction head;
    private BaseInstruction tail;

    private ArrayList<BB> successors;
    private ArrayList<BB> predecessors;

    private BB father;

    public int reversePostOrderIndex;
    public List<BB> bucket = new ArrayList<>();
    public BB ancestor;
    public BB best;

    public BB semiDom;
    public BB sameDom;
    public BB iDom; 		// i.e. parent in dominator tree
    public List<BB> iDomChildren = new ArrayList<>();
    public Set<BB> domFrontier = new LinkedHashSet<>();

    public BB postIdom;
    public Set<BB> postDomFrontier = new LinkedHashSet<>();

    public void clear(){
        ancestor = null;
        semiDom = null;
        sameDom = null;
        iDom = null;
        bucket.clear();
    }


    public BB(Lable lable, String type) {
        this.lable = lable;
        this.type = type;
        this.head = null;
        this.tail = null;
        this.prev = null;
        this.next = null;
        successors = new ArrayList<>();
        predecessors = new ArrayList<>();
    }

    public BB(Lable lable) {
        this.lable = lable;
        this.head = null;
        this.tail = null;
        this.prev = null;
        this.next = null;
        successors = new ArrayList<>();
        predecessors = new ArrayList<>();
    }


    public Lable getLable() {
        return lable;
    }

//    public BB getNext() {
//        return next;
//    }
//
//    public BB getPrev() {
//        return prev;
//    }

    public BaseInstruction getHead() {
        return head;
    }

    public void setHead(BaseInstruction head) {
        this.head = head;
    }

    public void setTail(BaseInstruction tail) {
        this.tail = tail;
    }

    public BaseInstruction getTail() {
        return tail;
    }

    public void setSuccessors(ArrayList<BB> successors) {
        this.successors = successors;
    }

    public void setPredecessors(ArrayList<BB> predecessors) {
        this.predecessors = predecessors;
    }

    public BB getFather() {
        return father;
    }

    public void setFather(BB father) {
        this.father = father;
    }

    public ArrayList<BB> getSuccessors() {
        return successors;
    }

    public ArrayList<BB> getPredecessors() {
        return predecessors;
    }

    private void appendPredecessors(BB bb) {
        predecessors.add(bb);
    }

//    public void appendBlock(BB block) {
//        block.prev = this;
//        this.next = block;
//    }

    public boolean isEmpty() {
        return head == tail && head == null;
    }

    public void addInstruction(BaseInstruction instruction) {
        if (tail != null && tail.isIfTerminal()) return;
        if (isEmpty()) {
            head = instruction;
            tail = instruction;
            instruction.setPrev(null);
            instruction.setNext(null);
        } else if (!tail.isIfTerminal()) {
            tail.setNext(instruction);
            instruction.setPrev(tail);
            tail = instruction;
        } else if (tail.getNext() == null) {
            tail.setNext(instruction);
            instruction.setPrev(tail);
            tail = instruction;
        }
        instruction.setBasicBlock(this);
    }

    public void addInstructionAtFront(BaseInstruction instruction) {
        if (isEmpty())
            tail = instruction;
        else {
            head.setPrev(instruction);
            instruction.setNext(head);
        }
        head = instruction;
    }

    public void addLastInstruction(BaseInstruction instruction) {
        addInstruction(instruction);
        tail.setIfTerminal(true);
    }

//    public void makeSucBBList(){
//        assert tailIns != null;
//        sucBBList = new ArrayList<>();
//        if(tailIns instanceof Branch) {
//            if(((Branch) tailIns).getThenBB() != ((Branch) tailIns).getElseBB())
//                sucBBList.addAll(Arrays.asList(((Branch)tailIns).getThenBB(), ((Branch) tailIns).getElseBB()));
//            else
//                sucBBList.add(((Branch) tailIns).getThenBB());
//        }
//        else if(tailIns instanceof Jump) sucBBList.add(((Jump) tailIns).getBB());
//        else if(!(tailIns instanceof Return)) throw new FuckingException("basic block terminated by something strange");
//    }

    public void findSuccessorsAndPre() {
        if (lable.toString().equals("b_53")) {
            int a = 0;
        }
        successors = new ArrayList<>();
        if (getTail() instanceof Return) return;
        if (getTail() instanceof Jump) {

            successors.add(((Jump) getTail()).getToBB());
//            ((Jump) getTail()).getToBB().appendPredecessors(this);
        }
        else if (getTail() instanceof Branch) {
            successors.add(((Branch) getTail()).getB1());
//            ((Branch) getTail()).getB1().appendPredecessors(this);
            successors.add(((Branch) getTail()).getB2());
//            ((Branch) getTail()).getB2().appendPredecessors(this);
        }
    }

    public void replacePre(BB oldbb, BB newbb) {
        oldbb.successors.remove(this);
        predecessors.remove(oldbb);
        predecessors.add(newbb);
        newbb.successors.add(this);
    }

    public void deleteSelf() {
        successors.forEach(i->i.getPredecessors().remove(this));
        predecessors.forEach(i->i.getSuccessors().remove(this));
    }

    public void merge(BB mergebb) {

        for(var sucBB : mergebb.getSuccessors()){
            for(var ins = sucBB.getHead(); ins instanceof PhiNode; ins = ins.getNext()){
                ((PhiNode) ins).replacePath(mergebb, this);
            }
        }
        for (var ins = mergebb.getHead(); ins != null; ins = ins.getNext()) {
            ins.setBasicBlock(this);
        }
        for (var sucbb : mergebb.getSuccessors()) {
            sucbb.getPredecessors().add(this);
            sucbb.getPredecessors().remove(mergebb);
        }
        if (head == tail) {
            head = mergebb.getHead();
        }
        else {
            tail.delete();
            tail.setNext(mergebb.getHead());
            mergebb.getHead().setPrev(tail);
        }
        setTail(mergebb.getTail());
        successors = mergebb.getSuccessors();
    }


    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

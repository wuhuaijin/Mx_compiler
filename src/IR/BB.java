package IR;

import IR.Instruction.BaseInstruction;
import IR.Instruction.Branch;
import IR.Instruction.Jump;
import IR.Instruction.Return;

import java.util.ArrayList;

public class BB {

    private BB prev;
    private BB next;

    Lable lable;
    String type;

    private BaseInstruction head;
    private BaseInstruction tail;

    private ArrayList<BB> successors;
    private ArrayList<BB> predecessors;

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

    public void findSuccessorsAndPre() {
        if (getTail() instanceof Return) return;
        if (getTail() instanceof Jump) {
            successors.add(((Jump) getTail()).getToBB());
            ((Jump) getTail()).getToBB().appendPredecessors(this);
        }
        else if (getTail() instanceof Branch) {
            successors.add(((Branch) getTail()).getB1());
            ((Branch) getTail()).getB1().appendPredecessors(this);
            successors.add(((Branch) getTail()).getB2());
            ((Branch) getTail()).getB2().appendPredecessors(this);
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


    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

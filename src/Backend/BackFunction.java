package Backend;

import Backend.Inst.Branch;
import Backend.Inst.Jump;

import java.util.*;

public class BackFunction {
    private boolean isSystem;
    private String id;
    private BackBB inbb;
    private BackBB outbb;
    public int stackSize;

    private List<BackBB> bbList = new ArrayList<>();

    public BackFunction(String id, boolean isSystem) {
        this.id = id;
        this.isSystem = isSystem;
        stackSize = 0;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BackBB> getBbList() {
        return bbList;
    }

    public int getStackSize() {
        return stackSize;
    }

    public BackBB getInbb() {
        return inbb;
    }

    public void setInbb(BackBB inbb) {
        this.inbb = inbb;
    }

    public BackBB getOutbb() {
        return outbb;
    }

    public void setOutbb(BackBB outbb) {
        this.outbb = outbb;
    }

    public int getRealStackSize() {
        return getStackSize() * 4 + (16 - getStackSize() * 4 % 16);
    }

    public void makeBBList(){
        bbList = new ArrayList<>();
        dfsBB(inbb, null, new HashSet<>());
    }


    public void dfsBB(BackBB bb, BackBB prebb, Set<BackBB> visited) {
        if (bb == null) return;
        if (visited.contains(bb) && !bb.getPrecessors().contains(prebb)) {
            bb.getPrecessors().add(prebb);
            return;
        }
        else if (visited.contains(bb)){
            return;
        }
        if (prebb != null) {
            bb.setPrecessors(new ArrayList<>(Collections.singleton(prebb)));
        }
        else bb.setPrecessors(new ArrayList<>());
        visited.add(bb);
        bbList.add(bb);
        findSuccessors(bb);
        bb.getSuccessors().forEach(i->dfsBB(i, bb, visited));
    }

    public void findSuccessors(BackBB bb) {
        HashSet<BackBB> tmp = new HashSet<>();
        if (bb.getTail() != null) {
            if (bb.getTail() instanceof Branch) {
                tmp.add(((Branch) bb.getTail()).getTarget());
            }
            else if (bb.getTail() instanceof Jump) {
                tmp.add(((Jump) bb.getTail()).getTarget());
            }
        }
        if (bb.getTail() != null && bb.getTail().getPrev() != null) {
            if (bb.getTail().getPrev() instanceof Branch) {
                tmp.add(((Branch) bb.getTail().getPrev()).getTarget());
            }
            else if (bb.getTail().getPrev() instanceof Jump) {
                tmp.add(((Jump) bb.getTail().getPrev()).getTarget());
            }
        }
        bb.setSuccessors(new ArrayList<>(tmp));

    }

}

package Backend;

import Backend.Inst.*;
import IR.Operand.Int32;
import IR.Operand.Register;
import IR.Operand.StackAllocate;
import IR.Operand.VirtualRegister;

import javax.print.DocFlavor;
import java.util.*;

public class RegAllocator {

    private static class Edge {
        Register u, v;

        public Edge(Register u, Register v) {
            this.u = u;
            this.v = v;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof Edge)
                return u == ((Edge) obj).u && v == ((Edge) obj).v;
            else return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(u,v );
        }
    }

    private static final int MAX_DEG = 0x7fffffff;
    private int k = 28; // allocatable size
    private BackModule module;

    private Set<Register> preColored = new LinkedHashSet<>();
    private Set<Register> initial = new LinkedHashSet<>();
    private Set<Register> simplifyWorkList = new LinkedHashSet<>();
    private Set<Register> freezeWorkList = new LinkedHashSet<>();
    private Set<Register> spillWorkList = new LinkedHashSet<>();
    private Set<Register> spilledNodes = new LinkedHashSet<>();
    private Set<Register> coalescedNodes = new LinkedHashSet<>();
    private Set<Register> coloredNodes = new LinkedHashSet<>();

    private Stack<Register> selectStack = new Stack<>();

    private Set<Move> workListMoves = new LinkedHashSet<>();
    private Set<Move> frozenMoves = new LinkedHashSet<>();
    private Set<Move> constrainedMoves = new LinkedHashSet<>();
    private Set<Move> activeMoves = new LinkedHashSet<>();
    private Set<Move> coalescedMoves = new LinkedHashSet<>();

    private Set<Edge> adjSet = new LinkedHashSet<>();

    public RegAllocator(BackModule module) {
        this.module = module;
    }

    public void run() {
        preColored.addAll(module.getPhyRegisterHashMap().values());
        for (var i : module.getFunctionList()) {
            if (!i.isSystem()) visit(i);
        }
    }

    public void visit(BackFunction func) {
        while (true) {
            init();
            new LiveAnalysis(func, module).run();
            build(func);
            makeWorkList();
//            int number = 0;
            while (!simplifyWorkList.isEmpty() || !workListMoves.isEmpty() || !freezeWorkList.isEmpty() || !spillWorkList.isEmpty()){
//                if (!selectStack.isEmpty()) {
//                    int a = 1;
//                }
//                if (number < 100) {
//                    System.out.print(simplifyWorkList.size());
//                    System.out.print(" ");
//                    System.out.print(workListMoves.size());
//                    System.out.print(" ");
//                    System.out.print(freezeWorkList.size());
//                    System.out.print(" ");
//                    System.out.print(spillWorkList.size());
//                    System.out.println(" ");
//                }
                if (!simplifyWorkList.isEmpty()) simplify();
                else if (!workListMoves.isEmpty()) coalesce();
                else if (!freezeWorkList.isEmpty()) freeze();
                else selectSpill();
//                if (func.getId().equals("main")) {
//                    int a = 0;
//                }
//                number++;
            }
//            System.out.println("++");
//            System.out.println(number);
            assignColors();
            if (!spilledNodes.isEmpty()) {
                rewriteProgram(func);
            }
            else break;
        }
        allocate(func);
    }
    

    public void init() {
        initial.clear();
        simplifyWorkList.clear();
        freezeWorkList.clear();
        spillWorkList.clear();
        spilledNodes.clear();
        coalescedMoves.clear();
        coalescedNodes.clear();
        selectStack.clear();
        frozenMoves.clear();
        activeMoves.clear();
        workListMoves.clear();
        adjSet.clear();
        constrainedMoves.clear();
        coloredNodes.clear();
    }

    private void addEdge(Register u, Register v) {
        if (u != v && !adjSet.contains(new Edge(u, v))) {
            adjSet.add(new Edge(u, v));
            adjSet.add(new Edge(v, u));
            if (!preColored.contains(u)) {
                u.adjSet.add(v);
                u.degree++;
            }
            if (!preColored.contains(v)) {
                v.adjSet.add(u);
                v.degree++;
            }
        }
    }

    public void build(BackFunction function) {
//        int number = 0;
        for (var bb : function.getBbList()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if (function.getId().equals("main")){
//                    System.out.println(inst);
//                    number++;
//                    if (number < 100){
////                        System.out.println(inst);
//                    }
                }
                if (inst.getDefReg() != null ) initial.add(inst.getDefReg());
                var k = inst.getUseReg();
                initial.addAll(inst.getUseReg());
//                int a = 0;
            }
        }
//        System.out.println(number);
//        int a = initial.size();
        for (var i : module.getPhyRegName()) {
            var reg = module.getPhyRegisterHashMap().get(i);
            reg.color = i;
            preColored.add(reg);
            reg.degree = MAX_DEG;
        }
        initial.addAll(preColored);
        for (var reg : initial) {
//            if (reg == null) {
//                int a =1;
//            }
//            if (reg.adjSet == null) {
//                int  a =1;
//            }
            assert reg.adjSet != null;
            reg.adjSet.clear();
            reg.moveSet.clear();
            reg.alias = null;
            reg.spillAddr = null;
        }
        initial.removeAll(preColored);
        for (var reg : initial) {
            reg.degree = 0;
            reg.color = null;
        }
//        int count = 0;
        for (var bb : function.getBbList()) {
            Set<Register> live = new HashSet<>(bb.getLiveOut());
            for (var inst = bb.getTail(); inst != null; inst = inst.getPrev()) {
                if (inst instanceof Move) {
                    live.removeAll(inst.getUseReg());
                    if (inst.getDefReg() != null) inst.getDefReg().moveSet.add((Move) inst);
//                    Backend.Inst.Inst finalInst = inst;
//                    inst.getUseReg().forEach(i->i.moveSet.add((Move) finalInst));
                    for (var i : inst.getUseReg()) i.moveSet.add(((Move) inst));
                    workListMoves.add((Move) inst);
                    if (function.getId().equals("main")) {
                        int o = 0;
                    }
                }
                List<Register> defs = new ArrayList<>();
                if (inst.getDefReg() != null) defs.add(inst.getDefReg());
                if (inst instanceof Call) {
                    for (var i : module.getCallerSaveRegisterName()) {
                        defs.add(module.getPhyRegisterHashMap().get(i));
                    }
                }
                if(inst instanceof Store && ((Store) inst).getSrc2() != null)
                    addEdge(((Store) inst).getSrc(), ((Store) inst).getSrc2());

                for (var def :defs) {
                    for (var l : live) {
//                        if (count < 100) {
////                            System.out.println(def.getId());
////                            System.out.println(l.getId());
//                        }
//                        count++;
                        addEdge(def, l);
                    }
                }
                live.removeAll(defs);
                live.addAll(inst.getUseReg());
            }
        }
    }

    private Set<Move> nodeMoves(Register reg) {
        Set<Move> ret = new HashSet<>(reg.moveSet);
        Set<Move> tmp = new HashSet<>(activeMoves);
        tmp.addAll(workListMoves);
        ret.retainAll(tmp);
        return ret;
    }

    private boolean moveRelated(Register reg) {
        return !nodeMoves(reg).isEmpty();
    }

    private void makeWorkList() {
        for (var reg : initial) {
            if (reg.degree >= k) spillWorkList.add(reg);
            else if (moveRelated(reg)) freezeWorkList.add(reg);
            else simplifyWorkList.add(reg);
        }
        initial.clear();
    }

    private Set<Register> adjacent(Register reg) {
        Set<Register> ret = new HashSet<>(reg.adjSet);
        ret.removeAll(selectStack);
        ret.removeAll(coalescedNodes);
        return ret;
    }

    private void enableMoves(Set<Register> regs) {
        for (var i :regs) {
            for (var move : nodeMoves(i)) {
                if (activeMoves.contains(move)) {
                    activeMoves.remove(move);
                    workListMoves.add(move);
                }
            }
        }
    }

    private void decrementDegree(Register reg) {
        int d = reg.degree;
        reg.degree = d - 1;
        if (d == k) {
            var tmp = adjacent(reg);
            tmp.add(reg);
            enableMoves(tmp);
            spillWorkList.remove(reg);
            if (moveRelated(reg)) freezeWorkList.add(reg);
            else simplifyWorkList.add(reg);
        }
    }

    private void simplify() {
        Register reg = simplifyWorkList.iterator().next();
        simplifyWorkList.remove(reg);
        selectStack.push(reg);
        adjacent(reg).forEach(this::decrementDegree);
//        for (var i : adjacent(reg)) decrementDegree(i);
    }

    private Register getAlias(Register reg) {
        if (coalescedNodes.contains(reg))
            return getAlias(reg.alias);
        else return reg;
    }

    private void addWorkList(Register reg) {
        if (!preColored.contains(reg) && !moveRelated(reg) && reg.degree < k) {
            freezeWorkList.remove(reg);
            simplifyWorkList.add(reg);
        }
    }

    private boolean OK(Register u, Register v) {
//        System.out.println(u.degree);
//        System.out.println( preColored.contains(u));
//        System.out.println(adjSet.contains(new Edge(u, v)));
        return u.degree < k || preColored.contains(u) || adjSet.contains(new Edge(u, v));
    }


    private boolean conservative(Set<Register> nodes) {
        int a = 0;
        for (var i : nodes) {
            if (i.degree >= k) a++;
        }
        return a < k;
    }


    private void conbine(Register u, Register v) {
        freezeWorkList.remove(v);
        spillWorkList.remove(v);
        coalescedNodes.add(v);
        v.alias = u;
        u.moveSet.addAll(v.moveSet);
        enableMoves(Collections.singleton(v));
        for (var i : adjacent(v)) {
            addEdge(i, u);
            decrementDegree(i);
        }
        if (u.degree >= k && freezeWorkList.contains(u)) {
            freezeWorkList.remove(u);
            spillWorkList.add(u);
        }
    }

    private void coalesce() {
        Move m = workListMoves.iterator().next();
        Register x = getAlias(m.getSrc());
        Register y = getAlias(m.getRd());
//        Register x = getAlias(m.getRd());
//        Register y = getAlias(m.getSrc());
        Register u, v;
        if (preColored.contains(y)) {
            u = y; v = x;
        }
        else {
            u = x; v = y;
        }
        workListMoves.remove(m);
        if (u == v) {
            coalescedMoves.add(m);
            addWorkList(u);
        } else if (preColored.contains(v) || adjSet.contains(new Edge(u, v))) {
            constrainedMoves.add(m);
            addWorkList(u); addWorkList(v);
        }
        else {
//            boolean cond1 = preColored.contains(u);
//            for (Register i : adjacent(v)) cond1 &= OK(i, u);
//            boolean cond2 = !preColored.contains(u);
//            Set<Register> tmpSet = adjacent(u);
//            tmpSet.addAll(adjacent(v));
//            cond2 &= conservative(tmpSet);
//            if (cond1 || cond2) {
//                coalescedMoves.add(m);
//                conbine(u, v);
//                addWorkList(u);
//            }
//            else {
//                activeMoves.add(m);
//            }
            boolean cond = true;
            if(preColored.contains(u)){
                for(var nei : adjacent(v)) {
                    cond &= OK(nei, u);
//                    System.out.println(nei);
//                    System.out.println(u);
                }
            }
            else{
                var tmp = adjacent(u);
                tmp.addAll(adjacent(v));
                cond = conservative(tmp);
            }

            if(cond){
                coalescedMoves.add(m);
                conbine(u, v);
                addWorkList(u);
            }
            else{
                activeMoves.add(m);
            }
        }

    }

    private void freezeMoves(Register reg) {
        for (Move m : nodeMoves(reg)) {
            Register x = m.getRd();
            Register y = m.getSrc();
            Register v = getAlias(y) == getAlias(reg) ? getAlias(x) : getAlias(y);
            activeMoves.remove(m);
            frozenMoves.add(m);
            if (freezeWorkList.contains(v) && nodeMoves(v).isEmpty()) {
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        }
    }


    private void freeze() {
        var reg = freezeWorkList.iterator().next();
        freezeWorkList.remove(reg);
        simplifyWorkList.add(reg);
        freezeMoves(reg);
    }



    private void selectSpill(){
        // random policy
		var reg = spillWorkList.iterator().next();
        assert reg != null;
        spillWorkList.remove(reg);
        simplifyWorkList.add(reg);
        freezeMoves(reg);
    }

    private void assignColors() {
        int b = 0;
        while (!selectStack.isEmpty()) {
            var reg = selectStack.pop();
            Set<String> okColors = new HashSet<>(Arrays.asList(module.getAllocatableRegName()));
            for (var i : reg.adjSet) {
                var ii = getAlias(i);
                if (coloredNodes.contains(ii) || preColored.contains(ii)) {
                    okColors.remove(ii.color);
                }
            }
            if (okColors.isEmpty()) {
                spilledNodes.add(reg);
            }
            else {
                coloredNodes.add(reg);
                String c = null;
                for (var i : module.getCallerSaveRegisterName()) {
                    if (okColors.contains(i)) {
                        c = i;
                        break;
                    }
                }
                if (c == null) {
                    for (var i : module.getCalleeSaveRegisterName()) {
                        if (okColors.contains(i)) {
                            c = i;
                            break;
                        }
                    }
                }
                reg.color = c;
            }
        }
        for (Register reg : coalescedNodes) {
            assert getAlias(reg).color != null;
            reg.color = getAlias(reg).color;
        }
        int g = 0;
    }

    public void rewriteProgram(BackFunction function) {
        for (var i : spilledNodes) {
//            i.spillAddr = new StackAllocate(function, true, function.stackAllocFromBot(4));
            i.spillAddr = new StackAllocate(function, false);
        }
        for (var bb : function.getBbList()) {
            Inst nextins;
            for (var inst = bb.getHead(); inst != null; inst = nextins) {
                nextins = inst.getNext();
                for (var reg : inst.getUseReg()) {

                    if (reg.spillAddr != null) {

                        var a = new Int32("spill_" + reg.getId());
                        inst.pushFront(new Load(reg.spillAddr, a, 4));
                        inst.replaceUseReg(reg, a);
                    }

                }

                if (inst.getDefReg() != null && inst.getDefReg().spillAddr != null) {
                    var defReg = inst.getDefReg();
                    var tmp = new Int32("spill_" + inst.getDefReg().getId());
                    inst.replaceDefReg(tmp);
                    inst.pushBack(new Store(defReg.spillAddr, tmp, null, 4));
                }
            }
        }

    }

    public void allocate(BackFunction function) {
        for (var bb : function.getBbList()) {
            for (var inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                for (var reg : inst.getUseReg()) {
                    if (!(reg instanceof PhyRegister) && reg.color != null) {
                        inst.replaceUseReg(reg, module.getPhyRegisterHashMap().get(reg.color));
                    }
                }
                var defreg = inst.getDefReg();
                if (defreg != null && !(defreg instanceof PhyRegister) && defreg.color != null) {
                    inst.replaceDefReg(module.getPhyRegisterHashMap().get(defreg.color));
                }
            }
        }
    }


}

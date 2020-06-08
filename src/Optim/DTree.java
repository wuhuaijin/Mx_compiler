package Optim;

import IR.BB;
import IR.Function;
import IR.Module;

import java.util.List;

public class DTree extends Pass {
    public DTree(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for (var func : module.getFuncs()) {
            computeDTree(func, false);
            computeDTreeFrontier(func);
        }
        return false;
    }

    static public BB ancestorLowestSemi(BB v) {
        BB p = v.ancestor;
        if (p.ancestor != null) {
            BB q = ancestorLowestSemi(p);
            v.ancestor = p.ancestor;
            if (q.semiDom.reversePostOrderIndex < v.best.semiDom.reversePostOrderIndex)
                v.best = q;
        }
        return v.best;
    }
//
////
    static public void link(BB a, BB b) {
        b.ancestor = a;
        b.best = b;
    }
//
     static public void computeDTree(Function function, boolean ifPost) {
        if (ifPost) function.reverseOrderBBList();
        else function.setPreOrderBBList();

        List<BB> bbList = function.getPreOrderBBList();

        for (int i = 0; i < bbList.size(); ++i) {
            bbList.get(i).reversePostOrderIndex = i + 1;
        }
        bbList.forEach(BB::clear);

        for (int i = bbList.size() - 1; i >= 1; --i) {
            BB n = bbList.get(i);
            BB p = n.getFather();
            BB s = p;
            for (BB j : n.getPredecessors()) {
                BB ss = j.reversePostOrderIndex <= n.reversePostOrderIndex ? j : ancestorLowestSemi(j).semiDom;
                if (ss.reversePostOrderIndex < s.reversePostOrderIndex)
                    s = ss;
            }
            n.semiDom = s;
            s.bucket.add(n);
            link(p, n);
            for (BB j : p.bucket) {
                BB y = ancestorLowestSemi(j);
                if (y.semiDom == j.semiDom) j.iDom = p;
                else j.sameDom = y;
            }
            p.bucket.clear();
        }

        for (int i = 1; i < bbList.size(); ++i) {
            BB n = bbList.get(i);
            if (n.sameDom != null) {
                n.iDom = n.sameDom.iDom;
            }
        }

        for (var i : bbList) {
            i.iDomChildren.clear();
        }

        for (var i : bbList) {
            if (i.iDom != null)
                i.iDom.iDomChildren.add(i);
        }

    }

    static public void computeDTreeFrontier(Function function) {
        List<BB> bbList = function.getPreOrderBBList();
        bbList.forEach(i->i.domFrontier.clear());
        for (var i : bbList) {
            for (var j : i.getPredecessors()) {
                BB x = j;
                while (x != i.iDom) {
                    x.domFrontier.add(i);
                    x = x.iDom;
                }
            }
        }
    }


}



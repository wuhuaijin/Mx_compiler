package IR;

import AST.*;
import Frontend.Scope;
import IR.Instruction.*;
import IR.Operand.*;
import SymbolTable.Symbol.FuncSymbol;
import SymbolTable.Symbol.VarSymbol;
import SymbolTable.Type.*;

import java.util.ArrayList;
import java.util.Stack;

public class IRBuilder implements ASTVisitor {
    private Module module;
    private Function currentfunction;
    private ClassType currentClass;
    private BB currentBB;
    private Stack<BB> loopContinue;
    private Stack<BB> loopBreak;
    private Scope scope;
    private ArrayList<Return> funcRets;
    private boolean ifReturn = false;

    public IRBuilder(Scope scope) {
        this.scope = scope;
        module = new Module();
        this.currentfunction = null;
        this.currentBB = null;
        this.currentClass = null;
        this.loopBreak = new Stack<>();
        this.loopContinue = new Stack<>();
        this.funcRets = new ArrayList<>();
    }

    public Module getModule() {
        return module;
    }

    public Function getCurrentfunction() {
        return currentfunction;
    }

    public BB getCurrentBB() {
        return currentBB;
    }

    @Override
    public void visit(MainProgNode node) {

    }

    @Override
    public void visit(ProgNode node) {
        Function init = new Function("__init");
        module.addFunc(init);
        BB startBB = new BB(Lable.getLable(), "initBB");
        currentBB = startBB;
        init.setInBB(startBB);

        // var
        for (var i : node.getDefList()) {
            if (i instanceof VarListNode) {
                for (var j : ((VarListNode) i).getVarDefList()) {
                    Pointer ptr = new Pointer(j.getVarName());
                    ptr.setGlobal(true);

                    getCurrentBB().addInstruction(new Allocate(currentBB, false, new Const(4), ptr));
                    if (j.getAssignExpr() != null) {
                        j.getAssignExpr().accept(this);
                        currentBB.addInstruction(new Store(currentBB, false, j.getAssignExpr().getResultOpr(), ptr));
                    }
                    module.addGlobalVar(ptr);

                    Int32 tmp = new Int32("tmp");
                    ((VarSymbol)j.getSymbol()).setStorage(tmp);
                    tmp.setPtr(ptr);
                }
            }
        }
        currentBB.addInstruction(new Call(currentBB, false, null, scope.getFuncSymbol("main").getFunction(), new ArrayList<>(), null));
        currentBB.addLastInstruction(new Return(currentBB, true, null));
        init.setOutBB(currentBB);

        for (var i : node.getDefList()) {
            if (i instanceof FuncDefNode || i instanceof ClassDefNode)
                i.accept(this);
        }

    }

    @Override
    public void visit(VarListNode node) {
        for (var i : node.getVarDefList())
            i.accept(this);
    }

    @Override
    public void visit(VarDefNode node) {
        Int32 var = new Int32(node.getVarName());
//        System.out.println(node.getVarName());
//        System.out.println(var);
        ((VarSymbol) node.getSymbol()).setStorage(var);
//        System.out.println(node.getSymbol());
        if (node.getAssignExpr() != null) {
            node.getAssignExpr().accept(this);
            currentBB.addInstruction(new Move(currentBB, false, change(node.getAssignExpr().getResultOpr()), var));
        }
    }

    @Override
    public void visit(FuncDefNode node) {
        funcRets.clear();
        Function func = node.getSymbol().getFunction();
        module.addFunc(func);
        currentfunction = func;

        if (currentfunction.isMethod()) {
            func.setObj(new Int32("this"));
        }

        BB funcIn = new BB(Lable.getLable(), "funcIn");
        func.setInBB(funcIn);
        currentBB = funcIn;

        for (var i : node.getSymbol().getParaList()) {
            Int32 val = new Int32(i.getId());
            func.addPara(val);
            i.setStorage(val);
        }
        node.getStatNode().accept(this);

        boolean hasRetVal = true;
        if (node.getTypetype().equals(new VoidType()) || node.getTypetype().equals(new NullType())) hasRetVal = false;

        if (!(currentBB.getTail() instanceof Return) && !(currentBB.getTail() instanceof Jump)) {
            if (hasRetVal) currentBB.addLastInstruction(new Return(currentBB, true, new Const(0)));
            else currentBB.addLastInstruction(new Return(currentBB, true, null));
            funcRets.add((Return) currentBB.getTail());
        }
        if (funcRets.size() > 1) {
            BB outBB = new BB(Lable.getLable());
            func.setOutBB(outBB);
            Int32 retVal = hasRetVal ? new Int32("retVal") : null;
            for (var i : funcRets) {
                i.delete();
                if (i.getRetVar() != null) i.getBasicBlock().addInstruction(new Move(i.getBasicBlock(), false, i.getRetVar(), retVal));
                i.getBasicBlock().addLastInstruction(new Jump(i.getBasicBlock(), true, outBB));
            }
            outBB.addLastInstruction(new Return(outBB, true, retVal));
        }
        else {
            currentfunction.setOutBB(funcRets.get(0).getBasicBlock());
        }
        currentfunction = null;
    }

    @Override
    public void visit(ClassDefNode node) {
        currentClass = ((ClassType) node.getType());
        for (var i : node.getFuncList()) {
            i.accept(this);
        }
        if (node.getCreator() != null ) node.getCreator().accept(this);
        currentClass = null;
    }

    @Override
    public void visit(ParaListNode node) {}

    @Override
    public void visit(VardefStatNode node) {
        for (var i : node.getVarDefList()) {
            i.accept(this);
        }
    }

    @Override
    public void visit(IfStatNode node) {
        node.getCond().accept(this);
        Operand cond = change(node.getCond().getResultOpr());
        BB b1 = new BB(Lable.getLable(), "then");
        currentfunction.addBB(b1);
        BB b3 = new BB(Lable.getLable(), "out");
        if (node.getElseContent() != null) {
            BB b2 = new BB(Lable.getLable(), "else");
            currentBB.addLastInstruction(new Branch(currentBB, true, cond, b1, b2));
            currentBB = b1;
            node.getMainContent().accept(this);
            currentBB.addLastInstruction(new Jump(currentBB, true, b3));
            currentBB = b2;
            node.getElseContent().accept(this);
            currentBB.addLastInstruction(new Jump(currentBB, true, b3));
            currentBB = b3;
        }
        else {
            currentBB.addLastInstruction(new Branch(currentBB, true, cond, b1, b3));
            currentBB = b1;
            if (node.getMainContent() != null) node.getMainContent().accept(this);
            currentBB.addLastInstruction(new Jump(currentBB, true, b3));
            currentBB = b3;
        }
    }

    @Override
    public void visit(ForStatNode node) {
        BB condBB = new BB(Lable.getLable());
        BB stepBB = new BB(Lable.getLable());
        BB bodyBB = new BB(Lable.getLable());
        BB outBB = new BB(Lable.getLable());

        loopBreak.push(outBB);
        loopContinue.push(stepBB);

        if (node.getInit() != null) {
            node.getInit().accept(this);
        }

        currentBB.addLastInstruction(new Jump(currentBB, true, condBB));
        currentBB = condBB;
        if (node.getCond() != null) node.getCond().accept(this);
        Operand cond = (node.getCond() == null) ? new Const(1) : change(node.getCond().getResultOpr());
        currentBB.addLastInstruction(new Branch(currentBB, true, cond, bodyBB, outBB));
        currentBB = stepBB;
        if (node.getStep() != null) node.getStep().accept(this);
        currentBB.addLastInstruction(new Jump(currentBB, true, condBB));
        currentBB = bodyBB;
        if (node.getContent() != null)node.getContent().accept(this);
        currentBB.addLastInstruction(new Jump(currentBB, true, stepBB));
        currentBB = outBB;
        loopContinue.pop();
        loopBreak.pop();

    }

    @Override
    public void visit(WhileStatNode node) {
        BB condBB = new BB(Lable.getLable());
        BB stepBB = new BB(Lable.getLable());
        BB bodyBB = new BB(Lable.getLable());
        BB outBB = new BB(Lable.getLable());

        loopBreak.push(outBB);
        loopContinue.push(condBB);

        currentBB.addLastInstruction(new Jump(currentBB, true, condBB));
        currentBB = condBB;
        if (node.getCond() != null) node.getCond().accept(this);
        Operand cond = (node.getCond() == null) ? new Const(1) : change(node.getCond().getResultOpr());
        currentBB.addLastInstruction(new Branch(currentBB, true, cond, bodyBB, outBB));
        currentBB = bodyBB;
        if (node.getStatList() != null) node.getStatList().accept(this);
        currentBB.addLastInstruction(new Jump(currentBB, true, condBB));
        currentBB = outBB;
        loopContinue.pop();
        loopBreak.pop();
    }

    @Override
    public void visit(BreakStatNode node) {
        currentBB.addLastInstruction(new Jump(currentBB, true, loopBreak.peek()));
    }

    @Override
    public void visit(ContinueStatNode node) {
        currentBB.addLastInstruction(new Jump(currentBB, true, loopContinue.peek()));
    }

    @Override
    public void visit(ReturnStatNode node) {
        if (node.getReturnExpr() != null) {
            if (node.getReturnExpr() instanceof FunccallExprNode) {
//                if (((FunccallExprNode) node.getReturnExpr()).getFuncName().equals(currentfunction.getFuncName())) ifReturn = true;
                if (((FuncSymbol) ((FunccallExprNode) node.getReturnExpr()).getFuncName().getSymbol()).getFunction() == currentfunction) ifReturn = true;
            }
            node.getReturnExpr().accept(this);
            currentBB.addInstruction(new Return(currentBB, true, change(node.getReturnExpr().getResultOpr())));
        }
        else {
            currentBB.addInstruction(new Return(currentBB, true, null));
        }

        if (currentBB.getTail() instanceof Return) funcRets.add(((Return) currentBB.getTail()));
    }

    @Override
    public void visit(ExprStatNode node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(BlockStatNode node) {
        if (node.getStat() != null) {
            for (var i : node.getStat()) {
                if (i != null) i.accept(this);
            }
        }
    }

    @Override
    public void visit(PostfixExprNode node) {
        node.getExpr().accept(this);
        PostfixExprNode.Op op = node.getOp();
        Operand val = node.getExpr().getResultOpr();
        switch (op) {
            case add: {
                Operand tmp = new Int32("tmp");
                if (val instanceof Pointer) {
                    currentBB.addInstruction(new Load(currentBB, false, tmp, val));
                    Int32 tmp2 = new Int32("tmp2");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, new Const(1), tmp, tmp2));
                    currentBB.addInstruction(new Store(currentBB, false, tmp2, val));
                } else {
                    currentBB.addInstruction(new Move(currentBB, false, val, tmp));
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, new Const(1), val, val));
                }
                node.setResultOpr(tmp);
                break;
            }
            case sub: {
                Operand tmp = new Int32("tmp");
                if (val instanceof Pointer) {
                    currentBB.addInstruction(new Load(currentBB, false, tmp, val));
                    Int32 tmp2 = new Int32("tmp2");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.sub, tmp, new Const(1), tmp2));
                    currentBB.addInstruction(new Store(currentBB, false, tmp2, val));
                } else {
                    currentBB.addInstruction(new Move(currentBB, false, val, tmp));
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.sub, val, new Const(1), val));
                }
                node.setResultOpr(tmp);
                break;
            }
        }
    }

    @Override
    public void visit(NewExprNode node) {
        Int32 res = new Int32("res");
        if (node.getType() instanceof ClassType) {
            currentBB.addInstruction(new Allocate(currentBB, false, new Const(node.getType().getSize()), res));
            if (((ClassType) node.getType()).getCreator() != null) {
                currentBB.addInstruction(new Call(currentBB, false, null, ((ClassType) node.getType()).getCreator().getFunction(), new ArrayList<>(), res));
            }
        }
        else if (node.getType() instanceof ArrayType) {
            int dimension = node.getDimension();
            for (var i : node.getDimList()) {
                if (i != null) {
                    i.accept(this);
                }
                else {
                    dimension--;
                }
            }
            int size = node.getClassType() == null ? 4 : node.getClassType().getSize();
            getArray(node.getDimList(), dimension, 0, size, res);
//            System.out.println(node.getType());
//            System.out.println(node.getType().getId());
//            System.out.println(node.getType().getSize());
        }
        node.setResultOpr(res);
    }

    public void getArray(ArrayList<ExprNode> dimList, int dim, int curdim, int classSize, Operand result) {
        ExprNode dimExpr = dimList.get(curdim);
        Operand size = change(dimExpr.getResultOpr());
        Int32 space = new Int32("space");

        if(curdim != dim - 1) {
            currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.mul, size, new Const(4), space));
        }
        else {
            currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.mul, size, new Const(classSize), space));
        }
//        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.mul, size, new Const(4), space));
        Int32 spaceIncludeSize = new Int32("tot");

        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, space, new Const(4), spaceIncludeSize));
        if (result instanceof Int32) {
            currentBB.addInstruction(new Allocate(currentBB, false, spaceIncludeSize, result));
            currentBB.addInstruction(new Store(currentBB, false, size, result));
            currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, result, new Const(4), result));
        }
        else {
            //todo : maybe exist a bug
            Operand tmp = new Int32("tmp");
            currentBB.addInstruction(new Allocate(currentBB, false, spaceIncludeSize, tmp));
//            currentBB.addInstruction(new Store(currentBB, false, size, tmp));
            currentBB.addInstruction(new Store(currentBB, false, tmp, result));
            currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, tmp, new Const(4), tmp));
        }
        if (curdim != dim - 1) {
            BB condBB = new BB(Lable.getLable());
            BB loopBB = new BB(Lable.getLable());
            BB outBB = new BB(Lable.getLable());

            Int32 index = new Int32("index");
            currentBB.addInstruction(new Move(currentBB, false, new Const(0), index));
            Pointer ptr = new Pointer("ptr");
            currentBB.addInstruction(new Move(currentBB, false, result, ptr));
            currentBB.addLastInstruction(new Jump(currentBB, true, condBB));

            Int32 tmp = new Int32("tmp");
            condBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.slt, index, size, tmp));
            condBB.addLastInstruction(new Branch(currentBB, true, tmp, loopBB, outBB));

            currentBB = loopBB;
            getArray(dimList, dim, curdim + 1, classSize, ptr);
            currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, new Const(1), index, index));
            currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, new Const(4), ptr, ptr));
            currentBB.addLastInstruction(new Jump(currentBB, true, condBB));

            currentBB = outBB;


        }

    }


    public void visit(FunccallExprNode node) {
        Operand obj = null;
        Operand res;
        if (node.getType().equals(new VoidType())) res = null;
        else res = new Int32("res");

        node.getFuncName().accept(this);
//        System.out.println(node.getFuncName().getSymbol().getId());
//        System.out.println(node.getFuncName().getSymbol());
        Function func = ((FuncSymbol) node.getFuncName().getSymbol()).getFunction();
        ArrayList<Operand> paras = new ArrayList<>();
        for (var i : node.getParaList()) {
            i.accept(this);
            paras.add(change(i.getResultOpr()));
        }
        if (func.isMethod()) {
            if (node.getFuncName() instanceof MemberExprNode) obj = change(((MemberExprNode) node.getFuncName()).getExpr().getResultOpr());
            else obj = currentfunction.getObj();
        }



        if (func == currentfunction && ifReturn) {
            ArrayList<Int32> tmpList = new ArrayList<>();
            ifReturn = false;
            for (var i = 0; i < paras.size(); i++) {
                var tmp = new Int32("para_tmp");
                currentBB.addInstruction(new Move(currentBB, false, paras.get(i), tmp));
                tmpList.add(tmp);
            }
            for (var i = 0; i < paras.size(); i++) {
                currentBB.addInstruction(new Move(currentBB, false, tmpList.get(i), currentfunction.getParas().get(i)));
            }
            currentBB.addInstruction(new Jump(currentBB, true, currentfunction.getInBB()));
            return;
        }
        currentBB.addInstruction(new Call(currentBB, false, res, func, paras, obj));
        node.setResultOpr(res);

    }

    @Override
    public void visit(ArrayExprNode node) {
        node.getBaseExpr().accept(this);
        node.getIndexExpr().accept(this);
        Int32 baseAddr = ((Int32) change(node.getBaseExpr().getResultOpr()));
        Operand indexVal = change(node.getIndexExpr().getResultOpr());
        Int32 tmpindex = new Int32("tmp_index");
        Int32 tmpoffset = new Int32("tmp_offset");
        Pointer resAddr = new Pointer("res_addr");
        int size = node.getType() instanceof ArrayType ? 4 : node.getType().getSize();
//        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, indexVal, new Const(1), tmpindex));
        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.mul, indexVal, new Const(size), tmpoffset));
        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, tmpoffset, baseAddr, resAddr));
        node.setResultOpr(resAddr);
    }

    @Override
    public void visit(MemberExprNode node) {
        node.getExpr().accept(this);
        if (node.getSymbol() instanceof VarSymbol) {
            Int32 addr = ((Int32) change(node.getExpr().getResultOpr()));
            Pointer ptr = new Pointer("resAddr");
//            System.out.println(node.getSymbol().getId());
//            System.out.println(((VarSymbol) node.getSymbol()).getOffset());
            currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, addr, new Const(((VarSymbol) node.getSymbol()).getOffset()), ptr));
            node.setResultOpr(ptr);
        }
    }

    @Override
    public void visit(PrefixExprNode node) {
        node.getExpr().accept(this);
        PrefixExprNode.Op op = node.getOp();
        Operand val = node.getExpr().getResultOpr();
        switch (op) {
            case addadd: {

                if (val instanceof Pointer) {
                    Int32 tmp = new Int32("tmp");
                    currentBB.addInstruction(new Load(currentBB, false, tmp, val));

                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, new Const(1), tmp, tmp));
                    currentBB.addInstruction(new Store(currentBB, false, tmp, val));
                    node.setResultOpr(tmp);
                } else {
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, new Const(1), val, val));
                    node.setResultOpr(val);
                }

                break;
            }
            case subsub: {

                if (val instanceof Pointer) {
                    Int32 tmp = new Int32("tmp");
                    currentBB.addInstruction(new Load(currentBB, false, tmp, val));
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.sub, tmp, new Const(1), tmp));
                    currentBB.addInstruction(new Store(currentBB, false, tmp, val));
                    node.setResultOpr(tmp);
                } else {
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.sub, val, new Const(1), val));
                    node.setResultOpr(val);
                }

                break;
            }
            case not: {
                Operand res = new Int32("res");
                BB b0 = new BB(Lable.getLable());
                BB b1 = new BB(Lable.getLable());
                BB resbb = new BB(Lable.getLable());
                currentBB.addLastInstruction(new Branch(currentBB, true, val, b1, b0));
                b1.addInstruction(new Move(b1, false, new Const(0), res));
                b0.addInstruction(new Move(b0, false, new Const(1), res));
                b0.addLastInstruction(new Jump(b0, true, resbb));
                b1.addLastInstruction(new Jump(b1, true, resbb));
                currentBB = resbb;
                node.setResultOpr(res);
                break;
            }
            case add: {
                node.setResultOpr(val);
                break;
            }
            case sub: {
                Operand res = new Int32("res");
                currentBB.addInstruction(new UnaryOp(currentBB, false, res, change(val), UnaryOp.Op.NEG));
                node.setResultOpr(res);
                break;
            }
            case bitnot: {
                Operand res = new Int32("res");
                currentBB.addInstruction(new UnaryOp(currentBB, false, res, change(val), UnaryOp.Op.NOT));
                node.setResultOpr(res);
                break;
            }
        }
    }

    public Operand change(Operand operand) {
        if (operand instanceof Pointer) {
            Int32 val = new Int32("val");
            currentBB.addInstruction(new Load(currentBB, false, val, operand));
            return val;
        }
        else return operand;
    }

    @Override
    public void visit(BinaryExprNode node) {
        if (node.getOp() != BinaryExprNode.Op.and && node.getOp() != BinaryExprNode.Op.or) {
//            if (node.getOp() == BinaryExprNode.Op.ge) {
//                int a = 0;
//            }
            node.getLs().accept(this);
            node.getRs().accept(this);
            switch (node.getOp()) {
                case sub : {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.sub, ls, rs, res));
                    node.setResultOpr(res);
                    break;
                }
                case div: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.div, ls, rs, res));
                    node.setResultOpr(res);
                    break;
                }
                case bitand: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.and, ls, rs, res));
                    node.setResultOpr(res);
                    break;
                }
                case mod: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.mod, ls, rs, res));
                    node.setResultOpr(res);
                    break;
                }
                case shiftleft: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.shl, ls, rs, res));
                    node.setResultOpr(res);
                    break;
                }
                case shiftright: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.shr, ls, rs, res));
                    node.setResultOpr(res);
                    break;
                }
                case mul: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.mul, ls, rs, res));
                    node.setResultOpr(res);
                    break;
                }
                case bitxor: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.xor, ls, rs, res));
                    node.setResultOpr(res);
                    break;
                }
                case bitor: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.or, ls, rs, res));
                    node.setResultOpr(res);
                    break;
                }
                case add: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    if (node.getLs().getType().equals(new StringType())) {
                        ArrayList<Operand> para = new ArrayList<>();
                        para.add(ls);
                        para.add(rs);
                        currentBB.addInstruction(new Call(currentBB, false, res, SystemFunc.StrAdd, para, null));
                    }
                    else {
                        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, ls, rs, res));
                    }
                    node.setResultOpr(res);
                    break;
                }
                case le: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    if (node.getLs().getType().equals(new StringType())) {
                        ArrayList<Operand> para = new ArrayList<>();
                        para.add(ls);
                        para.add(rs);
                        currentBB.addInstruction(new Call(currentBB, false, res, SystemFunc.StrLt, para, null));
                    }
                    else {
                        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.slt, ls, rs, res));
                    }
                    node.setResultOpr(res);
                    break;
                }
                case leq: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    if (node.getLs().getType().equals(new StringType())) {
                        ArrayList<Operand> para = new ArrayList<>();
                        para.add(ls);
                        para.add(rs);
                        currentBB.addInstruction(new Call(currentBB, false, res, SystemFunc.StrLe, para, null));
                    }
                    else {
                        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.sle, ls, rs, res));
                    }
                    node.setResultOpr(res);
                    break;
                }
                case ge: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    if (node.getLs().getType().equals(new StringType())) {
                        ArrayList<Operand> para = new ArrayList<>();
                        para.add(ls);
                        para.add(rs);
                        currentBB.addInstruction(new Call(currentBB, false, res, SystemFunc.StrGt, para, null));
                    }
                    else {
                        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.sgt, ls, rs, res));
                    }
                    node.setResultOpr(res);
                    break;
                }
                case geq: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    if (node.getLs().getType().equals(new StringType())) {
                        ArrayList<Operand> para = new ArrayList<>();
                        para.add(ls);
                        para.add(rs);
                        currentBB.addInstruction(new Call(currentBB, false, res, SystemFunc.StrGe, para, null));
                    }
                    else {
                        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.sge, ls, rs, res));
                    }
                    node.setResultOpr(res);
                    break;
                }
                case eq: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    if (node.getLs().getType().equals(new StringType())) {
                        ArrayList<Operand> para = new ArrayList<>();
                        para.add(ls);
                        para.add(rs);
                        currentBB.addInstruction(new Call(currentBB, false, res, SystemFunc.StrEq, para, null));
                    }
                    else {
                        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.seq, ls, rs, res));
                    }
                    node.setResultOpr(res);
                    break;
                }
                case neq: {
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand rs = change(node.getRs().getResultOpr());
                    Operand res = new Int32("res");
                    if (node.getLs().getType().equals(new StringType())) {
                        ArrayList<Operand> para = new ArrayList<>();
                        para.add(ls);
                        para.add(rs);
                        currentBB.addInstruction(new Call(currentBB, false, res, SystemFunc.StrNeq, para, null));
                    }
                    else {
                        currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.sne, ls, rs, res));
                    }
                    node.setResultOpr(res);
                    break;
                }
                case assign: {
                    Operand rs = change(node.getRs().getResultOpr());
                    if (node.getLs().getResultOpr() instanceof Pointer) {
                        currentBB.addInstruction(new Store(currentBB, false, rs, node.getLs().getResultOpr()));
                    }
                    else {
                        currentBB.addInstruction(new Move(currentBB, false, rs, node.getLs().getResultOpr()));
                    }
                    break;
                }
            }
        }
        else {
            switch (node.getOp()) {
                case and: {
                    node.getLs().accept(this);
                    Operand ls = change(node.getLs().getResultOpr());

                    Operand res = new Int32("res");
                    BB b1 = new BB(Lable.getLable());
                    BB b0 = new BB(Lable.getLable());
                    BB b2 = new BB(Lable.getLable());
                    currentBB.addLastInstruction(new Branch(currentBB, true, ls, b1, b0));
                    b0.addInstruction(new Move(b0, false, new Const(0), res));
                    b0.addLastInstruction(new Jump(b0, true, b2));
                    currentBB = b1;
                    node.getRs().accept(this);
                    Operand rs = change(node.getRs().getResultOpr());
                    currentBB.addInstruction(new Move(currentBB, false, rs, res));
                    currentBB.addLastInstruction(new Jump(currentBB, true, b2));
                    node.setResultOpr(res);
                    currentBB = b2;
                    break;
                }
                case or: {
                    node.getLs().accept(this);
                    Operand ls = change(node.getLs().getResultOpr());
                    Operand res = new Int32("res");
                    BB b1 = new BB(Lable.getLable());
                    BB b0 = new BB(Lable.getLable());
                    BB b2 = new BB(Lable.getLable());
                    currentBB.addLastInstruction(new Branch(currentBB, true, ls, b1, b0));
                    b1.addInstruction(new Move(b1, false, new Const(1), res));
                    b1.addLastInstruction(new Jump(b1, true, b2));
                    currentBB = b0;
                    node.getRs().accept(this);
                    Operand rs = change(node.getRs().getResultOpr());
                    currentBB.addInstruction(new Move(currentBB, false, rs, res));
                    currentBB.addLastInstruction(new Jump(currentBB, true, b2));
                    node.setResultOpr(res);
                    currentBB = b2;
                    break;
                }
            }

        }


    }

    @Override
    public void visit(ThisExprNode node) {
        node.setResultOpr(getCurrentfunction().getObj());
    }

    @Override
    public void visit(ConstExprNode node) {
        if (node.getConstType() == ConstExprNode.ConstType.INT) {
//            System.out.println(node.getValue());
//            int b = 3;
//            Operand a = new Const(b);
//            System.out.println(a.toString());
//            System.out.println(node.getValue().getClass());
//            int a = ((int) node.getValue());
            node.setResultOpr(new Const(((Integer) node.getValue())));
        }
        else if (node.getConstType() == ConstExprNode.ConstType.BOOL) {
            node.setResultOpr(new Const((Integer) node.getValue()));
        }
        else if (node.getConstType() == ConstExprNode.ConstType.STRING) {
            ConstString str = new ConstString((String) node.getValue());
            node.setResultOpr(str);
            module.addConstString(str);
        }
        else if (node.getConstType() == ConstExprNode.ConstType.NULL) {
            node.setResultOpr(new Const(0));
        }
    }

    @Override
    public void visit(IdExprNode node) {
        node.getScope().getSymbol(node.getId());
        if (node.getScope().getSymbol(node.getId()) instanceof VarSymbol) {
            VarSymbol symbol = node.getScope().getVarSymbol(node.getId());
            boolean flag = true;
            if (currentClass != null) {
                flag = currentClass.getMember(node.getId()) == null ? false : true;
            }
            if (node.getScope().inClassScope() && flag) {
                Pointer ptr = new Pointer("memberptr");
                currentBB.addInstruction(new BinaryOp(currentBB, false, BinaryOp.Op.add, currentfunction.getObj(), new Const(symbol.getOffset()), ptr));
                node.setResultOpr(ptr);
            }
            else {
//                System.out.println(symbol);
                if (symbol.getStorage() == null) {
                    node.setResultOpr(((VarSymbol) node.getSymbol()).getStorage());
                }
                else {
                    node.setResultOpr(symbol.getStorage());
                }
            }
        }
    }

    @Override
    public void visit(ExprListNode node) {}

    @Override
    public void visit(TypeNode node) {}

    @Override
    public void visit(ArrayTypeNode node) {}

    @Override
    public void visit(SystemTypeNode node) {}

}

package Frontend;

import AST.*;
import Helper.*;
import SymbolTable.Symbol.*;
import SymbolTable.Type.*;
import SymbolTable.TypeTable;

import java.util.ArrayList;
import java.util.Stack;

public class SemaChecker implements ASTVisitor{
    private Scope globalScope;
    private Stack<Scope> scopeStack;
    private TypeTable typeTable;


    public SemaChecker() {
        this.scopeStack = new Stack<>();
        this.typeTable = new TypeTable();
//        TypeNode c = new SystemTypeNode(new Location(0,0), "int");
//        TypeNode d = new TypeNode(new Location(0,0), "int");
//        System.out.println(c.equals(d));
//        System.out.println(typeTable.hasType(c));
        this.globalScope = new Scope(Scope.ScopeType.progScope,
                null, null, null);
        globalScope.sysFunc();
        StringType.addSystemMethods();
        ArrayType.addSystemMethods();
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public Scope getCurrentScope() {
        return scopeStack.peek();
    }

    public TypeTable getTypeTable() {
        return typeTable;
    }



    @Override
    public void visit(MainProgNode node) { }

    @Override
    public void visit(ProgNode node) throws SyntaxException {
        scopeStack.push(globalScope);

        boolean error = false;
        ArrayList<DefNode> defList = node.getDefList();
        //define classes and put them into typetable
        for (var i : defList) {
            if (i instanceof ClassDefNode) {
                if (((ClassDefNode) i).getClassName().equals("main")) throw new SyntaxException(node.getLocation(), "class name can't be main");
                TypeNode typeNode = new TypeNode(i.getLocation(),
                        ((ClassDefNode) i).getClassName()); //错
                ArrayList<VarSymbol> members = new ArrayList<>();
                ArrayList<FuncSymbol> methods = new ArrayList<>();
                for (var j : ((ClassDefNode) i).getVarList()) {
                    members.add(j.getVarSymbol(VarSymbol.SymbolType.member));
                }
                for (var j : ((ClassDefNode) i).getFuncList()) {
                    methods.add(j.getFuncSymbol(FuncSymbol.SymbolType.method));
                }
                FuncSymbol creator = ((ClassDefNode) i).getCreator() == null ? null : ((ClassDefNode) i).getCreator().getFuncSymbol(FuncSymbol.SymbolType.creator);
                ClassType classType = new ClassType(((ClassDefNode) i).getClassName(), members, methods, creator);
                ((ClassDefNode) i).setType(classType);
                try {
                    typeTable.put(typeNode, classType);
                } catch (SyntaxException e) {
                    System.out.println(e.getMessage());
                    error = true;
                }
            }
        }
        if (error) throw new SyntaxException();
        //define func and declare them
        for (var i : defList){
            if (i instanceof FuncDefNode) {
                FuncSymbol func = ((FuncDefNode) i).getFuncSymbol(FuncSymbol.SymbolType.func);
                try {
                    getCurrentScope().declareSymbol(i.getLocation(), func);
                } catch (SyntaxException e){
                    System.out.println(e.getMessage());
                    error = true;
                }
            }
        }
        if (error) throw new SyntaxException();

        // check if has main() func
        try{
            if (getCurrentScope().getSymbol("main") == null)
                throw new SyntaxException(new Location(0,0), "no main function");
//            getCurrentScope();
//            getCurrentScope().getSymbol("main");
//            TypeNode a = ((FuncSymbol) getCurrentScope().getSymbol("main")).getReturnType();
//            Type i = new IntType();
//            TypeNode c = new SystemTypeNode(new Location(0,0), "int");
//            System.out.println(typeTable.hasType(c));
//            Type b = typeTable.getType(new SystemTypeNode(new Location(0,0), "int"));
//            if (b == null) System.out.println("fuck");
//            System.out.println(b.getId());
            if (!(typeTable.getType(((FuncSymbol) getCurrentScope().getSymbol("main")).getReturnType()).equals(new IntType())))
                throw new SyntaxException(new Location(0,0), "main func's return type isn't int");
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }
        if (error) throw new SyntaxException();

        //define var and declare them
//        for (var i : defList) {
//            if (i instanceof VarDefNode) {
//                try {
//                    i.accept(this);
//                    VarSymbol var = ((VarDefNode) i).getVarSymbol(VarSymbol.SymbolType.global);
//                    getCurrentScope().declareSymbol(i.getLocation(), var);
//                } catch (SyntaxException e){
//                    System.out.println(e.getMessage());
//                    error = true;
//                }
//            }
//        }
        //resolve one by one

        for (var i : defList) {
            if (i instanceof ClassDefNode || i instanceof FuncDefNode) {
                try {
                    i.accept(this);
                } catch (SyntaxException e) {
                    System.out.println(e.getMessage());
                    error = true;
                }
            }
            if (i instanceof VarListNode) {
                try {
                    i.accept(this);
                    for (var j : ((VarListNode) i).getVarDefList()) {
                        VarSymbol var = j.getVarSymbol(VarSymbol.SymbolType.global);
                        if (typeTable.hasType(new TypeNode(node.getLocation(), j.getVarName())))
                            throw new SyntaxException(node.getLocation(), "var name can't be the same as class name");
                        getCurrentScope().declareSymbol(j.getLocation(), var);
                    }
                } catch (SyntaxException e){
                    System.out.println(e.getMessage());
                    error = true;
                }
            }
        }

        if (error) throw new SyntaxException();

    }

    @Override
    public void visit(TypeNode node) throws SyntaxException {
        boolean error = false;
        try{
//            System.out.println(node.getTypeName());
            if (!typeTable.hasType(node))
                throw new SyntaxException(node.getLocation(), "don't have a type");
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }
        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(VarDefNode node) throws SyntaxException {
        node.setScope(getCurrentScope());
        boolean error = false;

        try{
            if (node.getType().getTypeName().equals("void"))
                throw new SyntaxException(node.getLocation(), "var type can't be void");
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }
        if (error) throw new SyntaxException();

        try {
            node.getType().accept(this);
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }
        if (error) throw new SyntaxException();

        try {
            if (node.getAssignExpr() != null) {
                node.getAssignExpr().accept(this);
                if (Type.notEqualType(typeTable.getType(node.getType()), node.getAssignExpr().getType())) {
                    System.out.println("+++++");
                    System.out.println(typeTable.getType(node.getType()) instanceof ClassType);
                    throw new SyntaxException(node.getLocation(), "can't assign");
                }

            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(FuncDefNode node) throws SyntaxException {
        boolean error = false;
        Scope scope = new Scope(Scope.ScopeType.funcScope, getCurrentScope(), node.getType(), getCurrentScope().getClassType());
        scopeStack.push(scope);
        node.setScope(scope);
        // resolve return type
        try {
//            System.out.println("+++");
            node.getType().accept(this);
        } catch (SyntaxException e){
            System.out.println(e.getMessage());
            error = true;
        }
        if (error) throw new SyntaxException();
        //resolve var in paralist
        try {
//            node.getParaList()
            if (node.getFuncName().equals("main") && node.getParaList().size() != 0) throw new SyntaxException(node.getLocation(), "main function shouldn't have para");
            ArrayList<VarDefNode> varList = node.getParaList();
            ArrayList<VarSymbol> varSymbolList = getCurrentScope().getFuncSymbol(node.getFuncName()).getParaList();
            if (varList.size() != varSymbolList.size()) throw new SyntaxException(node.getLocation(), "para numbers not conformity");
            for (int i = 0; i < varList.size(); ++i) {
                varList.get(i).accept(this);
                scope.declareSymbol(node.getLocation(), varSymbolList.get(i));
            }
        } catch (SyntaxException e){
            System.out.println(e.getMessage());
            error = true;
        }
        if (error) throw new SyntaxException();
        //resole the function body
        try {
            node.getStatNode().accept(this);
            if (!(typeTable.getType(node.getType()).equals(new VoidType())) && !getCurrentScope().hasReturnStat() && !node.getFuncName().equals("main")) {
                Scope a = getCurrentScope();
//                getCurrentScope().
                throw new SyntaxException(node.getLocation(), "no return state");
            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }
        if (node.getType() != null) node.setTypetype(typeTable.getType(node.getType()));
        else node.setTypetype(null);

        scopeStack.pop();

        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(ClassDefNode node) throws SyntaxException {
        boolean error = false;
        Scope scope = new Scope(Scope.ScopeType.classScope, getCurrentScope(), null, typeTable.getType(new TypeNode(new Location(0, 0), node.getClassName()))); // 错
        scopeStack.push(scope);
        node.setScope(scope);

        ArrayList<VarDefNode> memberList = node.getVarList();
        try {
            for (var i : memberList) {
                i.accept(this);
                if (typeTable.hasType(new TypeNode(node.getLocation(), i.getVarName()))) throw new SyntaxException(node.getLocation(), "var name can't be the same as class name");
                scope.declareSymbol(node.getLocation(), i.getSymbol());
            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

        ArrayList<FuncDefNode> funcList = node.getFuncList();
        try {
            for (var i : funcList) {
                scope.declareSymbol(node.getLocation(), i.getSymbol());
            }
            for (var i : funcList) {
                i.accept(this);
            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

        FuncDefNode creator = node.getCreator();
        try {
            if (creator != null){
                scope.declareSymbol(node.getLocation(), creator.getSymbol());
                if (!(creator.getFuncName().equals(node.getClassName())))
                    throw new SyntaxException(node.getLocation(), "creator has a wrong name");
                creator.accept(this);
            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        scopeStack.pop();
        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(ParaListNode node) {}

    @Override
    public void visit(VardefStatNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;

        for (var i : node.getVarDefList()) {
            i.accept(this);
        }
        try {
            for (var j : node.getVarDefList()) {
                VarSymbol var = j.getVarSymbol(VarSymbol.SymbolType.local);
                if (typeTable.hasType(new TypeNode(node.getLocation(), j.getVarName())))
                    throw new SyntaxException(node.getLocation(), "var name can't be the same as class name");
                getCurrentScope().declareSymbol(j.getLocation(), var);
                j.setSymbol(var);
            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }
//        getCurrentScope().declareSymbol(node.getLocation(), node.getVarSymbol(VarSymbol.SymbolType.local));

        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(IfStatNode node) throws SyntaxException {
        node.setScope(getCurrentScope());
        boolean error = false;

        try {
            node.getCond().accept(this); // visit ExprNode
            if (!(node.getCond().getType().equals(new BoolType())))
                throw new SyntaxException(node.getLocation(), "condition not a bool type");
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

        try {
            if (node.getMainContent() != null){
                Scope scope = new Scope(Scope.ScopeType.blockScope, getCurrentScope(), getCurrentScope().getFuncReturnType(), getCurrentScope().getClassType());
                scopeStack.push(scope);
                node.getMainContent().accept(this);
                scopeStack.pop();
            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

        try {
            if (node.getElseContent() != null) {
                Scope scope = new Scope(Scope.ScopeType.blockScope, getCurrentScope(), getCurrentScope().getFuncReturnType(), getCurrentScope().getClassType());
                scopeStack.push(scope);
                node.getElseContent().accept(this);
                scopeStack.pop();
            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(ForStatNode node) throws SyntaxException {
        node.setScope(getCurrentScope());
        boolean error = false;

        try {
            if (node.getInit() != null)
                node.getInit().accept(this);
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

        try {
            if (node.getCond() != null) {
                node.getCond().accept(this);
                if (!(node.getCond().getType().equals(new BoolType())))
                    throw new SyntaxException(node.getLocation(), "condition not a bool type");
            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

        try {
            if (node.getStep() != null)
                node.getStep().accept(this);
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

        if (node.getContent() != null) {
            Scope scope = new Scope(Scope.ScopeType.loopScope, getCurrentScope(), getCurrentScope().getFuncReturnType(), getCurrentScope().getClassType());
            scopeStack.push(scope);
            try {
                node.getContent().accept(this);
            } catch (SyntaxException e) {
                System.out.println(e.getMessage());
                error = true;
            }
            scopeStack.pop();
        }

        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(WhileStatNode node) throws SyntaxException {
        node.setScope(getCurrentScope());
        boolean error = false;

        try {
            if (node.getCond() != null)
                node.getCond().accept(this);
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

        if (node.getStatList() != null) {
            Scope scope = new Scope(Scope.ScopeType.loopScope, getCurrentScope(), getCurrentScope().getFuncReturnType(), getCurrentScope().getClassType());
            scopeStack.push(scope);
            try {
                node.getStatList().accept(this);
            } catch (SyntaxException e) {
                System.out.println(e.getMessage());
                error = true;
            }
            scopeStack.pop();
        }


        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(BreakStatNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;

        try{
            if (!getCurrentScope().inLoopScope())
                throw new SyntaxException(node.getLocation(), "break statement not in a loop!");
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(ContinueStatNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;

        try{
            if (!getCurrentScope().inLoopScope())
                throw new SyntaxException(node.getLocation(), "continue statement not in a loop!");
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

    }

    @Override
    public void visit(ReturnStatNode node) throws SyntaxException {
        node.setScope(getCurrentScope());
        boolean error = false;
        getCurrentScope().setReturnStat(true);

        try{
            if (!getCurrentScope().inFuncScope())
                throw new SyntaxException(node.getLocation(), "return statement not in a function!");
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();

        Type returnType = typeTable.getType(getCurrentScope().getFuncReturnType());
        try {
            if (node.getReturnExpr() != null) {
                node.getReturnExpr().accept(this);
                if (returnType.equals(new VoidType()))
                    throw new SyntaxException(node.getLocation(), "don't need a return value");
                else if (Type.notEqualType(returnType, node.getReturnExpr().getType()))
                    throw new SyntaxException(node.getLocation(), "return type doesn't match");
            }
            else if (!returnType.equals(new VoidType())) {
                throw new SyntaxException(node.getLocation(), "need a return value");
            }
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(ExprStatNode node) throws SyntaxException {
        node.setScope(getCurrentScope());
        boolean error = false;

        try {
            node.getExpr().accept(this);
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();
    }


    @Override
    public void visit(BlockStatNode node) throws SyntaxException {
        boolean error = false;
        Scope scope = new Scope(Scope.ScopeType.blockScope, getCurrentScope(), getCurrentScope().getFuncReturnType(), getCurrentScope().getClassType());
        scopeStack.push(scope);
        node.setScope(scope);

        try {
            for (var i : node.getStat()) {
                if (i != null) i.accept(this);
            }
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        scopeStack.pop();
        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(PostfixExprNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;

        // number++;
        try {
            node.getExpr().accept(this);
            if (!node.getExpr().getType().equals(new IntType()) || !node.getExpr().getIfLvalue())
                throw new SyntaxException(node.getLocation(), "can't postfix");
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        node.setIfLvalue(false);
        node.setType(new IntType());

        if (error) throw new SyntaxException();
    }


    @Override
    public void visit(NewExprNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;

        TypeNode type = node.getTypeName();
        type.accept(this);

        int dimensions = node.getDimension();

        try {
            if (dimensions == 0) {
                if (!(typeTable.getType(node.getTypeName()) instanceof ClassType))
                    throw new SyntaxException(node.getLocation(), "can't create a system type with new");
                node.setIfLvalue(true);
                node.setType(typeTable.getType(node.getTypeName()));
            }
            else {
                for (int i = 0; i < dimensions - 1; ++i){
                    if (node.getDimList().get(i) == null && node.getDimList().get(i + 1) != null)
                        throw new SyntaxException(node.getLocation(), "The shape of multidimensional array must be specified from left to right.");
                }
                for (var i : node.getDimList()) {
                    if (i != null) {
                        i.accept(this);
                        if (!(i.getType().equals(new IntType())))
                            throw new SyntaxException(node.getLocation(), "the dimension must be int type");
                    }
                }
//                if ((typeTable.getType(node.getTypeName())) instanceof ArrayType) System.out.println("qaqqqqqqqqqqqqq");
                if (typeTable.getType(type) instanceof ClassType){
                    node.setClassType((ClassType) typeTable.getType(type));
                }
                node.setType(new ArrayType(typeTable.getType(node.getTypeName()), node.getDimension()));
                node.setIfLvalue(true);
            }
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        if (error) throw new SyntaxException();

    }


    @Override
    public void visit(FunccallExprNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;
        ExprNode id = node.getFuncName();
        FuncSymbol func = null;

        try {
            if (id instanceof IdExprNode) {
                id.setScope(getCurrentScope());
                Symbol idSymbol = getCurrentScope().getSymbol(((IdExprNode) id).getId());
                if (!(idSymbol instanceof FuncSymbol)) throw new SyntaxException(node.getLocation(), "not a functionName");
                else func = (FuncSymbol) idSymbol;
                id.setSymbol(func);
            }
            else if (id instanceof MemberExprNode) {
                id.accept(this);
                if (!(id.getType() instanceof MethodType)) throw new SyntaxException(node.getLocation(), "not a method");
                MethodType method = (MethodType) id.getType();
                if (method.getType() instanceof ArrayType) {
                    if (!(((ArrayType) method.getType()).hasMethod(method.getId()))) throw new SyntaxException(node.getLocation(), "have no method");
                    func = ((ArrayType) method.getType()).getMethod(method.getId());
                    id.setSymbol(func);
                }
                else if (method.getType() instanceof StringType) {
                    if (!(((StringType) method.getType()).hasMethod(method.getId()))) throw new SyntaxException(node.getLocation(), "have no method");
                    func = ((StringType) method.getType()).getMethod(method.getId());
                    id.setSymbol(func);
                }
                else if (method.getType() instanceof ClassType) {
//                    System.out.println("hehehe");
                    if (!(((ClassType) method.getType()).hasMethod(method.getId()))) throw new SyntaxException(node.getLocation(), "have no method");
                    func = ((ClassType) method.getType()).getMethod(method.getId());
                    node.setSymbol(func);
                    id.setSymbol(func);
                }
                else throw new SyntaxException(node.getLocation(), "not a method");
            }
            else throw new SyntaxException(node.getLocation(), "can't call a function");
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        if (error) throw new SyntaxException();

        try {
            for (var i : node.getParaList())
                i.accept(this);
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        if (error) throw new SyntaxException();

        try {
//            System.out.println(func.getParaList().size());
//            System.out.println(node.getParaList().size());
            if (func.getParaList().size() != node.getParaList().size()) throw new SyntaxException(node.getLocation(), "para isn't consistent");
            for (int i = 0; i < func.getParaList().size(); ++i) {
                if (Type.notEqualType(typeTable.getType(func.getParaList().get(i).getType()), node.getParaList().get(i).getType())){
//                    System.out.println(node.getParaList().get(i).getType().getId());
//                    System.out.println(typeTable.getType(func.getParaList().get(i).getType()).getId());
                    throw new SyntaxException(node.getLocation(), "para type don't match");
                }

            }
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        node.setIfLvalue(false);
        node.setType(typeTable.getType(func.getReturnType()));

        if (error) throw new SyntaxException();

    }


    @Override
    public void visit(ArrayExprNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;

        try {
            node.getBaseExpr().accept(this);
            node.getIndexExpr().accept(this);
            Type baseType = node.getBaseExpr().getType();
            Type indexType = node.getIndexExpr().getType();
            if (!(baseType instanceof ArrayType) || !(indexType instanceof IntType))
                throw new SyntaxException(node.getLocation(), "subscript error");
            node.setIfLvalue(true);
            if (((ArrayType) baseType).getDimemsion() == 1) {
//                System.out.println("yesyesyse@");
                node.setType(((ArrayType) baseType).getBaseType());
            }
            else node.setType(new ArrayType(((ArrayType) baseType).getBaseType(), ((ArrayType) baseType).getDimemsion() - 1));
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(MemberExprNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;

        try {
            node.getExpr().accept(this);
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }
        if (error) throw new SyntaxException();

        Type type = node.getExpr().getType();
        if (node.getId() != null) {
            String id = node.getId();
            try {
                if (type instanceof StringType) {
                    if (!((StringType) type).hasMethod(id)) throw new SyntaxException(node.getLocation(), "member error");
                    node.setSymbol(((StringType) type).getMethod(id));
                    node.setType(new MethodType(id, type));///   $$$$$$$$$$$$
                    node.setIfLvalue(false);
                }
                else if (type instanceof ArrayType) {
                    if (!((ArrayType) type).hasMethod(id)) throw new SyntaxException(node.getLocation(), "member error");
                    node.setSymbol(((ArrayType) type).getMethod(id));
                    node.setType(new MethodType(id, type));
                    node.setIfLvalue(false);
                }
                else if (type instanceof ClassType) {
//                    System.out.println("1++");
                    if (((ClassType) type).hasMember(id)) {
                        VarSymbol i = ((ClassType) type).getMember(id);
//                        System.out.println(typeTable.getType(i.getType()).getId());
                        typeTable.getType(i.getType());
                        node.setSymbol(i);
                        node.setType(typeTable.getType(i.getType()));
                        node.setIfLvalue(true);
                    }
                    else if (((ClassType) type).hasMethod(id)) {
                        node.setSymbol(((ClassType) type).getMethod(id));
                        node.setType(new MethodType(id, type));
                        node.setIfLvalue(false);
                    }
                    else throw new SyntaxException(node.getLocation(), "member error");
                }
                else throw new SyntaxException(node.getLocation(), "member error");
            } catch (SyntaxException e) {
                System.out.println(e.getMessage());
                error = true;
            }
        }
        else if (node.getThisExprNode() != null) {
            node.getThisExprNode().accept(this);
            throw new SyntaxException(node.getLocation(), "can't use this as a member");
        }


        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(PrefixExprNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;

        try {
            node.getExpr().accept(this);
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }
        if (error) throw new SyntaxException();

        PrefixExprNode.Op op = node.getOp();
        Type type = node.getExpr().getType();

        try {
            if (op == PrefixExprNode.Op.add || op == PrefixExprNode.Op.sub || op == PrefixExprNode.Op.bitnot) {
                if (!type.equals(new IntType())) throw new SyntaxException(node.getLocation(), "can't prefix_add_sub");
                node.setType(new IntType());
                node.setIfLvalue(false);
            }
            else if (op == PrefixExprNode.Op.addadd || op == PrefixExprNode.Op.subsub) {
                if (!type.equals(new IntType()) ||! node.getExpr().getIfLvalue())
                    throw new SyntaxException(node.getLocation(), "can't prefix_addadd_subsub");
                node.setType(new IntType());
                node.setIfLvalue(true);
            }
            else if (op == PrefixExprNode.Op.not) {
                if (!type.equals(new BoolType()))
                    throw new SyntaxException(node.getLocation(), "can't prefix_not");
                node.setType(new BoolType());
                node.setIfLvalue(false);
            }
            else throw new SyntaxException(node.getLocation(), "can't find a prefix_op");
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        if (error) throw new SyntaxException();
    }


    @Override
    public void visit(BinaryExprNode node) throws SyntaxException {
        node.setScope(getCurrentScope());
        boolean error = false;

        try {
//            System.out.println("go into yes ");
            node.getLs().accept(this);

            node.getRs().accept(this);
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        Type type1 = node.getLs().getType();
        Type type2 = node.getRs().getType();
        BinaryExprNode.Op op = node.getOp();



        if (error) throw new SyntaxException();
        try {
            // int - * / %  >> << & ^ |
            if (op == BinaryExprNode.Op.sub || op == BinaryExprNode.Op.mul || op == BinaryExprNode.Op.div ||
                    op == BinaryExprNode.Op.mod || op == BinaryExprNode.Op.shiftleft || op == BinaryExprNode.Op.shiftright ||
                    op == BinaryExprNode.Op.bitand || op == BinaryExprNode.Op.bitor || op == BinaryExprNode.Op.bitxor) {
                if (!type1.equals(new IntType()) || !type2.equals(new IntType()))
                    throw new SyntaxException(node.getLocation(), "l/r is not a int");
                node.setType(new IntType());
            } // int or string < > <= >=
            else if (op == BinaryExprNode.Op.le || op == BinaryExprNode.Op.leq || op == BinaryExprNode.Op.ge || op == BinaryExprNode.Op.geq) {
                if (!((type1.equals(new IntType()) && type2.equals(new IntType())) || (type1.equals(new StringType()) && type2.equals(new StringType()))))
                    throw new SyntaxException(node.getLocation(), "l/r is not string/int");
                node.setType(new BoolType());
            } //  int or string +
            else if (op == BinaryExprNode.Op.add) {
                if (!((type1.equals(new IntType()) && type2.equals(new IntType())) || (type1.equals(new StringType()) && type2.equals(new StringType()))))
                    throw new SyntaxException(node.getLocation(), "l/r is not string/int");
                if (type1.equals(new IntType()) && type2.equals(new IntType())) node.setType(new IntType());
                if (type1.equals(new StringType()) && type2.equals(new StringType())) node.setType(new StringType());
            }// int bool string ArrayType and ClassType nullType  == !=
            else if (op == BinaryExprNode.Op.eq || op == BinaryExprNode.Op.neq) {
                if ((type1.equals(new IntType()) && type2.equals(new IntType())) ||
                        (type1.equals(new IntType()) && type2.equals(new IntType())) ||
                        (type1.equals(new BoolType()) && type2.equals(new BoolType())) ||
                        (type1.equals(new StringType()) && type2.equals(new StringType())) ||
                        (type1 instanceof ArrayType && type2 instanceof ArrayType) ||
                        (type1 instanceof ArrayType && type2 instanceof NullType) ||
                        (type1.equals(new NullType()) && type2 instanceof ClassType) ||
                        (type1 instanceof ClassType && type2.equals(new NullType())) ||
                                (type1.equals(new NullType()) && type2.equals(new NullType()))) {
                    node.setType(new BoolType());
                    node.setIfLvalue(false);
                }
                else throw new SyntaxException(node.getLocation(), "can't be equal or not");
            } // bool && ||
            else if (op == BinaryExprNode.Op.and || op == BinaryExprNode.Op.or) {
                if (type1.equals(new BoolType()) && type2.equals(new BoolType()))
                    node.setType(new BoolType());
                else throw new SyntaxException(node.getLocation(), "can't && or ||");
            } //assign
            else if (op == BinaryExprNode.Op.assign) {
//                System.out.println(type1.getId());
//                System.out.println(type2.getId());
                if (!node.getLs().getIfLvalue() || Type.notEqualType(type1, type2)){
//                    System.out.println("+++++");
//                    System.out.println(type1 instanceof ArrayType);
//                    System.out.println(((ArrayType) type1).getDimemsion());
//                    System.out.println(type2 instanceof ArrayType);
//                    System.out.println(node.getLs().getIfLvalue());
                    throw new SyntaxException(node.getLocation(), "can't assign");
                }

                else node.setType(type1);
            }
            else throw new SyntaxException(node.getLocation(), "can't find binary op");
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }

        if (error) throw new SyntaxException();
    }


    @Override
    public void visit(ThisExprNode node) {
        boolean error = false;

        try {
            if (!getCurrentScope().inClassScope() || !getCurrentScope().inFuncScope()) {
//                System.out.println(getCurrentScope().inClassScope());
                throw new SyntaxException(node.getLocation(), "this key word not in the right scope");
            }
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        node.setIfLvalue(false);
        node.setType(getCurrentScope().getClassType());
//        System.out.println(getCurrentScope().getClassType().getId());
        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(ConstExprNode node) {
        node.setScope(getCurrentScope());
        if (node.getConstType() == ConstExprNode.ConstType.NULL) node.setType(new NullType());
        else if (node.getConstType() == ConstExprNode.ConstType.INT) node.setType(new IntType());
        else if (node.getConstType() == ConstExprNode.ConstType.STRING) node.setType(new StringType());
        else if (node.getConstType() == ConstExprNode.ConstType.BOOL) node.setType(new BoolType());
        else throw new SyntaxException(node.getLocation(), "no match const type");

        node.setIfLvalue(false);
    }

    @Override
    public void visit(IdExprNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;

        try {
            Symbol idSymbol = getCurrentScope().getSymbol(node.getId());
            if (!(idSymbol instanceof VarSymbol)) throw new SyntaxException(node.getLocation(), "not a varSymbol");
            node.setIfLvalue(true);
            node.setType(typeTable.getType(((VarSymbol) idSymbol).getType()));
            node.setSymbol(idSymbol);
            idSymbol.setReferred(true);
        } catch (SyntaxException e) {
            error = true;
            System.out.println(e.getMessage());
        }

        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(ArrayTypeNode node) {
        node.setScope(getCurrentScope());
        boolean error = false;
        try{
            if (!typeTable.hasType(node.getBaseType()))
                throw new SyntaxException(node.getLocation(), "don't have a type");
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
            error = true;
        }
        if (error) throw new SyntaxException();
    }

    @Override
    public void visit(SystemTypeNode node) {

    }

    @Override
    public void visit(ExprListNode node) {}

    @Override
    public void visit(VarListNode node) {
        node.setScope(getCurrentScope());
        for (var i : node.getVarDefList()) {
            i.accept(this);
        }
    }
}


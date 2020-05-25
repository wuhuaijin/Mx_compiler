package IR;

import AST.*;
import SymbolTable.Symbol.FuncSymbol;

import SymbolTable.Symbol.FuncSymbol.*;
import SymbolTable.Symbol.VarSymbol;
import SymbolTable.Type.ClassType;
import SymbolTable.TypeTable;

public class FuncVisitor implements ASTVisitor {

    private TypeTable typeTable;
    public FuncVisitor(TypeTable typeTable) {
        this.typeTable = typeTable;
    }


    @Override
    public void visit(MainProgNode node) {

    }

    @Override
    public void visit(ProgNode node) {
        for (var i : node.getDefList()) {
            i.accept(this);
        }
    }

    @Override
    public void visit(ClassDefNode node) {
        int offset = 0;
        for (var i : node.getVarList()) {
            VarSymbol j = ((VarSymbol) i.getSymbol());
//            System.out.println(j.getId());
//            System.out.println(offset);
            j.setOffset(offset);
            offset += 4;
        }
//        System.out.println(typeTable.getType(node.getClassName()));
//        System.out.println(typeTable.getType(node.getClassName()).getId());
        ((ClassType) typeTable.getType(node.getClassName())).setSize(offset);
        for (var i : node.getFuncList()) {
            i.setClassname(node.getClassName() + "_");
            i.accept(this);
        }
        if (node.getCreator() != null) {
            node.getCreator().setClassname(node.getClassName() + "_");
            node.getCreator().accept(this);
        }
    }



    @Override
    public void visit(FuncDefNode node) {

        FuncSymbol funcSymbol = node.getSymbol();
        Function func = new Function(node.getClassname() + node.getFuncName());
        if (node.getClassname().equals("")) func.setMethod(false);
        else func.setMethod(true);
        funcSymbol.setFunction(func);
//        System.out.println(funcSymbol);
//        System.out.println(node.getFuncName());
//        System.out.println(func.isMethod());
    }

    @Override
    public void visit(TypeNode node) {}

    @Override
    public void visit(ArrayTypeNode node) {}

    @Override
    public void visit(SystemTypeNode node) {}

    @Override
    public void visit(VarListNode node) {

    }

    @Override
    public void visit(VarDefNode node) {

    }

    @Override
    public void visit(ParaListNode node) {

    }

    @Override
    public void visit(VardefStatNode node) {

    }

    @Override
    public void visit(IfStatNode node) {

    }

    @Override
    public void visit(ForStatNode node) {

    }

    @Override
    public void visit(WhileStatNode node) {

    }

    @Override
    public void visit(BreakStatNode node) {

    }

    @Override
    public void visit(ContinueStatNode node) {

    }

    @Override
    public void visit(ReturnStatNode node) {

    }

    @Override
    public void visit(ExprStatNode node) {

    }

    @Override
    public void visit(BlockStatNode node) {

    }

    @Override
    public void visit(PostfixExprNode node) {

    }

    @Override
    public void visit(NewExprNode node) {

    }

    @Override
    public void visit(FunccallExprNode node) {

    }

    @Override
    public void visit(ArrayExprNode node) {

    }

    @Override
    public void visit(MemberExprNode node) {

    }

    @Override
    public void visit(PrefixExprNode node) {

    }

    @Override
    public void visit(BinaryExprNode node) {

    }

    @Override
    public void visit(ThisExprNode node) {

    }

    @Override
    public void visit(ConstExprNode node) {

    }

    @Override
    public void visit(IdExprNode node) {

    }

    @Override
    public void visit(ExprListNode node) {

    }
}

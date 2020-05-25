package AST;

import Helper.Location;
import AST.*;

public interface ASTVisitor {

    //prog
    void visit(MainProgNode node);

    void visit(ProgNode node);

    //type
    public abstract void visit(TypeNode node);

    public abstract void visit(ArrayTypeNode node);

    public abstract void visit(SystemTypeNode node);


    //def
    public abstract void visit(VarListNode node);

    public abstract void visit(VarDefNode node);

    public abstract void visit(FuncDefNode node);

    public abstract void visit(ClassDefNode node);

    public abstract void visit(ParaListNode node);
    //stat
    public abstract void visit(VardefStatNode node);

    public abstract void visit(IfStatNode node);

    public abstract void visit(ForStatNode node);

    public abstract void visit(WhileStatNode node);

    public abstract void visit(BreakStatNode node);

    public abstract void visit(ContinueStatNode node);

    public abstract void visit(ReturnStatNode node);

    public abstract void visit(ExprStatNode node);

    public abstract void visit(BlockStatNode node);

    //expr
    public abstract void visit(PostfixExprNode node);

    public abstract void visit(NewExprNode node);

    public abstract void visit(FunccallExprNode node);

    public abstract void visit(ArrayExprNode node);

    public abstract void visit(MemberExprNode node);

    public abstract void visit(PrefixExprNode node);

    public abstract void visit(BinaryExprNode node);

    public abstract void visit(ThisExprNode node);

    public abstract void visit(ConstExprNode node);

    public abstract void visit(IdExprNode node);

    //
    public abstract void visit(ExprListNode node);



}

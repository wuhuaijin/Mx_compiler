package Frontend;

import java.util.ArrayList;

import Parser.MxBaseVisitor;
import Parser.MxParser;

import Helper.Location;

import AST.*;


public class ASTBuilder extends MxBaseVisitor<Node> {


    @Override
    public Node visitMainProg(MxParser.MainProgContext ctx) {
        if (ctx.prog() != null) return visitProg(ctx.prog());
        else return null;
    }


    @Override
    public Node visitProg(MxParser.ProgContext ctx) {
        ArrayList<DefNode> defList = new ArrayList<>();
        for (var i : ctx.def()) {
            Node initdef = visitDef(i);
            if (initdef instanceof ClassDefNode)
                defList.add((ClassDefNode) initdef);
            else if (initdef instanceof  FuncDefNode)
                defList.add((FuncDefNode) initdef);
            else if (initdef instanceof  VarListNode)
                defList.add((VarListNode) initdef);
            else
                continue;
        }
        return new ProgNode(new Location(ctx.getStart()), defList);
    }

    @Override
    public Node visitDef(MxParser.DefContext ctx) {
        if (ctx.classDef() != null)
            return visitClassDef(ctx.classDef());
        else if (ctx.funcDef() != null)
            return visitFuncDef(ctx.funcDef());
        else if (ctx.varDef() != null)
            return visitVarDef(ctx.varDef());
        else return null;
    }

    @Override
    public Node visitClassDef(MxParser.ClassDefContext ctx) {
        String className = ctx.ID().getText();
        ArrayList<VarDefNode> varList = new ArrayList<>();
        ArrayList<FuncDefNode> funcList = new ArrayList<>();

        for (var i : ctx.varDef()) {
            ArrayList<VarDefNode> vars = ((VarListNode) visitVarDef(i)).getVarDefList();
            varList.addAll(vars);
        }
        for (var i : ctx.funcDef()) {
            Node funcdef = visitFuncDef(i);
            funcList.add((FuncDefNode) funcdef);
        }
        FuncDefNode creator = null;
        if (ctx.creatorDef().size() != 0) {
            creator = (FuncDefNode) visitCreatorDef(ctx.creatorDef(0));
        }

        return new ClassDefNode(new Location(ctx.getStart()), className, varList, funcList, creator);

    }

    @Override
    public Node visitFuncDef(MxParser.FuncDefContext ctx) {
        String funcName;
        funcName = ctx.ID().getText();
        TypeNode type;
        type = (TypeNode) visitClassStat(ctx.classStat());
        ArrayList<VarDefNode> paraList = new ArrayList<>();


        if (ctx.paraList() != null)
//            System.out.println("++++");
            paraList = ((ParaListNode) visitParaList(ctx.paraList())).getVarList();
//        else paraList = null;

        StatNode statNode = (StatNode) visitBlock(ctx.block());
        return new FuncDefNode(new Location(ctx.getStart()), funcName, type, paraList, statNode);

    }


    @Override
    public Node visitParaList(MxParser.ParaListContext ctx) {
        ArrayList<VarDefNode> varList = new ArrayList<>();
        for (var i : ctx.paraDef())
            varList.add((VarDefNode) visitParaDef(i));
        return new ParaListNode(new Location(ctx.getStart()), varList);
    }

    @Override
    public Node visitParaDef(MxParser.ParaDefContext ctx) {
        TypeNode type = (TypeNode) visitClassStat(ctx.classStat());
        return new VarDefNode(new Location(ctx.getStart()), type, ctx.ID().getText(), null);
    }

    @Override
    public Node visitVarDef(MxParser.VarDefContext ctx) {
        TypeNode type;
        type = (TypeNode) visitClassStat(ctx.classStat());
        ArrayList<VarDefNode> defList = new ArrayList<>();
        if (ctx.ID().size() == 1) {
            ExprNode assignExpr = (ctx.expr(0) == null)? null : (ExprNode) visit(ctx.expr(0));
            defList.add(new VarDefNode(new Location(ctx.getStart()), type, ctx.ID(0).getText(), assignExpr));
        }
        else {
            for (var id : ctx.ID()) {
                defList.add(new VarDefNode(new Location(ctx.getStart()), type, id.getText(), null));
            }
        }
        return new VarListNode(new Location(ctx.getStart()), defList);
    }

//    @Override
//    public Node visitVariableDef(MxParser.VariableDefContext ctx) {
//
//        TypeNode type;
//        type = (TypeNode) visitClassStat(ctx.classStat());
//        for (var i : ctx.ID())
//        varName = ctx.ID().getText();
//        ExprNode assignExpr = (ctx.expr() == null)? null : (ExprNode) visit(ctx.expr());
//        return new VarDefNode(new Location(ctx.getStart()), type, varName, assignExpr);
//    }


    @Override
    public Node visitVariableDef(MxParser.VariableDefContext ctx) {
        return super.visitVariableDef(ctx);
    }

    @Override
    public Node visitCreatorDef(MxParser.CreatorDefContext ctx) {
        TypeNode type = new TypeNode(new Location(ctx.getStart()), "void");
        StatNode statNode = (StatNode) visitBlock(ctx.block());
        ArrayList<VarDefNode> paraList = new ArrayList<>();
        return new FuncDefNode(new Location(ctx.getStart()), ctx.ID().getText(), type, paraList, statNode);
    }

    @Override
    public Node visitClassStat(MxParser.ClassStatContext ctx) {
        if (ctx.className() != null)
            return visitClassName(ctx.className());
        else return new ArrayTypeNode(new Location(ctx.getStart()), (TypeNode) visitClassStat(ctx.classStat()));
    }

    @Override
    public Node visitClassName(MxParser.ClassNameContext ctx) {
        if (ctx.BOOL() != null)
            return new TypeNode(new Location(ctx.getStart()), "bool");
        else if (ctx.INT() != null)
            return new TypeNode(new Location(0,0), "int");
        else if (ctx.STRING() != null)
            return new TypeNode(new Location(ctx.getStart()), "string");
        else if (ctx.VOID() != null)
            return new TypeNode(new Location(ctx.getStart()), "void");
        else if (ctx.ID() != null)
            return new TypeNode(new Location(ctx.getStart()), ctx.ID().getText());
        else {
//            errorCompile.throwError(new Location(ctx.getStart()), "no type of var");
            return null;
        }
    }

    @Override
    public Node visitBlock(MxParser.BlockContext ctx) {
        ArrayList<StatNode> statList = new ArrayList<>();
        for (var i : ctx.stat())
            statList.add((StatNode) visit(i));
        return new BlockStatNode(new Location(ctx.getStart()), statList);
    }

    @Override
    public Node visitIfStat(MxParser.IfStatContext ctx) {
        ExprNode cond = (ExprNode) visit(ctx.expr());
        StatNode mainContent = (StatNode) visit(ctx.stat(0));
        int flag = (ctx.ELSE() == null) ? 0 : 1;
        StatNode elseContent = (flag == 1) ? (StatNode) visit(ctx.stat(1)) : null;
        return new IfStatNode(new Location(ctx.getStart()), cond, mainContent, flag, elseContent);
    }

    @Override
    public Node visitDefineStat(MxParser.DefineStatContext ctx) {
        ArrayList<VarDefNode> a = ((VarListNode) visitVarDef(ctx.varDef())).getVarDefList();
        return new VardefStatNode(new Location(ctx.getStart()), a);
    }

    @Override
    public Node visitForStat(MxParser.ForStatContext ctx) {
        ExprNode init = ctx.init != null ? (ExprNode) visit(ctx.init) : null;
        ExprNode cond = ctx.cond != null ? (ExprNode) visit(ctx.cond) : null;
        ExprNode step = ctx.step != null ? (ExprNode) visit(ctx.step) : null;
        StatNode content = ctx.stat() != null ? (StatNode) visit(ctx.stat()) : null;
        return new ForStatNode(new Location(ctx.getStart()), init, cond, step, content);
    }

    @Override
    public Node visitWhileStat(MxParser.WhileStatContext ctx) {
        ExprNode cond = ctx.expr() != null ? (ExprNode) visit(ctx.expr()) : null;
        return new WhileStatNode(new Location(ctx.getStart()), cond, (StatNode) visit(ctx.stat()));
    }

    @Override
    public Node visitBreakStat(MxParser.BreakStatContext ctx) {
        return new BreakStatNode(new Location(ctx.getStart()));
    }

    @Override
    public Node visitContinueStat(MxParser.ContinueStatContext ctx) {
        return new ContinueStatNode(new Location(ctx.getStart()));
    }

    @Override
    public Node visitReturnStat(MxParser.ReturnStatContext ctx) {
        ExprNode returnExpr = ctx.expr() == null ? null :  (ExprNode) visit(ctx.expr());
        return new ReturnStatNode(new Location(ctx.getStart()), returnExpr);
    }

    @Override
    public Node visitExprStat(MxParser.ExprStatContext ctx) {
        ExprNode expr = (ExprNode) visit(ctx.expr());
        return new ExprStatNode(new Location(ctx.getStart()), expr);
    }

    @Override
    public Node visitBlockStat(MxParser.BlockStatContext ctx) {
        return visitBlock(ctx.block());   /// something strange
    }

    @Override
    public Node visitPrefixExpr(MxParser.PrefixExprContext ctx) {
        PrefixExprNode.Op op = null;
        String opText = ctx.op.getText();
        ExprNode expr = (ExprNode) visit(ctx.expr());
        switch (opText) {
            case "++":
                op = PrefixExprNode.Op.addadd;
                break;
            case "--":
                op = PrefixExprNode.Op.subsub;
                break;
            case "+":
                op = PrefixExprNode.Op.add;
                break;
            case "-":
                op = PrefixExprNode.Op.sub;
                break;
            case "!":
                op = PrefixExprNode.Op.not;
                break;
            case "~":
                op = PrefixExprNode.Op.bitnot;
                break;
        }
        return new PrefixExprNode(new Location(ctx.getStart()), op, expr);
    }

    @Override
    public Node visitPostfixExpr(MxParser.PostfixExprContext ctx) {
        PostfixExprNode.Op op = null;
        String opText = ctx.op.getText();
        ExprNode expr = (ExprNode) visit(ctx.expr());
        switch (opText) {
            case "++":
                op = PostfixExprNode.Op.add;
                break;
            case "--":
                op = PostfixExprNode.Op.sub;
                break;
        }
        return new PostfixExprNode(new Location(ctx.getStart()), op, expr);
    }


    @Override
    public Node visitFuncCallExpr(MxParser.FuncCallExprContext ctx) {
        ExprNode funcName = (ExprNode) visit(ctx.expr());
        if (ctx.exprList() != null)
            return new FunccallExprNode(new Location(ctx.getStart()), funcName,
                ((ExprListNode) visitExprList(ctx.exprList())).getExprList());
        else return new FunccallExprNode(new Location(ctx.getStart()), funcName,
                new ArrayList<ExprNode>());
    }

    @Override
    public Node visitArrayExpr(MxParser.ArrayExprContext ctx) {
        ExprNode baseExpr = (ExprNode) visit(ctx.expr(0));
        ExprNode indexExpr = (ExprNode) visit(ctx.expr(1));
        return new ArrayExprNode(new Location(ctx.getStart()), baseExpr, indexExpr);
    }

    @Override
    public Node visitThisExpr(MxParser.ThisExprContext ctx) {
        return new ThisExprNode(new Location(ctx.getStart()));
    }

    @Override
    public Node visitMemberExpr(MxParser.MemberExprContext ctx) {
        ExprNode expr = (ExprNode) visit(ctx.expr());
        String id = ctx.ID() == null ? null : ctx.ID().getText();
        ThisExprNode thisExprNode = ctx.THIS() == null ? null : new ThisExprNode(new Location(ctx.getStart()));
        return new MemberExprNode(new Location(ctx.getStart()), id, expr, thisExprNode);
    }

    @Override
    public Node visitBinaryExpr(MxParser.BinaryExprContext ctx) {
//        if (ctx.op == null) System.out.println("fuck");
        String opText = ctx.op.getText();
        BinaryExprNode.Op op = null;
        ExprNode ls = (ExprNode) visit(ctx.expr(0));
        ExprNode rs = (ExprNode) visit(ctx.expr(1));
        switch (opText) {
            case "*":
                op = BinaryExprNode.Op.mul;
                break;
            case "/":
                op = BinaryExprNode.Op.div;
                break;
            case "%":
                op = BinaryExprNode.Op.mod;
                break;
            case "+":
                op = BinaryExprNode.Op.add;
                break;
            case "-":
                op = BinaryExprNode.Op.sub;
                break;
            case "<<":
                op = BinaryExprNode.Op.shiftleft;
                break;
            case ">>":
                op = BinaryExprNode.Op.shiftright;
                break;
            case "<":
                op = BinaryExprNode.Op.le;
                break;
            case "<=":
                op = BinaryExprNode.Op.leq;
                break;
            case ">":
                op = BinaryExprNode.Op.ge;
                break;
            case ">=":
                op = BinaryExprNode.Op.geq;
                break;
            case "==":
                op = BinaryExprNode.Op.eq;
                break;
            case "!=":
                op = BinaryExprNode.Op.neq;
                break;
            case "&":
                op = BinaryExprNode.Op.bitand;
                break;
            case "^":
                op = BinaryExprNode.Op.bitxor;
                break;
            case "|":
                op = BinaryExprNode.Op.bitor;
                break;
            case "&&":
                op = BinaryExprNode.Op.and;
                break;
            case "||":
                op = BinaryExprNode.Op.or;
                break;
            case "=":
                op = BinaryExprNode.Op.assign;
                break;
        }
        return new BinaryExprNode(new Location(ctx.getStart()), op, ls, rs);
    }

    @Override
    public Node visitConstExpr(MxParser.ConstExprContext ctx) {
        Object value;
        if (ctx.TRUE() != null) {
            value = ctx.TRUE();
            return new ConstExprNode(new Location(ctx.getStart()), value, ConstExprNode.ConstType.BOOL);
        }
        else if (ctx.FALSE() != null) {
            value = ctx.FALSE();
            return new ConstExprNode(new Location(ctx.getStart()), value, ConstExprNode.ConstType.BOOL);
        }
        else if (ctx.NUM() != null) {
            value = ctx.NUM();
            return new ConstExprNode(new Location(ctx.getStart()), value, ConstExprNode.ConstType.INT);
        }
        else if (ctx.STR() != null) {
            value = ctx.STR();
            return new ConstExprNode(new Location(ctx.getStart()), value, ConstExprNode.ConstType.STRING);
        }
        else if (ctx.NULL() != null) {
            value = ctx.NULL();
            return new ConstExprNode(new Location(ctx.getStart()), value, ConstExprNode.ConstType.NULL);
        }
        else {
            value = null;
            return null;
        }
    }

    @Override
    public Node visitIdExpr(MxParser.IdExprContext ctx) {
        return new IdExprNode(new Location(ctx.getStart()), ctx.ID().getText());
    }


    @Override
    public Node visitNewExpr(MxParser.NewExprContext ctx) {
        NewExprNode newExpr = (NewExprNode) visitCreator(ctx.creator());
        return new NewExprNode(newExpr.getLocation(), newExpr.getTypeName(), newExpr.getDimList(), newExpr.getDimension());
    }

    @Override
    public Node visitCreator(MxParser.CreatorContext ctx) {
        TypeNode typeName = (TypeNode) visitClassName(ctx.className());

        int dimension = 0;
        ArrayList<ExprNode> dimList = new ArrayList<>();
        for (var i : ctx.dimension()){
            dimension++;
            if(i.expr() != null) dimList.add((ExprNode) visitDimension(i));
            else dimList.add(null);
        }
        return new NewExprNode(new Location(ctx.getStart()), typeName, dimList, dimension);

    }

    @Override
    public Node visitNoStat(MxParser.NoStatContext ctx) {
        return null;
    }

    @Override
    public Node visitExprList(MxParser.ExprListContext ctx) {
        ArrayList<ExprNode> exprList = new ArrayList<>();
        for (var i : ctx.expr())
            exprList.add((ExprNode) visit(i));
        return new ExprListNode(new Location(ctx.getStart()), exprList);
    }

    @Override
    public Node visitDimension(MxParser.DimensionContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Node visitSubExpr(MxParser.SubExprContext ctx) {
        return visit(ctx.expr());
    }
}

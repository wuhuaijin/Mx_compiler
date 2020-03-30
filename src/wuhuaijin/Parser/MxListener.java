// Generated from /Users/wuhuaijin/Documents/IntelliJ/Mxcompiler/src/Parser/Mx.g4 by ANTLR 4.8
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxParser}.
 */
public interface MxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxParser#mainProg}.
	 * @param ctx the parse tree
	 */
	void enterMainProg(MxParser.MainProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#mainProg}.
	 * @param ctx the parse tree
	 */
	void exitMainProg(MxParser.MainProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(MxParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(MxParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#def}.
	 * @param ctx the parse tree
	 */
	void enterDef(MxParser.DefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#def}.
	 * @param ctx the parse tree
	 */
	void exitDef(MxParser.DefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classDef}.
	 * @param ctx the parse tree
	 */
	void enterClassDef(MxParser.ClassDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classDef}.
	 * @param ctx the parse tree
	 */
	void exitClassDef(MxParser.ClassDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void enterFuncDef(MxParser.FuncDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void exitFuncDef(MxParser.FuncDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#varDef}.
	 * @param ctx the parse tree
	 */
	void enterVarDef(MxParser.VarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#varDef}.
	 * @param ctx the parse tree
	 */
	void exitVarDef(MxParser.VarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#variableDef}.
	 * @param ctx the parse tree
	 */
	void enterVariableDef(MxParser.VariableDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#variableDef}.
	 * @param ctx the parse tree
	 */
	void exitVariableDef(MxParser.VariableDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#creatorDef}.
	 * @param ctx the parse tree
	 */
	void enterCreatorDef(MxParser.CreatorDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#creatorDef}.
	 * @param ctx the parse tree
	 */
	void exitCreatorDef(MxParser.CreatorDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classStat}.
	 * @param ctx the parse tree
	 */
	void enterClassStat(MxParser.ClassStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classStat}.
	 * @param ctx the parse tree
	 */
	void exitClassStat(MxParser.ClassStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#className}.
	 * @param ctx the parse tree
	 */
	void enterClassName(MxParser.ClassNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#className}.
	 * @param ctx the parse tree
	 */
	void exitClassName(MxParser.ClassNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#paraList}.
	 * @param ctx the parse tree
	 */
	void enterParaList(MxParser.ParaListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#paraList}.
	 * @param ctx the parse tree
	 */
	void exitParaList(MxParser.ParaListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#paraDef}.
	 * @param ctx the parse tree
	 */
	void enterParaDef(MxParser.ParaDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#paraDef}.
	 * @param ctx the parse tree
	 */
	void exitParaDef(MxParser.ParaDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MxParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MxParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code defineStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterDefineStat(MxParser.DefineStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code defineStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitDefineStat(MxParser.DefineStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterIfStat(MxParser.IfStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitIfStat(MxParser.IfStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterForStat(MxParser.ForStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitForStat(MxParser.ForStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterWhileStat(MxParser.WhileStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitWhileStat(MxParser.WhileStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterBreakStat(MxParser.BreakStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitBreakStat(MxParser.BreakStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterContinueStat(MxParser.ContinueStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitContinueStat(MxParser.ContinueStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterReturnStat(MxParser.ReturnStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitReturnStat(MxParser.ReturnStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code noStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterNoStat(MxParser.NoStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code noStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitNoStat(MxParser.NoStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterExprStat(MxParser.ExprStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitExprStat(MxParser.ExprStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blockStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterBlockStat(MxParser.BlockStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blockStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitBlockStat(MxParser.BlockStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNewExpr(MxParser.NewExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNewExpr(MxParser.NewExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPrefixExpr(MxParser.PrefixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPrefixExpr(MxParser.PrefixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterThisExpr(MxParser.ThisExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitThisExpr(MxParser.ThisExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterArrayExpr(MxParser.ArrayExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitArrayExpr(MxParser.ArrayExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code memberExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMemberExpr(MxParser.MemberExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code memberExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMemberExpr(MxParser.MemberExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpr(MxParser.BinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpr(MxParser.BinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfixExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpr(MxParser.PostfixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postfixExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpr(MxParser.PostfixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallExpr(MxParser.FuncCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallExpr(MxParser.FuncCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code subExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterSubExpr(MxParser.SubExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code subExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitSubExpr(MxParser.SubExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterConstExpr(MxParser.ConstExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitConstExpr(MxParser.ConstExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIdExpr(MxParser.IdExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIdExpr(MxParser.IdExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#exprList}.
	 * @param ctx the parse tree
	 */
	void enterExprList(MxParser.ExprListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#exprList}.
	 * @param ctx the parse tree
	 */
	void exitExprList(MxParser.ExprListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreator(MxParser.CreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreator(MxParser.CreatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#dimension}.
	 * @param ctx the parse tree
	 */
	void enterDimension(MxParser.DimensionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#dimension}.
	 * @param ctx the parse tree
	 */
	void exitDimension(MxParser.DimensionContext ctx);
}
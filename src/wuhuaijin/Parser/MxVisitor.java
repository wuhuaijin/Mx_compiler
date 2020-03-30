// Generated from /Users/wuhuaijin/Documents/IntelliJ/Mxcompiler/src/Parser/Mx.g4 by ANTLR 4.8
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MxParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MxVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MxParser#mainProg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMainProg(MxParser.MainProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(MxParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDef(MxParser.DefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#classDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDef(MxParser.ClassDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#funcDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDef(MxParser.FuncDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef(MxParser.VarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#variableDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDef(MxParser.VariableDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#creatorDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreatorDef(MxParser.CreatorDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#classStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassStat(MxParser.ClassStatContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#className}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassName(MxParser.ClassNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#paraList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParaList(MxParser.ParaListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#paraDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParaDef(MxParser.ParaDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(MxParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code defineStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefineStat(MxParser.DefineStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStat(MxParser.IfStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStat(MxParser.ForStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStat(MxParser.WhileStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code breakStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStat(MxParser.BreakStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continueStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStat(MxParser.ContinueStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code returnStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStat(MxParser.ReturnStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code noStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNoStat(MxParser.NoStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStat(MxParser.ExprStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code blockStat}
	 * labeled alternative in {@link MxParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStat(MxParser.BlockStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpr(MxParser.NewExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixExpr(MxParser.PrefixExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisExpr(MxParser.ThisExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayExpr(MxParser.ArrayExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code memberExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberExpr(MxParser.MemberExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpr(MxParser.BinaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code postfixExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixExpr(MxParser.PostfixExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCallExpr(MxParser.FuncCallExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code subExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubExpr(MxParser.SubExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstExpr(MxParser.ConstExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MxParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdExpr(MxParser.IdExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#exprList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprList(MxParser.ExprListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#creator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreator(MxParser.CreatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#dimension}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDimension(MxParser.DimensionContext ctx);
}
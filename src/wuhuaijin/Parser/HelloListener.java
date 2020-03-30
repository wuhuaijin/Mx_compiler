// Generated from /Users/wuhuaijin/Documents/IntelliJ/Mxcompiler/src/Parser/Hello.g4 by ANTLR 4.8
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link HelloParser}.
 */
public interface HelloListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link HelloParser#assign}.
	 * @param ctx the parse tree
	 */
	void enterAssign(HelloParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by {@link HelloParser#assign}.
	 * @param ctx the parse tree
	 */
	void exitAssign(HelloParser.AssignContext ctx);
	/**
	 * Enter a parse tree produced by {@link HelloParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(HelloParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link HelloParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(HelloParser.ExprContext ctx);
}